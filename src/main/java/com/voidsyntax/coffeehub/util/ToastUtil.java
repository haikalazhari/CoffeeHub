package com.voidsyntax.coffeehub.util;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.util.Duration;

/**
 * Utility untuk menampilkan notifikasi Toast yang elegan, minimalis,
 * dan selaras dengan tema warna CoffeeHub.
 */
public class ToastUtil {

    public enum Type {
        SUCCESS,
        INFO,
        WARNING
    }

    private static Popup activePopup;

    public static void showSuccess(Node ownerNode, String message) {
        show(ownerNode, message, Type.SUCCESS);
    }

    public static void showInfo(Node ownerNode, String message) {
        show(ownerNode, message, Type.INFO);
    }

    public static void showWarning(Node ownerNode, String message) {
        show(ownerNode, message, Type.WARNING);
    }

    public static void showSuccess(Window window, String message) {
        show(window, message, Type.SUCCESS);
    }

    public static void showInfo(Window window, String message) {
        show(window, message, Type.INFO);
    }

    public static void showWarning(Window window, String message) {
        show(window, message, Type.WARNING);
    }

    public static void show(Node ownerNode, String message, Type type) {
        if (ownerNode == null) {
            return;
        }

        if (ownerNode.getScene() == null || ownerNode.getScene().getWindow() == null) {
            javafx.application.Platform.runLater(() -> {
                if (ownerNode.getScene() != null && ownerNode.getScene().getWindow() != null) {
                    show(ownerNode.getScene().getWindow(), message, type);
                }
            });
            return;
        }

        show(ownerNode.getScene().getWindow(), message, type);
    }

    public static void show(Window window, String message, Type type) {
        if (window == null) {
            return;
        }

        if (activePopup != null && activePopup.isShowing()) {
            activePopup.hide();
        }

        Popup popup = new Popup();
        activePopup = popup;
        popup.setAutoFix(true);
        popup.setAutoHide(true);

        HBox toastBox = new HBox(12);
        toastBox.setAlignment(Pos.CENTER_LEFT);

        String iconChar = "☕";
        String accentColor = "#C58B55";
        String iconBg = "#56301F";

        if (type == Type.WARNING) {
            iconChar = "⚠";
            accentColor = "#E06D53";
            iconBg = "#7A2E20";
        } else if (type == Type.INFO) {
            iconChar = "ℹ";
            accentColor = "#A48B78";
            iconBg = "#4A3528";
        }

        StackPane iconCircle = new StackPane();
        iconCircle.setPrefSize(28, 28);
        iconCircle.setMaxSize(28, 28);
        iconCircle.setMinSize(28, 28);
        iconCircle.setStyle(
                "-fx-background-color: " + iconBg + ";"
                + "-fx-background-radius: 14;"
                + "-fx-border-color: " + accentColor + ";"
                + "-fx-border-radius: 14;"
                + "-fx-border-width: 1.5;"
        );

        Label iconLabel = new Label(iconChar);
        iconLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold;");
        iconCircle.getChildren().add(iconLabel);

        Label messageLabel = new Label(message);
        messageLabel.setStyle(
                "-fx-text-fill: #FFF8ED;"
                + "-fx-font-size: 13px;"
                + "-fx-font-weight: bold;"
        );
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(380);

        toastBox.getChildren().addAll(iconCircle, messageLabel);
        toastBox.setStyle(
                "-fx-background-color: #361E14;"
                + "-fx-background-radius: 12;"
                + "-fx-border-color: " + accentColor + ";"
                + "-fx-border-width: 0 0 0 4;"
                + "-fx-border-radius: 12;"
                + "-fx-padding: 12 20 12 16;"
                + "-fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.4), 16, 0, 0, 6);"
                + "-fx-cursor: hand;"
        );

        toastBox.setOnMouseClicked(e -> popup.hide());

        popup.getContent().add(toastBox);
        toastBox.setOpacity(0.0);

        popup.show(window);

        // Posisikan di pojok kanan atas jendela aplikasi
        double targetX = window.getX() + window.getWidth() - toastBox.getWidth() - 35;
        double targetY = window.getY() + 50;
        popup.setX(targetX);
        popup.setY(targetY);

        // Animasi muncul (Fade in + slide down)
        FadeTransition fadeIn = new FadeTransition(Duration.millis(250), toastBox);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        TranslateTransition slideDown = new TranslateTransition(Duration.millis(250), toastBox);
        slideDown.setFromY(-15);
        slideDown.setToY(0);

        ParallelTransition showAnim = new ParallelTransition(fadeIn, slideDown);

        // Tahan selama 2.5 detik
        PauseTransition pause = new PauseTransition(Duration.millis(2500));

        // Animasi menghilang (Fade out + slide up)
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), toastBox);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        TranslateTransition slideUp = new TranslateTransition(Duration.millis(300), toastBox);
        slideUp.setFromY(0);
        slideUp.setToY(-10);

        ParallelTransition hideAnim = new ParallelTransition(fadeOut, slideUp);

        SequentialTransition sequence = new SequentialTransition(showAnim, pause, hideAnim);
        sequence.setOnFinished(e -> popup.hide());
        sequence.play();
    }
}
