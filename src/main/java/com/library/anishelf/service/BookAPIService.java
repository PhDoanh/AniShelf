package com.library.anishelf.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.library.anishelf.dao.BookDAO;
import com.library.anishelf.model.Author;
import com.library.anishelf.model.Book;
import com.library.anishelf.model.Category;
import com.library.anishelf.util.CacheManagerUtil;
import com.library.anishelf.util.RuntimeDebugUtil;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Service tương tác với AniList GraphQL API để tìm kiếm manga/anime.
 * Chuyển đổi dữ liệu từ AniList sang đối tượng Book để tương thích với ứng dụng.
 */
public class BookAPIService {
    private static final String ANILIST_API_URL = "https://graphql.anilist.co";
    private static final int MAX_BOOK = 50;
    private static final Cache<String, List<Book>> bookCache = CacheManagerUtil.buildCache(20);
    private static final Random random = new Random(); // Dùng để tạo ID giống ISBN
    private static final RuntimeDebugUtil logger = RuntimeDebugUtil.getInstance();
    
    // Danh sách các từ khoá cần lọc nội dung người lớn
    private static final Set<String> ADULT_CONTENT_KEYWORDS = new HashSet<>(Arrays.asList(
            "hentai", "ecchi", "adult", "mature", "erotic", "erotica", "sex", "sexual", 
            "nsfw", "nude", "nudity", "pornography", "porn", "18+", "explicit"
    ));

    /**
     * Thực thi truy vấn GraphQL tới AniList API.
     * 
     * @param query Chuỗi truy vấn GraphQL 
     * @param variables Các biến cho truy vấn GraphQL
     * @return Kết quả dạng JSONObject
     * @throws IOException Nếu xảy ra lỗi I/O
     */
    private static JSONObject executeGraphQLQuery(String query, JSONObject variables) throws IOException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(ANILIST_API_URL);
            
            // Tạo thân yêu cầu
            JSONObject requestBody = new JSONObject();
            requestBody.put("query", query);
            if (variables != null) {
                requestBody.put("variables", variables);
            }
            
            // Thiết lập header và entỉty
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Accept", "application/json");
            httpPost.setEntity(new StringEntity(requestBody.toString(), StandardCharsets.UTF_8));
            
            logger.debug("BookAPIService", "Gửi yêu cầu đến AniList API: " + variables);
            
