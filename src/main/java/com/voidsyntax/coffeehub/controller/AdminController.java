package com.voidsyntax.coffeehub.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import com.voidsyntax.coffeehub.enums.OrderStatus;
import com.voidsyntax.coffeehub.enums.PaymentStatus;
import com.voidsyntax.coffeehub.model.Order;
import com.voidsyntax.coffeehub.model.OrderDetail;
import com.voidsyntax.coffeehub.model.User;
import com.voidsyntax.coffeehub.repository.CategoryDAO;
import com.voidsyntax.coffeehub.repository.OrderDAO;
import com.voidsyntax.coffeehub.repository.ProductDAO;
import com.voidsyntax.coffeehub.service.SessionManager;
import com.voidsyntax.coffeehub.util.AlertUtil;
import com.voidsyntax.coffeehub.util.ToastUtil;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Window;

public class AdminController {

    private static AdminController instance;

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

    // Tabel Pesanan Terbaru di Dashboard
    @FXML
    private TableView<Order> recentOrdersTable;

    @FXML
    private TableColumn<Order, String> recentCodeColumn;

    @FXML
    private TableColumn<Order, String> recentCustomerColumn;

    @FXML
    private TableColumn<Order, String> recentDateColumn;

    @FXML
    private TableColumn<Order, String> recentTotalColumn;

    @FXML
    private TableColumn<Order, String> recentPaymentColumn;

    @FXML
    private TableColumn<Order, String> recentStatusColumn;

    @FXML
    private TableColumn<Order, Void> recentActionColumn;

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

    @FXML
    private Button profileButton;

    private Node dashboardContent;

    private final ObservableList<Order> daftarRecentOrders =
            FXCollections.observableArrayList();

    private final DateTimeFormatter dateFormatter =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public static AdminController getInstance() {
        return instance;
    }

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
        instance = this;

        dashboardContent = contentArea.getChildren().get(0);

        aturHighlight(dashboardButton);
        tampilkanNamaAdmin();
        aturKolomRecentOrders();
        aturTombolAksiRecentOrders();
        muatDataDashboard();

