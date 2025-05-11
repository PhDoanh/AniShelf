package com.library.anishelf.controller;

import com.library.anishelf.util.NotificationManagerUtil;
import com.library.anishelf.service.ServiceInvoker;
import com.library.anishelf.model.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Stack;

public class BasicController {
    private static final String DEFAULT_USER_FXML = "/image/general/small_avatar.png";
    private static final String LOGIN_FXML = "/view/UserLoginPage.fxml";
    protected static final String DEFAULT_BOOK_IMAGE = "/image/default/book.png";

    protected static final String ADMIN_MENU_FXML = "/view/AdminNavBar.fxml";
    protected static final String SETTING_FXML = "/view/AdminSettingPage.fxml";

    private static final String DASHBOARD_FXML = "/view/AdminHomePage.fxml";
    protected static final String TOPBOOK_CARD_FXML = "/view/TopBookCard.fxml";

    protected static final String MESSAGE_FXML = "/view/AdminEmailMessage.fxml";

    private static final String BOOK_PAGE_FXML = "/view/BookPage.fxml";
    private static final String USER_PAGE_FXML = "/view/UsersPage.fxml";
    private static final String BORROW_PAGE_FXML = "/view/BorrowedBookPage.fxml";
    private static final String RESERVATION_PAGE_FXML = "/view/ReservedBooksPage.fxml";

    private static Stack<String> titlePageStack = new Stack<>();

    protected static final FXMLLoader loginLoader;
    protected static final Node loginPane;

    protected static final FXMLLoader adminMenuPaneLoader;
    protected static final Node adminMenuPane;

    protected static Image defaultUserImage;
    protected static Image defaultBookImage;

    protected static final FXMLLoader dashboardLoader;
    protected static final AnchorPane dashboardPane;

    protected static final FXMLLoader topBookCardLoader;
    protected static final Node topBookCardPane;

    protected static final FXMLLoader messageLoader;
    protected static final Node messagePane;

    protected static final FXMLLoader userPagePaneLoader;
    protected static final Node userPagePane;

    protected static final FXMLLoader borrowPagePaneLoader;
    protected static final Node borrowPagePane;

    protected static final FXMLLoader bookPagePaneLoader;
    protected static final Node bookPagePane;

    protected static final FXMLLoader reservationPagePaneLoader;
    protected static final Node reservationPagePane;

    protected ServiceInvoker serviceInvoker = new ServiceInvoker();

    static {
        //load login
        loginLoader = loadFXML(LOGIN_FXML, BasicController.class);
        loginPane = loadPane(loginLoader, BasicController.class);

        //load default image
        defaultUserImage = new Image(BasicController.class.getResource(DEFAULT_USER_FXML).toExternalForm());
        defaultBookImage = new Image(BasicController.class.getResource(DEFAULT_BOOK_IMAGE).toExternalForm());

        //load dashboard
        dashboardLoader = loadFXML(DASHBOARD_FXML, BasicController.class);
        dashboardPane = loadPane(dashboardLoader, BasicController.class);

        //load some Item
        messageLoader = loadFXML(MESSAGE_FXML, BasicController.class);
        messagePane = loadPane(messageLoader, BasicController.class);
        topBookCardLoader = loadFXML(TOPBOOK_CARD_FXML, BasicController.class);
        topBookCardPane = loadPane(topBookCardLoader, BasicController.class);

        //load BookManagement-view
        bookPagePaneLoader = loadFXML(BOOK_PAGE_FXML, BasicController.class);
        bookPagePane = loadPane(bookPagePaneLoader, BasicController.class);

        //load UserManagement-view
        userPagePaneLoader = loadFXML(USER_PAGE_FXML, BasicController.class);
        userPagePane = loadPane(userPagePaneLoader, BasicController.class);

        //load BorrowMangament-view
        borrowPagePaneLoader = loadFXML(BORROW_PAGE_FXML, BasicController.class);
        borrowPagePane = loadPane(borrowPagePaneLoader, BasicController.class);

        //load Reservation-view
        reservationPagePaneLoader = loadFXML(RESERVATION_PAGE_FXML, BasicController.class);
        reservationPagePane = loadPane(reservationPagePaneLoader, BasicController.class);

        adminMenuPaneLoader = loadFXML(ADMIN_MENU_FXML,BasicController.class);
        adminMenuPane = loadPane(adminMenuPaneLoader, BasicController.class);
    }

    public Stack<String> getTitlePageStack() {
        return titlePageStack;
    }

