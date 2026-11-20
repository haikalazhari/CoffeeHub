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
                || noTelepon == null || noTelepon.isBlank()
                || alamat == null || alamat.isBlank()
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

    // Reset / lupa password pengguna
    public boolean resetPassword(String email, String newPassword, String confirmPassword) {
        if (email == null || email.isBlank()
                || newPassword == null || newPassword.isBlank()
                || confirmPassword == null || confirmPassword.isBlank()) {
            return false;
        }

        if (!newPassword.equals(confirmPassword)) {
            return false;
        }

        if (newPassword.length() < 8) {
            return false;
        }

        if (userDAO.cariBerdasarkanEmail(email) == null) {
            return false;
        }

        String passwordHash = PasswordUtil.hashPassword(newPassword);
        return userDAO.perbaruiPassword(email, passwordHash);
    }

    // Memperbarui profil nama, email, dan no telepon pengguna
    public boolean perbaruiProfil(int id, String nama, String email, String noTelepon) {
        if (nama == null || nama.trim().isBlank()) {
            throw new IllegalArgumentException("Nama lengkap tidak boleh kosong.");
        }
        if (email == null || email.trim().isBlank()) {
            throw new IllegalArgumentException("Email tidak boleh kosong.");
        }
        if (noTelepon == null || noTelepon.trim().isBlank()) {
            throw new IllegalArgumentException("Nomor telepon tidak boleh kosong.");
        }
        if (!email.contains("@") || !email.contains(".")) {
            throw new IllegalArgumentException("Format email tidak valid.");
        }

        User existing = userDAO.cariBerdasarkanEmail(email.trim());
        if (existing != null && existing.getId() != id) {
            throw new IllegalArgumentException("Email '" + email.trim() + "' sudah digunakan oleh akun lain.");
        }

        return userDAO.perbaruiProfil(id, nama.trim(), email.trim(), noTelepon != null ? noTelepon.trim() : null);
    }

    public boolean perbaruiProfil(int id, String nama, String email) {
        return perbaruiProfil(id, nama, email, null);
    }

    // Mengubah password akun dengan validasi password lama
    public boolean ubahPassword(int id, String oldPassword, String newPassword, String confirmPassword) {
        if (oldPassword == null || oldPassword.isBlank()) {
            throw new IllegalArgumentException("Password saat ini harus diisi.");
        }
        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("Password baru harus diisi.");
        }
        if (newPassword.length() < 8) {
            throw new IllegalArgumentException("Password baru minimal 8 karakter.");
        }
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("Konfirmasi password baru tidak cocok.");
        }

        User user = userDAO.cariBerdasarkanId(id);
        if (user == null) {
            throw new IllegalArgumentException("Pengguna tidak ditemukan.");
        }

        if (!PasswordUtil.verifyPassword(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Password saat ini tidak sesuai.");
        }

        String passwordHash = PasswordUtil.hashPassword(newPassword);
        return userDAO.perbaruiPasswordById(id, passwordHash);
    }

    // Mengambil pengguna terbaru berdasarkan ID
    public User cariBerdasarkanId(int id) {
        return userDAO.cariBerdasarkanId(id);
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
