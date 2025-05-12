package com.library.anishelf.util;

import atlantafx.base.controls.Notification;
import atlantafx.base.theme.Styles;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * Manages popup notifications using AtlantaFX's Notification component
 */
public class NotificationManagerUtil {

    // Constants for notification positioning
    private static final int NOTIFICATION_SPACING = 10;
    private static final int NOTIFICATION_OFFSET_X = 15;
    private static final int NOTIFICATION_OFFSET_Y = 40;
    private static final Duration DEFAULT_AUTO_HIDE_DURATION = Duration.seconds(5);

    // Store active notifications so we can position them properly
    private static final ConcurrentLinkedQueue<Popup> activeNotifications = new ConcurrentLinkedQueue<>();
    private static final Map<Popup, PauseTransition> autoHideTimers = new HashMap<>();
    private static Window ownerWindow;
    private static AtomicInteger currentNotificationHeight = new AtomicInteger(NOTIFICATION_OFFSET_Y);

    // The current interactive notification that needs user attention
    private static Popup currentInteractiveNotification = null;

    // Change listeners to handle window position and size changes
    private static ChangeListener<Number> xChangeListener;
    private static ChangeListener<Number> yChangeListener;
    private static ChangeListener<Number> widthChangeListener;
    private static ChangeListener<Number> heightChangeListener;

    /**
     * Set the owner window for notifications
     *
     * @param window The window that will own the notifications
     */
    public static void setOwnerWindow(Window window) {
        // Remove old listeners if they exist
        removeWindowListeners();

        ownerWindow = window;

        if (window instanceof Stage) {
            Stage stage = (Stage) window;

            // Add listeners to update notification positions when window changes
            xChangeListener = (obs, oldVal, newVal) -> repositionAllNotifications();
            yChangeListener = (obs, oldVal, newVal) -> repositionAllNotifications();
            widthChangeListener = (obs, oldVal, newVal) -> repositionAllNotifications();
            heightChangeListener = (obs, oldVal, newVal) -> repositionAllNotifications();

            stage.xProperty().addListener(xChangeListener);
            stage.yProperty().addListener(yChangeListener);
            stage.widthProperty().addListener(widthChangeListener);
            stage.heightProperty().addListener(heightChangeListener);

            // Also listen for window showing events to ensure notifications are positioned correctly
            stage.addEventHandler(WindowEvent.WINDOW_SHOWN, e -> Platform.runLater(NotificationManagerUtil::repositionAllNotifications));

            // Listen for window maximize/restore events
            stage.maximizedProperty().addListener((obs, oldVal, newVal) ->
                    Platform.runLater(NotificationManagerUtil::repositionAllNotifications));
        }
    }

    /**
     * Remove window listeners when they're no longer needed
     */
    private static void removeWindowListeners() {
        if (ownerWindow instanceof Stage) {
            Stage stage = (Stage) ownerWindow;

            if (xChangeListener != null) {
                stage.xProperty().removeListener(xChangeListener);
            }
            if (yChangeListener != null) {
                stage.yProperty().removeListener(yChangeListener);
            }
            if (widthChangeListener != null) {
                stage.widthProperty().removeListener(widthChangeListener);
            }
            if (heightChangeListener != null) {
                stage.heightProperty().removeListener(heightChangeListener);
            }
        }
    }

