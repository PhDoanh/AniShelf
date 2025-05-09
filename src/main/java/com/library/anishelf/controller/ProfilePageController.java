package com.library.anishelf.controller;

import com.library.anishelf.dao.MemberDAO;
import com.library.anishelf.model.enums.Gender;
import com.library.anishelf.util.SceneManagerUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.control.ChoiceBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.io.IOException;


public class ProfilePageController {
    @FXML
    private VBox contentBox;

    @FXML
    private TextField phoneText, emailText, lastNameText,firstNameText;

    @FXML
    private DatePicker birthDate;

    @FXML
    private ChoiceBox<String> genderChoiceBox;

    @FXML
    private Label userIDText;

    @FXML
    private ImageView avatarImage;

    private static final String USER_MENU_FXML = "/view/NavigationBar.fxml";
    private String newImageFile;
    private String originalImagePath; // Lưu đường dẫn ảnh gốc để khôi phục nếu cần
    private boolean hasUnsavedImage = false; // Flag để kiểm tra xem có ảnh chưa lưu hay không
    private Path tempSrcImagePath = null; // Đường dẫn tệp ảnh tạm trong src
    private Path tempTargetImagePath = null; // Đường dẫn tệp ảnh tạm trong target

    public void initialize() {
        genderChoiceBox.getItems().addAll("nữ", "nam", "bê đê slay");
        showInfo();
        
        // Đảm bảo không có ảnh tạm từ phiên trước
        deleteTemporaryImage();
    }

