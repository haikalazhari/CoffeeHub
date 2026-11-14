package com.voidsyntax.coffeehub.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.voidsyntax.coffeehub.config.DatabaseConnection;
import com.voidsyntax.coffeehub.model.Category;
import com.voidsyntax.coffeehub.model.Product;

public class ProductDAO {

    // Menambahkan produk baru
    public boolean tambah(Product product) {
        String sql = """
                INSERT INTO products
                (category_id, name, description, price, stock, image_path, is_available)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (product.getKategori() != null) {
                stmt.setInt(1, product.getKategori().getId());
            } else {
                stmt.setNull(1, java.sql.Types.INTEGER);
            }

            stmt.setString(2, product.getNama());
            stmt.setString(3, product.getDeskripsi());
            stmt.setBigDecimal(4, product.getHarga());
            stmt.setInt(5, product.getStok());
            stmt.setString(6, product.getGambar());
            stmt.setBoolean(7, product.isTersedia());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Gagal menambahkan produk: " + e.getMessage());
            return false;
        }
    }

    // Mengambil seluruh produk beserta kategorinya
    public List<Product> tampilkanSemua() {
        List<Product> daftarProduk = new ArrayList<>();

        String sql = """
                SELECT p.*, c.name AS category_name,
                       c.description AS category_description
                FROM products p
                LEFT JOIN categories c ON p.category_id = c.id
                ORDER BY p.id ASC
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                daftarProduk.add(ambilProductDariResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Gagal mengambil produk: " + e.getMessage());
        }

        return daftarProduk;
    }

    // Mencari produk berdasarkan ID
    public Product cariBerdasarkanId(int id) {
        String sql = """
                SELECT p.*, c.name AS category_name,
                       c.description AS category_description
                FROM products p
                LEFT JOIN categories c ON p.category_id = c.id
                WHERE p.id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return ambilProductDariResultSet(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Gagal mencari produk: " + e.getMessage());
        }

        return null;
    }

    // Memperbarui data produk
    public boolean ubah(Product product) {
        String sql = """
                UPDATE products
                SET category_id = ?, name = ?, description = ?,
                    price = ?, stock = ?, image_path = ?, is_available = ?
                WHERE id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (product.getKategori() != null) {
                stmt.setInt(1, product.getKategori().getId());
            } else {
                stmt.setNull(1, java.sql.Types.INTEGER);
            }

            stmt.setString(2, product.getNama());
            stmt.setString(3, product.getDeskripsi());
            stmt.setBigDecimal(4, product.getHarga());
            stmt.setInt(5, product.getStok());
            stmt.setString(6, product.getGambar());
            stmt.setBoolean(7, product.isTersedia());
            stmt.setInt(8, product.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Gagal mengubah produk: " + e.getMessage());
            return false;
        }
    }

    // Menghapus produk berdasarkan ID
    public boolean hapus(int id) {
        String sql = "DELETE FROM products WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Gagal menghapus produk: " + e.getMessage());
            return false;
        }
    }

    // Mengambil data produk dari hasil query
    private Product ambilProductDariResultSet(ResultSet rs)
            throws SQLException {

        Category category = null;

        if (rs.getObject("category_id") != null) {
            category = new Category(
                    rs.getInt("category_id"),
                    rs.getString("category_name"),
                    rs.getString("category_description")
            );
        }

        return new Product(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getBigDecimal("price"),
                rs.getInt("stock"),
                rs.getString("image_path"),
                rs.getBoolean("is_available"),
                category
        );
    }
}
