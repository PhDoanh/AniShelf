package com.library.anishelf.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class CustomerAlterApp {

    private static final String ALTER_FXML_VIEW = "/view/Alert-view.fxml";

    private static final FXMLLoader alterLoader;
    private static final AnchorPane alterPaneContainer;
    protected static AlertAppController alertAppController;
    private static Scene sceneAlter;
    static {
        //load alter
        alterLoader = new FXMLLoader(CustomerAlterApp.class.getResource(ALTER_FXML_VIEW));
        try {
            alterPaneContainer = alterLoader.load();
            alertAppController = alterLoader.getController();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        sceneAlter = new Scene(alterPaneContainer);
    }

    /**
     * hàm dùng để mở ra cửa sổ thông báo. (Alter đã customer)
     * @param message là đoạn văn bản chính thông báo đến người dùng
     * @return true nếu người dùng xác nhận, false nếu người dùng hủy
     */
    public static boolean showAlterApp(String message) {
        boolean userConfirmed = false;

        try {

            alertAppController.setAppAlertMessage(message); // Thiết lập thông điệp
            alertAppController.resetAlert(); // Đặt lại trạng thái trước khi hiển thị

            // Tạo Stage mới cho cảnh báo
            Stage alertStage = new Stage();
            alertStage.setResizable(false);
            alertStage.setTitle("Xác nhận");
            alertStage.setScene(sceneAlter);

            // Hiển thị Alert và chờ người dùng phản hồi
            alertStage.initModality(Modality.APPLICATION_MODAL);
            alertStage.showAndWait();

            // Lấy kết quả từ controller sau khi Alert đóng
            userConfirmed = alertAppController.isUserAlertConfirmed();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return userConfirmed;
    }

    public static void showAlertMessage(String message) {
        try {

            alertAppController.setAppAlertMessage(message); // Thiết lập thông điệp
            alertAppController.resetAlert(); // Đặt lại trạng thái trước khi hiển thị
            alertAppController.setNotificationAppMode();
            // Tạo Stage mới cho cảnh báo
            Stage alertStage = new Stage();
            alertStage.setResizable(false);
            alertStage.setTitle("Thông báo");
            alertStage.setScene(sceneAlter);

            // Hiển thị Alert và chờ người dùng phản hồi
            alertStage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
