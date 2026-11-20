package com.voidsyntax.coffeehub.controller;

import java.util.List;
import java.util.Optional;

import com.voidsyntax.coffeehub.model.Product;
import com.voidsyntax.coffeehub.repository.ProductDAO;
import com.voidsyntax.coffeehub.util.AlertUtil;
import com.voidsyntax.coffeehub.util.ToastUtil;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class StockController {

    @FXML
    private Label totalProductsLabel;

    @FXML
    private Label lowStockLabel;

    @FXML
    private Label outOfStockLabel;

    @FXML
    private TableView<Product> stockTable;

    @FXML
    private TableColumn<Product, Integer> idColumn;

    @FXML
    private TableColumn<Product, String> nameColumn;

    @FXML
    private TableColumn<Product, String> categoryColumn;

    @FXML
    private TableColumn<Product, Integer> stockColumn;

    @FXML
    private TableColumn<Product, String> statusColumn;

    @FXML
    private TableColumn<Product, Void> actionColumn;

    private final ProductDAO productDAO = new ProductDAO();

    private final ObservableList<Product> daftarProduk =
            FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        aturKolom();
        aturTombolUpdate();
        muatDataStok();
    }

    // Mengatur kolom tabel
    private void aturKolom() {

        idColumn.setCellValueFactory(data ->
                new SimpleIntegerProperty(
                        data.getValue().getId()
                ).asObject()
        );

        nameColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getNama()
                )
        );

        categoryColumn.setCellValueFactory(data -> {
            Product product = data.getValue();

            String kategori = product.getKategori() != null
                    ? product.getKategori().getNama()
                    : "Tanpa Kategori";

            return new SimpleStringProperty(kategori);
        });

        categoryColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Label badge = new Label();
                    if ("Coffee".equalsIgnoreCase(item)) {
                        badge.setText("☕ " + item);
                        badge.setStyle(
                                "-fx-background-color: #FAF4ED; -fx-text-fill: #56301F; -fx-font-weight: bold; " +
                                "-fx-font-size: 11px; -fx-padding: 3 9; -fx-background-radius: 8; -fx-border-color: #E8D8C8; -fx-border-radius: 8;"
                        );
                    } else if ("Non-Coffee".equalsIgnoreCase(item)) {
                        badge.setText("🍵 " + item);
                        badge.setStyle(
                                "-fx-background-color: #ECFDF5; -fx-text-fill: #047857; -fx-font-weight: bold; " +
                                "-fx-font-size: 11px; -fx-padding: 3 9; -fx-background-radius: 8; -fx-border-color: #A7F3D0; -fx-border-radius: 8;"
                        );
                    } else if (item.contains("Pastry") || item.contains("Bakery")) {
                        badge.setText("🥐 " + item);
                        badge.setStyle(
                                "-fx-background-color: #FFF7ED; -fx-text-fill: #C2410C; -fx-font-weight: bold; " +
                                "-fx-font-size: 11px; -fx-padding: 3 9; -fx-background-radius: 8; -fx-border-color: #FED7AA; -fx-border-radius: 8;"
                        );
                    } else {
                        badge.setText(item);
                        badge.setStyle(
                                "-fx-background-color: #F5EBE1; -fx-text-fill: #70452F; -fx-font-weight: bold; " +
                                "-fx-font-size: 11px; -fx-padding: 3 9; -fx-background-radius: 8;"
                        );
                    }
                    setGraphic(badge);
                    setText(null);
                }
            }
        });

        stockColumn.setCellValueFactory(data ->
                new SimpleIntegerProperty(
                        data.getValue().getStok()
                ).asObject()
        );

        statusColumn.setCellValueFactory(data -> {
            int stok = data.getValue().getStok();

            String status;

            if (stok == 0) {
                status = "Out of Stock";
            } else if (stok <= 10) {
                status = "Low Stock";
            } else {
                status = "In Stock";
            }

            return new SimpleStringProperty(status);
        });

        statusColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Label badge = new Label();
                    if ("In Stock".equals(item)) {
                        badge.setText("✓ In Stock");
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
                    } else if ("Low Stock".equals(item)) {
                        badge.setText("⚠️ Low Stock");
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
                    } else {
                        badge.setText("✕ Out of Stock");
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
                    setGraphic(badge);
                    setText(null);
                }
            }
        });
    }

    // Membuat tombol Update Stock
    private void aturTombolUpdate() {

        actionColumn.setCellFactory(column -> new TableCell<>() {

            private final Button updateButton =
                    new Button("Update");

            {
                updateButton.setStyle(
                        "-fx-background-color: #70452F;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 12px;" +
                        "-fx-background-radius: 6;" +
                        "-fx-padding: 5 14;" +
                        "-fx-cursor: hand;"
                );

                updateButton.setOnAction(event -> {
                    Product product = getTableView()
                            .getItems()
                            .get(getIndex());

                    handleUpdateStock(product);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                setGraphic(empty ? null : updateButton);
            }
        });
    }

    // Memuat data stok dari database
    private void muatDataStok() {

        List<Product> produk = productDAO.tampilkanSemua();

        daftarProduk.setAll(produk);
        stockTable.setItems(daftarProduk);

        hitungRingkasanStok(produk);
    }

    // Menghitung ringkasan stok
    private void hitungRingkasanStok(List<Product> produk) {

        int totalProduk = produk.size();
        int stokRendah = 0;
        int stokHabis = 0;

        for (Product product : produk) {

            int stok = product.getStok();

            if (stok == 0) {
                stokHabis++;
            } else if (stok <= 10) {
                stokRendah++;
            }
        }

        totalProductsLabel.setText(
                String.valueOf(totalProduk)
        );

        lowStockLabel.setText(
                String.valueOf(stokRendah)
        );

        outOfStockLabel.setText(
                String.valueOf(stokHabis)
        );
    }

    // Memperbarui stok produk
    private void handleUpdateStock(Product product) {
        Integer stokBaru = AlertUtil.inputStok(
                stockTable.getScene().getWindow(),
                product.getNama(),
                product.getStok()
        );

        if (stokBaru == null || stokBaru == product.getStok()) {
            return;
        }

        product.setStok(stokBaru);
        boolean berhasil = productDAO.ubah(product);

        if (berhasil) {
            ToastUtil.showSuccess(
                    stockTable,
                    "Stok untuk " + product.getNama() + " berhasil diubah menjadi " + stokBaru + "!"
            );
            muatDataStok();
        } else {
            ToastUtil.showWarning(
                    stockTable,
                    "Gagal memperbarui stok produk."
            );
        }
    }

    private void tampilkanPeringatan(String pesan) {
        ToastUtil.showWarning(stockTable, pesan);
    }

    private void tampilkanInformasi(String pesan) {
        ToastUtil.showInfo(stockTable, pesan);
    }

    private void tampilkanError(String pesan) {
        ToastUtil.showWarning(stockTable, pesan);
    }
}