    /**
     * hàm load Pane
     *
     * @param fxml
     * @param clazz
     * @param <T>
     * @return pane
     */
    public static <T> T loadPane(FXMLLoader fxml, Class<?> clazz) {
        T pane = null; // Khai báo biến pane kiểu T

        try {
            pane = fxml.load(); // Tải FXML và gán cho pane

            // Thiết lập các ràng buộc cho AnchorPane (nếu T là AnchorPane)
            if (pane instanceof AnchorPane) {
                AnchorPane.setTopAnchor((AnchorPane) pane, 0.0);
                AnchorPane.setBottomAnchor((AnchorPane) pane, 0.0);
                AnchorPane.setLeftAnchor((AnchorPane) pane, 0.0);
                AnchorPane.setRightAnchor((AnchorPane) pane, 0.0);
            } else if (pane instanceof HBox) {
                AnchorPane.setTopAnchor((HBox) pane, 0.0);
                AnchorPane.setBottomAnchor((HBox) pane, 0.0);
                AnchorPane.setLeftAnchor((HBox) pane, 0.0);
                AnchorPane.setRightAnchor((HBox) pane, 0.0);
            } else if (pane instanceof VBox) {
                AnchorPane.setTopAnchor((VBox) pane, 0.0);
                AnchorPane.setBottomAnchor((VBox) pane, 0.0);
                AnchorPane.setLeftAnchor((VBox) pane, 0.0);
                AnchorPane.setRightAnchor((VBox) pane, 0.0);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return pane; // Trả về pane
    }

    /**
     * hàm này dùng để loadFXML.
     *
     * @param fxml  đường dẫn của FXML
     * @param clazz
     * @return FXMLLoader yêu cầu
     */
    public static FXMLLoader loadFXML(String fxml, Class<?> clazz) {
        return new FXMLLoader(clazz.getResource(fxml));
    }

    /**
     * hàm dùng để set chiều rộng của child theo parent.
     *
     * @param child  có thể là VBox, HBox
     * @param parent là scrollPane
     * @param <T>    thuộc tính của child
     * @param <U>    thuộc tính của parent
     */
    public <T, U> void childFitWidthParent(T child, U parent) {
        if (child instanceof VBox && parent instanceof ScrollPane) {
            VBox vboxChild = (VBox) child;
            ScrollPane scrollPaneParent = (ScrollPane) parent;

            vboxChild.prefWidthProperty().bind(scrollPaneParent.widthProperty().subtract(16));
        } else if (child instanceof HBox && parent instanceof ScrollPane) {
            HBox hBoxChild = (HBox) child;
            ScrollPane scrollPaneParent = (ScrollPane) parent;

            hBoxChild.prefWidthProperty().bind(scrollPaneParent.widthProperty().subtract(16));
        } else if (child instanceof HBox && parent instanceof VBox) {
            HBox hBoxChild = (HBox) child;
            VBox vboxChild = (VBox) parent;

            hBoxChild.prefWidthProperty().bind(vboxChild.widthProperty().subtract(16));
        } else if (child instanceof AnchorPane && parent instanceof ScrollPane) {
            AnchorPane anchorPaneChild = (AnchorPane) child;
            ScrollPane scrollPaneParent = (ScrollPane) parent;

            anchorPaneChild.prefWidthProperty().bind(scrollPaneParent.widthProperty());
        } else if (child instanceof AnchorPane && parent instanceof VBox) {
            AnchorPane anchorPaneChild = (AnchorPane) child;
            VBox vboxChild = (VBox) parent;
            anchorPaneChild.prefWidthProperty().bind(vboxChild.widthProperty());
        }

    }

    /**
     * hàm dùng để set chiều cao của child theo parent.
     *
     * @param child  có thể là VBox, HBox
     * @param parent là scrollPane
     * @param <T>    thuộc tính của child
     * @param <U>    thuộc tính của parent
     */
    public <T, U> void childFitHeightParent(T child, U parent) {
        if (child instanceof VBox && parent instanceof ScrollPane) {
            VBox vboxChild = (VBox) child;
            ScrollPane scrollPaneParent = (ScrollPane) parent;

            vboxChild.prefHeightProperty().bind(scrollPaneParent.heightProperty().subtract(16));
        } else if (child instanceof HBox && parent instanceof ScrollPane) {
            HBox hBoxChild = (HBox) child;
            ScrollPane scrollPaneParent = (ScrollPane) parent;

            hBoxChild.prefHeightProperty().bind(scrollPaneParent.heightProperty().subtract(16));
        } else if (child instanceof HBox && parent instanceof VBox) {
            HBox hBoxChild = (HBox) child;
            VBox vboxChild = (VBox) parent;

            hBoxChild.prefHeightProperty().bind(vboxChild.heightProperty().subtract(16));
        } else if (child instanceof AnchorPane && parent instanceof ScrollPane) {
            AnchorPane anchorPaneChild = (AnchorPane) child;
            ScrollPane scrollPaneParent = (ScrollPane) parent;

            anchorPaneChild.prefHeightProperty().bind(scrollPaneParent.widthProperty().subtract(17));
        } else if(child instanceof AnchorPane && parent instanceof VBox) {
            AnchorPane anchorPaneChild = (AnchorPane) child;
            VBox vboxChild = (VBox) parent;
            anchorPaneChild.prefHeightProperty().bind(vboxChild.heightProperty());
        }

    }

    /**
     * Hàm lấy Cat từ List và chuyển về định dạng Cat1, Cat2, Cat3...
     * @param categories
     * @return
     */
    public String getCategories(List<Category> categories) {
        if (categories == null || categories.isEmpty()) {
            return "Không có danh mục"; // Trả về thông báo nếu không có danh mục
        }

        StringBuilder result = new StringBuilder(); // Sử dụng StringBuilder để xây dựng chuỗi

        for (int i = 0; i < categories.size(); i++) {
            // Giả sử mỗi Category có phương thức getName()
            result.append(categories.get(i).getCatagoryName());

            // Nếu không phải là phần tử cuối cùng, thêm dấu phẩy
            if (i < categories.size() - 1) {
                result.append(", ");
            }
        }

        return result.toString(); // Chuyển đổi StringBuilder thành String
    }

    /**
     * Hàm để lấy tất cả Author từ List và chuyển nó về định dạng Au1, Au2,...
     * @param authors
     * @return
     */
    public String getAuthors(List<Author> authors) {
        if (authors == null || authors.isEmpty()) {
            return "Không có danh mục"; // Trả về thông báo nếu không có danh mục
        }

        StringBuilder result = new StringBuilder(); // Sử dụng StringBuilder để xây dựng chuỗi

        for (int i = 0; i < authors.size(); i++) {
            result.append(authors.get(i).getName());

            if (i < authors.size() - 1) {
                result.append(", ");
            }
        }

        return result.toString(); // Chuyển đổi StringBuilder thành String
    }

    /**
     * Hàm để lấy mở cửa sổ lấy ảnh.
     *
     * @return đường dẫn của ảnh
     */
    public String getImagePath(Object o) {
        // Tạo một FileChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn ảnh");

        // Lọc chỉ cho phép chọn các tệp ảnh
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        // Mở hộp thoại chọn tệp và lấy tệp đã chọn
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            // Tạo tên tệp mới dựa trên ID người dùng
            String imageFile = o.toString() + getFileExtension(selectedFile.toPath());
            String newImageFile = "";
            Path avatarFolder =Paths.get("");
            if(o instanceof Member) {
                avatarFolder = Paths.get("src/main/resources/image/upload");
                newImageFile = "/image/upload/" + imageFile;
            } else if (o instanceof Book){
                avatarFolder = Paths.get("src/main/resources/image/upload");
                newImageFile = "/image/upload/" + imageFile;
            }
            try {
                // Tạo thư mục nếu chưa tồn tại
                if (Files.notExists(avatarFolder)) {
                    Files.createDirectories(avatarFolder);
                }

                Path destinationPath = avatarFolder.resolve(imageFile);

                // Xóa file nếu nó đã tồn tại
                if (Files.exists(destinationPath)) {
                    Files.delete(destinationPath);
                }

                BufferedImage originalImage = ImageIO.read(selectedFile);

                ImageIO.write(originalImage, "PNG",destinationPath.toFile());

                return destinationPath.toUri().toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;

        } else {
            System.out.println("Không có ảnh nào được chọn.");
            return null;
        }
    }
    private String getFileExtension(Path path) {
        String fileName = path.toString();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0) {
            return fileName.substring(dotIndex);
        }
        return "";
    }

    /**
     * Hàm kiểm tra tính hợp lệ của ngày tháng năm.
     *
     * @param dateStr ngày tháng cần kiểm tra
     * @return true/false
     */
    public boolean isValidDate(String dateStr) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setLenient(false); // Không cho phép ngày không hợp lệ

        try {
            Date date = dateFormat.parse(dateStr); // Kiểm tra định dạng và tính hợp lệ của ngày

            // Kiểm tra ngày không phải là ngày trong tương lai
            if (date.after(new Date())) {
                NotificationManagerUtil.showInfo("Ngày không được lớn hơn ngày hiện tại");
                return false;
            }
        } catch (ParseException e) {
            // Xảy ra ngoại lệ nếu định dạng ngày không hợp lệ
            NotificationManagerUtil.showInfo("Định dạng ngày không hợp lệ");
            return false;
        }
        return true;
    }


    /**
     * Hàm chuyển đổi format cho ngày tháng năm.
     * Nếu là định dạng dd/MM/yyyy thì chuyển sang yyyy-MM-dd.
     * Nếu là định dạng yyyy-MM-dd thì chuyển sang định dạng dd/MM/yyyy.
     *
     * @param dateStr ngày tháng năm cần chuyển đổi
     * @return ngày tháng năm đã dược chuyển đổi
     */
    public static String reformatDate(String dateStr) {
        SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");

        try {
            // Thử parse theo định dạng dd/MM/yyyy
            try {
                Date date = format1.parse(dateStr);
                return format2.format(date); // Chuyển sang yyyy-MM-dd
            } catch (ParseException e1) {
                // Nếu không được, thử parse theo định dạng yyyy-MM-dd
                Date date = format2.parse(dateStr);
                return format1.format(date); // Chuyển sang dd/MM/yyyy
            }
        } catch (ParseException e2) {
            // Nếu cả hai định dạng đều không đúng
            return null;
        }
    }
}