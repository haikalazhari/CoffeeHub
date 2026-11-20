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

    private static final java.util.Map<Integer, String> userPhones = new java.util.concurrent.ConcurrentHashMap<>();
    private static final String PHONE_FILE = "user_phones.properties";

    static {
        muatDataTelepon();
        pastikanKolomNoTelepon();
    }

    private static void pastikanKolomNoTelepon() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "ALTER TABLE users ADD COLUMN IF NOT EXISTS no_telepon VARCHAR(20) NOT NULL AFTER email")) {
            stmt.executeUpdate();
        } catch (Exception ignored) {
        }
    }

    private static void muatDataTelepon() {
        java.io.File file = new java.io.File(PHONE_FILE);
        if (file.exists()) {
            java.util.Properties props = new java.util.Properties();
            try (java.io.FileInputStream fis = new java.io.FileInputStream(file)) {
                props.load(fis);
                for (String key : props.stringPropertyNames()) {
                    try {
                        userPhones.put(Integer.parseInt(key), props.getProperty(key));
                    } catch (NumberFormatException ignored) {}
                }
            } catch (java.io.IOException e) {
                System.err.println("Gagal memuat data telepon pengguna: " + e.getMessage());
            }
        }
    }

    private static synchronized void simpanDataTelepon() {
        java.util.Properties props = new java.util.Properties();
        for (java.util.Map.Entry<Integer, String> entry : userPhones.entrySet()) {
            if (entry.getValue() != null) {
                props.setProperty(String.valueOf(entry.getKey()), entry.getValue());
            }
        }
        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(PHONE_FILE)) {
            props.store(fos, "CoffeeHub User Phone Numbers");
        } catch (java.io.IOException e) {
            System.err.println("Gagal menyimpan data telepon pengguna: " + e.getMessage());
        }
    }

    // Menambahkan pengguna baru
    public boolean tambah(User user, String passwordHash) {
        String sql = """
                INSERT INTO users (name, email, no_telepon, password_hash, role)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getNama());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getNoTelepon());
            stmt.setString(4, passwordHash);
            stmt.setString(5, user.getRole().name());

            boolean berhasil = stmt.executeUpdate() > 0;
            if (berhasil && user.getNoTelepon() != null && !user.getNoTelepon().isBlank()) {
                User created = cariBerdasarkanEmail(user.getEmail());
                if (created != null) {
                    userPhones.put(created.getId(), user.getNoTelepon().trim());
                    simpanDataTelepon();
                }
            }
            return berhasil;

        } catch (SQLException e) {
            // Fallback jika database belum memiliki kolom no_telepon
            String fallbackSql = """
                    INSERT INTO users (name, email, password_hash, role)
                    VALUES (?, ?, ?, ?)
                    """;
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(fallbackSql)) {

                stmt.setString(1, user.getNama());
                stmt.setString(2, user.getEmail());
                stmt.setString(3, passwordHash);
                stmt.setString(4, user.getRole().name());

                boolean berhasil = stmt.executeUpdate() > 0;
                if (berhasil && user.getNoTelepon() != null && !user.getNoTelepon().isBlank()) {
                    User created = cariBerdasarkanEmail(user.getEmail());
                    if (created != null) {
                        userPhones.put(created.getId(), user.getNoTelepon().trim());
                        simpanDataTelepon();
                    }
                }
                return berhasil;
            } catch (SQLException ex) {
                System.err.println("Gagal menambahkan pengguna: " + ex.getMessage());
                return false;
            }
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

    // Memperbarui password pengguna berdasarkan email
    public boolean perbaruiPassword(String email, String passwordHash) {
        String sql = "UPDATE users SET password_hash = ? WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, passwordHash);
            stmt.setString(2, email);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Gagal memperbarui password pengguna: " + e.getMessage());
            return false;
        }
    }

    // Memperbarui profil pengguna (nama, email, dan no telepon) berdasarkan ID
    public boolean perbaruiProfil(int id, String nama, String email, String noTelepon) {
        String sql = "UPDATE users SET name = ?, email = ?, no_telepon = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nama);
            stmt.setString(2, email);
            stmt.setString(3, noTelepon);
            stmt.setInt(4, id);

            boolean sukses = stmt.executeUpdate() > 0;
            if (sukses && noTelepon != null) {
                userPhones.put(id, noTelepon.trim());
                simpanDataTelepon();
            }
            return sukses;

        } catch (SQLException e) {
            // Fallback jika database belum memiliki kolom no_telepon
            String fallbackSql = "UPDATE users SET name = ?, email = ? WHERE id = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(fallbackSql)) {

                stmt.setString(1, nama);
                stmt.setString(2, email);
                stmt.setInt(3, id);

                boolean sukses = stmt.executeUpdate() > 0;
                if (sukses && noTelepon != null) {
                    userPhones.put(id, noTelepon.trim());
                    simpanDataTelepon();
                }
                return sukses;
            } catch (SQLException ex) {
                System.err.println("Gagal memperbarui profil pengguna: " + ex.getMessage());
                return false;
            }
        }
    }

    public boolean perbaruiProfil(int id, String nama, String email) {
        return perbaruiProfil(id, nama, email, null);
    }

    // Memperbarui password pengguna berdasarkan ID
    public boolean perbaruiPasswordById(int id, String passwordHash) {
        String sql = "UPDATE users SET password_hash = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, passwordHash);
            stmt.setInt(2, id);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Gagal memperbarui password pengguna: " + e.getMessage());
            return false;
        }
    }

    // Mencari pengguna berdasarkan ID
    public User cariBerdasarkanId(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return buatUserDariResultSet(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Gagal mencari pengguna berdasarkan ID: " + e.getMessage());
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

        String phone = null;
        try {
            phone = rs.getString("no_telepon");
        } catch (Exception ignored) {
            try {
                phone = rs.getString("phone");
            } catch (Exception ignored2) {}
        }
        if (phone == null || phone.isBlank()) {
            phone = userPhones.get(id);
        }

        User userObj;
        if (role == Role.ADMIN) {
            userObj = new Admin(id, nama, email, passwordHash, "Administrator");
        } else {
            userObj = new Pelanggan(id, nama, email, passwordHash, phone, null);
        }
        userObj.setNoTelepon(phone != null ? phone : "");
        return userObj;
    }

}