    /**
     * Repositions all active notifications based on current window position and size
     */
    private static void repositionAllNotifications() {
        if (ownerWindow == null || activeNotifications.isEmpty()) {
            return;
        }

        double yPosition = ownerWindow.getY() + NOTIFICATION_OFFSET_Y;

        for (Popup popup : activeNotifications) {
            if (popup.getContent().isEmpty()) {
                continue;
            }

            Node content = popup.getContent().get(0);
            if (!(content instanceof Notification)) {
                continue;
            }

            Notification notification = (Notification) content;

            // Ensure the notification's layout is computed
            notification.applyCss();
            notification.layout();

            // Get actual notification dimensions
            double notificationWidth = notification.getWidth();
            if (notificationWidth <= 0) {
                notificationWidth = notification.prefWidth(-1);
            }

            double notificationHeight = notification.getHeight();
            if (notificationHeight <= 0) {
                notificationHeight = notification.prefHeight(-1);
            }

            // Calculate x position - ensure the notification fits completely within the window
            // Position from right edge of window, keeping the notification fully visible
            double x = ownerWindow.getX() + ownerWindow.getWidth() - notificationWidth - NOTIFICATION_OFFSET_X;

            // Set new position
            popup.setX(x);
            popup.setY(yPosition);

            // Update y position for next notification, ensuring proper stacking
            yPosition += notificationHeight + NOTIFICATION_SPACING;
        }

        // Update the current height for new notifications
        if (!activeNotifications.isEmpty()) {
            currentNotificationHeight.set((int) (yPosition - ownerWindow.getY()));
        } else {
            currentNotificationHeight.set(NOTIFICATION_OFFSET_Y);
        }
    }

    /**
     * Shows a notification with success style
     *
     * @param message The message to display
     */
    public static void showSuccess(String message) {
        showNotification(message, Styles.SUCCESS, Feather.CHECK_CIRCLE, null, true);
    }

    /**
     * Shows a notification with warning style
     *
     * @param message The message to display
     */
    public static void showWarning(String message) {
        showNotification(message, Styles.WARNING, Feather.ALERT_TRIANGLE, null, true);
    }

    /**
     * Shows a notification with danger style
     *
     * @param message The message to display
     */
    public static void showError(String message) {
        showNotification(message, Styles.DANGER, Feather.ALERT_OCTAGON, null, true);
    }

    /**
     * Shows a notification with info/accent style
     *
     * @param message The message to display
     */
    public static void showInfo(String message) {
        showNotification(message, Styles.ACCENT, Feather.INFO, null, true);
    }

    /**
     * Shows a confirmation notification with buttons
     *
     * @param message   The message to display
     * @param onConfirm Action to run when user confirms
     * @return the popup containing the notification
     */
    public static Popup showConfirmation(String message, Consumer<Boolean> onConfirm) {
        Popup popup = showNotification(message, Styles.ACCENT, Feather.HELP_CIRCLE, onConfirm, false);
        currentInteractiveNotification = popup;
        return popup;
    }

