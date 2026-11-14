package com.voidsyntax.coffeehub.controller;

import java.math.BigDecimal;

import com.voidsyntax.coffeehub.model.Category;
import com.voidsyntax.coffeehub.model.Product;
import com.voidsyntax.coffeehub.repository.CategoryDAO;
import com.voidsyntax.coffeehub.repository.ProductDAO;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddProductController {

    @FXML
    private TextField nameField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private ComboBox<Category> categoryComboBox;

    @FXML
    private TextField priceField;

    @FXML
    private TextField stockField;

    @FXML
    private TextField imageField;

    @FXML
    private CheckBox availabilityCheckBox;

    @FXML
    private Button saveButton;

    private final ProductDAO productDAO = new ProductDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    @FXML
    public void initialize() {
        muatKategori();
    }

    private void muatKategori() {
        categoryComboBox.setItems(
                FXCollections.observableArrayList(
                        categoryDAO.tampilkanSemua()
                )
        );

        categoryComboBox.setCellFactory(list -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(Category category, boolean empty) {
                super.updateItem(category, empty);

                setText(empty || category == null
                        ? null
                        : category.getNama());
            }
        });

        categoryComboBox.setButtonCell(new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(Category category, boolean empty) {
                super.updateItem(category, empty);

                setText(empty || category == null
                        ? null
                        : category.getNama());
            }
        });
    }

    @FXML
    private void handleSave() {

        String nama = nameField.getText().trim();
        String deskripsi = descriptionField.getText().trim();
        String hargaText = priceField.getText().trim();
        String stokText = stockField.getText().trim();
        String gambar = imageField.getText().trim();

        if (nama.isEmpty()) {
            tampilkanPeringatan("Nama produk wajib diisi.");
            return;
        }

        if (hargaText.isEmpty() || stokText.isEmpty()) {
            tampilkanPeringatan("Harga dan stok wajib diisi.");
            return;
        }

        BigDecimal harga;
        int stok;

        try {
            harga = new BigDecimal(hargaText);

            if (harga.compareTo(BigDecimal.ZERO) <= 0) {
                tampilkanPeringatan("Harga harus lebih dari 0.");
                return;
            }

            stok = Integer.parseInt(stokText);

            if (stok < 0) {
                tampilkanPeringatan("Stok tidak boleh negatif.");
                return;
            }

        } catch (NumberFormatException e) {
            tampilkanPeringatan(
                    "Harga harus berupa angka dan stok harus berupa bilangan bulat."
            );
            return;
        }

        Category kategori = categoryComboBox.getValue();

        Product product = new Product(
                0,
                nama,
                deskripsi,
                harga,
                stok,
                gambar.isEmpty() ? null : gambar,
                availabilityCheckBox.isSelected(),
                kategori
        );

        boolean berhasil = productDAO.tambah(product);

        if (berhasil) {
            tampilkanInformasi("Produk berhasil ditambahkan.");
            tutupForm();
        } else {
            tampilkanError("Gagal menambahkan produk. Periksa koneksi database.");
        }
    }

    @FXML
    private void handleCancel() {
        tutupForm();
    }

    private void tutupForm() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }

    private void tampilkanPeringatan(String pesan) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Validasi Produk");
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
