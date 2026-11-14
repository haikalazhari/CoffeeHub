package com.voidsyntax.coffeehub.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.voidsyntax.coffeehub.config.DatabaseConnection;
import com.voidsyntax.coffeehub.model.Category;

public class CategoryDAO {

    // Memeriksa apakah nama kategori sudah digunakan.
    private boolean namaSudahDigunakan(
            Connection conn,
            String nama,
            int excludeId
    ) throws SQLException {

        String sql = """
                SELECT COUNT(*)
                FROM categories
                WHERE LOWER(TRIM(name)) = LOWER(TRIM(?))
                AND id <> ?
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nama);
            stmt.setInt(2, excludeId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }

        return false;
    }

    // Menambahkan kategori baru.
    public boolean tambah(Category category) {
        String sql = """
                INSERT INTO categories (name, description)
                VALUES (?, ?)
                """;

        try (Connection conn = DatabaseConnection.getConnection()) {

            if (namaSudahDigunakan(conn, category.getNama(), -1)) {
                System.err.println("Nama kategori sudah digunakan.");
                return false;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, category.getNama());
                stmt.setString(2, category.getDeskripsi());

                return stmt.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            System.err.println("Gagal menambahkan kategori: " + e.getMessage());
            return false;
        }
    }

    // Mengambil seluruh kategori.
    public List<Category> tampilkanSemua() {
        List<Category> daftarKategori = new ArrayList<>();
        String sql = "SELECT * FROM categories ORDER BY id ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Category category = new Category(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description")
                );

                daftarKategori.add(category);
            }

        } catch (SQLException e) {
            System.err.println("Gagal mengambil kategori: " + e.getMessage());
        }

        return daftarKategori;
    }

    // Mencari kategori berdasarkan ID.
    public Category cariBerdasarkanId(int id) {
        String sql = "SELECT * FROM categories WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Category(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("description")
                    );
                }
            }

        } catch (SQLException e) {
            System.err.println("Gagal mencari kategori: " + e.getMessage());
        }

        return null;
    }

    // Memperbarui kategori.
    public boolean ubah(Category category) {
        String sql = """
                UPDATE categories
                SET name = ?, description = ?
                WHERE id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection()) {

            if (namaSudahDigunakan(
                    conn,
                    category.getNama(),
                    category.getId()
            )) {
                System.err.println("Nama kategori sudah digunakan.");
                return false;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, category.getNama());
                stmt.setString(2, category.getDeskripsi());
                stmt.setInt(3, category.getId());

                return stmt.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            System.err.println("Gagal mengubah kategori: " + e.getMessage());
            return false;
        }
    }

    // Menghapus kategori.
    public boolean hapus(int id) {
        String sql = "DELETE FROM categories WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Gagal menghapus kategori: " + e.getMessage());
            return false;
        }
    }
}