    /**
     * Shows a notification with custom style
     *
     * @param message   The message to display
     * @param style     The style class to apply
     * @param icon      The icon to display
     * @param onConfirm Optional action to run when user confirms (if null, no buttons will be shown)
     * @param autoHide  Whether to auto hide the notification
     * @return the popup containing the notification
     */
    public static Popup showNotification(String message, String style, Feather icon, Consumer<Boolean> onConfirm, boolean autoHide) {
        if (ownerWindow == null) {
            // Try to find a window
            List<Window> windows = Stage.getWindows().filtered(Window::isShowing);
            if (!windows.isEmpty()) {
                ownerWindow = windows.get(0);
                // Set up listeners for the window
                setOwnerWindow(ownerWindow);
            } else {
                throw new IllegalStateException("No window found to show notification");
            }
        }

        // Create notification
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.getStyleClass().add(style);
        notification.setGraphic(new FontIcon(icon));

        // Add confirm/cancel buttons if needed
        if (onConfirm != null) {
            Button confirmButton = new Button("Xác nhận");
            confirmButton.getStyleClass().add(Styles.BUTTON_OUTLINED);
            confirmButton.getStyleClass().add(Styles.SUCCESS);

            Button cancelButton = new Button("Hủy");
            cancelButton.getStyleClass().add(Styles.BUTTON_OUTLINED);
            cancelButton.getStyleClass().add(Styles.DANGER);

            HBox buttonBar = new HBox(10, confirmButton, cancelButton);
            buttonBar.setAlignment(Pos.CENTER_RIGHT);
            notification.setPrimaryActions(confirmButton, cancelButton);

            // Setup button actions
            confirmButton.setOnAction(e -> {
                onConfirm.accept(true);
                currentInteractiveNotification = null;
                closeNotification(notification);
            });

            cancelButton.setOnAction(e -> {
                onConfirm.accept(false);
                currentInteractiveNotification = null;
                closeNotification(notification);
            });
        }

        // Setup popup
        Popup popup = new Popup();
        popup.getContent().add(notification);
        popup.setAutoHide(false);
        popup.setHideOnEscape(true);

        // Force layout computation to get accurate dimensions
        notification.applyCss();
        notification.layout();

        // Get notification width (use preferred width if actual width is not yet computed)
        double notificationWidth = notification.getWidth();
        if (notificationWidth <= 0) {
            notificationWidth = notification.prefWidth(-1);
        }

        // Calculate initial position - ensure notification is fully visible
        double x = ownerWindow.getX() + ownerWindow.getWidth() - notificationWidth - NOTIFICATION_OFFSET_X;
        double y = ownerWindow.getY() + currentNotificationHeight.get();

        popup.setX(x);
        popup.setY(y);

        // Add close button action
        notification.setOnClose(e -> {
            if (currentInteractiveNotification == popup) {
                currentInteractiveNotification = null;

                // If this is an interactive notification being closed without user response,
                // we need to call the callback with a default value (false)
                if (onConfirm != null) {
                    onConfirm.accept(false);
                }
            }
            closeNotification(notification);
        });

        // Show the popup
        Platform.runLater(() -> {
            popup.show(ownerWindow);
            activeNotifications.add(popup);

            // Set auto-hide timer if needed
            if (autoHide) {
                PauseTransition hideTimer = new PauseTransition(DEFAULT_AUTO_HIDE_DURATION);
                hideTimer.setOnFinished(e -> closeNotification(notification));
                hideTimer.play();
                autoHideTimers.put(popup, hideTimer);
            }

            // Ensure notification has the correct position after it's been added
            Platform.runLater(() -> {
                // Get actual height now that the notification is visible
                double notificationHeight = notification.getHeight();

                // Update the height tracker for the next notification
                currentNotificationHeight.addAndGet((int) notificationHeight + NOTIFICATION_SPACING);

                // Make sure all notifications are properly positioned
                repositionAllNotifications();
            });
        });

        return popup;
    }

    /**
     * Closes a notification and reflows other notifications
     *
     * @param notification The notification to close
     */
    private static void closeNotification(Notification notification) {
        Popup popupToRemove = null;
        double removedHeight = 0;

        // Find the popup that contains this notification
        for (Popup popup : activeNotifications) {
            ObservableList<Node> content = popup.getContent();
            if (!content.isEmpty() && content.get(0) == notification) {
                popupToRemove = popup;
                removedHeight = notification.getHeight() + NOTIFICATION_SPACING;
                break;
            }
        }

        if (popupToRemove != null) {
            final Popup popup = popupToRemove;
            final double height = removedHeight;

            // Cancel auto-hide timer if it exists
            PauseTransition timer = autoHideTimers.remove(popup);
            if (timer != null) {
                timer.stop();
            }

            // Remove from active notifications and hide
            activeNotifications.remove(popup);
            popup.hide();

            // Reposition remaining notifications
            Platform.runLater(() -> reflowNotifications(height));
        }
    }

    /**
     * Repositions all active notifications after one is closed
     *
     * @param heightDifference The height of the removed notification
     */
    private static void reflowNotifications(double heightDifference) {
        // Reset the current height
        currentNotificationHeight.addAndGet(-(int) heightDifference);

        // No need to reposition if there are no active notifications
        if (activeNotifications.isEmpty()) {
            currentNotificationHeight.set(NOTIFICATION_OFFSET_Y);
            return;
        }

        repositionAllNotifications();
    }
}