            // Thực thi yêu cầu
            try (CloseableHttpResponse response = client.execute(httpPost)) {
                HttpEntity entity = response.getEntity();
                
                if (entity != null) {
                    String responseString = EntityUtils.toString(entity);
                    return new JSONObject(responseString);
                }
            }
        }
        return null;
    }

    /**
     * Tìm kiếm manga dựa theo từ khoá.
     * 
     * @param title Từ khoá tìm kiếm
     * @return Danh sách truyện tranhj phù hợp với từ khoá
     */
    public static List<Book> searchBooksByKeyword(String title) {
        if (title == null || title.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        // Kiểm tra cache trước
        List<Book> cachedBooks = bookCache.getIfPresent(title);
        if (cachedBooks != null) {
            logger.info("BookAPIService", "Trả về kết quả từ cache cho từ khóa: " + title);
            return cachedBooks;
        }
        
        List<Book> books = new ArrayList<>();
        
        try {
            // Định nghĩa truy vấn GraphQL để tìm kiếm manga theo tiêu đề
            String query = "query ($search: String) {\n" +
                           "  Page(page: 1, perPage: 20) {\n" +
                           "    media(search: $search, type: MANGA, sort: POPULARITY_DESC) {\n" +
                           "      id\n" +
                           "      title {\n" +
                           "        romaji\n" +
                           "        english\n" +
                           "        native\n" +
                           "      }\n" +
                           "      description\n" +
                           "      coverImage {\n" +
                           "        large\n" +
                           "        medium\n" +
                           "      }\n" +
                           "      staff {\n" +
                           "        edges {\n" +
                           "          node {\n" +
                           "            name {\n" +
                           "              full\n" +
                           "            }\n" +
                           "          }\n" +
                           "          role\n" +
                           "        }\n" +
                           "      }\n" +
                           "      genres\n" +
                           "      siteUrl\n" +
                           "      isAdult\n" +
                           "    }\n" +
                           "  }\n" +
                           "}";
            
            // Thiết lập biến truy vấn
            JSONObject variables = new JSONObject();
            variables.put("search", title);
            
            // Thực thi truy vấn
            JSONObject response = executeGraphQLQuery(query, variables);
            
            if (response != null && !response.has("errors")) {
                JSONArray media = response.getJSONObject("data")
                                         .getJSONObject("Page")
                                         .getJSONArray("media");
                
                logger.info("BookAPIService", "Tìm thấy " + media.length() + " kết quả từ AniList cho từ khóa: " + title);
                
                for (int i = 0; i < media.length() && books.size() < MAX_BOOK; i++) {
                    JSONObject manga = media.getJSONObject(i);
                    
                    // Kiểm tra nội dung người lớn
                    if (containsAdultContent(manga)) {
                        logger.debug("BookAPIService", "Bỏ qua nội dung người lớn: " + 
                                manga.getJSONObject("title").optString("english", 
                                manga.getJSONObject("title").optString("romaji")));
                        continue;
                    }
                    
                    // Chuyển đổi manga từ AniList thành đối tượng Book
                    Book book = mapMangaToBook(manga);
                    if (book != null) {
                        books.add(book);
                    }
                }
            }
            
            // Lưu kết quả vào cache
            bookCache.put(title, books);
            logger.info("BookAPIService", "Đã lưu " + books.size() + " sách vào cache cho từ khóa: " + title);
            
        } catch (Exception e) {
            logger.error("BookAPIService", "Lỗi khi tìm kiếm truyện: " + e.getMessage(), e);
        }
        
        return books;
    }

    /**
     * Tìm kiếm manga theo ID (được dùng như thay thế ISBN).
     * 
     * @param id ID AniList hoặc chuỗi giống ISBN
     * @return Đối tượng Book nếu tìm thấy, null nếu không
     */
    public static Book searchBookByISBN(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }
        
        // Kiểm tra cache trước
        List<Book> cachedBooks = bookCache.getIfPresent(id);
        if (cachedBooks != null && !cachedBooks.isEmpty()) {
            logger.info("BookAPIService", "Trả về kết quả từ cache cho ID: " + id);
            return cachedBooks.get(0);
        }
        
        try {
            int mediaId;
            try {
                mediaId = Integer.parseInt(id);
            } catch (NumberFormatException e) {
                // Nếu ID không phải ID hợp lệ của AniList, thử tìm theo tiêu đề
                logger.debug("BookAPIService", "ID không hợp lệ, thử tìm theo từ khóa: " + id);
                List<Book> books = searchBooksByKeyword(id);
                if (!books.isEmpty()) {
                    return books.get(0);
                }
                return null;
            }
            
            // Định nghĩa truy vấn GraphQL để lấy manga theo ID
            String query = "query ($id: Int) {\n" +
                           "  Media(id: $id, type: MANGA) {\n" +
                           "    id\n" +
                           "    title {\n" +
                           "      romaji\n" +
                           "      english\n" +
                           "      native\n" +
                           "    }\n" +
                           "    description\n" +
                           "    coverImage {\n" +
                           "      large\n" +
                           "      medium\n" +
                           "    }\n" +
                           "    staff {\n" +
                           "      edges {\n" +
                           "        node {\n" +
                           "          name {\n" +
                           "            full\n" +
                           "          }\n" +
                           "        }\n" +
                           "        role\n" +
                           "      }\n" +
                           "    }\n" +
                           "    genres\n" +
                           "    siteUrl\n" +
                           "    isAdult\n" +
                           "  }\n" +
                           "}";
            
            // Thiết lập biến truy vấn
            JSONObject variables = new JSONObject();
            variables.put("id", mediaId);
            
            // Thực thi truy vấn
            JSONObject response = executeGraphQLQuery(query, variables);
            
            if (response != null && !response.has("errors") && response.getJSONObject("data").has("Media")) {
                JSONObject manga = response.getJSONObject("data").getJSONObject("Media");
                
                // Kiểm tra nội dung người lớn
                if (containsAdultContent(manga)) {
                    logger.warning("BookAPIService", "Bỏ qua nội dung người lớn cho ID: " + id);
                    return null;
                }
                
                // Chuyển đổi manga từ AniList thành đối tuợng Book
                Book book = mapMangaToBook(manga);
                
                // Lưu kết quả vào cache
                if (book != null) {
                    List<Book> cacheList = new ArrayList<>();
                    cacheList.add(book);
                    bookCache.put(id, cacheList);
                    logger.info("BookAPIService", "Đã lưu sách vào cache cho ID: " + id);
                    return book;
                }
            }
            
        } catch (Exception e) {
            logger.error("BookAPIService", "Lỗi khi lấy truyện theo ID: " + e.getMessage(), e);
        }
        
        return null;
    }

    /**
     * Kiểm tra xem manga có chứa nội dung người lớn hay không.
     * 
     * @param manga JSONObject chứa dữ liệu manga từ AniList
     * @return true nếu chứa nội dung người lớn, false nếu không
     */
    private static boolean containsAdultContent(JSONObject manga) {
        // Kiểm tra flag isAdult
        if (manga.has("isAdult") && manga.getBoolean("isAdult")) {
            return true;
        }
        
        // Kiểm tra thể loại (genres)
        if (manga.has("genres") && !manga.isNull("genres")) {
            JSONArray genres = manga.getJSONArray("genres");
            for (int i = 0; i < genres.length(); i++) {
                String genre = genres.getString(i).toLowerCase();
                if (ADULT_CONTENT_KEYWORDS.contains(genre)) {
                    return true;
                }
            }
        }
        
        // Kiểm tra mô tả
        if (manga.has("description") && !manga.isNull("description")) {
            String description = manga.getString("description").toLowerCase();
            for (String keyword : ADULT_CONTENT_KEYWORDS) {
                if (description.contains(keyword)) {
                    return true;
                }
            }
        }
        
        // Kiểm tra tiêu đề
        if (manga.has("title") && !manga.isNull("title")) {
            JSONObject title = manga.getJSONObject("title");
            String[] titleFields = {"english", "romaji", "native"};
            
            for (String field : titleFields) {
                if (title.has(field) && !title.isNull(field)) {
                    String titleText = title.getString(field).toLowerCase();
                    for (String keyword : ADULT_CONTENT_KEYWORDS) {
                        if (titleText.contains(keyword)) {
                            return true;
                        }
                    }
                }
            }
        }
        
        return false;
    }

    /**
     * Chuyển đổi dữ liệu manga từ AniList sang đối tượng Book.
     * 
     * @param manga JSONObject chứa dữ liệu manga từ AniList
     * @return Đối tượng Book được điền dữ liệu từ manga
     */
    private static Book mapMangaToBook(JSONObject manga) {
        try {
            JSONObject title = manga.getJSONObject("title");
            
            // Tạo ID giống ISBN từ ID AniList
            long isbn = generateIsbnFromId(manga.getLong("id"));
            
            // Lấy tiêu đề tốt nhất có thể
            String bookTitle = title.optString("english");
            if (bookTitle == null || bookTitle.isEmpty() || bookTitle.equals("null")) {
                bookTitle = title.optString("romaji");
                if (bookTitle == null || bookTitle.isEmpty() || bookTitle.equals("null")) {
                    bookTitle = title.optString("native", "Tiêu đề không xác định");
                }
            }
            
            // Lấy mô tả (xoá các thẻ HTML)
            String description = manga.optString("description", "Không có mô tả.");
            description = description.replaceAll("<br>", "\n").replaceAll("<.*?>", "");
            
            // Kiểm tra và lọc nội dung không phù hợp trong mô tả
            description = filterAdultContent(description);
            
            // Lấy ảnh bìa
            String imagePath = Book.DEFAULT_IMAGE_PATH;
            if (manga.has("coverImage") && !manga.isNull("coverImage")) {
                JSONObject coverImage = manga.getJSONObject("coverImage");
                imagePath = coverImage.optString("large", coverImage.optString("medium", Book.DEFAULT_IMAGE_PATH));
            }
            
            // Lấy tác giả (thành viên trong nhóm "Story" hoặc "Art")
            List<Author> authors = new ArrayList<>();
            if (manga.has("staff") && !manga.isNull("staff")) {
                JSONArray staffEdges = manga.getJSONObject("staff").optJSONArray("edges");
                if (staffEdges != null) {
                    for (int i = 0; i < staffEdges.length(); i++) {
                        JSONObject edge = staffEdges.getJSONObject(i);
                        String role = edge.optString("role", "").toLowerCase();
                        
                        if (role.contains("story") || role.contains("art") || role.contains("author") || 
                            role.contains("illustrat") || role.contains("original")) {
                            JSONObject node = edge.getJSONObject("node");
                            String name = node.getJSONObject("name").optString("full", "Không xác định");
                            authors.add(new Author(name));
                        }
                    }
                }
            }
            
            // Nếu không tìm thấy tác giả cụ thể, thêm giá trị mặc định
            if (authors.isEmpty()) {
                authors.add(new Author("Tác giả không xác định"));
            }
            
            // Lấy thể lọai như các danh mục
            List<Category> categories = new ArrayList<>();
            JSONArray genres = manga.optJSONArray("genres");
            if (genres != null) {
                for (int i = 0; i < genres.length(); i++) {
                    String genre = genres.getString(i);
                    // Chỉ thêm thể loại phù hợp
                    if (!ADULT_CONTENT_KEYWORDS.contains(genre.toLowerCase())) {
                        categories.add(new Category(genre));
                    }
                }
            }
            
            // Tạo đối tượng Book
            Book book = new Book(isbn, bookTitle, imagePath, description, "Khu vực Manga", authors, categories);
            
            // Đặt URL xem trước tới trang AniList
            String siteUrl = manga.optString("siteUrl", "");
            book.setPreview(siteUrl);
            
            logger.debug("BookAPIService", "Đã chuyển đổi manga thành sách: " + bookTitle);
            return book;
            
        } catch (Exception e) {
            logger.error("BookAPIService", "Lỗi khi chuyển đội manga thành sách: " + e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Lọc nội dung người lớn khỏi mô tả
     * 
     * @param description Mô tả gốc
     * @return Mô tả đã lọc
     */
    private static String filterAdultContent(String description) {
        if (description == null) {
            return "Không có mô tả.";
        }
        
        String lowercaseDesc = description.toLowerCase();
        for (String keyword : ADULT_CONTENT_KEYWORDS) {
            if (lowercaseDesc.contains(keyword)) {
                // Thay thế đoạn văn chứa từ khóa bằng dấu [...]
                String[] sentences = description.split("\\. ");
                StringBuilder filtered = new StringBuilder();
                
                for (String sentence : sentences) {
                    if (!sentence.toLowerCase().contains(keyword)) {
                        filtered.append(sentence).append(". ");
                    } else {
                        filtered.append("[...] ");
                    }
                }
                
                return filtered.toString().trim();
            }
        }
        
        return description;
    }
    
    /**
     * Tạo ID giống ISBN từ ID AniList.
     * Đảm bảo tương thích với mã hiện tại yêu cầu số ISBN.
     * 
     * @param id ID AniList
     * @return Số 13 chữ số giống ISBN
     */
    private static long generateIsbnFromId(long id) {
        // Sử dụng ID AniList làm seed để tạo ngẫu nhiên nhưng xác định ISBN
        Random seededRandom = new Random(id);
        
        // ISBN-13 bắt đầu bằng 978 hoặc 979
        StringBuilder isbnBuilder = new StringBuilder("978");
        
        // Thêm 9 chữ số ngẫu nhiên (giữ ID gốc trong chuỗi khi có thể)
        String idStr = String.valueOf(id);
        if (idStr.length() <= 9) {
            isbnBuilder.append(idStr);
            // Thêm các chữ số ngẫu nhiên nếu cần
            for (int i = idStr.length(); i < 9; i++) {
                isbnBuilder.append(seededRandom.nextInt(10));
            }
        } else {
            // Nếu ID dài hơn 9 chữ số, sử dụng 9 ký tự đầu tiên
            isbnBuilder.append(idStr.substring(0, 9));
        }
        
        // Tính toán và thêm chữ số kiểm tra
        String isbn12 = isbnBuilder.toString();
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int digit = Character.getNumericValue(isbn12.charAt(i));
            sum += (i % 2 == 0) ? digit : digit * 3;
        }
        int checkDigit = (10 - (sum % 10)) % 10;
        isbnBuilder.append(checkDigit);
        
        return Long.parseLong(isbnBuilder.toString());
    }

    /**
     * Phương thức kiểm tra cho service.
     */
    public static void main(String[] args) {
        // Thử tìm kiếm theo từ khoá
        List<Book> books = searchBooksByKeyword("One Piece");
        for (Book book : books) {
            book.setQuantity(10);
            try {
                if (BookDAO.getInstance().findById(book.getIsbn()) == null) {
                    BookDAO.getInstance().insert(book);
                }
            } catch (Exception e) {
                logger.error("BookAPIService", "Lỗi khi thêm sách vào CSDL: " + e.getMessage(), e);
            }
            System.out.println(book.getIsbn() + " " + book.getTitle());
        }
        
        // Thứ tìm theo ID
        Book book = searchBookByISBN("21");  // ID của One Piece trong AniList
        if (book != null) {
            System.out.println("Tìm thấy bằng ID: " + book.getTitle());
        }
    }
}