        User admin = SessionManager.getCurrentUser();
        String nama = (admin != null && admin.getNama() != null) ? admin.getNama() : "Admin";
        ToastUtil.showSuccess(contentArea, "Selamat datang di Admin Panel, " + nama + "!");
    }

    // Mengatur kolom tabel pesanan terbaru
    private void aturKolomRecentOrders() {
        if (recentOrdersTable == null) return;

        recentCodeColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getOrderCode())
        );

        recentCodeColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-font-weight: bold; -fx-text-fill: #56301F;");
                }
            }
        });

        recentCustomerColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getPelanggan() != null
                                ? data.getValue().getPelanggan().getNama()
                                : "-"
                )
        );

        recentDateColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getOrderDate() != null
                                ? data.getValue().getOrderDate().format(dateFormatter)
                                : "-"
                )
        );

        recentTotalColumn.setCellValueFactory(data -> {
            BigDecimal total = data.getValue().getTotalAmount();
            NumberFormat format = NumberFormat.getCurrencyInstance(Locale.of("id", "ID"));
            format.setMaximumFractionDigits(0);
            return new SimpleStringProperty(total != null ? format.format(total) : "Rp0");
        });

        recentTotalColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-font-weight: bold; -fx-text-fill: #70452F;");
                }
            }
        });

        recentPaymentColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getPaymentStatus() != null
                                ? data.getValue().getPaymentStatus().name()
                                : ""
                )
        );

        recentPaymentColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Label badge = new Label();
                    if ("LUNAS".equals(item)) {
                        badge.setText("✓ Lunas");
                        badge.setStyle(
                                "-fx-background-color: #ECFDF5;" +
                                "-fx-text-fill: #047857;" +
                                "-fx-border-color: #A7F3D0;" +
                                "-fx-border-radius: 12;" +
                                "-fx-background-radius: 12;" +
                                "-fx-padding: 3 10;" +
                                "-fx-font-weight: bold;" +
                                "-fx-font-size: 11px;"
                        );
                    } else {
                        badge.setText("⏳ Belum Bayar");
                        badge.setStyle(
                                "-fx-background-color: #FFF7ED;" +
                                "-fx-text-fill: #C2410C;" +
                                "-fx-border-color: #FED7AA;" +
                                "-fx-border-radius: 12;" +
                                "-fx-background-radius: 12;" +
                                "-fx-padding: 3 10;" +
                                "-fx-font-weight: bold;" +
                                "-fx-font-size: 11px;"
                        );
                    }
                    setGraphic(badge);
                    setText(null);
                }
            }
        });

        recentStatusColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getOrderStatus() != null
                                ? data.getValue().getOrderStatus().name()
                                : ""
                )
        );

        recentStatusColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Label badge = new Label();
                    switch (item) {
                        case "MENUNGGU" -> {
                            badge.setText("🕒 Menunggu");
                            badge.setStyle(
                                    "-fx-background-color: #FEFCE8;" +
                                    "-fx-text-fill: #A16207;" +
                                    "-fx-border-color: #FEF08A;" +
                                    "-fx-border-radius: 12;" +
                                    "-fx-background-radius: 12;" +
                                    "-fx-padding: 3 10;" +
                                    "-fx-font-weight: bold;" +
                                    "-fx-font-size: 11px;"
                            );
                        }
                        case "DIPROSES" -> {
                            badge.setText("⚙️ Diproses");
                            badge.setStyle(
                                    "-fx-background-color: #EFF6FF;" +
                                    "-fx-text-fill: #1D4ED8;" +
                                    "-fx-border-color: #BFDBFE;" +
                                    "-fx-border-radius: 12;" +
                                    "-fx-background-radius: 12;" +
                                    "-fx-padding: 3 10;" +
                                    "-fx-font-weight: bold;" +
                                    "-fx-font-size: 11px;"
                            );
                        }
                        case "SIAP" -> {
                            badge.setText("📦 Siap");
                            badge.setStyle(
                                    "-fx-background-color: #FAF5FF;" +
                                    "-fx-text-fill: #7E22CE;" +
                                    "-fx-border-color: #E9D5FF;" +
                                    "-fx-border-radius: 12;" +
                                    "-fx-background-radius: 12;" +
                                    "-fx-padding: 3 10;" +
                                    "-fx-font-weight: bold;" +
                                    "-fx-font-size: 11px;"
                            );
                        }
                        case "SELESAI" -> {
                            badge.setText("✓ Selesai");
                            badge.setStyle(
                                    "-fx-background-color: #ECFDF5;" +
                                    "-fx-text-fill: #047857;" +
                                    "-fx-border-color: #A7F3D0;" +
                                    "-fx-border-radius: 12;" +
                                    "-fx-background-radius: 12;" +
                                    "-fx-padding: 3 10;" +
                                    "-fx-font-weight: bold;" +
                                    "-fx-font-size: 11px;"
                            );
                        }
                        default -> {
                            badge.setText("✕ Dibatalkan");
                            badge.setStyle(
                                    "-fx-background-color: #FEF2F2;" +
                                    "-fx-text-fill: #B91C1C;" +
                                    "-fx-border-color: #FECACA;" +
                                    "-fx-border-radius: 12;" +
                                    "-fx-background-radius: 12;" +
                                    "-fx-padding: 3 10;" +
                                    "-fx-font-weight: bold;" +
                                    "-fx-font-size: 11px;"
                            );
                        }
                    }
                    setGraphic(badge);
                    setText(null);
                }
            }
        });

        // Double click row untuk melihat detail
        recentOrdersTable.setRowFactory(tv -> {
            TableRow<Order> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    tampilkanDetailPesanan(row.getItem());
                }
            });
            return row;
        });
    }

    // Mengatur tombol aksi pada tabel pesanan terbaru
    private void aturTombolAksiRecentOrders() {
        if (recentActionColumn == null) return;

        recentActionColumn.setCellFactory(col -> new TableCell<>() {
            private final Button detailBtn = new Button("Detail");

            {
                detailBtn.setStyle(
                        "-fx-background-color: #70452F;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 11px;" +
                        "-fx-background-radius: 6;" +
                        "-fx-padding: 4 10;" +
                        "-fx-cursor: hand;"
                );
                detailBtn.setOnAction(e -> {
                    Order order = getTableView().getItems().get(getIndex());
                    tampilkanDetailPesanan(order);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(detailBtn);
                }
            }
        });
    }

    // Menampilkan popup detail pesanan
    private void tampilkanDetailPesanan(Order order) {
        if (order == null) return;
        List<OrderDetail> details = orderDAO.ambilDetailPesanan(order.getId());
        AlertUtil.tampilkanDetailPesanan(contentArea.getScene().getWindow(), order, details);
    }

    // Mengatur highlight menu sidebar
    private void aturHighlight(Button tombolAktif) {

        dashboardButton.setStyle(MENU_INACTIVE_STYLE);
        productsButton.setStyle(MENU_INACTIVE_STYLE);
        categoriesButton.setStyle(MENU_INACTIVE_STYLE);
        stockButton.setStyle(MENU_INACTIVE_STYLE);
        ordersButton.setStyle(MENU_INACTIVE_STYLE);
        if (profileButton != null) {
            profileButton.setStyle(MENU_INACTIVE_STYLE);
        }

        tombolAktif.setStyle(MENU_ACTIVE_STYLE);
    }

    // Menampilkan nama Admin
    private void tampilkanNamaAdmin() {
        adminNameLabel.setText("Admin");
    }

    // Mengambil data dari database
    private void muatDataDashboard() {

        totalProductsLabel.setText(
                String.valueOf(productDAO.tampilkanSemua().size())
        );

        totalCategoriesLabel.setText(
                String.valueOf(categoryDAO.tampilkanSemua().size())
        );

        List<Order> semuaOrder = orderDAO.tampilkanSemua();
        totalOrdersLabel.setText(
                String.valueOf(semuaOrder.size())
        );

        if (recentOrdersTable != null) {
            int limit = Math.min(semuaOrder.size(), 6);
            daftarRecentOrders.setAll(semuaOrder.subList(0, limit));
            recentOrdersTable.setItems(daftarRecentOrders);
        }
    }

    // Refresh data dashboard dan pesanan terbaru
    @FXML
    private void handleRefreshDashboard() {
        muatDataDashboard();
        ToastUtil.showSuccess(contentArea, "Data dashboard berhasil diperbarui!");
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

    // Menangani menu Profile
    @FXML
    private void handleProfile() {
        if (muatHalaman("/fxml/profile.fxml")) {
            aturHighlight(profileButton);
        }
    }

    public void perbaruiNamaAdmin() {
        tampilkanNamaAdmin();
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

                javafx.scene.Scene scene =
                        new javafx.scene.Scene(loginPage, 900, 600);

                ((Stage) window).setScene(scene);
                ((Stage) window).setTitle("CoffeeHub - Login");
                ((Stage) window).centerOnScreen();

            } catch (IOException e) {
                e.printStackTrace();
                tampilkanError("Gagal kembali ke halaman Login.");
            }
        }
    }
}
