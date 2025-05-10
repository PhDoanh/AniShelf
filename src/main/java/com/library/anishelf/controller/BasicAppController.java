package com.library.anishelf.controller;

import com.library.anishelf.service.command.CommandInvoker;
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

public class BasicAppController {
    private static final String DEFAULT_USER_FXML_VIEW = "/image/customer/menu/ava.png";
    private static final String LOGIN_FXML_VIEW = "/view/UserLogin.fxml";
    protected static final String DEFAULT_BOOK_IMAGE_VIEW = "/image/book/default.png";

    protected static final String ADMIN_MENU_FXML_VIEW = "/view/AdminMenu.fxml";
    protected static final String SETTING_FXML ="/view/AdminSetting.fxml";

    private static final String DASHBOARD_FXML_VIEW = "/view/AdminDashBoardMain.fxml";
    protected static final String TOPBOOK_CARD_FXML_VIEW = "/view/TopBookCard.fxml";
    protected static final String ISSUE_RECENT_ROW_FXML_VIEW = "/view/AdminRecentIssuelTableRow.fxml";

    protected static final String MESSAGE_FXML_VIEW = "/view/AdminMessage.fxml";
1
    private static final String BOOK_PAGE_FXML_VIEW = "/view/AdminBookPage.fxml";
    private static final String USER_PAGE_FXML_VIEW = "/view/AdminUserPage.fxml";
    private static final String ISSUE_PAGE_FXML_VIEW = "/view/AdminIssuePage.fxml";
    private static final String BORROW_PAGE_FXML_VIEW = "/view/AdminBorrowPage.fxml";
    private static final String RESERVATION_PAGE_FXML_VIEW ="/view/AdminReservationPage.fxml";

    private static Stack<String> titleOfPageStack = new Stack<>();

    protected static final FXMLLoader loginViewLoader;
    protected static final Node loginView;

    protected static final FXMLLoader adminMenuLoader;
    protected static final Node adminMenuView;

    protected static Image defaultUserAvatar;
    protected static Image defaultBookCover;

    protected static final FXMLLoader dashboardLoaderView;
    protected static final AnchorPane dashboardView;

    protected static final FXMLLoader topBookCardLoaderView;
    protected static final Node topBookCardView;
    protected static final FXMLLoader recentIssueRowPaneLoaderView;
    protected static final Node recentIssueRowView;

    protected static final FXMLLoader messageLoaderView;
    protected static final Node messagePaneView;

    protected static final FXMLLoader userPagePaneLoaderView;
    protected static final Node userPagePaneView;

    protected static final FXMLLoader issuePagePaneLoaderView;
    protected static final Node issuePagePaneView;

    protected static final FXMLLoader borrowPagePaneLoader;
    protected static final Node borrowPagePane;

    protected static final FXMLLoader bookPagePaneLoaderView;
    protected static final Node bookPagePaneView;

    protected static final FXMLLoader reservationPagePaneLoaderView;
    protected static final Node reservationPagePaneView;

    protected CommandInvoker commandProcessor = new CommandInvoker();

