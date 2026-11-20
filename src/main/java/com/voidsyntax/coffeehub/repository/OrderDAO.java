package com.voidsyntax.coffeehub.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.voidsyntax.coffeehub.config.DatabaseConnection;
import com.voidsyntax.coffeehub.enums.OrderStatus;
import com.voidsyntax.coffeehub.enums.PaymentMethod;
import com.voidsyntax.coffeehub.enums.PaymentStatus;
import com.voidsyntax.coffeehub.model.Order;
import com.voidsyntax.coffeehub.model.OrderDetail;
import com.voidsyntax.coffeehub.model.Pelanggan;
import com.voidsyntax.coffeehub.model.Product;
import com.voidsyntax.coffeehub.model.User;

public class OrderDAO {

    // Cache catatan pesanan dalam memori agar tidak perlu mengubah struktur tabel database
    private static final Map<String, String> cacheOrderNotes = new ConcurrentHashMap<>();

    // Menambahkan pesanan beserta detail produknya

    public boolean tambah(Order order) {
        // Query SQL asli yang 100% sesuai dengan skema database asli coffeehub
        String sqlOrder = """
                INSERT INTO orders
                (order_code, user_id, order_date, total_amount,
                payment_method, payment_status, order_status)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        String sqlDetail = """
                INSERT INTO order_details
                (order_id, product_id, quantity, unit_price, subtotal)
                VALUES (?, ?, ?, ?, ?)
                """;

        String sqlUpdateStock = """
                UPDATE products
                SET stock = stock - ?
                WHERE id = ?
                AND stock >= ?
                AND is_available = 1
                """;

        if (order == null || order.getOrderDetails() == null
                || order.getOrderDetails().isEmpty()) {
            System.err.println("Pesanan tidak valid atau tidak memiliki detail.");
            return false;
        }

        Connection conn = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            int orderId;

            // 1. Simpan pesanan utama
            try (PreparedStatement stmt = conn.prepareStatement(
                    sqlOrder, Statement.RETURN_GENERATED_KEYS)) {

                stmt.setString(1, order.getOrderCode());
                stmt.setInt(2, order.getPelanggan().getId());
                stmt.setTimestamp(3, Timestamp.valueOf(order.getOrderDate()));
                stmt.setBigDecimal(4, order.getTotalAmount());
                // Tetap kompatibel dengan skema enum database asli ('TUNAI')
                stmt.setString(5, "TUNAI");
                stmt.setString(6, order.getPaymentStatus().name());
                stmt.setString(7, order.getOrderStatus().name());

                stmt.executeUpdate();

                // Simpan catatan ke cache lokal jika ada
                if (order.getNotes() != null && !order.getNotes().isBlank()) {
                    cacheOrderNotes.put(order.getOrderCode(), order.getNotes());
                }

                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (!keys.next()) {
                        throw new SQLException("ID pesanan gagal dibuat.");
                    }

                    orderId = keys.getInt(1);
                }
            }

            // 2. Kurangi stok dan simpan detail produk
            try (PreparedStatement stockStmt =
                        conn.prepareStatement(sqlUpdateStock);
                PreparedStatement detailStmt =
                        conn.prepareStatement(sqlDetail)) {

                for (OrderDetail detail : order.getOrderDetails()) {
                    int productId = detail.getProduk().getId();
                    int quantity = detail.getQuantity();

                    if (quantity <= 0) {
                        throw new SQLException("Jumlah produk tidak valid.");
                    }

                    stockStmt.setInt(1, quantity);
                    stockStmt.setInt(2, productId);
                    stockStmt.setInt(3, quantity);

                    int stockUpdated = stockStmt.executeUpdate();

                    if (stockUpdated == 0) {
                        throw new SQLException(
                                "Stok produk tidak mencukupi atau produk tidak tersedia. "
                                + "ID Produk: " + productId
                        );
                    }

                    detailStmt.setInt(1, orderId);
                    detailStmt.setInt(2, productId);
                    detailStmt.setInt(3, quantity);
                    detailStmt.setBigDecimal(4, detail.getUnitPrice());
                    detailStmt.setBigDecimal(5, detail.getSubtotal());

                    detailStmt.addBatch();
                }

                detailStmt.executeBatch();
            }

            // 3. Simpan seluruh perubahan
            conn.commit();

            System.out.println("Pesanan berhasil disimpan: "
                    + order.getOrderCode());

            return true;

        } catch (SQLException e) {

            // 4. Batalkan seluruh perubahan jika terjadi kegagalan
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackError) {
                    System.err.println(
                            "Rollback gagal: " + rollbackError.getMessage()
                    );
                }
            }

            System.err.println(
                    "Gagal menambahkan pesanan: " + e.getMessage()
            );

            return false;

        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println(
                            "Gagal menutup koneksi: " + e.getMessage()
                    );
                }
            }
        }
    }


    // Mengambil seluruh pesanan
    public List<Order> tampilkanSemua() {
        List<Order> daftarOrder = new ArrayList<>();

        String sql = """
                SELECT o.*, u.name, u.email, u.password_hash, u.role
                FROM orders o
                JOIN users u ON o.user_id = u.id
                ORDER BY o.order_date DESC
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                daftarOrder.add(ambilOrderDariResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Gagal mengambil pesanan: " + e.getMessage());
        }

        return daftarOrder;
    }

