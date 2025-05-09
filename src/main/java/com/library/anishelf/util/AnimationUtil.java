package com.library.anishelf.util;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * The type Animation util.
 */
public class AnimationUtil {
    private static AnimationUtil instance;
    private double previousScrollPosition = 0;

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static AnimationUtil getInstance() {
        if (instance == null) {
            instance = new AnimationUtil();
        }
        return instance;
    }

    /**
     * Hiển thị hoặc ẩn thanh điều hướng dựa trên hướng cuộn
     *
     * @param navigationBar         Thanh điều hướng cần ẩn/hiện
     * @param currentScrollPosition Vị trí cuộn hiện tại
     */
    public void toggleNavigationBarVisibility(HBox navigationBar, double currentScrollPosition) {
        // Xác định hướng cuộn
        boolean scrollingDown = currentScrollPosition > previousScrollPosition;
        previousScrollPosition = currentScrollPosition;

        // Nếu cuộn xuống, ẩn thanh điều hướng, ngược lại hiện lên
        double targetTranslateY = scrollingDown ? -navigationBar.getHeight() : 0;

        // Tạo animation cho việc ẩn/hiện
        TranslateTransition transition = new TranslateTransition(Duration.millis(250), navigationBar);
        transition.setToY(targetTranslateY);
        transition.play();
    }

    /**
     * Hiển thị thanh điều hướng (khi ở đầu trang hoặc muốn hiện lại thanh)
     *
     * @param navigationBar Thanh điều hướng cần hiển thị
     */
    public void showNavigationBar(Node navigationBar) {
        TranslateTransition transition = new TranslateTransition(Duration.millis(250), navigationBar);
        transition.setToY(0);
        transition.play();
    }

    /**
     * Ẩn thanh điều hướng
     *
     * @param navigationBar Thanh điều hướng cần ẩn
     */
    public void hideNavigationBar(HBox navigationBar) {
        TranslateTransition transition = new TranslateTransition(Duration.millis(250), navigationBar);
        transition.setToY(-navigationBar.getHeight());
        transition.play();
    }
}
