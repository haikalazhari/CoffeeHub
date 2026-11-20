package com.voidsyntax.coffeehub.controller;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.voidsyntax.coffeehub.model.Category;
import com.voidsyntax.coffeehub.model.Product;
import com.voidsyntax.coffeehub.repository.CategoryDAO;
import com.voidsyntax.coffeehub.repository.ProductDAO;
import com.voidsyntax.coffeehub.service.CartManager;
import com.voidsyntax.coffeehub.service.SessionManager;
import com.voidsyntax.coffeehub.util.ProductDetailDialog;
import com.voidsyntax.coffeehub.util.ToastUtil;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class CustomerProductsController {

    @FXML
    private VBox productsContainer;

    @FXML
    private HBox categoryFilterBar;

    private final ProductDAO productDAO = new ProductDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    private Integer selectedCategoryId = null; // null = Semua
    private final List<Button> filterButtons = new ArrayList<>();

    private final NumberFormat currencyFormat =
            NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));

    @FXML
    private void initialize() {
        siapkanFilterKategori();
        muatProduk();
    }

    private void siapkanFilterKategori() {
        if (categoryFilterBar == null) return;
        categoryFilterBar.getChildren().clear();
        filterButtons.clear();

        // Tombol "Semua"
        Button btnSemua = buatTombolFilter("Semua", null);
        categoryFilterBar.getChildren().add(btnSemua);
        filterButtons.add(btnSemua);

        // Tombol masing-masing kategori
        List<Category> daftarKategori = categoryDAO.tampilkanSemua();
        for (Category c : daftarKategori) {
            String icon = switch (c.getNama().toLowerCase()) {
                case "coffee" -> "☕ ";
                case "non-coffee" -> "🍵 ";
                default -> c.getNama().toLowerCase().contains("pastry") || c.getNama().toLowerCase().contains("bakery") ? "🥐 " : "🏷️ ";
            };
            Button btn = buatTombolFilter(icon + c.getNama(), c.getId());
            categoryFilterBar.getChildren().add(btn);
            filterButtons.add(btn);
        }

        perbaruiTampilanFilter();
    }

    private Button buatTombolFilter(String label, Integer categoryId) {
        Button btn = new Button(label);
        btn.setUserData(categoryId);
        btn.setOnAction(e -> {
            selectedCategoryId = categoryId;
            perbaruiTampilanFilter();
            muatProduk();
        });
        return btn;
    }

    private void perbaruiTampilanFilter() {
        for (Button btn : filterButtons) {
            Object data = btn.getUserData();
            boolean isActive = (selectedCategoryId == null && data == null)
                    || (selectedCategoryId != null && selectedCategoryId.equals(data));

            if (isActive) {
                btn.setStyle(
                        "-fx-background-color: #70452F;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 12px;" +
                        "-fx-background-radius: 20;" +
                        "-fx-padding: 6 16;" +
                        "-fx-cursor: hand;"
                );
            } else {
                btn.setStyle(
                        "-fx-background-color: #FAF4ED;" +
                        "-fx-text-fill: #56301F;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 12px;" +
                        "-fx-background-radius: 20;" +
                        "-fx-border-color: #E8D8C8;" +
                        "-fx-border-radius: 20;" +
                        "-fx-padding: 6 16;" +
                        "-fx-cursor: hand;"
                );
            }
        }
    }

    private void muatProduk() {
        productsContainer.getChildren().clear();

        List<Product> semuaProduk;
        if (selectedCategoryId == null) {
            semuaProduk = productDAO.tampilkanSemua();
        } else {
            semuaProduk = productDAO.tampilkanBerdasarkanKategori(selectedCategoryId);
        }

        List<Product> produkTersedia = semuaProduk.stream()
                .filter(product -> product.isTersedia() && product.getStok() > 0)
                .toList();

        if (produkTersedia.isEmpty()) {
            Label emptyLabel = new Label("Belum ada produk yang tersedia dalam kategori ini.");
            emptyLabel.setStyle(
                    "-fx-font-size: 14px;" +
                    "-fx-text-fill: #8B7562;" +
                    "-fx-padding: 10 0;"
            );

            productsContainer.getChildren().add(emptyLabel);
            return;
        }

        for (Product product : produkTersedia) {
            productsContainer.getChildren().add(buatKartuProduk(product));
        }
    }

    private VBox buatKartuProduk(Product product) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(18));
        card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: #E8D8C8;" +
                "-fx-border-radius: 10;"
        );

        HBox titleRow = new HBox(10);
        titleRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label namaLabel = new Label(product.getNama());
        namaLabel.setStyle(
                "-fx-font-size: 17px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #493629;"
        );

        Label kategoriLabel = new Label();
        if (product.getKategori() != null) {
            String katNama = product.getKategori().getNama();
            if (katNama.equalsIgnoreCase("Coffee")) {
                kategoriLabel.setText("☕ " + katNama);
                kategoriLabel.setStyle(
                        "-fx-background-color: #FAF4ED; -fx-text-fill: #56301F; -fx-font-weight: bold; " +
                        "-fx-font-size: 11px; -fx-padding: 3 9; -fx-background-radius: 8; -fx-border-color: #E8D8C8; -fx-border-radius: 8;"
                );
            } else if (katNama.equalsIgnoreCase("Non-Coffee")) {
                kategoriLabel.setText("🍵 " + katNama);
                kategoriLabel.setStyle(
                        "-fx-background-color: #ECFDF5; -fx-text-fill: #047857; -fx-font-weight: bold; " +
                        "-fx-font-size: 11px; -fx-padding: 3 9; -fx-background-radius: 8; -fx-border-color: #A7F3D0; -fx-border-radius: 8;"
                );
            } else if (katNama.contains("Pastry") || katNama.contains("Bakery")) {
                kategoriLabel.setText("🥐 " + katNama);
                kategoriLabel.setStyle(
                        "-fx-background-color: #FFF7ED; -fx-text-fill: #C2410C; -fx-font-weight: bold; " +
                        "-fx-font-size: 11px; -fx-padding: 3 9; -fx-background-radius: 8; -fx-border-color: #FED7AA; -fx-border-radius: 8;"
                );
            } else {
                kategoriLabel.setText("🏷️ " + katNama);
                kategoriLabel.setStyle(
                        "-fx-background-color: #F5EBE1; -fx-text-fill: #70452F; -fx-font-weight: bold; " +
                        "-fx-font-size: 11px; -fx-padding: 3 9; -fx-background-radius: 8;"
                );
            }
        } else {
            kategoriLabel.setText("Tanpa Kategori");
            kategoriLabel.setStyle(
                    "-fx-background-color: #F3F4F6; -fx-text-fill: #6B7280; " +
                    "-fx-font-size: 11px; -fx-padding: 3 9; -fx-background-radius: 8;"
            );
        }

        titleRow.getChildren().addAll(namaLabel, kategoriLabel);

        Label deskripsiLabel = new Label(
                product.getDeskripsi() == null || product.getDeskripsi().isBlank()
                        ? "Tidak ada deskripsi."
                        : product.getDeskripsi()
        );
        deskripsiLabel.setWrapText(true);
        deskripsiLabel.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-text-fill: #6D5A4D;"
        );

        Label hargaLabel = new Label(
                currencyFormat.format(product.getHarga())
        );
        hargaLabel.setStyle(
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #795548;"
        );

        Label stokLabel = new Label(
                "Stok tersedia: " + product.getStok()
        );
        stokLabel.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-text-fill: #8B7562;"
        );

        Button detailButton = new Button("Detail Produk");
        detailButton.setStyle(
                "-fx-background-color: #FAF4ED;" +
                "-fx-text-fill: #70452F;" +
                "-fx-border-color: #C58B55;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 8 16;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
        );
        detailButton.setOnAction(event -> ProductDetailDialog.tampilkan(
                productsContainer.getScene().getWindow(),
                product
        ));

        Button addButton = new Button("Add to Cart");
        addButton.setStyle(
                "-fx-background-color: #795548;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 9 18;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
        );

        addButton.setOnAction(event -> tambahKeKeranjang(product));

        HBox actionBox = new HBox(10, detailButton, addButton);

        card.getChildren().addAll(
                titleRow,
                deskripsiLabel,
                hargaLabel,
                stokLabel,
                actionBox
        );

        card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: #E8D8C8;" +
                "-fx-border-radius: 12;" +
                "-fx-cursor: hand;"
        );

        card.setOnMouseEntered(e -> card.setStyle(
                "-fx-background-color: #FFFDFB;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: #C58B55;" +
                "-fx-border-radius: 12;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(112, 69, 47, 0.12), 10, 0, 0, 3);"
        ));

        card.setOnMouseExited(e -> card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: #E8D8C8;" +
                "-fx-border-radius: 12;" +
                "-fx-cursor: hand;"
        ));

        return card;
    }

    private void tambahKeKeranjang(Product product) {
        try {
            if (!SessionManager.isPelanggan()
                    || SessionManager.getCurrentUser() == null) {
                throw new IllegalArgumentException(
                        "Sesi pengguna tidak valid. Silakan login kembali."
                );
            }

            int userId = SessionManager.getCurrentUser().getId();

            CartManager.tambahProduk(userId, product, 1);

            ToastUtil.showSuccess(
                    productsContainer,
                    product.getNama() + " berhasil ditambahkan ke keranjang!"
            );

        } catch (IllegalArgumentException e) {
            ToastUtil.showWarning(
                    productsContainer,
                    e.getMessage()
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
