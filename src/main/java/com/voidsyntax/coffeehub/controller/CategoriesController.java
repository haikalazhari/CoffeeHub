package com.voidsyntax.coffeehub.controller;

import java.util.Optional;

import com.voidsyntax.coffeehub.model.Category;
import com.voidsyntax.coffeehub.repository.CategoryDAO;

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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class CategoriesController {

    @FXML
    private TableView<Category> categoryTable;

    @FXML
    private TableColumn<Category, Integer> idColumn;

    @FXML
    private TableColumn<Category, String> nameColumn;

    @FXML
    private TableColumn<Category, String> descriptionColumn;

    @FXML
    private TableColumn<Category, Void> actionColumn;

    private final CategoryDAO categoryDAO = new CategoryDAO();

    private final ObservableList<Category> daftarKategori =
            FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        aturKolom();
        aturTombolAksi();
        muatDataKategori();
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

        descriptionColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getDeskripsi()
                )
        );
    }

    // Membuat tombol Edit dan Delete
    private void aturTombolAksi() {

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
                    Category category = getTableView()
                            .getItems()
                            .get(getIndex());

                    handleEditCategory(category);
                });

                deleteButton.setOnAction(event -> {
                    Category category = getTableView()
                            .getItems()
                            .get(getIndex());

                    handleDeleteCategory(category);
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

    // Mengambil data kategori dari database
    private void muatDataKategori() {

        daftarKategori.setAll(
                categoryDAO.tampilkanSemua()
        );

        categoryTable.setItems(daftarKategori);
    }

    // Menambahkan kategori
    @FXML
    private void handleAddCategory() {

        Dialog<Category> dialog = buatDialogKategori(
                "Add Category",
                "Add New Category",
                null
        );

        Optional<Category> result = dialog.showAndWait();

        result.ifPresent(category -> {
            boolean berhasil = categoryDAO.tambah(category);

            if (berhasil) {
                tampilkanInformasi("Kategori berhasil ditambahkan.");
                muatDataKategori();
            } else {
                tampilkanError("Gagal menambahkan kategori.");
            }
        });
    }

    // Mengedit kategori
    private void handleEditCategory(Category category) {

        Dialog<Category> dialog = buatDialogKategori(
                "Edit Category",
                "Edit Category",
                category
        );

        Optional<Category> result = dialog.showAndWait();

        result.ifPresent(kategoriBaru -> {

            category.setNama(kategoriBaru.getNama());
            category.setDeskripsi(kategoriBaru.getDeskripsi());

            boolean berhasil = categoryDAO.ubah(category);

            if (berhasil) {
                tampilkanInformasi("Kategori berhasil diperbarui.");
                muatDataKategori();
            } else {
                tampilkanError("Gagal memperbarui kategori.");
            }
        });
    }

    // Membuat dialog tambah dan edit
    private Dialog<Category> buatDialogKategori(
            String judul,
            String header,
            Category category) {

        Dialog<Category> dialog = new Dialog<>();

        dialog.setTitle(judul);
        dialog.setHeaderText(header);

        ButtonType saveButton = new ButtonType(
                "Save",
                ButtonBar.ButtonData.OK_DONE
        );

        dialog.getDialogPane().getButtonTypes().addAll(
                saveButton,
                ButtonType.CANCEL
        );

        TextField nameField = new TextField();
        nameField.setPromptText("Enter category name");

        TextArea descriptionField = new TextArea();
        descriptionField.setPromptText("Enter category description");
        descriptionField.setPrefRowCount(3);
        descriptionField.setWrapText(true);

        if (category != null) {
            nameField.setText(category.getNama());
            descriptionField.setText(category.getDeskripsi());
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);

        grid.add(new Label("Category Name:"), 0, 0);
        grid.add(nameField, 1, 0);

        grid.add(new Label("Description:"), 0, 1);
        grid.add(descriptionField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        Button save = (Button) dialog.getDialogPane()
                .lookupButton(saveButton);

        save.addEventFilter(
                javafx.event.ActionEvent.ACTION,
                event -> {
                    if (nameField.getText().trim().isEmpty()) {
                        event.consume();
                        tampilkanPeringatan(
                                "Nama kategori wajib diisi."
                        );
                    }
                }
        );

        dialog.setResultConverter(button -> {

            if (button == saveButton) {
                return new Category(
                        category != null ? category.getId() : 0,
                        nameField.getText().trim(),
                        descriptionField.getText().trim()
                );
            }

            return null;
        });

        return dialog;
    }

    // Menghapus kategori
    private void handleDeleteCategory(Category category) {

        Alert confirmation = new Alert(
                Alert.AlertType.CONFIRMATION
        );

        confirmation.setTitle("Delete Category");
        confirmation.setHeaderText(
                "Delete " + category.getNama() + "?"
        );

        confirmation.setContentText(
                "Are you sure you want to delete this category?"
        );

        Optional<ButtonType> result = confirmation.showAndWait();

        if (result.isPresent()
                && result.get() == ButtonType.OK) {

            boolean berhasil = categoryDAO.hapus(
                    category.getId()
            );

            if (berhasil) {
                tampilkanInformasi("Kategori berhasil dihapus.");
                muatDataKategori();
            } else {
                tampilkanError("Gagal menghapus kategori.");
            }
        }
    }

    private void tampilkanPeringatan(String pesan) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Category Validation");
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
