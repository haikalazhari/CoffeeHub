package com.voidsyntax.coffeehub.service;

import com.voidsyntax.coffeehub.enums.Role;
import com.voidsyntax.coffeehub.model.User;

public class SessionManager {

    private static User currentUser;

    // Menyimpan pengguna yang sedang login
    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    // Mengambil pengguna yang sedang login
    public static User getCurrentUser() {
        return currentUser;
    }

    // Memeriksa apakah pengguna sudah login
    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    // Memeriksa apakah pengguna adalah Admin
    public static boolean isAdmin() {
        return currentUser != null
                && currentUser.getRole() == Role.ADMIN;
    }

    // Memeriksa apakah pengguna adalah Pelanggan
    public static boolean isPelanggan() {
        return currentUser != null
                && currentUser.getRole() == Role.PELANGGAN;
    }

    // Mengakhiri sesi pengguna
    public static void logout() {
        currentUser = null;
    }
}
