package com.voidsyntax.coffeehub.controller;

import com.voidsyntax.coffeehub.service.UserService;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController {

    @FXML
    private TextField namaField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField noTeleponField;

    @FXML
    private TextField alamatField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label messageLabel;

    private final UserService userService = new UserService();

    // Menangani proses registrasi
    @FXML
    private void handleRegister() {

        String nama = namaField.getText().trim();
        String email = emailField.getText().trim();
        String noTelepon = noTeleponField.getText().trim();
        String alamat = alamatField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (nama.isEmpty() || email.isEmpty()
                || password.isEmpty() || confirmPassword.isEmpty()) {
            tampilkanPesan("Field wajib harus diisi.", false);
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            tampilkanPesan("Format email tidak valid.", false);
            return;
        }

        if (!password.equals(confirmPassword)) {
            tampilkanPesan("Konfirmasi password tidak cocok.", false);
            return;
        }

        if (password.length() < 8) {
            tampilkanPesan("Password minimal 8 karakter.", false);
            return;
        }

        boolean berhasil = userService.registrasi(
                nama,
                email,
                password,
                noTelepon,
                alamat
        );

        if (berhasil) {
            tampilkanPesan(
                    "Registrasi berhasil! Silakan login.",
                    true
            );

            bersihkanForm();

        } else {
            tampilkanPesan(
                    "Registrasi gagal. Email mungkin sudah digunakan.",
                    false
            );
        }
    }

    // Menampilkan pesan
    private void tampilkanPesan(String pesan, boolean berhasil) {
        messageLabel.setText(pesan);

        if (berhasil) {
            messageLabel.setStyle("-fx-text-fill: #347A46;");
        } else {
            messageLabel.setStyle("-fx-text-fill: #B33A3A;");
        }
    }

    // Membersihkan formulir
    private void bersihkanForm() {
        namaField.clear();
        emailField.clear();
        noTeleponField.clear();
        alamatField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
    }

    // Kembali ke halaman login
    @FXML
    private void handleLogin(javafx.event.ActionEvent event) {
        SceneNavigator.pindahHalaman(event, "/fxml/login.fxml");
    }
}