    /**
     * hiển thị thông tin của user
     */
    public void showInfo() {
        lastNameText.setText(NavigationBarController.getMember().getPerson().getLastName());
        firstNameText.setText(NavigationBarController.getMember().getPerson().getFirstName());
        phoneText.setText(NavigationBarController.getMember().getPerson().getPhone());
        emailText.setText(NavigationBarController.getMember().getPerson().getEmail());
        genderChoiceBox.setValue(getGender(NavigationBarController.getMember().getPerson().getGender().toString()));
        String dateString = NavigationBarController.getMember().getPerson().getBirthdate();
        userIDText.setText(Integer.toString(NavigationBarController.getMember().getPerson().getId()));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate parsedDate = LocalDate.parse(dateString, formatter);
        birthDate.setValue(parsedDate);

        String imagePath = NavigationBarController.getMember().getPerson().getImagePath();

        try {
            if (imagePath.startsWith("/")) {
                // Đây là đường dẫn resource
                avatarImage.setImage(new Image(getClass().getResourceAsStream(imagePath)));
            } else {
                // Đây có thể là đường dẫn tuyệt đối hoặc URL
                File file = new File(imagePath);
                if (file.exists()) {
                    avatarImage.setImage(new Image(file.toURI().toString()));
                } else {
                    avatarImage.setImage(new Image(getClass().getResourceAsStream("/image/default/avatar.png")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            avatarImage.setImage(new Image(getClass().getResourceAsStream("/image/default/avatar.png")));
        }
    }

    /**
     * tải ảnh lên từ local folder.
     * @param actionEvent khi ấn
     */
    public void onLoadImageButtonAction(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn ảnh để tải lên");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog((Stage) this.userIDText.getScene().getWindow());
        if (selectedFile != null) {
            try {
                // Xóa ảnh tạm cũ nếu có
                deleteTemporaryImage();
                
                // Tạo tên file tạm cho phiên này
                String timestamp = String.valueOf(System.currentTimeMillis());
                String imageFile = userIDText.getText() + "_temp_" + timestamp + getFileExtension(selectedFile.toPath());
                
                // Lưu đường dẫn mới vào biến newImageFile để sử dụng khi Save
                newImageFile = "/image/upload/" + imageFile;
                
                // Lưu đường dẫn gốc nếu chưa lưu (để khôi phục khi cần)
                if (originalImagePath == null) {
                    originalImagePath = NavigationBarController.getMember().getPerson().getImagePath();
                    System.out.println("Đường dẫn ảnh gốc đã lưu: " + originalImagePath);
                }
                
                // Lưu vào thư mục tạm
                Path srcAvatarFolder = Paths.get("src/main/resources/image/upload");
                Path targetAvatarFolder = Paths.get("target/classes/image/upload");
                
                // Đảm bảo thư mục tồn tại
                if (Files.notExists(srcAvatarFolder)) {
                    Files.createDirectories(srcAvatarFolder);
                }
                if (Files.notExists(targetAvatarFolder)) {
                    Files.createDirectories(targetAvatarFolder);
                }

                // Đường dẫn tệp đích tạm thời
                tempSrcImagePath = srcAvatarFolder.resolve(imageFile);
                tempTargetImagePath = targetAvatarFolder.resolve(imageFile);

                // Xử lý ảnh
                BufferedImage originalImage = ImageIO.read(selectedFile);
                int width = originalImage.getWidth();
                int height = originalImage.getHeight();
                int size = Math.min(width, height);
                int x = (width - size) / 2;
                int y = (height - size) / 2;
                BufferedImage squareImage = originalImage.getSubimage(x, y, size, size);

                // Lưu vào cả hai thư mục tạm
                ImageIO.write(squareImage, "PNG", tempSrcImagePath.toFile());
                ImageIO.write(squareImage, "PNG", tempTargetImagePath.toFile());
                
                // Đánh dấu có ảnh chưa lưu
                hasUnsavedImage = true;
                
                // Chỉ hiển thị ảnh mới trên màn hình Information cho xem trước
                Image image = new Image(tempTargetImagePath.toUri().toString(), true);
                avatarImage.setImage(image);
                avatarImage.setPreserveRatio(true);

            } catch (IOException e) {
                e.printStackTrace();
                CustomerAlter.showMessage("Lỗi khi xử lý ảnh: " + e.getMessage());
            }
        }
    }

    /**
     * tìm cái đuôi file ảnh.
     * @param path đường dẫn
     * @return đuôi file ảnh
     */
    private String getFileExtension(Path path) {
        String fileName = path.toString();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0) {
            return fileName.substring(dotIndex);
        }
        return "";
    }

    /**
     * lưu ảnh.
     * @param actionEvent ấn vào
     */
    public void onSaveButtonAction(ActionEvent actionEvent) {
        try {
            // Cập nhật thông tin cá nhân
            if (hasUnsavedImage && newImageFile != null) {
                // Đổi tên tệp từ tạm thành chính thức
                String permanentImageFile = userIDText.getText() + getFileExtension(tempSrcImagePath);
                Path srcAvatarFolder = Paths.get("src/main/resources/image/upload");
                Path targetAvatarFolder = Paths.get("target/classes/image/upload");
                
                Path permanentSrcPath = srcAvatarFolder.resolve(permanentImageFile);
                Path permanentTargetPath = targetAvatarFolder.resolve(permanentImageFile);
                
                // Xóa tệp cũ nếu tồn tại
                if (Files.exists(permanentSrcPath)) {
                    Files.delete(permanentSrcPath);
                }
                if (Files.exists(permanentTargetPath)) {
                    Files.delete(permanentTargetPath);
                }
                
                // Di chuyển từ tệp tạm sang tệp chính thức
                Files.copy(tempSrcImagePath, permanentSrcPath);
                Files.copy(tempTargetImagePath, permanentTargetPath);
                
                // Cập nhật đường dẫn mới (không còn là tạm nữa)
                newImageFile = "/image/upload/" + permanentImageFile;
                NavigationBarController.getMember().getPerson().setImagePath(newImageFile);
                System.out.println("Đã lưu ảnh mới: " + newImageFile);
                
                // Xóa tệp tạm
                deleteTemporaryImage();
            } else if (newImageFile != null) {
                NavigationBarController.getMember().getPerson().setImagePath(newImageFile);
                System.out.println("Cập nhật đường dẫn ảnh: " + newImageFile);
            } else {
                System.out.println("Không có ảnh mới để lưu");
            }
            
            // Cập nhật các thông tin khác
            NavigationBarController.getMember().getPerson().setLastName(lastNameText.getText());
            NavigationBarController.getMember().getPerson().setFirstName(firstNameText.getText());
            NavigationBarController.getMember().getPerson().setGender(getGenderEnum(genderChoiceBox.getValue().toString()));
            NavigationBarController.getMember().getPerson().setEmail(emailText.getText());
            NavigationBarController.getMember().getPerson().setPhone(phoneText.getText());
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedDate = birthDate.getValue().format(formatter);
            NavigationBarController.getMember().getPerson().setBirthdate(formattedDate);

            // Lưu thông tin vào cơ sở dữ liệu
            MemberDAO.getInstance().updateEntity(NavigationBarController.getMember());
            
            // Làm mới hiển thị trên các màn hình khác
            refreshAllViews();
            
            // Reset các biến sau khi lưu thành công
            newImageFile = null;
            originalImagePath = null;
            hasUnsavedImage = false;
            
            // Thông báo thành công
            CustomerAlter.showMessage("Đã lưu thành công");
            
        } catch (SQLException e) {
            CustomerAlter.showMessage("Lỗi khi cập nhật cơ sở dữ liệu: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            CustomerAlter.showMessage("Lỗi khi xử lý tệp: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            CustomerAlter.showMessage("Lỗi không xác định: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Cập nhật giao diện người dùng trên tất cả các màn hình liên quan
     */
    private void refreshAllViews() {
        try {
            // Cập nhật NavigationBarController
            NavigationBarController navigationBarController = SceneManagerUtil.getInstance().getController(USER_MENU_FXML);
            if (navigationBarController != null) {
                navigationBarController.showInfo();
            }
            
            // Làm mới màn hình hiện tại
            showInfo();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * trả về String giới tính
     * @param genderEnum enum giới tính
     * @return String giới tính
     */
    private String getGender(String genderEnum) {
        if(genderEnum.equals("MALE")) {
            return "nam";
        } else if(genderEnum.equals("FEMALE")) {
            return "nữ";
        }
        return "bê đê slay";
    }

    /**
     * trả về enum giới tính
     * @param gender String
     * @return enum
     */
    private Gender getGenderEnum(String gender) {
        if(gender.equals("nữ")) {
            return Gender.FEMALE;
        } else if(gender.equals("nam")) {
            return Gender.MALE;
        }
        return Gender.OTHER;
    }

    /**
     * Xóa ảnh tạm nếu có
     */
    private void deleteTemporaryImage() {
        try {
            if (tempSrcImagePath != null && Files.exists(tempSrcImagePath)) {
                Files.delete(tempSrcImagePath);
            }
            if (tempTargetImagePath != null && Files.exists(tempTargetImagePath)) {
                Files.delete(tempTargetImagePath);
            }
            // Reset các biến
            tempSrcImagePath = null;
            tempTargetImagePath = null;
            hasUnsavedImage = false;
            originalImagePath = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Khôi phục ảnh đại diện gốc nếu người dùng chưa lưu thay đổi
     * Phương thức này được gọi khi điều hướng sang view khác
     */
    public void restoreOriginalImageIfNeeded() {
        if (hasUnsavedImage && originalImagePath != null) {
            System.out.println("Khôi phục ảnh gốc: " + originalImagePath);
            
            // Khôi phục đường dẫn ảnh gốc trong đối tượng Member
            NavigationBarController.getMember().getPerson().setImagePath(originalImagePath);
            
            // Cập nhật lại NavigationBarController để hiển thị ảnh gốc
            try {
                NavigationBarController navigationBarController = SceneManagerUtil.getInstance().getController(USER_MENU_FXML);
                if (navigationBarController != null) {
                    navigationBarController.showInfo();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            // Xóa các tệp ảnh tạm để tránh rò rỉ bộ nhớ
            deleteTemporaryImage();
        }
    }
}
