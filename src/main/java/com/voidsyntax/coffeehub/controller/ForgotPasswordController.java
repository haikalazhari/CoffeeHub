package com.voidsyntax.coffeehub.controller;

import com.voidsyntax.coffeehub.service.UserService;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class ForgotPasswordController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private TextField newPasswordTextField;

    @FXML
    private Button toggleNewPasswordButton;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private TextField confirmPasswordTextField;

    @FXML
    private Button toggleConfirmPasswordButton;

    @FXML
    private Label messageLabel;

    private boolean isNewPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    private final UserService userService = new UserService();

    @FXML
    private void initialize() {
        // Sinkronisasi teks dua arah untuk password baru dan konfirmasi password
        newPasswordTextField.textProperty().bindBidirectional(newPasswordField.textProperty());
        confirmPasswordTextField.textProperty().bindBidirectional(confirmPasswordField.textProperty());
    }

    // Toggle mata untuk Password Baru
    @FXML
    private void handleToggleNewPassword() {
        isNewPasswordVisible = !isNewPasswordVisible;

        if (isNewPasswordVisible) {
            newPasswordField.setVisible(false);
            newPasswordField.setManaged(false);
            newPasswordTextField.setVisible(true);
            newPasswordTextField.setManaged(true);
            newPasswordTextField.requestFocus();
            String text = newPasswordTextField.getText();
            if (text != null) {
                newPasswordTextField.positionCaret(text.length());
            }
            toggleNewPasswordButton.setText("🙈");
        } else {
            newPasswordTextField.setVisible(false);
            newPasswordTextField.setManaged(false);
            newPasswordField.setVisible(true);
            newPasswordField.setManaged(true);
            newPasswordField.requestFocus();
            String text = newPasswordField.getText();
            if (text != null) {
                newPasswordField.positionCaret(text.length());
            }
            toggleNewPasswordButton.setText("👁");
        }
    }

    // Toggle mata untuk Konfirmasi Password
    @FXML
    private void handleToggleConfirmPassword() {
        isConfirmPasswordVisible = !isConfirmPasswordVisible;

        if (isConfirmPasswordVisible) {
            confirmPasswordField.setVisible(false);
            confirmPasswordField.setManaged(false);
            confirmPasswordTextField.setVisible(true);
            confirmPasswordTextField.setManaged(true);
            confirmPasswordTextField.requestFocus();
            String text = confirmPasswordTextField.getText();
            if (text != null) {
                confirmPasswordTextField.positionCaret(text.length());
            }
            toggleConfirmPasswordButton.setText("🙈");
        } else {
            confirmPasswordTextField.setVisible(false);
            confirmPasswordTextField.setManaged(false);
            confirmPasswordField.setVisible(true);
            confirmPasswordField.setManaged(true);
            confirmPasswordField.requestFocus();
            String text = confirmPasswordField.getText();
            if (text != null) {
                confirmPasswordField.positionCaret(text.length());
            }
            toggleConfirmPasswordButton.setText("👁");
        }
    }

    // Menangani aksi reset password
    @FXML
    private void handleResetPassword() {
        String email = emailField.getText().trim();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (email.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            tampilkanPesan("Semua field wajib diisi.", false);
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            tampilkanPesan("Format email tidak valid.", false);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            tampilkanPesan("Konfirmasi password baru tidak cocok.", false);
            return;
        }

        if (newPassword.length() < 8) {
            tampilkanPesan("Password minimal 8 karakter.", false);
            return;
        }

        boolean berhasil = userService.resetPassword(email, newPassword, confirmPassword);

        if (berhasil) {
            tampilkanPesan("Password berhasil diubah! Silakan kembali ke login.", true);
            bersihkanForm();
        } else {
            tampilkanPesan("Reset password gagal. Email tidak terdaftar.", false);
        }
    }

    private void bersihkanForm() {
        emailField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();
    }

    private void tampilkanPesan(String pesan, boolean berhasil) {
        messageLabel.setText(pesan);
        if (berhasil) {
            messageLabel.setStyle("-fx-text-fill: #347A46; -fx-font-weight: bold;");
        } else {
            messageLabel.setStyle("-fx-text-fill: #B33A3A; -fx-font-weight: bold;");
        }
    }

    // Kembali ke halaman Login
    @FXML
    private void handleBackToLogin(ActionEvent event) {
        SceneNavigator.pindahHalaman(event, "/fxml/login.fxml");
    }
}
