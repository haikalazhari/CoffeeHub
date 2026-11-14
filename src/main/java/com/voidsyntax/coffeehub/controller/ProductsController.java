package com.voidsyntax.coffeehub.controller;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import com.voidsyntax.coffeehub.model.Product;
import com.voidsyntax.coffeehub.repository.ProductDAO;

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
    }


        private void aturTombolEdit() {

        actionColumn.setCellFactory(column -> new TableCell<>() {

                private final Button editButton = new Button("Edit");
                private final Button deleteButton = new Button("Delete");

                {
                editButton.setStyle(
                        "-fx-background-color: #8B6048;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 5;"
                );

                deleteButton.setStyle(
                        "-fx-background-color: #A65345;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 5;"
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
                                new javafx.scene.layout.HBox(5);

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

        Alert confirmation = new Alert(
                Alert.AlertType.CONFIRMATION
        );

        confirmation.setTitle("Delete Product");
        confirmation.setHeaderText("Delete " + product.getNama() + "?");
        confirmation.setContentText(
                "Are you sure you want to delete this product?"
        );

        java.util.Optional<javafx.scene.control.ButtonType> result =
                confirmation.showAndWait();

        if (result.isPresent()
                && result.get() == javafx.scene.control.ButtonType.OK) {

                boolean berhasil = productDAO.hapus(product.getId());

                if (berhasil) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("CoffeeHub");
                alert.setHeaderText(null);
                alert.setContentText("Produk berhasil dihapus.");
                alert.showAndWait();

                muatDataProduk();

                } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("CoffeeHub");
                alert.setHeaderText(null);
                alert.setContentText(
                        "Gagal menghapus produk. Produk mungkin masih digunakan dalam pesanan."
                );
                alert.showAndWait();
                }
        }
        }

}
