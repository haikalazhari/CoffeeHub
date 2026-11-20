package com.voidsyntax.coffeehub.controller;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import com.voidsyntax.coffeehub.model.Product;
import com.voidsyntax.coffeehub.model.User;
import com.voidsyntax.coffeehub.repository.ProductDAO;
import com.voidsyntax.coffeehub.service.SessionManager;
import com.voidsyntax.coffeehub.util.AlertUtil;
import com.voidsyntax.coffeehub.util.ProductDetailDialog;
import com.voidsyntax.coffeehub.util.ToastUtil;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;

public class CustomerController {

    private static CustomerController instance;

    @FXML private Label customerNameLabel;
    @FXML private Label totalProductsLabel;
    @FXML private Label availableProductsLabel;
    @FXML private VBox featuredProductsBox;

    @FXML private StackPane contentArea;

    @FXML private Button dashboardButton;
    @FXML private Button productsButton;
    @FXML private Button cartButton;
    @FXML private Button ordersButton;
    @FXML private Button profileButton;

    private Node dashboardContent;

    private final ProductDAO productDAO = new ProductDAO();

    private final NumberFormat currencyFormat =
            NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));

    public static CustomerController getInstance() {
        return instance;
    }

    public void bukaHalamanOrders() {
        handleOrders();
    }

    public void bukaHalamanCart() {
        handleCart();
    }

    public void showToast(String message) {
        ToastUtil.showSuccess(contentArea, message);
    }

    public void showWarningToast(String message) {
        ToastUtil.showWarning(contentArea, message);
    }

    @FXML
    private void initialize() {
        instance = this;
        dashboardContent = contentArea.getChildren().get(0);

        tampilkanNamaPelanggan();
        muatDataDashboard();
        aturHighlight(dashboardButton);

        User pelanggan = SessionManager.getCurrentUser();
        String nama = (pelanggan != null) ? pelanggan.getNama() : "Customer";
        javafx.application.Platform.runLater(() -> {
            ToastUtil.showSuccess(contentArea, "Selamat datang di CoffeeHub, " + nama + "!");
        });
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
            Label emptyLabel = new Label("Belum ada produk yang tersedia.");
            emptyLabel.setStyle("-fx-text-fill: #8B7562; -fx-font-size: 14px;");
            featuredProductsBox.getChildren().add(emptyLabel);
            return;
        }

        String styleNormal =
                "-fx-background-color: white;" +
                "-fx-padding: 16 20 16 20;" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: #E8D8C8;" +
                "-fx-border-radius: 14;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(73, 54, 41, 0.04), 8, 0, 0, 2);";

        String styleHover =
                "-fx-background-color: #FFFDFB;" +
                "-fx-padding: 16 20 16 20;" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: #C58B55;" +
                "-fx-border-radius: 14;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(112, 69, 47, 0.14), 12, 0, 0, 4);";

        for (Product product : produkTersedia) {
            VBox card = new VBox(8);
            card.setStyle(styleNormal);

            // Header baris atas: Kategori dan Hint "Klik untuk Detail →"
            javafx.scene.layout.HBox topRow = new javafx.scene.layout.HBox(10);
            topRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            String kategori = product.getKategori() != null
                    ? product.getKategori().getNama()
                    : "Menu Kopi";

            Label kategoriLabel = new Label("☕ " + kategori);
            kategoriLabel.setStyle(
                    "-fx-background-color: #F7EFE7;" +
                    "-fx-text-fill: #70452F;" +
                    "-fx-font-size: 11px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-background-radius: 10;" +
                    "-fx-padding: 3 9 3 9;"
            );

            javafx.scene.layout.Region topSpacer = new javafx.scene.layout.Region();
            javafx.scene.layout.HBox.setHgrow(topSpacer, javafx.scene.layout.Priority.ALWAYS);

            Label detailHint = new Label("Lihat Detail →");
            detailHint.setStyle(
                    "-fx-font-size: 12px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-text-fill: #8A5A3D;"
            );

            topRow.getChildren().addAll(kategoriLabel, topSpacer, detailHint);

            // Baris Tengah: Nama Produk & Harga
            javafx.scene.layout.HBox middleRow = new javafx.scene.layout.HBox(12);
            middleRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            Label namaLabel = new Label(product.getNama());
            namaLabel.setStyle(
                    "-fx-font-size: 17px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-text-fill: #493629;"
            );

            javafx.scene.layout.Region middleSpacer = new javafx.scene.layout.Region();
            javafx.scene.layout.HBox.setHgrow(middleSpacer, javafx.scene.layout.Priority.ALWAYS);

            Label hargaLabel = new Label(
                    currencyFormat.format(product.getHarga())
            );
            hargaLabel.setStyle(
                    "-fx-font-size: 16px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-text-fill: #70452F;"
            );

            middleRow.getChildren().addAll(namaLabel, middleSpacer, hargaLabel);

            // Deskripsi singkat
            String desc = (product.getDeskripsi() != null && !product.getDeskripsi().isBlank())
                    ? product.getDeskripsi()
                    : "Pilihan kopi istimewa khas CoffeeHub. Klik untuk melihat detail & memesan.";
            Label descLabel = new Label(desc);
            descLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #8B7562;");
            descLabel.setWrapText(true);

            card.getChildren().addAll(topRow, middleRow, descLabel);

            // Hover dan Click Handler
            card.setOnMouseEntered(e -> card.setStyle(styleHover));
            card.setOnMouseExited(e -> card.setStyle(styleNormal));

            card.setOnMouseClicked(e -> {
                ProductDetailDialog.tampilkan(
                        contentArea.getScene().getWindow(),
                        product
                );
            });

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

    @FXML
    private void handleProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/profile.fxml")
            );

            Parent profilePage = loader.load();

            contentArea.getChildren().setAll(profilePage);
            aturHighlight(profileButton);

        } catch (IOException e) {
            tampilkanError(
                    "Gagal membuka Profil: " + e.getMessage()
            );
            e.printStackTrace();
        }
    }

    public void perbaruiNamaPelanggan() {
        tampilkanNamaPelanggan();
    }

    private void aturHighlight(Button tombolAktif) {
        Button[] tombolNavigasi = {
                dashboardButton,
                productsButton,
                cartButton,
                ordersButton,
                profileButton
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
    private void handleLogout() {
        boolean yakin = AlertUtil.konfirmasi(
                contentArea.getScene().getWindow(),
                "Konfirmasi Logout",
                "Konfirmasi Keluar Akun",
                "Apakah Anda yakin ingin keluar dari akun?",
                "Ya, Keluar",
                "Batal"
        );

        if (yakin) {
            SessionManager.logout();

            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/fxml/login.fxml")
                );

                Parent loginPage = loader.load();
                Window window = contentArea.getScene().getWindow();
                Scene scene = new Scene(loginPage, 900, 600);

                ((Stage) window).setScene(scene);
                ((Stage) window).setTitle("CoffeeHub - Login");
                ((Stage) window).centerOnScreen();

            } catch (IOException e) {
                e.printStackTrace();
                tampilkanError("Gagal kembali ke halaman Login: " + e.getMessage());
            }
        }
    }
}
