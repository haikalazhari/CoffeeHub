package com.voidsyntax.coffeehub.controller;

import java.util.Optional;

import com.voidsyntax.coffeehub.model.Category;
import com.voidsyntax.coffeehub.repository.CategoryDAO;
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

        boolean setuju = AlertUtil.konfirmasi(
                categoryTable.getScene().getWindow(),
                "Hapus Kategori",
                "Hapus Kategori " + category.getNama() + "?",
                "Apakah Anda yakin ingin menghapus kategori ini?",
                "Hapus",
                "Batal"
        );

        if (setuju) {
            boolean berhasil = categoryDAO.hapus(
                    category.getId()
            );

            if (berhasil) {
                ToastUtil.showSuccess(categoryTable, "Kategori " + category.getNama() + " berhasil dihapus!");
                muatDataKategori();
            } else {
                ToastUtil.showWarning(categoryTable, "Gagal menghapus kategori.");
            }
        }
    }

    private void tampilkanPeringatan(String pesan) {
        ToastUtil.showWarning(categoryTable, pesan);
    }

    private void tampilkanInformasi(String pesan) {
        ToastUtil.showSuccess(categoryTable, pesan);
    }

    private void tampilkanError(String pesan) {
        ToastUtil.showWarning(categoryTable, pesan);
    }
}