    static {
        //load login
        loginViewLoader = loadFXMLResource(LOGIN_FXML_VIEW, BasicAppController.class);
        loginView = loadPane(loginViewLoader, BasicAppController.class);

        //load default image
        defaultUserAvatar = new Image(BasicAppController.class.getResource(DEFAULT_USER_FXML_VIEW).toExternalForm());
        defaultBookCover = new Image(BasicAppController.class.getResource(DEFAULT_BOOK_IMAGE_VIEW).toExternalForm());

        //load dashboard
        dashboardLoaderView = loadFXMLResource(DASHBOARD_FXML_VIEW, BasicAppController.class);
        dashboardView = loadPane(dashboardLoaderView, BasicAppController.class);

        //load some Item
        messageLoaderView = loadFXMLResource(MESSAGE_FXML_VIEW, BasicAppController.class);
        messagePaneView = loadPane(messageLoaderView, BasicAppController.class);
        topBookCardLoaderView = loadFXMLResource(TOPBOOK_CARD_FXML_VIEW, BasicAppController.class);
        topBookCardView = loadPane(topBookCardLoaderView, BasicAppController.class);
        recentIssueRowPaneLoaderView = loadFXMLResource(ISSUE_RECENT_ROW_FXML_VIEW, BasicAppController.class);
        recentIssueRowView = loadPane(recentIssueRowPaneLoaderView, BasicAppController.class);


        //load BookManagement-view
        bookPagePaneLoaderView = loadFXMLResource(BOOK_PAGE_FXML_VIEW, BasicAppController.class);
        bookPagePaneView = loadPane(bookPagePaneLoaderView, BasicAppController.class);

        //load UserManagement-view
        userPagePaneLoaderView = loadFXMLResource(USER_PAGE_FXML_VIEW, BasicAppController.class);
        userPagePaneView = loadPane(userPagePaneLoaderView, BasicAppController.class);

        //load IssueManagement-view
        issuePagePaneLoaderView = loadFXMLResource(ISSUE_PAGE_FXML_VIEW, BasicAppController.class);
        issuePagePaneView = loadPane(issuePagePaneLoaderView, BasicAppController.class);

        //load BorrowMangament-view
        borrowPagePaneLoader = loadFXMLResource(BORROW_PAGE_FXML_VIEW, BasicAppController.class);
        borrowPagePane = loadPane(borrowPagePaneLoader, BasicAppController.class);

        //load Reservation-view
        reservationPagePaneLoaderView = loadFXMLResource(RESERVATION_PAGE_FXML_VIEW, BasicAppController.class);
        reservationPagePaneView = loadPane(reservationPagePaneLoaderView, BasicAppController.class);

        adminMenuLoader = loadFXMLResource(ADMIN_MENU_FXML_VIEW, BasicAppController.class);
        adminMenuView = loadPane(adminMenuLoader, BasicAppController.class);
    }

    public Stack<String> getTitlePageStack() {
        return titleOfPageStack;
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
     * hàm này dùng để loadFXMLResource.
     *
     * @param fxml  đường dẫn của FXML
     * @param clazz
     * @return FXMLLoader yêu cầu
     */
    public static FXMLLoader loadFXMLResource(String fxml, Class<?> clazz) {
        return new FXMLLoader(clazz.getResource(fxml));
    }

    /**
     * hàm dùng để set chiều rộng của child theo parent.
     *
     * @param child  có thể là VBox, HBox
     * @param parent là scrollPaneContainer
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
     * @param parent là scrollPaneContainer
     * @param <T>    thuộc tính của child
     * @param <U>    thuộc tính của parent
     */
    public <T, U> void childFitHeightOfParent(T child, U parent) {
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
    public String getCategoriesFromList(List<Category> categories) {
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
    public String getAuthorsFromList(List<Author> authors) {
        if (authors == null || authors.isEmpty()) {
            return "Không có danh mục"; // Trả về thông báo nếu không có danh mục
        }

        StringBuilder result = new StringBuilder(); // Sử dụng StringBuilder để xây dựng chuỗi

        for (int i = 0; i < authors.size(); i++) {
            result.append(authors.get(i).getAuthorName());

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
    public String getImagePathView(Object o) {
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
            String imageFile = o.toString() + getFileExtensionFromPath(selectedFile.toPath());
            String newImageFile = "";
            Path avatarFolder =Paths.get("");
            if(o instanceof Member) {
                avatarFolder = Paths.get("src/main/resources/image/avatar");
                newImageFile = "/image/avatar/" + imageFile;
            } else if (o instanceof Book){
                avatarFolder = Paths.get("src/main/resources/image/book");
                newImageFile = "/image/book/" + imageFile;
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
    private String getFileExtensionFromPath(Path path) {
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
    public boolean isitValidDate(String dateStr) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setLenient(false); // Không cho phép ngày không hợp lệ

        try {
            Date date = dateFormat.parse(dateStr); // Kiểm tra định dạng và tính hợp lệ của ngày

            // Kiểm tra ngày không phải là ngày trong tương lai
            if (date.after(new Date())) {
                CustomerAlter.showMessage("Ngày tháng không được là ngày trong tương lai.");
                return false;
            }
        } catch (ParseException e) {
            // Xảy ra ngoại lệ nếu định dạng ngày không hợp lệ
            CustomerAlter.showMessage("Ngày tháng định dạng không hợp lệ phải định dạng dd/MM/yyyy");
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
    public static String reformatTheDate(String dateStr) {
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

