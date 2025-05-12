package com.library.anishelf.controller;

import com.library.anishelf.util.NotificationManagerUtil;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

/**
 * The type Custom notification popup.
 */
public class CustomNotificationPopup {

    // Original dialog-based notification for synchronous confirmations
    private static final String ALTER_FXML = "/view/NotificationPopup.fxml";
    /**
     * The constant notificationPopupController.
     */
    protected static NotificationPopupController notificationPopupController;
    private static AnchorPane alterPane;
    private static FXMLLoader alterLoader;
    private static Scene scene;

    static {
        try {
            // Load the controller so we can initialize it
            alterLoader = new FXMLLoader(CustomNotificationPopup.class.getResource(ALTER_FXML));
            alterPane = alterLoader.load();
            notificationPopupController = alterLoader.getController();
            scene = new Scene(alterPane);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Shows a confirmation notification with a modal dialog.
     * This method is synchronous and will block until the user responds.
     *
     * @param message The message to display
     * @return true if user confirmed, false if user cancelled
     */
    public static boolean showAlter(String message) {
        // Reset controller state
        notificationPopupController.reset();
        notificationPopupController.setAlertMessage(message);

        // Create and configure a new dialog stage
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL); // Block until closed
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setResizable(false);
        stage.setScene(scene);

        // Show the dialog and wait for it to close
        stage.showAndWait();

        // Return the user's response
        return notificationPopupController.isUserConfirmed();
    }

    /**
     * Shows an informational notification that automatically disappears.
     * This method is non-blocking and returns immediately.
     *
     * @param message The message to display
     */
    public static void showMessage(String message) {
        NotificationManagerUtil.showInfo(message);
    }

    /**
     * Shows a success notification that automatically disappears.
     * This method is non-blocking and returns immediately.
     *
     * @param message The message to display
     */
    public static void showSuccess(String message) {
        NotificationManagerUtil.showSuccess(message);
    }

    /**
     * Shows an error notification that automatically disappears.
     * This method is non-blocking and returns immediately.
     *
     * @param message The message to display
     */
    public static void showError(String message) {
        NotificationManagerUtil.showError(message);
    }

    /**
     * Shows a warning notification that automatically disappears.
     * This method is non-blocking and returns immediately.
     *
     * @param message The message to display
     */
    public static void showWarning(String message) {
        NotificationManagerUtil.showWarning(message);
    }

    /**
     * Sets the owner window for notifications to position them correctly
     *
     * @param stage The main application window
     */
    public static void setOwnerWindow(Stage stage) {
        NotificationManagerUtil.setOwnerWindow(stage);
    }
}