    // Mengambil pesanan berdasarkan ID pelanggan
    public List<Order> tampilkanBerdasarkanPelanggan(int userId) {
        List<Order> daftarOrder = new ArrayList<>();

        String sql = """
                SELECT o.*, u.name, u.email, u.password_hash, u.role
                FROM orders o
                JOIN users u ON o.user_id = u.id
                WHERE o.user_id = ?
                ORDER BY o.order_date DESC
                """;

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    daftarOrder.add(ambilOrderDariResultSet(rs));
                }
            }

            // Muat detail produk untuk setiap pesanan.
            for (Order order : daftarOrder) {
                muatDetailPesanan(conn, order);
            }

        } catch (SQLException e) {
            System.err.println(
                    "Gagal mengambil riwayat pesanan: " + e.getMessage()
            );
        }

        return daftarOrder;
    }

    // Mengambil detail item untuk suatu pesanan
    public List<OrderDetail> ambilDetailPesanan(int orderId) {
        List<OrderDetail> details = new ArrayList<>();
        String sql = """
                SELECT od.id, od.product_id, od.quantity,
                    od.unit_price, od.subtotal, p.name
                FROM order_details od
                JOIN products p ON od.product_id = p.id
                WHERE od.order_id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, orderId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Product product = new Product(
                            rs.getInt("product_id"),
                            rs.getString("name"),
                            null,
                            rs.getBigDecimal("unit_price"),
                            0,
                            null,
                            true,
                            null
                    );

                    OrderDetail detail = new OrderDetail(
                            rs.getInt("id"),
                            product,
                            rs.getInt("quantity"),
                            rs.getBigDecimal("unit_price")
                    );

                    details.add(detail);
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal mengambil detail pesanan: " + e.getMessage());
        }

        return details;
    }

    private void muatDetailPesanan(Connection conn, Order order)
            throws SQLException {

        String sql = """
                SELECT od.id, od.product_id, od.quantity,
                    od.unit_price, od.subtotal, p.name
                FROM order_details od
                JOIN products p ON od.product_id = p.id
                WHERE od.order_id = ?
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, order.getId());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {

                    Product product = new Product(
                            rs.getInt("product_id"),
                            rs.getString("name"),
                            null,
                            rs.getBigDecimal("unit_price"),
                            0,
                            null,
                            true,
                            null
                    );

                    OrderDetail detail = new OrderDetail(
                            rs.getInt("id"),
                            product,
                            rs.getInt("quantity"),
                            rs.getBigDecimal("unit_price")
                    );

                    order.tambahDetail(detail);
                }
            }
        }
    }

    // Mencari pesanan berdasarkan ID
    public Order cariBerdasarkanId(int id) {
        String sql = """
                SELECT o.*, u.name, u.email, u.password_hash, u.role
                FROM orders o
                JOIN users u ON o.user_id = u.id
                WHERE o.id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return ambilOrderDariResultSet(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Gagal mencari pesanan: " + e.getMessage());
        }

        return null;
    }

    // Memperbarui status pembayaran
    public boolean ubahStatusPembayaran(int id, PaymentStatus status) {
        String sql = """
                UPDATE orders
                SET payment_status = ?
                WHERE id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.name());
            stmt.setInt(2, id);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Gagal mengubah pembayaran: " + e.getMessage());
            return false;
        }
    }

    // Memperbarui status pesanan dan mengembalikan stok jika dibatalkan.
    public boolean ubahStatusPesanan(int id, OrderStatus status) {

        if (status == null) {
            return false;
        }

        Connection conn = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            OrderStatus statusSaatIni;
            PaymentStatus paymentStatusSaatIni;

            // 1. Kunci pesanan dan baca status pesanan serta pembayaran.
            String sqlCek = """
                    SELECT order_status, payment_status
                    FROM orders
                    WHERE id = ?
                    FOR UPDATE
                    """;

            try (PreparedStatement stmt = conn.prepareStatement(sqlCek)) {
                stmt.setInt(1, id);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return false;
                    }

                    statusSaatIni = OrderStatus.valueOf(
                            rs.getString("order_status")
                    );

                    paymentStatusSaatIni = PaymentStatus.valueOf(
                            rs.getString("payment_status")
                    );
                }
            }

            // 2. Cegah pesanan yang sudah dibatalkan diaktifkan kembali.
            if (statusSaatIni == OrderStatus.DIBATALKAN) {

                if (status == OrderStatus.DIBATALKAN) {
                    conn.commit();
                    return true;
                }

                conn.rollback();
                return false;
            }

            // 3. Pesanan yang sudah lunas tidak boleh dibatalkan.
            if (status == OrderStatus.DIBATALKAN
                    && paymentStatusSaatIni == PaymentStatus.LUNAS) {

                conn.rollback();
                return false;
            }

            // 4. Kembalikan stok jika pesanan dibatalkan.
            if (status == OrderStatus.DIBATALKAN) {

                String sqlDetail = """
                        SELECT product_id, quantity
                        FROM order_details
                        WHERE order_id = ?
                        """;

                String sqlRestoreStock = """
                        UPDATE products
                        SET stock = stock + ?
                        WHERE id = ?
                        """;

                try (PreparedStatement detailStmt =
                             conn.prepareStatement(sqlDetail);
                     PreparedStatement stockStmt =
                             conn.prepareStatement(sqlRestoreStock)) {

                    detailStmt.setInt(1, id);

                    try (ResultSet rs = detailStmt.executeQuery()) {
                        while (rs.next()) {

                            int productId = rs.getInt("product_id");
                            int quantity = rs.getInt("quantity");

                            stockStmt.setInt(1, quantity);
                            stockStmt.setInt(2, productId);

                            if (stockStmt.executeUpdate() != 1) {
                                throw new SQLException(
                                        "Gagal mengembalikan stok produk ID: "
                                        + productId
                                );
                            }
                        }
                    }
                }
            }

            // 5. Perbarui status pesanan.
            String sqlUpdate = """
                    UPDATE orders
                    SET order_status = ?
                    WHERE id = ?
                    """;

            try (PreparedStatement stmt = conn.prepareStatement(sqlUpdate)) {
                stmt.setString(1, status.name());
                stmt.setInt(2, id);

                if (stmt.executeUpdate() != 1) {
                    throw new SQLException(
                            "Gagal memperbarui status pesanan."
                    );
                }
            }

            // 6. Simpan seluruh perubahan.
            conn.commit();
            return true;

        } catch (SQLException e) {

            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackError) {
                    System.err.println(
                            "Rollback gagal: " + rollbackError.getMessage()
                    );
                }
            }

            System.err.println(
                    "Gagal mengubah status pesanan: " + e.getMessage()
            );

            return false;

        } finally {

            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println(
                            "Gagal menutup koneksi: " + e.getMessage()
                    );
                }
            }
        }
    }


    private Order ambilOrderDariResultSet(ResultSet rs)
            throws SQLException {

        User pelanggan = new Pelanggan(
                rs.getInt("user_id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("password_hash"),
                null,
                null
        );

        PaymentMethod method = PaymentMethod.TUNAI;
        try {
            String dbMethod = rs.getString("payment_method");
            if (dbMethod != null) {
                method = PaymentMethod.valueOf(dbMethod);
            }
        } catch (Exception ignored) {
        }

        Order order = new Order(
                rs.getInt("id"),
                rs.getString("order_code"),
                pelanggan,
                rs.getTimestamp("order_date").toLocalDateTime(),
                rs.getBigDecimal("total_amount"),
                method,
                PaymentStatus.valueOf(rs.getString("payment_status")),
                OrderStatus.valueOf(rs.getString("order_status"))
        );

        String note = cacheOrderNotes.get(order.getOrderCode());
        if (note != null) {
            order.setNotes(note);
        } else {
            try {
                order.setNotes(rs.getString("notes"));
            } catch (SQLException ignored) {
            }
        }

        return order;
    }
}
