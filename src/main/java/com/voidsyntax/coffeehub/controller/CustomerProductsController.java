package com.voidsyntax.coffeehub.controller;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import com.voidsyntax.coffeehub.model.Product;
import com.voidsyntax.coffeehub.repository.ProductDAO;
import com.voidsyntax.coffeehub.service.CartManager;
import com.voidsyntax.coffeehub.service.SessionManager;

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

    private final ProductDAO productDAO = new ProductDAO();

    private final NumberFormat currencyFormat =
            NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));

    @FXML
    private void initialize() {
        muatProduk();
    }

    private void muatProduk() {
        productsContainer.getChildren().clear();

        List<Product> semuaProduk = productDAO.tampilkanSemua();

        List<Product> produkTersedia = semuaProduk.stream()
                .filter(product -> product.isTersedia() && product.getStok() > 0)
                .toList();

        if (produkTersedia.isEmpty()) {
            Label emptyLabel = new Label("Belum ada produk yang tersedia.");
            emptyLabel.setStyle(
                    "-fx-font-size: 14px;" +
                    "-fx-text-fill: #8B7562;"
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

        Label namaLabel = new Label(product.getNama());
        namaLabel.setStyle(
                "-fx-font-size: 17px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #493629;"
        );

        String kategori = product.getKategori() != null
                ? product.getKategori().getNama()
                : "Tanpa Kategori";

        Label kategoriLabel = new Label(kategori);
        kategoriLabel.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-text-fill: #8B7562;"
        );

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

        Button addButton = new Button("Add to Cart");
        addButton.setStyle(
                "-fx-background-color: #795548;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 9 18;" +
                "-fx-cursor: hand;"
        );

        addButton.setOnAction(event -> tambahKeKeranjang(product));

        HBox actionBox = new HBox(addButton);

        card.getChildren().addAll(
                namaLabel,
                kategoriLabel,
                deskripsiLabel,
                hargaLabel,
                stokLabel,
                actionBox
        );

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

            tampilkanPesan(
                    Alert.AlertType.INFORMATION,
                    "Berhasil",
                    product.getNama() + " berhasil ditambahkan ke keranjang."
            );

        } catch (IllegalArgumentException e) {
            tampilkanPesan(
                    Alert.AlertType.WARNING,
                    "Tidak Dapat Menambahkan Produk",
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
