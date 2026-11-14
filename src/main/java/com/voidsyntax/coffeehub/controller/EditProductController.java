package com.voidsyntax.coffeehub.controller;

import java.math.BigDecimal;

import com.voidsyntax.coffeehub.model.Category;
import com.voidsyntax.coffeehub.model.Product;
import com.voidsyntax.coffeehub.repository.CategoryDAO;
import com.voidsyntax.coffeehub.repository.ProductDAO;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EditProductController {

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

    private final ProductDAO productDAO = new ProductDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    private Product product;

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

        categoryComboBox.setCellFactory(list ->
                new ListCell<>() {
                    @Override
                    protected void updateItem(Category category, boolean empty) {
                        super.updateItem(category, empty);

                        setText(empty || category == null
                                ? null
                                : category.getNama());
                    }
                }
        );

        categoryComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Category category, boolean empty) {
                super.updateItem(category, empty);

                setText(empty || category == null
                        ? null
                        : category.getNama());
            }
        });
    }

    // Menerima produk yang dipilih dari tabel
    public void setProduct(Product product) {
        this.product = product;

        nameField.setText(product.getNama());
        descriptionField.setText(product.getDeskripsi());
        priceField.setText(product.getHarga().toPlainString());
        stockField.setText(String.valueOf(product.getStok()));
        imageField.setText(
                product.getGambar() != null
                        ? product.getGambar()
                        : ""
        );

        availabilityCheckBox.setSelected(
                product.isTersedia()
        );

        // Memilih kategori produk saat ini
        if (product.getKategori() != null) {
            for (Category category : categoryComboBox.getItems()) {
                if (category.getId() == product.getKategori().getId()) {
                    categoryComboBox.setValue(category);
                    break;
                }
            }
        }
    }

    @FXML
    private void handleSave() {

        if (product == null) {
            tampilkanError("Data produk tidak ditemukan.");
            return;
        }

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
                    "Harga harus berupa angka dan stok harus bilangan bulat."
            );
            return;
        }

        product.setNama(nama);
        product.setDeskripsi(deskripsi);
        product.setHarga(harga);
        product.setStok(stok);
        product.setGambar(gambar.isEmpty() ? null : gambar);
        product.setTersedia(availabilityCheckBox.isSelected());
        product.setKategori(categoryComboBox.getValue());

        boolean berhasil = productDAO.ubah(product);

        if (berhasil) {
            tampilkanInformasi("Produk berhasil diperbarui.");
            tutupForm();
        } else {
            tampilkanError("Gagal memperbarui produk.");
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
