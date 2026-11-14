package com.voidsyntax.coffeehub.controller;

import java.util.List;
import java.util.Optional;

import com.voidsyntax.coffeehub.model.Product;
import com.voidsyntax.coffeehub.repository.ProductDAO;

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
    }

    // Membuat tombol Update Stock
    private void aturTombolUpdate() {

        actionColumn.setCellFactory(column -> new TableCell<>() {

            private final Button updateButton =
                    new Button("Update");

            {
                updateButton.setStyle(
                        "-fx-background-color: #8B6048;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 5;"
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

        Dialog<Integer> dialog = new Dialog<>();

        dialog.setTitle("Update Stock");
        dialog.setHeaderText(
                "Update stock for " + product.getNama()
        );

        ButtonType saveButton = new ButtonType(
                "Save",
                ButtonBar.ButtonData.OK_DONE
        );

        dialog.getDialogPane().getButtonTypes().addAll(
                saveButton,
                ButtonType.CANCEL
        );

        TextField stockField = new TextField(
                String.valueOf(product.getStok())
        );

        stockField.setPromptText("Enter new stock");

        HBox content = new HBox(10);
        content.getChildren().addAll(
                new Label("New Stock:"),
                stockField
        );

        dialog.getDialogPane().setContent(content);

        Button save = (Button) dialog.getDialogPane()
                .lookupButton(saveButton);

        save.addEventFilter(
                javafx.event.ActionEvent.ACTION,
                event -> {

                    try {
                        int stok = Integer.parseInt(
                                stockField.getText().trim()
                        );

                        if (stok < 0) {
                            event.consume();
                            tampilkanPeringatan(
                                    "Stok tidak boleh negatif."
                            );
                        }

                    } catch (NumberFormatException e) {
                        event.consume();
                        tampilkanPeringatan(
                                "Stok harus berupa bilangan bulat."
                        );
                    }
                }
        );

        dialog.setResultConverter(button -> {

            if (button == saveButton) {
                return Integer.parseInt(
                        stockField.getText().trim()
                );
            }

            return null;
        });

        Optional<Integer> result = dialog.showAndWait();

        result.ifPresent(stokBaru -> {

            product.setStok(stokBaru);

            boolean berhasil = productDAO.ubah(product);

            if (berhasil) {
                tampilkanInformasi(
                        "Stok produk berhasil diperbarui."
                );

                muatDataStok();

            } else {
                tampilkanError(
                        "Gagal memperbarui stok produk."
                );
            }
        });
    }

    private void tampilkanPeringatan(String pesan) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Stock Validation");
        alert.setHeaderText(null);
        alert.setContentText(pesan);
        alert.showAndWait();
    }

    private void tampilkanInformasi(String pesan) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("CoffeeHub");
        alert.setHeaderText(null);
        alert.setContentText(pesan);
        alert.showAndWait();
    }

    private void tampilkanError(String pesan) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("CoffeeHub");
        alert.setHeaderText(null);
        alert.setContentText(pesan);
        alert.showAndWait();
    }
}
