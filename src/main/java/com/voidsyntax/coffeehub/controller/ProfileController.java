package com.voidsyntax.coffeehub.controller;

import com.voidsyntax.coffeehub.enums.Role;
import com.voidsyntax.coffeehub.model.User;
import com.voidsyntax.coffeehub.service.SessionManager;
import com.voidsyntax.coffeehub.service.UserService;
import com.voidsyntax.coffeehub.util.AlertUtil;
import com.voidsyntax.coffeehub.util.ToastUtil;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class ProfileController {

    @FXML
    private VBox profileContainer;

    @FXML
    private StackPane avatarCircle;

    @FXML
    private Label avatarInitialLabel;

    @FXML
    private Label displayNameLabel;

    @FXML
    private Label displayEmailLabel;

    @FXML
    private Label roleBadge;

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    private PasswordField oldPasswordField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Button saveButton;

    private final UserService userService = new UserService();

    @FXML
    private void initialize() {
        muatDataPengguna();
    }

    private void muatDataPengguna() {
        User user = SessionManager.getCurrentUser();
        if (user == null) {
            return;
        }

        // Ambil data terbaru dari database untuk memastikan akurasi
        User freshUser = userService.cariBerdasarkanId(user.getId());
        if (freshUser != null) {
            user = freshUser;
            SessionManager.setCurrentUser(freshUser);
        }

        String nama = user.getNama();
        String email = user.getEmail();
        Role role = user.getRole();

        nameField.setText(nama);
        emailField.setText(email);
        phoneField.setText(user.getNoTelepon() != null ? user.getNoTelepon() : "");

        displayNameLabel.setText(nama != null ? nama : "Pengguna");
        displayEmailLabel.setText(email != null ? email : "-");

        // Set inisial avatar
        String inisial = "U";
        if (nama != null && !nama.trim().isEmpty()) {
            inisial = nama.trim().substring(0, 1).toUpperCase();
        }
        avatarInitialLabel.setText(inisial);

        // Styling role badge
        if (role == Role.ADMIN) {
            roleBadge.setText("ADMINISTRATOR");
            roleBadge.setStyle(
                    "-fx-background-color: #FEE2E2;" +
                    "-fx-text-fill: #991B1B;" +
                    "-fx-font-weight: bold;" +
                    "-fx-font-size: 11px;" +
                    "-fx-padding: 4 10 4 10;" +
                    "-fx-background-radius: 12;" +
                    "-fx-border-color: #FCA5A5;" +
                    "-fx-border-radius: 12;"
            );
        } else {
            roleBadge.setText("PELANGGAN");
            roleBadge.setStyle(
                    "-fx-background-color: #EFE2D3;" +
                    "-fx-text-fill: #56301F;" +
                    "-fx-font-weight: bold;" +
                    "-fx-font-size: 11px;" +
                    "-fx-padding: 4 10 4 10;" +
                    "-fx-background-radius: 12;" +
                    "-fx-border-color: #D8C2AD;" +
                    "-fx-border-radius: 12;"
            );
        }

        oldPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();
    }

    @FXML
    private void handleReset() {
        muatDataPengguna();
        ToastUtil.showInfo(profileContainer, "Form profil telah direset ke data tersimpan.");
    }

    @FXML
    private void handleSaveProfile() {
        User user = SessionManager.getCurrentUser();
        if (user == null) {
            AlertUtil.error(
                    profileContainer.getScene().getWindow(),
                    "Sesi Berakhir",
                    "Akses Tidak Valid",
                    "Sesi Anda telah berakhir. Silakan login kembali."
            );
            return;
        }

        String inputNama = nameField.getText();
        String inputEmail = emailField.getText();
        String inputPhone = phoneField.getText();

        String oldPass = oldPasswordField.getText();
        String newPass = newPasswordField.getText();
        String confirmPass = confirmPasswordField.getText();

        boolean gantiPassword = (oldPass != null && !oldPass.isBlank())
                || (newPass != null && !newPass.isBlank())
                || (confirmPass != null && !confirmPass.isBlank());

        try {
            // 1. Simpan perubahan profil dasar (nama, email, no telepon) ke database
            boolean profilUpdated = userService.perbaruiProfil(user.getId(), inputNama, inputEmail, inputPhone);
            if (!profilUpdated) {
                AlertUtil.error(
                        profileContainer.getScene().getWindow(),
                        "Gagal Menyimpan",
                        "Perubahan Gagal",
                        "Gagal memperbarui data profil ke database."
                );
                return;
            }

            // 2. Simpan password baru jika pengguna mengisinya
            if (gantiPassword) {
                boolean passwordUpdated = userService.ubahPassword(user.getId(), oldPass, newPass, confirmPass);
                if (!passwordUpdated) {
                    AlertUtil.error(
                            profileContainer.getScene().getWindow(),
                            "Gagal Mengubah Password",
                            "Password Tidak Berubah",
                            "Gagal memperbarui password ke database."
                    );
                    return;
                }
            }

            // 3. Perbarui session pengguna
            User userTerbaru = userService.cariBerdasarkanId(user.getId());
            if (userTerbaru != null) {
                SessionManager.setCurrentUser(userTerbaru);
            }

            // 4. Perbarui header dashboard secara dinamis
            if (CustomerController.getInstance() != null) {
                CustomerController.getInstance().perbaruiNamaPelanggan();
            }
            if (AdminController.getInstance() != null) {
                AdminController.getInstance().perbaruiNamaAdmin();
            }

            // 5. Muat ulang data form & tampilan
            muatDataPengguna();

            // 6. Notifikasi sukses
            String pesanSukses = gantiPassword
                    ? "Profil dan password Anda berhasil diperbarui di database!"
                    : "Data profil Anda berhasil diperbarui di database!";

            AlertUtil.sukses(
                    profileContainer.getScene().getWindow(),
                    "Profil Tersimpan",
                    "Perubahan Berhasil Disimpan",
                    pesanSukses
            );

        } catch (IllegalArgumentException ex) {
            AlertUtil.peringatan(
                    profileContainer.getScene().getWindow(),
                    "Validasi Profil",
                    "Periksa Data Masukan",
                    ex.getMessage()
            );
        } catch (Exception ex) {
            ex.printStackTrace();
            AlertUtil.error(
                    profileContainer.getScene().getWindow(),
                    "Kesalahan Sistem",
                    "Gagal Memperbarui Profil",
                    "Terjadi kesalahan: " + ex.getMessage()
            );
        }
    }
}
