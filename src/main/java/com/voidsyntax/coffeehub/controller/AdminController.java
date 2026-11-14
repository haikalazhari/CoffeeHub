package com.voidsyntax.coffeehub.controller;

import java.io.IOException;

import com.voidsyntax.coffeehub.model.User;
import com.voidsyntax.coffeehub.repository.CategoryDAO;
import com.voidsyntax.coffeehub.repository.OrderDAO;
import com.voidsyntax.coffeehub.repository.ProductDAO;
import com.voidsyntax.coffeehub.service.SessionManager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Window;

public class AdminController {

    @FXML
    private Label adminNameLabel;

    @FXML
    private Label totalProductsLabel;

    @FXML
    private Label totalCategoriesLabel;

    @FXML
    private Label totalOrdersLabel;

    @FXML
    private StackPane contentArea;

    // Tombol navigasi sidebar
    @FXML
    private Button dashboardButton;

    @FXML
    private Button productsButton;

    @FXML
    private Button categoriesButton;

    @FXML
    private Button stockButton;

    @FXML
    private Button ordersButton;

    private Node dashboardContent;

    private static final String MENU_ACTIVE_STYLE =
            "-fx-background-color: #8B6249;" +
            "-fx-text-fill: white;" +
            "-fx-alignment: CENTER_LEFT;" +
            "-fx-background-radius: 7;";

    private static final String MENU_INACTIVE_STYLE =
            "-fx-background-color: transparent;" +
            "-fx-text-fill: white;" +
            "-fx-alignment: CENTER_LEFT;";

    private final ProductDAO productDAO = new ProductDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final OrderDAO orderDAO = new OrderDAO();

    // Inisialisasi dashboard
    @FXML
    private void initialize() {

        dashboardContent = contentArea.getChildren().get(0);

        aturHighlight(dashboardButton);
        tampilkanNamaAdmin();
        muatDataDashboard();
    }

    // Mengatur highlight menu sidebar
    private void aturHighlight(Button tombolAktif) {

        dashboardButton.setStyle(MENU_INACTIVE_STYLE);
        productsButton.setStyle(MENU_INACTIVE_STYLE);
        categoriesButton.setStyle(MENU_INACTIVE_STYLE);
        stockButton.setStyle(MENU_INACTIVE_STYLE);
        ordersButton.setStyle(MENU_INACTIVE_STYLE);

        tombolAktif.setStyle(MENU_ACTIVE_STYLE);
    }

    // Menampilkan nama Admin
    private void tampilkanNamaAdmin() {

        User admin = SessionManager.getCurrentUser();

        if (SessionManager.isAdmin() && admin != null) {
            adminNameLabel.setText(admin.getNama());
        } else {
            adminNameLabel.setText("Administrator");
        }
    }

    // Mengambil data dari database
    private void muatDataDashboard() {

        totalProductsLabel.setText(
                String.valueOf(productDAO.tampilkanSemua().size())
        );

        totalCategoriesLabel.setText(
                String.valueOf(categoryDAO.tampilkanSemua().size())
        );

        totalOrdersLabel.setText(
                String.valueOf(orderDAO.tampilkanSemua().size())
        );
    }

    // Memuat halaman ke area konten
    private boolean muatHalaman(String lokasiFXML) {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(lokasiFXML)
            );

            Parent halaman = loader.load();

            contentArea.getChildren().setAll(halaman);
            return true;

        } catch (IOException | RuntimeException e) {

            e.printStackTrace();

            tampilkanError(
                    "Gagal membuka halaman: " + lokasiFXML
            );
            return false;
        }
    }

    // Menangani menu Dashboard
    @FXML
    private void handleDashboard() {

        contentArea.getChildren().setAll(dashboardContent);
        aturHighlight(dashboardButton);

        tampilkanNamaAdmin();
        muatDataDashboard();
    }

    // Menangani menu Products
    @FXML
    private void handleProducts() {
        if (muatHalaman("/fxml/products.fxml")) {
            aturHighlight(productsButton);
        }
    }

    // Menangani menu Categories
    @FXML
    private void handleCategories() {
        if (muatHalaman("/fxml/categories.fxml")) {
            aturHighlight(categoriesButton);
        }
    }

    // Menangani menu Stock Management
    @FXML
    private void handleStock() {
        if (muatHalaman("/fxml/stock-management.fxml")) {
            aturHighlight(stockButton);
        }
    }

    // Menangani menu Orders
    @FXML
    private void handleOrders() {
        if (muatHalaman("/fxml/orders.fxml")) {
            aturHighlight(ordersButton);
        }
    }

    // Menampilkan pesan error
    private void tampilkanError(String pesan) {

        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle("CoffeeHub Admin");
        alert.setHeaderText(null);
        alert.setContentText(pesan);

        alert.showAndWait();
    }

    // Menangani Logout
    @FXML
    private void handleLogout() {

        SessionManager.logout();

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/login.fxml")
            );

            Parent loginPage = loader.load();

            Window window = contentArea.getScene().getWindow();

            javafx.scene.Scene scene =
                    new javafx.scene.Scene(loginPage, 900, 600);

            ((javafx.stage.Stage) window).setScene(scene);
            ((javafx.stage.Stage) window).setTitle("CoffeeHub - Login");

        } catch (IOException e) {
            e.printStackTrace();
            tampilkanError("Gagal kembali ke halaman Login.");
        }
    }
}
