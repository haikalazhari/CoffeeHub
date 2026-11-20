package com.voidsyntax.coffeehub.controller;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import com.voidsyntax.coffeehub.model.Product;
import com.voidsyntax.coffeehub.repository.ProductDAO;
import com.voidsyntax.coffeehub.util.AlertUtil;
import com.voidsyntax.coffeehub.util.ToastUtil;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ProductsController {

    @FXML
    private TableView<Product> productTable;

    @FXML
    private TableColumn<Product, Integer> idColumn;

    @FXML
    private TableColumn<Product, String> nameColumn;

    @FXML
    private TableColumn<Product, String> categoryColumn;

    @FXML
    private TableColumn<Product, String> priceColumn;

    @FXML
    private TableColumn<Product, Integer> stockColumn;

    @FXML
    private TableColumn<Product, String> availabilityColumn;

    @FXML
    private TableColumn<Product, Void> actionColumn;

    private final ProductDAO productDAO = new ProductDAO();

    private final ObservableList<Product> daftarProduk =
            FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        aturKolom();
        aturTombolEdit();
        muatDataProduk();
    }

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

            String namaKategori = product.getKategori() != null
                    ? product.getKategori().getNama()
                    : "Tanpa Kategori";

            return new SimpleStringProperty(namaKategori);
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

        priceColumn.setCellValueFactory(data -> {
            NumberFormat format =
                    NumberFormat.getCurrencyInstance(
                            Locale.forLanguageTag("id-ID")
                    );

            format.setMaximumFractionDigits(0);

            return new SimpleStringProperty(
                    format.format(data.getValue().getHarga())
            );
        });

        stockColumn.setCellValueFactory(data ->
                new SimpleIntegerProperty(
                        data.getValue().getStok()
                ).asObject()
        );

        availabilityColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().isTersedia()
                                ? "Available"
                                : "Unavailable"
                )
        );

        availabilityColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Label badge = new Label(item.equals("Available") ? "✓ Available" : "✕ Unavailable");
                    if (item.equals("Available")) {
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

        priceColumn.setCellFactory(col -> new TableCell<>() {
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
    }

    private void aturTombolEdit() {

        actionColumn.setCellFactory(column -> new TableCell<>() {

            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");

            {
                editButton.setStyle(
                        "-fx-background-color: #70452F;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 12px;" +
                        "-fx-background-radius: 6;" +
                        "-fx-padding: 5 12;" +
                        "-fx-cursor: hand;"
                );

                deleteButton.setStyle(
                        "-fx-background-color: #A64B3B;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 12px;" +
                        "-fx-background-radius: 6;" +
                        "-fx-padding: 5 12;" +
                        "-fx-cursor: hand;"
                );

                editButton.setOnAction(event -> {
                    Product product = getTableView()
                            .getItems()
                            .get(getIndex());

                    bukaFormEdit(product);
                });

                deleteButton.setOnAction(event -> {
                    Product product = getTableView()
                            .getItems()
                            .get(getIndex());

                    handleDeleteProduct(product);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    javafx.scene.layout.HBox buttons =
                            new javafx.scene.layout.HBox(8);
                    buttons.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

                    buttons.getChildren().addAll(
                            editButton,
                            deleteButton
                    );

                    setGraphic(buttons);
                }
            }
        });
    }

    private void muatDataProduk() {

        List<Product> produk = productDAO.tampilkanSemua();

        daftarProduk.setAll(produk);

        productTable.setItems(daftarProduk);
    }

    @FXML
    private void handleAddProduct() {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/add-product.fxml")
            );

            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Add Product");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();

            muatDataProduk();

        } catch (IOException e) {
            System.err.println("Gagal membuka form Add Product:");
            e.printStackTrace();
        }
    }

    private void bukaFormEdit(Product product) {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/edit-product.fxml")
            );

            Parent root = loader.load();

            EditProductController controller = loader.getController();
            controller.setProduct(product);

            Stage stage = new Stage();
            stage.setTitle("Edit Product");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();

            muatDataProduk();

        } catch (IOException e) {
            System.err.println("Gagal membuka form Edit Product:");
            e.printStackTrace();
        }
    }

    private void handleDeleteProduct(Product product) {

        boolean setuju = AlertUtil.konfirmasi(
                productTable.getScene().getWindow(),
                "Hapus Produk",
                "Hapus " + product.getNama() + "?",
                "Apakah Anda yakin ingin menghapus produk ini?",
                "Hapus",
                "Batal"
        );

        if (setuju) {
            boolean berhasil = productDAO.hapus(product.getId());

            if (berhasil) {
                ToastUtil.showSuccess(productTable, "Produk " + product.getNama() + " berhasil dihapus!");
                muatDataProduk();
            } else {
                ToastUtil.showWarning(
                        productTable,
                        "Gagal menghapus produk. Produk mungkin masih tercatat dalam pesanan."
                );
            }
        }
    }

}
