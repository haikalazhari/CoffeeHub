package com.voidsyntax.coffeehub.controller;

import com.voidsyntax.coffeehub.enums.Role;
import com.voidsyntax.coffeehub.model.User;
import com.voidsyntax.coffeehub.service.SessionManager;
import com.voidsyntax.coffeehub.service.UserService;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    private final UserService userService = new UserService();

    // Menangani proses login
    @FXML
    private void handleLogin(ActionEvent event) {
    
        String email = emailField.getText().trim();
        String password = passwordField.getText();
    
        if (email.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Email dan password wajib diisi.");
            return;
        }
    
        User user = userService.login(email, password);
    
        if (user == null) {
            messageLabel.setText("Email atau password salah.");
            return;
        }
    
        // Menyimpan sesi pengguna
        SessionManager.setCurrentUser(user);
    
        // Mengarahkan pengguna berdasarkan role
        if (user.getRole() == Role.ADMIN) {
        
            SceneNavigator.pindahHalaman(
                   event,
                   "/fxml/admin-dashboard.fxml"
        );
        
        } else if (user.getRole() == Role.PELANGGAN) {
            SceneNavigator.pindahHalaman(event, "/fxml/customer-dashboard.fxml");
        }
    }


    // Menangani tombol registrasi
    @FXML
    private void handleRegister(javafx.event.ActionEvent event) {
        SceneNavigator.pindahHalaman(event, "/fxml/register.fxml");
    }
}
