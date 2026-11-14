package com.voidsyntax.coffeehub.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.voidsyntax.coffeehub.config.DatabaseConnection;
import com.voidsyntax.coffeehub.enums.Role;
import com.voidsyntax.coffeehub.model.Admin;
import com.voidsyntax.coffeehub.model.Pelanggan;
import com.voidsyntax.coffeehub.model.User;

public class UserDAO {

    // Menambahkan pengguna baru
    public boolean tambah(User user, String passwordHash) {
        String sql = """
                INSERT INTO users (name, email, password_hash, role)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getNama());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, passwordHash);
            stmt.setString(4, user.getRole().name());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Gagal menambahkan pengguna: " + e.getMessage());
            return false;
        }
    }

    // Mencari pengguna berdasarkan email
    public User cariBerdasarkanEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return buatUserDariResultSet(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Gagal mencari pengguna: " + e.getMessage());
        }

        return null;
    }

    // Mengambil seluruh data pengguna
    public List<User> tampilkanSemua() {
        List<User> daftarUser = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                User user = buatUserDariResultSet(rs);

                daftarUser.add(user);
            }

        } catch (SQLException e) {
            System.err.println("Gagal mengambil data pengguna: " + e.getMessage());
        }

        return daftarUser;
    }

    private User buatUserDariResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String nama = rs.getString("name");
        String email = rs.getString("email");
        String passwordHash = rs.getString("password_hash");
        Role role = Role.valueOf(rs.getString("role"));

        if (role == Role.ADMIN) {
            return new Admin(id, nama, email, passwordHash, "Administrator");
        }

        return new Pelanggan(id, nama, email, passwordHash, null, null);
    }

}
