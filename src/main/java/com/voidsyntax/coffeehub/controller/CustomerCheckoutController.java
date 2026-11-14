package com.voidsyntax.coffeehub.controller;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import com.voidsyntax.coffeehub.enums.OrderStatus;
import com.voidsyntax.coffeehub.enums.PaymentMethod;
import com.voidsyntax.coffeehub.enums.PaymentStatus;
import com.voidsyntax.coffeehub.model.CartItem;
import com.voidsyntax.coffeehub.model.Order;
import com.voidsyntax.coffeehub.model.OrderDetail;
import com.voidsyntax.coffeehub.model.Pelanggan;
import com.voidsyntax.coffeehub.model.Product;
import com.voidsyntax.coffeehub.model.User;
import com.voidsyntax.coffeehub.repository.OrderDAO;
import com.voidsyntax.coffeehub.repository.ProductDAO;
import com.voidsyntax.coffeehub.service.CartManager;
import com.voidsyntax.coffeehub.service.SessionManager;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class CustomerCheckoutController {

    @FXML
    private VBox checkoutItemsContainer;

    @FXML
    private Label totalItemsLabel;

    @FXML
    private Label totalPriceLabel;

    private final OrderDAO orderDAO = new OrderDAO();
    private final ProductDAO productDAO = new ProductDAO();

    private final NumberFormat currencyFormat =
            NumberFormat.getCurrencyInstance(
                    Locale.forLanguageTag("id-ID")
            );

    private int getCurrentUserId() {
        if (!SessionManager.isPelanggan()
                || SessionManager.getCurrentUser() == null) {
            throw new IllegalStateException(
                    "Sesi pengguna tidak valid. Silakan login kembali."
            );
        }

        return SessionManager.getCurrentUser().getId();
    }

    @FXML
    private void initialize() {
        tampilkanRingkasanCheckout();
    }

    private void tampilkanRingkasanCheckout() {
        checkoutItemsContainer.getChildren().clear();

        int userId = getCurrentUserId();

        BigDecimal total = BigDecimal.ZERO;
        int totalItem = 0;

        for (CartItem item : CartManager.getItems(userId)) {
            Product product = productDAO.cariBerdasarkanId(
                    item.getProduct().getId()
            );

            if (product == null) {
                tambahLabel(
                        item.getProduct().getNama()
                        + " - Produk tidak ditemukan."
                );
                continue;
            }

            if (!product.isTersedia() || product.getStok() <= 0) {
                tambahLabel(
                        product.getNama()
                        + " - Produk tidak tersedia."
                );
                continue;
            }

            BigDecimal subtotal = product.getHarga()
                    .multiply(BigDecimal.valueOf(item.getQuantity()));

            total = total.add(subtotal);
            totalItem += item.getQuantity();

            tambahLabel(
                    product.getNama()
                    + " x " + item.getQuantity()
                    + " = " + currencyFormat.format(subtotal)
            );

            if (item.getQuantity() > product.getStok()) {
                tambahLabel(
                        "Peringatan: Stok " + product.getNama()
                        + " hanya tersisa " + product.getStok() + "."
                );
            }
        }

        totalItemsLabel.setText(totalItem + " item");
        totalPriceLabel.setText(currencyFormat.format(total));
    }

    private void tambahLabel(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setStyle(
                "-fx-font-size: 14px;"
                + "-fx-text-fill: #493629;"
        );

        checkoutItemsContainer.getChildren().add(label);
    }

    @FXML
    private void handlePlaceOrder() {
        int userId = getCurrentUserId();

        if (CartManager.isEmpty(userId)) {
            tampilkanPesan(
                    Alert.AlertType.WARNING,
                    "Keranjang Kosong",
                    "Tambahkan produk sebelum melakukan checkout."
            );
            return;
        }

        User user = SessionManager.getCurrentUser();

        if (!(user instanceof Pelanggan)) {
            tampilkanPesan(
                    Alert.AlertType.ERROR,
                    "Sesi Tidak Valid",
                    "Silakan login kembali."
            );
            return;
        }

        List<OrderDetail> orderDetails = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        // Ambil ulang harga dan stok terbaru dari database.
        for (CartItem item : CartManager.getItems(userId)) {
            Product product = productDAO.cariBerdasarkanId(
                    item.getProduct().getId()
            );

            if (product == null) {
                tampilkanPesan(
                        Alert.AlertType.WARNING,
                        "Produk Tidak Ditemukan",
                        item.getProduct().getNama()
                        + " sudah tidak tersedia di database."
                );
                return;
            }

            if (!product.isTersedia() || product.getStok() <= 0) {
                tampilkanPesan(
                        Alert.AlertType.WARNING,
                        "Produk Tidak Tersedia",
                        product.getNama() + " sedang tidak tersedia."
                );
                return;
            }

            if (item.getQuantity() > product.getStok()) {
                tampilkanPesan(
                        Alert.AlertType.WARNING,
                        "Stok Tidak Mencukupi",
                        "Stok " + product.getNama()
                        + " tersisa " + product.getStok()
                        + ", sedangkan jumlah pesanan "
                        + item.getQuantity() + "."
                );
                return;
            }

            BigDecimal unitPrice = product.getHarga();

            OrderDetail detail = new OrderDetail(
                    0,
                    product,
                    item.getQuantity(),
                    unitPrice
            );

            orderDetails.add(detail);
            total = total.add(detail.getSubtotal());
        }

        // Buat kode pesanan unik.
        String orderCode = "CH-"
                + UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase(Locale.ROOT);

        Order order = new Order(
                0,
                orderCode,
                (Pelanggan) user,
                LocalDateTime.now(),
                total,
                PaymentMethod.TUNAI,
                PaymentStatus.BELUM_BAYAR,
                OrderStatus.MENUNGGU
        );

        for (OrderDetail detail : orderDetails) {
            order.tambahDetail(detail);
        }

        // Simpan pesanan dan kurangi stok melalui transaksi OrderDAO.
        boolean berhasil = orderDAO.tambah(order);

        if (berhasil) {
            // Hanya kosongkan keranjang milik pengguna yang checkout.
            CartManager.kosongkanKeranjang(userId);

            tampilkanPesan(
                    Alert.AlertType.INFORMATION,
                    "Pesanan Berhasil",
                    "Pesanan berhasil dibuat."
                    + "\nKode Pesanan: " + orderCode
                    + "\nTotal: " + currencyFormat.format(total)
                    + "\nPembayaran: Tunai"
            );

        } else {
            tampilkanPesan(
                    Alert.AlertType.ERROR,
                    "Checkout Gagal",
                    "Pesanan gagal disimpan. "
                    + "Periksa kembali stok produk atau coba lagi."
            );
        }
    }

    private void tampilkanPesan(
            Alert.AlertType tipe,
            String judul,
            String pesan
    ) {
        Alert alert = new Alert(tipe);
        alert.setTitle(judul);
        alert.setHeaderText(null);
        alert.setContentText(pesan);
        alert.showAndWait();
    }
}
