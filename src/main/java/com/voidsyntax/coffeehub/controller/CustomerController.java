package com.voidsyntax.coffeehub.controller;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import com.voidsyntax.coffeehub.model.Product;
import com.voidsyntax.coffeehub.model.User;
import com.voidsyntax.coffeehub.repository.ProductDAO;
import com.voidsyntax.coffeehub.service.SessionManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class CustomerController {

    @FXML private Label customerNameLabel;
    @FXML private Label totalProductsLabel;
    @FXML private Label availableProductsLabel;
    @FXML private VBox featuredProductsBox;

    @FXML private StackPane contentArea;

    @FXML private Button dashboardButton;
    @FXML private Button productsButton;
    @FXML private Button cartButton;
    @FXML private Button ordersButton;

    private Node dashboardContent;

    private final ProductDAO productDAO = new ProductDAO();

    private final NumberFormat currencyFormat =
            NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));

    @FXML
    private void initialize() {
        dashboardContent = contentArea.getChildren().getFirst();

        tampilkanNamaPelanggan();
        muatDataDashboard();
        aturHighlight(dashboardButton);
    }

    private void tampilkanNamaPelanggan() {
        User pelanggan = SessionManager.getCurrentUser();

        if (SessionManager.isPelanggan() && pelanggan != null) {
            customerNameLabel.setText(pelanggan.getNama());
        } else {
            customerNameLabel.setText("Customer");
        }
    }

    private void muatDataDashboard() {
        List<Product> semuaProduk = productDAO.tampilkanSemua();

        long jumlahProdukTersedia = semuaProduk.stream()
                .filter(p -> p.isTersedia() && p.getStok() > 0)
                .count();

        totalProductsLabel.setText(String.valueOf(semuaProduk.size()));
        availableProductsLabel.setText(String.valueOf(jumlahProdukTersedia));

        tampilkanProdukUnggulan(semuaProduk);
    }

    private void tampilkanProdukUnggulan(List<Product> semuaProduk) {
        featuredProductsBox.getChildren().clear();

        List<Product> produkTersedia = semuaProduk.stream()
                .filter(p -> p.isTersedia() && p.getStok() > 0)
                .limit(4)
                .toList();

        if (produkTersedia.isEmpty()) {
            featuredProductsBox.getChildren().add(
                    new Label("Belum ada produk yang tersedia.")
            );
            return;
        }

        for (Product product : produkTersedia) {
            VBox card = new VBox(8);
            card.setStyle(
                    "-fx-background-color: white;" +
                    "-fx-padding: 15;" +
                    "-fx-background-radius: 10;" +
                    "-fx-border-color: #E8D8C8;" +
                    "-fx-border-radius: 10;"
            );

            Label namaLabel = new Label(product.getNama());
            namaLabel.setStyle(
                    "-fx-font-size: 16px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-text-fill: #493629;"
            );

            String kategori = product.getKategori() != null
                    ? product.getKategori().getNama()
                    : "Tanpa Kategori";

            Label kategoriLabel = new Label(kategori);
            kategoriLabel.setStyle("-fx-text-fill: #8B7562;");

            Label hargaLabel = new Label(
                    currencyFormat.format(product.getHarga())
            );
            hargaLabel.setStyle(
                    "-fx-font-weight: bold;" +
                    "-fx-text-fill: #795548;"
            );

            card.getChildren().addAll(
                    namaLabel,
                    kategoriLabel,
                    hargaLabel
            );

            featuredProductsBox.getChildren().add(card);
        }
    }

    @FXML
    private void handleDashboard() {
        contentArea.getChildren().setAll(dashboardContent);
        tampilkanNamaPelanggan();
        muatDataDashboard();
        aturHighlight(dashboardButton);
    }

    @FXML
    private void handleProducts() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/customer-products.fxml")
            );

            Node productsPage = loader.load();

            contentArea.getChildren().setAll(productsPage);
            aturHighlight(productsButton);

        } catch (IOException e) {
            tampilkanError("Gagal membuka katalog produk: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCart() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/customer-cart.fxml")
            );

            Node cartPage = loader.load();

            contentArea.getChildren().setAll(cartPage);
            aturHighlight(cartButton);

        } catch (IOException e) {
            tampilkanError("Gagal membuka Shopping Cart: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleOrders() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/customer-orders.fxml")
            );

            Node ordersPage = loader.load();

            contentArea.getChildren().setAll(ordersPage);
            aturHighlight(ordersButton);

        } catch (IOException e) {
            tampilkanError(
                    "Gagal membuka My Orders: " + e.getMessage()
            );
            e.printStackTrace();
        }
    }

    private void aturHighlight(Button tombolAktif) {
        Button[] tombolNavigasi = {
                dashboardButton,
                productsButton,
                cartButton,
                ordersButton
        };

        String styleNonaktif =
                "-fx-background-color: transparent;" +
                "-fx-text-fill: #FFF8ED;" +
                "-fx-font-size: 15px;" +
                "-fx-alignment: CENTER_LEFT;" +
                "-fx-background-radius: 12;" +
                "-fx-padding: 0 16 0 16;" +
                "-fx-cursor: hand;" +
                "-fx-graphic-text-gap: 14;";

        String styleAktif =
                "-fx-background-color: #8A5A3D;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;" +
                "-fx-alignment: CENTER_LEFT;" +
                "-fx-background-radius: 12;" +
                "-fx-padding: 0 16 0 16;" +
                "-fx-cursor: hand;" +
                "-fx-graphic-text-gap: 14;";

        for (Button tombol : tombolNavigasi) {
            tombol.setStyle(styleNonaktif);
        }

        tombolAktif.setStyle(styleAktif);
    }

    private void tampilkanPesan(String pesan) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("CoffeeHub");
        alert.setHeaderText(null);
        alert.setContentText(pesan);
        alert.showAndWait();
    }

    private void tampilkanError(String pesan) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Terjadi Kesalahan");
        alert.setContentText(pesan);
        alert.showAndWait();
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        SessionManager.logout();

        try {
            SceneNavigator.pindahHalaman(event, "/fxml/login.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
