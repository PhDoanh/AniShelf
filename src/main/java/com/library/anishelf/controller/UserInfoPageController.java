package com.library.anishelf.controller;

import com.library.anishelf.service.AdminService;
import com.library.anishelf.service.ServiceHandler;
import com.library.anishelf.dao.BookIssueDAO;
import com.library.anishelf.model.BookIssue;
import com.library.anishelf.model.Member;
import com.library.anishelf.model.Person;
import com.library.anishelf.model.enums.AccountStatus;
import com.library.anishelf.model.enums.BookIssueStatus;
import com.library.anishelf.model.enums.Gender;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserInfoPageController extends BaseDetailController<Member> {
    @FXML
    private TextField birthText;

    @FXML
    private Button choiceImageButton;

    @FXML
    private Button editButton;

    @FXML
    private TextField emailText;

    @FXML
    private ChoiceBox<Gender> genderBox;

    @FXML
    private TextField memberIDText;

    @FXML
    private TextField memberNameText;

    @FXML
    private TextField numberOfBorrowText;

    @FXML
    private TextField numberOfLostText;

    @FXML
    private TextField phoneNumberText;

    @FXML
    private TextField resignDateText;

    @FXML
    private Button saveButton;

    @FXML
    private Button deleteButton;

    @FXML
    private ChoiceBox<AccountStatus> statusBox;

    @FXML
    private ImageView userImage;

    private AdminEmailMessageController adminEmailMessageController;

    @FXML
    private ScrollPane historyScrollpane;

    @FXML
    private VBox historyTableVbox;


    @Override
    protected void loadItemDetails() {
        getTitlePageStack().push(item.getPerson().getId() + "");
        emailText.setText(item.getPerson().getEmail());
        adminEmailMessageController.setToEmail(item.getPerson().getEmail());
        genderBox.setValue(item.getPerson().getGender());
        memberIDText.setText(String.valueOf(item.getPerson().getId()));
        memberNameText.setText(item.getPerson().getLastName() + " " + item.getPerson().getFirstName());
        numberOfBorrowText.setText(String.valueOf(item.getCheckedOutBooksCount()));
        phoneNumberText.setText(item.getPerson().getPhone());
        try {
            Map<String, Object> findCriteriaa = new HashMap<>();
            findCriteriaa.put("BookIssueStatus", BookIssueStatus.BORROWED);
            findCriteriaa.put("member_ID", item.getPerson().getId());

            int borrowBook = BookIssueDAO.getInstance().findByCriteria(findCriteriaa).size();
            item.setCheckedOutBooksCount(borrowBook);
            numberOfBorrowText.setText(String.valueOf(item.getCheckedOutBooksCount()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Map<String, Object> findCriteriaa = new HashMap<>();
            findCriteriaa.put("BookIssueStatus", BookIssueStatus.LOST);
            findCriteriaa.put("member_ID", item.getPerson().getId());
            int lostBook = BookIssueDAO.getInstance().findByCriteria(findCriteriaa).size();
            item.setCheckedOutBooksCount(lostBook);
            numberOfLostText.setText(String.valueOf(lostBook));
        } catch (Exception e) {
            e.printStackTrace();
        }

        phoneNumberText.setText(item.getPerson().getPhone());
        birthText.setText(reformatDate(item.getPerson().getBirthdate().toString()));
        statusBox.setValue(item.getStatus());

        //Xử lý ngày tháng đăng ký
        String resignDate = item.getCreatedDate().toString();
        String formatResignDate = reformatDate(resignDate);
        resignDateText.setText(formatResignDate);

        // Nếu như ảnh của member mà không có hoặc đường dẫn ảnh lỗi thì set mặc định
        try {
            File file = new File(item.getPerson().getImagePath());
            userImage.setImage(new Image(file.toURI().toString()));
        } catch (Exception e) {
            userImage.setImage(new Image(getClass().getResourceAsStream("/image/default/avatar.png")));
        }

        loadHistoryUser();
    }
    @Override
    protected void updateAddModeUI() {
        // Xử lý ẩn hiện các nút
        editButton.setVisible(!addMode);
        //choiceImageButton.setVisible(!addMode);
        saveButton.setVisible(addMode);
        saveButton.setText("Add");

        //Cho phép người dùng nhập các trường
        emailText.setEditable(addMode);
        genderBox.setMouseTransparent(!addMode);
        memberNameText.setEditable(addMode);
        phoneNumberText.setEditable(addMode);
        resignDateText.setEditable(addMode);
        birthText.setEditable(addMode);
        statusBox.setMouseTransparent(!addMode);

        //Nếu như mà là mở addMode thì các trường sẽ rỗng (addMode == true)
        if (addMode) {
            item = new Member(new Person());
            userImage.setImage(defaultUserImage);
            emailText.setText(null);
            genderBox.setValue(null);
            birthText.setText(null);
            memberIDText.setText(null);
            memberNameText.setText(null);
            numberOfBorrowText.setText(null);
            numberOfLostText.setText(null);
            phoneNumberText.setText(null);
            resignDateText.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yy")));
            statusBox.setValue(AccountStatus.ACTIVE);
        }
    }
    @Override
    protected void updateEditModeUI() {
        //Xử lý các nút ẩn hiện
        editButton.setVisible(!editMode);
        //choiceImageButton.setVisible(!editMode);
        saveButton.setVisible(editMode);
        saveButton.setText("Save");
        deleteButton.setVisible(editMode);

        //Cho phép người dùng sửa các trường
        emailText.setEditable(editMode);
        genderBox.setMouseTransparent(!editMode);
        memberNameText.setEditable(editMode);
        phoneNumberText.setEditable(editMode);
        resignDateText.setEditable(editMode);
        birthText.setEditable(editMode);
        statusBox.setMouseTransparent(!editMode);
    }
    @Override
    protected boolean validateInput() {
        // Kiểm tra xem tên không được rỗng
        if (memberNameText.getText() == null || memberNameText.getText().isEmpty()) {
            CustomerAlter.showMessage("Tên không được để trống.");
            return false;
        }

        // Kiểm tra định dạng email hợp lệ
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        if (emailText.getText()== null || !emailText.getText().matches(emailRegex)) {
            CustomerAlter.showMessage("Email không hợp lệ");
            return false;
        }

        // Kiểm tra số điện thoại không rỗng và chỉ chứa số
        String phoneRegex = "^[0-9]{10,15}$"; // Ví dụ: chỉ chứa từ 10 đến 15 chữ số
        if (phoneNumberText.getText()== null || !phoneNumberText.getText().matches(phoneRegex)) {
            CustomerAlter.showMessage("Số điện thoại không hợp lệ.");
            return false;
        }

        // Kiểm tra giới tính không được rỗng
        if (genderBox.getValue() == null) {
            CustomerAlter.showMessage("Giới tính không được để trống.");
            return false;
        }

        // Kiểm tra ngày sinh hợp lệ
        if (!isValidDate(birthText.getText())) {
            return false;
        }

        // Nếu tất cả kiểm tra đều đạt, trả về true
        return true;
    }
    @Override
    protected boolean getNewItemInformation() throws Exception {
        // Xử lý firstName và lastName
        String memberName = memberNameText.getText();
        if (memberName != null) {
            String[] nameParts = memberName.trim().split("\\s+");
            item.getPerson().setLastName(nameParts[0]); // Tên đầu tiên
            StringBuilder firstName = new StringBuilder();
            for(int i = 1; i < nameParts.length; i++) {
                firstName.append(nameParts[i]).append(" ");
            }
            item.getPerson().setFirstName(firstName.toString().trim());
        } else {
            item.getPerson().setFirstName(null);
            item.getPerson().setLastName(null);
        }

        // Lấy các thông tin khác.
        item.getPerson().setEmail(emailText.getText());
        item.getPerson().setGender(genderBox.getValue());
        item.getPerson().setPhone(phoneNumberText.getText());
        item.getPerson().setGender(genderBox.getValue());
        item.setStatus(statusBox.getValue());

        // đổi kiểu ngay từ dd/MM/yyyy thành yyyy-MM-dd
        String birthDate = birthText.getText();
        String reformattedDate = reformatDate(birthDate);
        item.getPerson().setBirthdate(reformattedDate);

        //nếu như person không có ảnh thì cho ảnh mặc định
        if (item.getPerson().getImagePath() == null) {
            item.getPerson().setImagePath(Person.DEFAULT_IMAGE_PATH);
        }

        return true;

    }

    @Override
    public String getType() {
        return "độc giả";
    }

    @FXML
    void initialize() {
        genderBox.getItems().addAll(Gender.values());
        statusBox.getItems().addAll(AccountStatus.values());
        adminEmailMessageController = messageLoader.getController();
    }


    //Nút chọn ảnh
    @FXML
    void onChoiceImageButtonAction(ActionEvent event) {
        item.getPerson().setImagePath(getImagePath(item));

        // Xử lý nếu như có chọn ảnh thì trường avatar của người dùng hiện ảnh
        if (item.getPerson().getImagePath() != null) {
            Image image = new Image(item.getPerson().getImagePath());
            userImage.setImage(image);
        }
    }

    @FXML
    void onEditButtonAction(ActionEvent event) {
        getTitlePageStack().push("Edit");
        if (item != null) {
            setEditMode(true);
        }
    }

    /**
     * Save button dùng chung cho cả AddMode và EditMode.
     *
     * @param event
     */
    @FXML
    void onSaveButtonAction(ActionEvent event) {
        saveChanges();
    }

    @FXML
    void onDeleteButtonAction(ActionEvent event) {
        boolean confrimYes = CustomerAlter.showAlter("Bạn muốn xóa người dùng này?");
        if (confrimYes) {
            ServiceHandler deleteServiceHandler = new AdminService("delete", this.item);
            serviceInvoker.setServiceHandler(deleteServiceHandler);
            if (serviceInvoker.invokeService()) {
                mainController.loadData();
                loadStartStatus();
                System.out.println("Đã xóa người dùng");
            } else {
                CustomerAlter.showMessage("Xóa người dùng thất bại");
            }
        } else {
            System.out.println("Tiếp tục edit");
        }
    }


    /**
     * Hàm để load bảng Detail về trạng thái rỗng ban đầu.
     */
    @Override
    public void loadStartStatus() {
        //Set member của bảng Detail rỗng
        item= null;

        // Set các trường nhập thành rỗng
        userImage.setImage(null);
        emailText.setText(null);
        genderBox.setValue(null);
        birthText.setText(null);
        memberIDText.setText(null);
        memberNameText.setText(null);
        numberOfBorrowText.setText(null);
        numberOfLostText.setText(null);
        phoneNumberText.setText(null);
        resignDateText.setText(null);
        statusBox.setValue(null);

        // Set trạng thái bảng về noneMode
        setAddMode(false);
        setEditMode(false);
    }
    private void loadHistoryUser() {
        historyTableVbox.getChildren().clear();
        Map<String, Object> findCriteria2 = new HashMap<>();
        findCriteria2.put("member_ID", item.getPerson().getId());
        try {
            List<BookIssue> bookIssueList = BookIssueDAO.getInstance().findByCriteria(findCriteria2);
            for (BookIssue bookIssue : bookIssueList) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserHistoryRow.fxml"));
                HBox row = loader.load();

                UserHistoryRowController rowController = loader.getController();
                rowController.setBookItem(bookIssue);

                row.prefWidthProperty().bind(historyScrollpane.widthProperty());
                historyTableVbox.getChildren().add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
