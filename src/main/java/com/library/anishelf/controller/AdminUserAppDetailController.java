package com.library.anishelf.controller;

import com.library.anishelf.service.command.AdminCommand;
import com.library.anishelf.service.command.Command;
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

public class AdminUserAppDetailController extends BaseDetailController<Member> {
    @FXML
    private TextField birthTextField;

    @FXML
    private Button selectImageButton;

    @FXML
    private Button edittttButton;

    @FXML
    private TextField emailTextField;

    @FXML
    private ChoiceBox<Gender> genderUserBox;

    @FXML
    private TextField memberIDTextField;

    @FXML
    private TextField memberNameTextField;

    @FXML
    private TextField numberOfBorrowBookTextField;

    @FXML
    private TextField numberOfLostTextField;

    @FXML
    private TextField phoneNumberTextField;

    @FXML
    private TextField resignDateTextField;

    @FXML
    private Button saveeeButton;

    @FXML
    private Button deleteeeButton;

    @FXML
    private ChoiceBox<AccountStatus> ConditionBox;

    @FXML
    private ImageView userImageView;

    private AdminMessageController adminMessageController;

    @FXML
    private ScrollPane historyScrollContainer;

    @FXML
    private VBox historyTableContainer;


    @Override
    protected void fetchBookItemDetails() {
        getTitlePageStack().push(item.getPerson().getId() + "");
        emailTextField.setText(item.getPerson().getEmail());
        adminMessageController.setToEmail(item.getPerson().getEmail());
        genderUserBox.setValue(item.getPerson().getGender());
        memberIDTextField.setText(String.valueOf(item.getPerson().getId()));
        memberNameTextField.setText(item.getPerson().getLastName() + " " + item.getPerson().getFirstName());
        numberOfBorrowBookTextField.setText(String.valueOf(item.getTotalBooksCheckOut()));
        phoneNumberTextField.setText(item.getPerson().getPhone());
        try {
            Map<String, Object> findCriteriaa = new HashMap<>();
            findCriteriaa.put("BookIssueStatus", BookIssueStatus.BORROWED);
            findCriteriaa.put("member_ID", item.getPerson().getId());

            int borrowBook = BookIssueDAO.getInstance().searchByCriteria(findCriteriaa).size();
            item.setTotalBooksCheckOut(borrowBook);
            numberOfBorrowBookTextField.setText(String.valueOf(item.getTotalBooksCheckOut()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Map<String, Object> findCriteriaa = new HashMap<>();
            findCriteriaa.put("BookIssueStatus", BookIssueStatus.LOST);
            findCriteriaa.put("member_ID", item.getPerson().getId());
            int lostBook = BookIssueDAO.getInstance().searchByCriteria(findCriteriaa).size();
            item.setTotalBooksCheckOut(lostBook);
            numberOfLostTextField.setText(String.valueOf(lostBook));
        } catch (Exception e) {
            e.printStackTrace();
        }

        phoneNumberTextField.setText(item.getPerson().getPhone());
        birthTextField.setText(reformatDate(item.getPerson().getDateOfBirth().toString()));
        ConditionBox.setValue(item.getStatus());

        //Xử lý ngày tháng đăng ký
        String resignDate = item.getCreatedDate().toString();
        String formatResignDate = reformatDate(resignDate);
        resignDateTextField.setText(formatResignDate);

        // Nếu như ảnh của member mà không có hoặc đường dẫn ảnh lỗi thì set mặc định
        try {
            File file = new File(item.getPerson().getImagePath());
            userImageView.setImage(new Image(file.toURI().toString()));
        } catch (Exception e) {
            userImageView.setImage(new Image(getClass().getResourceAsStream("/image/avatar/default.png")));
        }

        loadHistoryUser();
    }
    @Override
    protected void updateAddModeUIUX() {
        // Xử lý ẩn hiện các nút
        edittttButton.setVisible(!addMode);
        //selectImageButton.setVisible(!addMode);
        saveeeButton.setVisible(addMode);
        saveeeButton.setText("Add");

        //Cho phép người dùng nhập các trường
        emailTextField.setEditable(addMode);
        genderUserBox.setMouseTransparent(!addMode);
        memberNameTextField.setEditable(addMode);
        phoneNumberTextField.setEditable(addMode);
        resignDateTextField.setEditable(addMode);
        birthTextField.setEditable(addMode);
        ConditionBox.setMouseTransparent(!addMode);

        //Nếu như mà là mở addMode thì các trường sẽ rỗng (addMode == true)
        if (addMode) {
            item = new Member(new Person());
            userImageView.setImage(defaultUserImage);
            emailTextField.setText(null);
            genderUserBox.setValue(null);
            birthTextField.setText(null);
            memberIDTextField.setText(null);
            memberNameTextField.setText(null);
            numberOfBorrowBookTextField.setText(null);
            numberOfLostTextField.setText(null);
            phoneNumberTextField.setText(null);
            resignDateTextField.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yy")));
            ConditionBox.setValue(AccountStatus.ACTIVE);
        }
    }
    @Override
    protected void updateEditModeUIUX() {
        //Xử lý các nút ẩn hiện
        edittttButton.setVisible(!editMode);
        //selectImageButton.setVisible(!editMode);
        saveeeButton.setVisible(editMode);
        saveeeButton.setText("Save");
        deleteeeButton.setVisible(editMode);

        //Cho phép người dùng sửa các trường
        emailTextField.setEditable(editMode);
        genderUserBox.setMouseTransparent(!editMode);
        memberNameTextField.setEditable(editMode);
        phoneNumberTextField.setEditable(editMode);
        resignDateTextField.setEditable(editMode);
        birthTextField.setEditable(editMode);
        ConditionBox.setMouseTransparent(!editMode);
    }
    @Override
    protected boolean validateDataItemInput() {
        // Kiểm tra xem tên không được rỗng
        if (memberNameTextField.getText() == null || memberNameTextField.getText().isEmpty()) {
            CustomerAlter.showMessage("Tên không được để trống.");
            return false;
        }

        // Kiểm tra định dạng email hợp lệ
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        if (emailTextField.getText()== null || !emailTextField.getText().matches(emailRegex)) {
            CustomerAlter.showMessage("Email không hợp lệ");
            return false;
        }

        // Kiểm tra số điện thoại không rỗng và chỉ chứa số
        String phoneRegex = "^[0-9]{10,15}$"; // Ví dụ: chỉ chứa từ 10 đến 15 chữ số
        if (phoneNumberTextField.getText()== null || !phoneNumberTextField.getText().matches(phoneRegex)) {
            CustomerAlter.showMessage("Số điện thoại không hợp lệ.");
            return false;
        }

        // Kiểm tra giới tính không được rỗng
        if (genderUserBox.getValue() == null) {
            CustomerAlter.showMessage("Giới tính không được để trống.");
            return false;
        }

        // Kiểm tra ngày sinh hợp lệ
        if (!isValidDate(birthTextField.getText())) {
            return false;
        }

        // Nếu tất cả kiểm tra đều đạt, trả về true
        return true;
    }
    @Override
    protected boolean getNewDataItemInformation() throws Exception {
        // Xử lý firstName và lastName
        String memberName = memberNameTextField.getText();
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
        item.getPerson().setEmail(emailTextField.getText());
        item.getPerson().setGender(genderUserBox.getValue());
        item.getPerson().setPhone(phoneNumberTextField.getText());
        item.getPerson().setGender(genderUserBox.getValue());
        item.setStatus(ConditionBox.getValue());

        // đổi kiểu ngay từ dd/MM/yyyy thành yyyy-MM-dd
        String birthDate = birthTextField.getText();
        String reformattedDate = reformatDate(birthDate);
        item.getPerson().setDateOfBirth(reformattedDate);

        //nếu như person không có ảnh thì cho ảnh mặc định
        if (item.getPerson().getImagePath() == null) {
            item.getPerson().setImagePath(Person.DEFAULT_IMAGE_PATH);
        }

        return true;

    }

    @Override
    public String getItemType() {
        return "độc giả";
    }

    @FXML
    void initialize() {
        genderUserBox.getItems().addAll(Gender.values());
        ConditionBox.getItems().addAll(AccountStatus.values());
        adminMessageController = messageLoader.getController();
    }


    //Nút chọn ảnh
    @FXML
    void handleChoiceImageButton(ActionEvent event) {
        item.getPerson().setImagePath(getImagePath(item));

        // Xử lý nếu như có chọn ảnh thì trường avatar của người dùng hiện ảnh
        if (item.getPerson().getImagePath() != null) {
            Image image = new Image(item.getPerson().getImagePath());
            userImageView.setImage(image);
        }
    }

    @FXML
    void handleEditButton(ActionEvent event) {
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
    void handleDeleteButton(ActionEvent event) {
        boolean confrimYes = CustomerAlter.showAlter("Bạn muốn xóa người dùng này?");
        if (confrimYes) {
            Command deleteCommand = new AdminCommand("delete", this.item);
            commandInvoker.setCommand(deleteCommand);
            if (commandInvoker.executeCommand()) {
                mainController.loadData();
                loadInitalStatus();
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
    public void loadInitalStatus() {
        //Set member của bảng Detail rỗng
        item= null;

        // Set các trường nhập thành rỗng
        userImageView.setImage(null);
        emailTextField.setText(null);
        genderUserBox.setValue(null);
        birthTextField.setText(null);
        memberIDTextField.setText(null);
        memberNameTextField.setText(null);
        numberOfBorrowBookTextField.setText(null);
        numberOfLostTextField.setText(null);
        phoneNumberTextField.setText(null);
        resignDateTextField.setText(null);
        ConditionBox.setValue(null);

        // Set trạng thái bảng về noneMode
        setAddMode(false);
        setEditMode(false);
    }
    private void loadHistoryUser() {
        historyTableContainer.getChildren().clear();
        Map<String, Object> findCriteria2 = new HashMap<>();
        findCriteria2.put("member_ID", item.getPerson().getId());
        try {
            List<BookIssue> bookIssueList = BookIssueDAO.getInstance().searchByCriteria(findCriteria2);
            for (BookIssue bookIssue : bookIssueList) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AdminUserHistoryRow.fxml"));
                HBox row = loader.load();

                AdminUserHistoryRowController rowController = loader.getController();
                rowController.setBookItem(bookIssue);

                row.prefWidthProperty().bind(historyScrollContainer.widthProperty());
                historyTableContainer.getChildren().add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
