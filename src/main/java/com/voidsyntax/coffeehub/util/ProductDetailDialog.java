package com.voidsyntax.coffeehub.util;

import java.text.NumberFormat;
import java.util.Locale;

import com.voidsyntax.coffeehub.controller.CustomerController;
import com.voidsyntax.coffeehub.model.Product;
import com.voidsyntax.coffeehub.model.User;
import com.voidsyntax.coffeehub.service.CartManager;
import com.voidsyntax.coffeehub.service.SessionManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

/**
 * Dialog Modal Detail Produk interaktif dengan tema CoffeeHub.
 * Menyediakan tombol "Tambah ke Keranjang" dan "Beli Sekarang".
 */
public class ProductDetailDialog {

    private static final NumberFormat currencyFormat =
            NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));

    public static void tampilkan(Window owner, Product product) {
        if (product == null) {
            return;
        }

        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        if (owner != null) {
            dialogStage.initOwner(owner);
        }
        dialogStage.initStyle(StageStyle.UNDECORATED);
        dialogStage.setResizable(false);

        try {
            dialogStage.getIcons().add(new Image(
                    ProductDetailDialog.class.getResourceAsStream("/images/coffeehub-logo.png")
            ));
        } catch (Exception ignored) {
        }

        VBox root = new VBox();
        root.setStyle(
                "-fx-background-color: #FAF4ED;" +
                "-fx-background-radius: 18;" +
                "-fx-border-color: #70452F;" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 18;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.30), 20, 0, 0, 8);"
        );
        root.setPrefWidth(480);
        root.setMaxWidth(480);

        // Header
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(18, 22, 18, 22));
        header.setStyle(
                "-fx-background-color: linear-gradient(to right, #56301F, #70452F);" +
                "-fx-background-radius: 16 16 0 0;"
        );

        Label iconHeader = new Label("☕");
        iconHeader.setStyle("-fx-font-size: 22px;");

        VBox titleBox = new VBox(2);
        Label titleLabel = new Label("Detail Produk");
        titleLabel.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: #FFF8ED;");
        Label subtitleLabel = new Label("Menu Pilihan CoffeeHub");
        subtitleLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #D9BDA4;");
        titleBox.getChildren().addAll(titleLabel, subtitleLabel);

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        Button closeBtn = new Button("✕");
        closeBtn.setStyle(
                "-fx-background-color: rgba(255,255,255,0.15);" +
                "-fx-text-fill: #FFF8ED;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 15;" +
                "-fx-min-width: 30; -fx-min-height: 30;" +
                "-fx-max-width: 30; -fx-max-height: 30;" +
                "-fx-cursor: hand;"
        );
        closeBtn.setOnAction(e -> dialogStage.close());

        header.getChildren().addAll(iconHeader, titleBox, headerSpacer, closeBtn);

        // Content
        VBox content = new VBox(14);
        content.setPadding(new Insets(20, 26, 22, 26));

        // Kategori Pill & Stok Badge
        HBox tagsBox = new HBox(10);
        tagsBox.setAlignment(Pos.CENTER_LEFT);

        String namaKategori = (product.getKategori() != null)
                ? product.getKategori().getNama()
                : "Menu Kopi";
        Label categoryTag = new Label("📁 " + namaKategori);
        categoryTag.setStyle(
                "-fx-background-color: #EFE6DC;" +
                "-fx-text-fill: #56301F;" +
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 12;" +
                "-fx-padding: 4 10 4 10;"
        );

        boolean isTersedia = product.isTersedia() && product.getStok() > 0;
        Label stockTag = new Label(isTersedia ? "✓ Tersedia (Stok: " + product.getStok() + ")" : "✕ Stok Habis");
        stockTag.setStyle(
                "-fx-background-color: " + (isTersedia ? "#E8F5E9;" : "#FFEBEE;") +
                "-fx-text-fill: " + (isTersedia ? "#2E7D32;" : "#C62828;") +
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 12;" +
                "-fx-padding: 4 10 4 10;"
        );
        tagsBox.getChildren().addAll(categoryTag, stockTag);

        // Nama Produk
        Label nameLabel = new Label(product.getNama());
        nameLabel.setWrapText(true);
        nameLabel.setStyle(
                "-fx-font-size: 22px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #493629;"
        );

        // Harga
        Label priceLabel = new Label(currencyFormat.format(product.getHarga()));
        priceLabel.setStyle(
                "-fx-font-size: 20px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #8A5A3D;"
        );

        // Deskripsi Card
        VBox descBox = new VBox(6);
        descBox.setPadding(new Insets(12, 14, 12, 14));
        descBox.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: #E8D8C8;" +
                "-fx-border-radius: 10;"
        );

        Label descTitle = new Label("Deskripsi");
        descTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #8B7562;");

        String deskripsiText = (product.getDeskripsi() != null && !product.getDeskripsi().isBlank())
                ? product.getDeskripsi()
                : "Nikmati racikan kopi spesial dengan biji kopi pilihan dan cita rasa istimewa khas CoffeeHub.";
        Label descContent = new Label(deskripsiText);
        descContent.setWrapText(true);
        descContent.setStyle("-fx-font-size: 13px; -fx-text-fill: #5E4A3D; -fx-line-spacing: 3;");
        descBox.getChildren().addAll(descTitle, descContent);

        // Pemilih Jumlah (Quantity Selector)
        HBox qtyRow = new HBox(12);
        qtyRow.setAlignment(Pos.CENTER_LEFT);
        qtyRow.setPadding(new Insets(6, 0, 6, 0));

        Label qtyTitle = new Label("Jumlah:");
        qtyTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #493629;");

        final int maxStock = product.getStok();
        final int[] quantity = {1};

        Button btnMinus = new Button("-");
        Label lblQty = new Label("1");
        Button btnPlus = new Button("+");

        String btnQtyStyle =
                "-fx-background-color: #EFE6DC;" +
                "-fx-text-fill: #56301F;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-min-width: 32; -fx-min-height: 32;" +
                "-fx-max-width: 32; -fx-max-height: 32;" +
                "-fx-background-radius: 6;" +
                "-fx-cursor: hand;";

        btnMinus.setStyle(btnQtyStyle);
        btnPlus.setStyle(btnQtyStyle);

        lblQty.setStyle(
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #493629;" +
                "-fx-min-width: 30;" +
                "-fx-alignment: CENTER;"
        );

        Region qtySpacer = new Region();
        HBox.setHgrow(qtySpacer, Priority.ALWAYS);

        Label subtotalLabel = new Label("Subtotal: " + currencyFormat.format(product.getHarga()));
        subtotalLabel.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #70452F;"
        );

        btnMinus.setOnAction(e -> {
            if (quantity[0] > 1) {
                quantity[0]--;
                lblQty.setText(String.valueOf(quantity[0]));
                subtotalLabel.setText("Subtotal: " + currencyFormat.format(product.getHarga().multiply(java.math.BigDecimal.valueOf(quantity[0]))));
            }
        });

        btnPlus.setOnAction(e -> {
            if (quantity[0] < maxStock) {
                quantity[0]++;
                lblQty.setText(String.valueOf(quantity[0]));
                subtotalLabel.setText("Subtotal: " + currencyFormat.format(product.getHarga().multiply(java.math.BigDecimal.valueOf(quantity[0]))));
            }
        });

        qtyRow.getChildren().addAll(qtyTitle, btnMinus, lblQty, btnPlus, qtySpacer, subtotalLabel);

        // Divider
        Region divider = new Region();
        divider.setPrefHeight(1);
        divider.setStyle("-fx-background-color: #E8D8C8;");

        // Action Buttons: Tambah ke Keranjang & Beli Sekarang
        HBox buttonBox = new HBox(12);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(6, 0, 0, 0));

        Button btnAddToCart = new Button("Tambah ke Keranjang");
        btnAddToCart.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btnAddToCart, Priority.ALWAYS);
        btnAddToCart.setPrefHeight(44);
        btnAddToCart.setStyle(
                "-fx-background-color: #FFF8ED;" +
                "-fx-text-fill: #70452F;" +
                "-fx-border-color: #C58B55;" +
                "-fx-border-width: 1.5;" +
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 10;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
        );

        Button btnBuyNow = new Button("Beli Sekarang");
        btnBuyNow.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btnBuyNow, Priority.ALWAYS);
        btnBuyNow.setPrefHeight(44);
        btnBuyNow.setStyle(
                "-fx-background-color: #70452F;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 10;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
        );

        if (!isTersedia) {
            btnAddToCart.setDisable(true);
            btnBuyNow.setDisable(true);
            btnAddToCart.setStyle(btnAddToCart.getStyle() + "-fx-opacity: 0.5; -fx-cursor: default;");
            btnBuyNow.setStyle(btnBuyNow.getStyle() + "-fx-opacity: 0.5; -fx-cursor: default;");
        }

        // Action: Tambah ke Keranjang
        btnAddToCart.setOnAction(e -> {
            User user = SessionManager.getCurrentUser();
            if (user == null) {
                AlertUtil.error(owner, "Gagal", "Sesi Berakhir", "Silakan login kembali.");
                dialogStage.close();
                return;
            }

            try {
                CartManager.tambahProduk(user.getId(), product, quantity[0]);
                dialogStage.close();
                if (CustomerController.getInstance() != null) {
                    CustomerController.getInstance().showToast(
                            product.getNama() + " (" + quantity[0] + " item) berhasil ditambahkan ke keranjang!"
                    );
                }
            } catch (IllegalArgumentException ex) {
                AlertUtil.peringatan(dialogStage, "Peringatan", "Gagal Menambahkan", ex.getMessage());
            }
        });

        // Action: Beli Sekarang
        btnBuyNow.setOnAction(e -> {
            User user = SessionManager.getCurrentUser();
            if (user == null) {
                AlertUtil.error(owner, "Gagal", "Sesi Berakhir", "Silakan login kembali.");
                dialogStage.close();
                return;
            }

            try {
                CartManager.tambahProduk(user.getId(), product, quantity[0]);
                dialogStage.close();

                if (CustomerController.getInstance() != null) {
                    CustomerController.getInstance().bukaHalamanCart();
                    CustomerController.getInstance().showToast(
                            "Produk berhasil ditambahkan. Silakan isi catatan dan lanjutkan checkout!"
                    );
                }
            } catch (IllegalArgumentException ex) {
                AlertUtil.peringatan(dialogStage, "Peringatan", "Gagal Menambahkan", ex.getMessage());
            }
        });

        buttonBox.getChildren().addAll(btnAddToCart, btnBuyNow);

        content.getChildren().addAll(
                tagsBox,
                nameLabel,
                priceLabel,
                descBox,
                qtyRow,
                divider,
                buttonBox
        );

        root.getChildren().addAll(header, content);

        Scene scene = new Scene(root);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        dialogStage.setScene(scene);

        // Center on parent
        if (owner != null) {
            dialogStage.setOnShown(ev -> {
                dialogStage.setX(owner.getX() + (owner.getWidth() - root.getPrefWidth()) / 2);
                dialogStage.setY(owner.getY() + (owner.getHeight() - root.getHeight()) / 2);
            });
        }

        dialogStage.showAndWait();
    }
}
