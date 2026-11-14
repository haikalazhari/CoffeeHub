package com.voidsyntax.coffeehub.service;

import com.voidsyntax.coffeehub.enums.Role;
import com.voidsyntax.coffeehub.model.Pelanggan;
import com.voidsyntax.coffeehub.model.User;
import com.voidsyntax.coffeehub.repository.UserDAO;

public class UserService {

    private final UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    // Registrasi akun pelanggan
    public boolean registrasi(String nama, String email,
                              String password, String noTelepon,
                              String alamat) {

        if (nama == null || nama.isBlank()
                || email == null || email.isBlank()
                || password == null || password.isBlank()) {
            return false;
        }

        if (password.length() < 8) {
            return false;
        }

        if (userDAO.cariBerdasarkanEmail(email) != null) {
            return false;
        }

        String passwordHash = PasswordUtil.hashPassword(password);

        Pelanggan pelanggan = new Pelanggan(
                0,
                nama,
                email,
                passwordHash,
                noTelepon,
                alamat
        );

        return userDAO.tambah(pelanggan, passwordHash);
    }

    // Login pengguna
    public User login(String email, String password) {

        if (email == null || email.isBlank()
                || password == null || password.isBlank()) {
            return null;
        }

        User user = userDAO.cariBerdasarkanEmail(email);

        if (user == null) {
            return null;
        }

        if (!PasswordUtil.verifyPassword(
                password, user.getPassword())) {
            return null;
        }

        return user;
    }

    // Memeriksa apakah pengguna merupakan Admin
    public boolean isAdmin(User user) {
        return user != null && user.getRole() == Role.ADMIN;
    }

    // Memeriksa apakah pengguna merupakan Pelanggan
    public boolean isPelanggan(User user) {
        return user != null && user.getRole() == Role.PELANGGAN;
    }
}
