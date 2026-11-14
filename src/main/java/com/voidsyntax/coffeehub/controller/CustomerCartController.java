package com.voidsyntax.coffeehub.controller;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

import com.voidsyntax.coffeehub.model.CartItem;
import com.voidsyntax.coffeehub.service.CartManager;
import com.voidsyntax.coffeehub.service.SessionManager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class CustomerCartController {

    @FXML
    private VBox cartItemsContainer;

    @FXML
    private Label totalLabel;

    @FXML
    private Label itemCountLabel;

    private final NumberFormat currencyFormat =
            NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));

    private int getCurrentUserId() {
        if (!SessionManager.isPelanggan()
                || SessionManager.getCurrentUser() == null) {
            throw new IllegalStateException(
                    "Sesi pengguna tidak valid. Silakan login kembali."
            );
        }

        return SessionManager.getCurrentUser().getId();
    }

    @FXML
    private void initialize() {
        tampilkanKeranjang();
    }

    private void tampilkanKeranjang() {
        cartItemsContainer.getChildren().clear();

        int userId = getCurrentUserId();

        if (CartManager.isEmpty(userId)) {
            Label emptyLabel = new Label("Keranjang belanja masih kosong.");
            emptyLabel.setStyle(
                    "-fx-font-size: 14px;" +
                    "-fx-text-fill: #8B7562;"
            );

            cartItemsContainer.getChildren().add(emptyLabel);
        } else {
            for (CartItem item : CartManager.getItems(userId)) {
                cartItemsContainer.getChildren().add(buatKartuItem(item));
            }
        }

        perbaruiRingkasan();
    }

    private VBox buatKartuItem(CartItem item) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(18));
        card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: #E8D8C8;" +
                "-fx-border-radius: 10;"
        );

        Label namaLabel = new Label(item.getProduct().getNama());
        namaLabel.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #493629;"
        );

        Label hargaLabel = new Label(
                "Harga: " + currencyFormat.format(item.getProduct().getHarga())
        );
        hargaLabel.setStyle("-fx-text-fill: #8B7562;");

        Label subtotalLabel = new Label(
                "Subtotal: " + currencyFormat.format(item.getSubtotal())
        );
        subtotalLabel.setStyle(
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #795548;"
        );

        Label quantityLabel = new Label(
                "Jumlah: " + item.getQuantity()
        );
        quantityLabel.setStyle("-fx-text-fill: #493629;");

        Button decreaseButton = new Button("-");
        Button increaseButton = new Button("+");
        Button removeButton = new Button("Remove");

        aturTampilanTombol(decreaseButton);
        aturTampilanTombol(increaseButton);

        removeButton.setStyle(
                "-fx-background-color: #A65D4E;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 6;" +
                "-fx-cursor: hand;"
        );

        decreaseButton.setOnAction(event -> {
            int quantity = item.getQuantity();

            if (quantity > 1) {
                CartManager.ubahJumlah(
                        getCurrentUserId(),
                        item.getProduct().getId(),
                        quantity - 1
                );

                tampilkanKeranjang();
            }
        });

        increaseButton.setOnAction(event -> {
            try {
                CartManager.ubahJumlah(
                        getCurrentUserId(),
                        item.getProduct().getId(),
                        item.getQuantity() + 1
                );

                tampilkanKeranjang();

            } catch (IllegalArgumentException e) {
                tampilkanPeringatan(e.getMessage());
            }
        });

        removeButton.setOnAction(event -> {
            CartManager.hapusProduk(
                    getCurrentUserId(),
                    item.getProduct().getId()
            );

            tampilkanKeranjang();
        });

        HBox quantityBox = new HBox(10);
        quantityBox.getChildren().addAll(
                decreaseButton,
                quantityLabel,
                increaseButton
        );

        HBox actionBox = new HBox(12);
        actionBox.getChildren().addAll(
                quantityBox,
                removeButton
        );

        card.getChildren().addAll(
                namaLabel,
                hargaLabel,
                actionBox,
                subtotalLabel
        );

        return card;
    }

    private void aturTampilanTombol(Button button) {
        button.setStyle(
                "-fx-background-color: #795548;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 6;" +
                "-fx-cursor: hand;" +
                "-fx-min-width: 35;"
        );
    }

    private void perbaruiRingkasan() {
        int userId = getCurrentUserId();

        itemCountLabel.setText(
                CartManager.hitungJumlahItem(userId) + " item"
        );

        totalLabel.setText(
                currencyFormat.format(CartManager.hitungTotal(userId))
        );
    }

    private void tampilkanPeringatan(String pesan) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Peringatan");
        alert.setHeaderText(null);
        alert.setContentText(pesan);
        alert.showAndWait();
    }

    @FXML
    private void handleCheckout() {
        int userId = getCurrentUserId();

        if (CartManager.isEmpty(userId)) {
            tampilkanPeringatan(
                    "Keranjang masih kosong. Tambahkan produk terlebih dahulu."
            );
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/customer-checkout.fxml")
            );

            Node checkoutPage = loader.load();

            Parent parent = cartItemsContainer.getParent();

            while (parent != null && !(parent instanceof StackPane)) {
                parent = parent.getParent();
            }

            if (parent instanceof StackPane contentArea) {
                contentArea.getChildren().setAll(checkoutPage);
            } else {
                tampilkanPeringatan(
                        "Area halaman Checkout tidak ditemukan."
                );
            }

        } catch (IOException e) {
            tampilkanPeringatan(
                    "Gagal membuka Checkout: " + e.getMessage()
            );
            e.printStackTrace();
        }
    }
}
