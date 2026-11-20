package com.voidsyntax.coffeehub.util;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import com.voidsyntax.coffeehub.enums.OrderStatus;
import com.voidsyntax.coffeehub.model.Order;
import com.voidsyntax.coffeehub.model.OrderDetail;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * Utility dialog modal kustom berdesain elegan, modern,
 * dan selaras dengan tema warna CoffeeHub (bebas dari pop-up bawaan Windows/JavaFX).
 */
public class AlertUtil {

    public enum Type {
        CONFIRM,
        SUCCESS,
        WARNING,
        ERROR
    }

    public static boolean konfirmasi(Window owner, String judul, String header, String pesan) {
        return konfirmasi(owner, judul, header, pesan, "Lanjutkan", "Batal");
    }

    public static boolean konfirmasi(Window owner, String judul, String header, String pesan, String teksYa, String teksBatal) {
        return tampilkanDialog(owner, judul, header, pesan, Type.CONFIRM, teksYa, teksBatal);
    }

    public static void sukses(Window owner, String judul, String header, String pesan) {
        tampilkanDialog(owner, judul, header, pesan, Type.SUCCESS, "OK", null);
    }

    public static void peringatan(Window owner, String judul, String header, String pesan) {
        tampilkanDialog(owner, judul, header, pesan, Type.WARNING, "OK", null);
    }

    public static void error(Window owner, String judul, String header, String pesan) {
        tampilkanDialog(owner, judul, header, pesan, Type.ERROR, "OK", null);
    }

    /**
     * Dialog kustom untuk update status pesanan yang rapi, modern, dan selaras dengan desain CoffeeHub.
     */
    public static OrderStatus pilihStatusPesanan(Window owner, String kodeOrder, OrderStatus statusSaatIni) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        if (owner != null) {
            dialogStage.initOwner(owner);
        }
        dialogStage.setTitle("Update Order Status - CoffeeHub");

        try {
            Image icon = new Image(AlertUtil.class.getResourceAsStream("/images/coffeehub-logo.png"));
            dialogStage.getIcons().add(icon);
        } catch (Exception ignored) {
        }

        final OrderStatus[] result = {null};

        // Header dialog
        HBox headerBox = new HBox(14);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setPadding(new Insets(18, 24, 18, 24));
        headerBox.setStyle("-fx-background-color: linear-gradient(to right, #56301F, #3F241A);");

        StackPane iconCircle = new StackPane();
        iconCircle.setPrefSize(38, 38);
        iconCircle.setMaxSize(38, 38);
        iconCircle.setMinSize(38, 38);
        iconCircle.setStyle(
                "-fx-background-color: #70452F;"
                + "-fx-background-radius: 19;"
                + "-fx-border-color: #C58B55;"
                + "-fx-border-radius: 19;"
                + "-fx-border-width: 1.5;"
        );

        Label iconLabel = new Label("📦");
        iconLabel.setStyle("-fx-font-size: 17px;");
        iconCircle.getChildren().add(iconLabel);

        VBox titleBox = new VBox(3);
        Label headerLabel = new Label("Update Order Status");
        headerLabel.setStyle("-fx-text-fill: #FFF8ED; -fx-font-size: 16px; -fx-font-weight: bold;");
        Label subLabel = new Label("Perbarui status untuk pesanan " + kodeOrder);
        subLabel.setStyle("-fx-text-fill: #D9BDA4; -fx-font-size: 13px;");
        titleBox.getChildren().addAll(headerLabel, subLabel);

        headerBox.getChildren().addAll(iconCircle, titleBox);

        // Content dialog
        VBox contentBox = new VBox(14);
        contentBox.setPadding(new Insets(24, 26, 22, 26));
        contentBox.setStyle("-fx-background-color: #FFFEFD;");

        HBox currentBox = new HBox(8);
        currentBox.setAlignment(Pos.CENTER_LEFT);
        Label currentLbl = new Label("Status saat ini:");
        currentLbl.setStyle("-fx-text-fill: #8B705D; -fx-font-size: 13px;");
        Label badge = new Label(statusSaatIni != null ? statusSaatIni.name() : "-");
        badge.setStyle(
                "-fx-background-color: #F5EBE1; -fx-text-fill: #56301F; -fx-font-weight: bold; "
                + "-fx-font-size: 12px; -fx-padding: 3 10; -fx-background-radius: 10; -fx-border-color: #E8D8C8; -fx-border-radius: 10;"
        );
        currentBox.getChildren().addAll(currentLbl, badge);

        Label selectLbl = new Label("Pilih status pesanan baru:");
        selectLbl.setStyle("-fx-text-fill: #56301F; -fx-font-size: 13px; -fx-font-weight: bold;");

        ComboBox<OrderStatus> comboBox = new ComboBox<>();
        comboBox.getItems().addAll(OrderStatus.values());
        comboBox.setValue(statusSaatIni);
        comboBox.setMaxWidth(Double.MAX_VALUE);
        comboBox.setStyle(
                "-fx-background-color: white; -fx-border-color: #D8C7B8; -fx-border-radius: 8; "
                + "-fx-background-radius: 8; -fx-font-size: 13px; -fx-padding: 6 12; -fx-cursor: hand;"
        );

        javafx.util.StringConverter<OrderStatus> converter = new javafx.util.StringConverter<>() {
            @Override
            public String toString(OrderStatus status) {
                if (status == null) return "";
                return switch (status) {
                    case MENUNGGU -> "🕒 Menunggu (Belum Diproses)";
                    case DIPROSES -> "⚙️ Diproses (Sedang Disiapkan)";
                    case SIAP -> "📦 Siap (Siap Diambil/Diantar)";
                    case SELESAI -> "✓ Selesai (Pesanan Selesai)";
                    case DIBATALKAN -> "✕ Dibatalkan";
                };
            }

            @Override
            public OrderStatus fromString(String string) {
                return null;
            }
        };
        comboBox.setConverter(converter);

        contentBox.getChildren().addAll(currentBox, selectLbl, comboBox);

        // Footer / Button bar
        HBox buttonBar = new HBox(12);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);
        buttonBar.setPadding(new Insets(14, 24, 18, 24));
        buttonBar.setStyle(
                "-fx-background-color: #F8F3ED;"
                + "-fx-border-color: #EFE6DC transparent transparent transparent;"
                + "-fx-border-width: 1;"
        );

        Button secondaryButton = new Button("Batal");
        secondaryButton.setStyle(
                "-fx-background-color: white;"
                + "-fx-text-fill: #6D5A4D;"
                + "-fx-font-weight: bold;"
                + "-fx-font-size: 13px;"
                + "-fx-background-radius: 8;"
                + "-fx-border-color: #D8C7B8;"
                + "-fx-border-radius: 8;"
                + "-fx-padding: 8 20;"
                + "-fx-cursor: hand;"
        );
        secondaryButton.setOnAction(e -> {
            result[0] = null;
            dialogStage.close();
        });

        Button primaryButton = new Button("Simpan Status");
        primaryButton.setStyle(
                "-fx-background-color: #70452F;"
                + "-fx-text-fill: white;"
                + "-fx-font-weight: bold;"
                + "-fx-font-size: 13px;"
                + "-fx-background-radius: 8;"
                + "-fx-padding: 8 22;"
                + "-fx-cursor: hand;"
        );
        primaryButton.setOnAction(e -> {
            result[0] = comboBox.getValue();
            dialogStage.close();
        });

        buttonBar.getChildren().addAll(secondaryButton, primaryButton);

        VBox root = new VBox(headerBox, contentBox, buttonBar);
        root.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 12;"
                + "-fx-border-color: #E8D8C8;"
                + "-fx-border-radius: 12;"
        );

        Scene scene = new Scene(root);
        try {
            scene.getStylesheets().add(AlertUtil.class.getResource("/css/style.css").toExternalForm());
        } catch (Exception ignored) {
        }
        dialogStage.setScene(scene);
        dialogStage.setResizable(false);
        dialogStage.setMinWidth(430);
        dialogStage.centerOnScreen();

        dialogStage.showAndWait();

        return result[0];
    }

    /**
     * Dialog kustom untuk pembaruan stok produk.
     */
    public static Integer inputStok(Window owner, String namaProduk, int stokSaatIni) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        if (owner != null) {
            dialogStage.initOwner(owner);
        }
        dialogStage.setTitle("Update Stok - CoffeeHub");

        try {
            Image icon = new Image(AlertUtil.class.getResourceAsStream("/images/coffeehub-logo.png"));
            dialogStage.getIcons().add(icon);
        } catch (Exception ignored) {
        }

        final Integer[] result = {null};

        // Header dialog
        HBox headerBox = new HBox(14);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setPadding(new Insets(18, 24, 18, 24));
        headerBox.setStyle("-fx-background-color: linear-gradient(to right, #56301F, #3F241A);");

        StackPane iconCircle = new StackPane();
        iconCircle.setPrefSize(38, 38);
        iconCircle.setMaxSize(38, 38);
        iconCircle.setMinSize(38, 38);
        iconCircle.setStyle(
                "-fx-background-color: #70452F;"
                + "-fx-background-radius: 19;"
                + "-fx-border-color: #C58B55;"
                + "-fx-border-radius: 19;"
                + "-fx-border-width: 1.5;"
        );

        Label iconLabel = new Label("📦");
        iconLabel.setStyle("-fx-font-size: 17px;");
        iconCircle.getChildren().add(iconLabel);

        VBox titleBox = new VBox(3);
        Label headerLabel = new Label("Update Stok Produk");
        headerLabel.setStyle("-fx-text-fill: #FFF8ED; -fx-font-size: 16px; -fx-font-weight: bold;");
        Label subLabel = new Label(namaProduk);
        subLabel.setStyle("-fx-text-fill: #D9BDA4; -fx-font-size: 13px;");
        titleBox.getChildren().addAll(headerLabel, subLabel);

        headerBox.getChildren().addAll(iconCircle, titleBox);

        // Content dialog
        VBox contentBox = new VBox(14);
        contentBox.setPadding(new Insets(24, 26, 22, 26));
        contentBox.setStyle("-fx-background-color: #FFFEFD;");

        HBox currentBox = new HBox(8);
        currentBox.setAlignment(Pos.CENTER_LEFT);
        Label currentLbl = new Label("Stok saat ini:");
        currentLbl.setStyle("-fx-text-fill: #8B705D; -fx-font-size: 13px;");
        Label badge = new Label(stokSaatIni + " unit");
        badge.setStyle(
                "-fx-background-color: #F5EBE1; -fx-text-fill: #56301F; -fx-font-weight: bold; "
                + "-fx-font-size: 12px; -fx-padding: 3 10; -fx-background-radius: 10; -fx-border-color: #E8D8C8; -fx-border-radius: 10;"
        );
        currentBox.getChildren().addAll(currentLbl, badge);

        Label inputLbl = new Label("Masukkan jumlah stok baru:");
        inputLbl.setStyle("-fx-text-fill: #56301F; -fx-font-size: 13px; -fx-font-weight: bold;");

        TextField stockField = new TextField(String.valueOf(stokSaatIni));
        stockField.setStyle(
                "-fx-background-color: white; -fx-border-color: #D8C7B8; -fx-border-radius: 8; "
                + "-fx-background-radius: 8; -fx-font-size: 14px; -fx-padding: 8 12;"
        );

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #B91C1C; -fx-font-size: 12px;");
        errorLabel.setVisible(false);

        contentBox.getChildren().addAll(currentBox, inputLbl, stockField, errorLabel);

        // Footer / Button bar
        HBox buttonBar = new HBox(12);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);
        buttonBar.setPadding(new Insets(14, 24, 18, 24));
        buttonBar.setStyle(
                "-fx-background-color: #F8F3ED;"
                + "-fx-border-color: #EFE6DC transparent transparent transparent;"
                + "-fx-border-width: 1;"
        );

        Button secondaryButton = new Button("Batal");
        secondaryButton.setStyle(
                "-fx-background-color: white;"
                + "-fx-text-fill: #6D5A4D;"
                + "-fx-font-weight: bold;"
                + "-fx-font-size: 13px;"
                + "-fx-background-radius: 8;"
                + "-fx-border-color: #D8C7B8;"
                + "-fx-border-radius: 8;"
                + "-fx-padding: 8 20;"
                + "-fx-cursor: hand;"
        );
        secondaryButton.setOnAction(e -> {
            result[0] = null;
            dialogStage.close();
        });

        Button primaryButton = new Button("Simpan Stok");
        primaryButton.setStyle(
                "-fx-background-color: #70452F;"
                + "-fx-text-fill: white;"
                + "-fx-font-weight: bold;"
                + "-fx-font-size: 13px;"
                + "-fx-background-radius: 8;"
                + "-fx-padding: 8 22;"
                + "-fx-cursor: hand;"
        );
        primaryButton.setOnAction(e -> {
            try {
                int val = Integer.parseInt(stockField.getText().trim());
                if (val < 0) {
                    errorLabel.setText("Stok tidak boleh bernilai negatif.");
                    errorLabel.setVisible(true);
                    return;
                }
                result[0] = val;
                dialogStage.close();
            } catch (NumberFormatException ex) {
                errorLabel.setText("Masukkan angka bulat yang valid.");
                errorLabel.setVisible(true);
            }
        });

        buttonBar.getChildren().addAll(secondaryButton, primaryButton);

        VBox root = new VBox(headerBox, contentBox, buttonBar);
        root.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 12;"
                + "-fx-border-color: #E8D8C8;"
                + "-fx-border-radius: 12;"
        );

        Scene scene = new Scene(root);
        try {
            scene.getStylesheets().add(AlertUtil.class.getResource("/css/style.css").toExternalForm());
        } catch (Exception ignored) {
        }
        dialogStage.setScene(scene);
        dialogStage.setResizable(false);
        dialogStage.setMinWidth(420);
        dialogStage.centerOnScreen();

        dialogStage.showAndWait();

        return result[0];
    }

    /**
     * Dialog kustom untuk melihat detail pesanan pelanggan.
     */
    public static void tampilkanDetailPesanan(Window owner, Order order, List<OrderDetail> details) {
        if (order == null) return;

        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        if (owner != null) {
            dialogStage.initOwner(owner);
        }
        dialogStage.setTitle("Detail Pesanan - CoffeeHub");

        try {
            Image icon = new Image(AlertUtil.class.getResourceAsStream("/images/coffeehub-logo.png"));
            dialogStage.getIcons().add(icon);
        } catch (Exception ignored) {
        }

        // Header dialog
        HBox headerBox = new HBox(14);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setPadding(new Insets(18, 24, 18, 24));
        headerBox.setStyle("-fx-background-color: linear-gradient(to right, #56301F, #3F241A);");

        StackPane iconCircle = new StackPane();
        iconCircle.setPrefSize(38, 38);
        iconCircle.setMaxSize(38, 38);
        iconCircle.setMinSize(38, 38);
        iconCircle.setStyle(
                "-fx-background-color: #70452F;"
                + "-fx-background-radius: 19;"
                + "-fx-border-color: #C58B55;"
                + "-fx-border-radius: 19;"
                + "-fx-border-width: 1.5;"
        );

        Label iconLabel = new Label("📋");
        iconLabel.setStyle("-fx-font-size: 17px;");
        iconCircle.getChildren().add(iconLabel);

        VBox titleBox = new VBox(3);
        Label headerLabel = new Label("Detail Pesanan Pelanggan");
        headerLabel.setStyle("-fx-text-fill: #FFF8ED; -fx-font-size: 16px; -fx-font-weight: bold;");
        Label subLabel = new Label("Kode: " + order.getOrderCode());
        subLabel.setStyle("-fx-text-fill: #D9BDA4; -fx-font-size: 13px;");
        titleBox.getChildren().addAll(headerLabel, subLabel);

        headerBox.getChildren().addAll(iconCircle, titleBox);

        // Content
        VBox contentBox = new VBox(14);
        contentBox.setPadding(new Insets(20, 26, 20, 26));
        contentBox.setStyle("-fx-background-color: #FFFEFD;");

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.of("id", "ID"));
        nf.setMaximumFractionDigits(0);

        // Info Grid
        VBox infoBox = new VBox(8);
        infoBox.setStyle("-fx-background-color: #FAF4ED; -fx-background-radius: 10; -fx-padding: 14; -fx-border-color: #E8D8C8; -fx-border-radius: 10;");

        HBox r1 = new HBox(20);
        Label p1 = new Label("Pemesan: " + (order.getPelanggan() != null ? order.getPelanggan().getNama() : "-"));
        p1.setStyle("-fx-font-weight: bold; -fx-text-fill: #56301F; -fx-font-size: 13px;");
        Label p2 = new Label("Waktu: " + (order.getOrderDate() != null ? order.getOrderDate().format(dtf) : "-"));
        p2.setStyle("-fx-text-fill: #8B705D; -fx-font-size: 13px;");
        r1.getChildren().addAll(p1, p2);

        HBox r2 = new HBox(20);
        Label p3 = new Label("Metode: " + order.getPaymentMethod());
        p3.setStyle("-fx-text-fill: #8B705D; -fx-font-size: 13px;");
        Label p4 = new Label("Pembayaran: " + order.getPaymentStatus());
        p4.setStyle("-fx-font-weight: bold; -fx-text-fill: " + (order.getPaymentStatus() != null && "LUNAS".equals(order.getPaymentStatus().name()) ? "#047857;" : "#C2410C;") + "-fx-font-size: 13px;");
        Label p5 = new Label("Status: " + order.getOrderStatus());
        p5.setStyle("-fx-font-weight: bold; -fx-text-fill: #56301F; -fx-font-size: 13px;");
        r2.getChildren().addAll(p3, p4, p5);

        infoBox.getChildren().addAll(r1, r2);

        if (order.getNotes() != null && !order.getNotes().isBlank()) {
            Label noteLbl = new Label("Catatan: " + order.getNotes());
            noteLbl.setStyle("-fx-font-style: italic; -fx-text-fill: #8B705D; -fx-font-size: 12px;");
            infoBox.getChildren().add(noteLbl);
        }

        // List item produk
        Label itemsTitle = new Label("Rincian Produk:");
        itemsTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #56301F; -fx-font-size: 13px;");

        VBox itemsList = new VBox(6);
        if (details == null || details.isEmpty()) {
            Label emptyLbl = new Label("Tidak ada rincian produk.");
            emptyLbl.setStyle("-fx-text-fill: #A08875; -fx-font-style: italic; -fx-font-size: 13px;");
            itemsList.getChildren().add(emptyLbl);
        } else {
            for (OrderDetail d : details) {
                HBox itemRow = new HBox();
                itemRow.setAlignment(Pos.CENTER_LEFT);
                String pName = (d.getProduk() != null && d.getProduk().getNama() != null)
                        ? d.getProduk().getNama()
                        : "Produk";
                Label nameLbl = new Label("• " + pName + " (x" + d.getQuantity() + ")");
                nameLbl.setStyle("-fx-text-fill: #493629; -fx-font-size: 13px;");
                Region r = new Region();
                HBox.setHgrow(r, Priority.ALWAYS);
                Label subLbl = new Label(nf.format(d.getSubtotal()));
                subLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #70452F; -fx-font-size: 13px;");
                itemRow.getChildren().addAll(nameLbl, r, subLbl);
                itemsList.getChildren().add(itemRow);
            }
        }

        // Total
        HBox totalRow = new HBox();
        totalRow.setAlignment(Pos.CENTER_LEFT);
        totalRow.setStyle("-fx-padding: 10 0 0 0; -fx-border-color: #EFE6DC transparent transparent transparent; -fx-border-width: 1;");
        Label totalText = new Label("Total Pembayaran:");
        totalText.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #56301F;");
        Region r = new Region();
        HBox.setHgrow(r, Priority.ALWAYS);
        Label totalVal = new Label(nf.format(order.getTotalAmount()));
        totalVal.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #70452F;");
        totalRow.getChildren().addAll(totalText, r, totalVal);

        contentBox.getChildren().addAll(infoBox, itemsTitle, itemsList, totalRow);

        // Footer
        HBox buttonBar = new HBox(12);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);
        buttonBar.setPadding(new Insets(14, 24, 18, 24));
        buttonBar.setStyle(
                "-fx-background-color: #F8F3ED;"
                + "-fx-border-color: #EFE6DC transparent transparent transparent;"
                + "-fx-border-width: 1;"
        );

        Button closeBtn = new Button("Tutup");
        closeBtn.setStyle(
                "-fx-background-color: #70452F;"
                + "-fx-text-fill: white;"
                + "-fx-font-weight: bold;"
                + "-fx-font-size: 13px;"
                + "-fx-background-radius: 8;"
                + "-fx-padding: 8 26;"
                + "-fx-cursor: hand;"
        );
        closeBtn.setOnAction(e -> dialogStage.close());
        buttonBar.getChildren().add(closeBtn);

        VBox root = new VBox(headerBox, contentBox, buttonBar);
        root.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 12;"
                + "-fx-border-color: #E8D8C8;"
                + "-fx-border-radius: 12;"
        );

        Scene scene = new Scene(root);
        try {
            scene.getStylesheets().add(AlertUtil.class.getResource("/css/style.css").toExternalForm());
        } catch (Exception ignored) {
        }
        dialogStage.setScene(scene);
        dialogStage.setResizable(false);
        dialogStage.setMinWidth(460);
        dialogStage.centerOnScreen();

        dialogStage.showAndWait();
    }

    private static boolean tampilkanDialog(
            Window owner,
            String judul,
            String header,
            String pesan,
            Type tipe,
            String teksTombolPrimer,
            String teksTombolSekunder
    ) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        if (owner != null) {
            dialogStage.initOwner(owner);
        }
        dialogStage.setTitle(judul != null ? judul : "CoffeeHub");

        try {
            Image icon = new Image(AlertUtil.class.getResourceAsStream("/images/coffeehub-logo.png"));
            dialogStage.getIcons().add(icon);
        } catch (Exception ignored) {
        }

        final boolean[] result = {false};

        // Header dialog
        HBox headerBox = new HBox(14);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setPadding(new Insets(16, 22, 16, 22));
        headerBox.setStyle("-fx-background-color: linear-gradient(to right, #56301F, #3F241A);");

        String iconChar = "☕";
        String iconBg = "#70452F";
        String iconBorder = "#C58B55";

        if (tipe == Type.CONFIRM) {
            iconChar = "?";
            iconBg = "#6B422B";
            iconBorder = "#C58B55";
        } else if (tipe == Type.SUCCESS) {
            iconChar = "✓";
            iconBg = "#2E5D38";
            iconBorder = "#74C686";
        } else if (tipe == Type.WARNING || tipe == Type.ERROR) {
            iconChar = "!";
            iconBg = "#8B2E24";
            iconBorder = "#F08C80";
        }

        StackPane iconCircle = new StackPane();
        iconCircle.setPrefSize(34, 34);
        iconCircle.setMaxSize(34, 34);
        iconCircle.setMinSize(34, 34);
        iconCircle.setStyle(
                "-fx-background-color: " + iconBg + ";"
                + "-fx-background-radius: 17;"
                + "-fx-border-color: " + iconBorder + ";"
                + "-fx-border-radius: 17;"
                + "-fx-border-width: 1.5;"
        );

        Label iconLabel = new Label(iconChar);
        iconLabel.setStyle("-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold;");
        iconCircle.getChildren().add(iconLabel);

        VBox titleBox = new VBox(2);
        Label headerLabel = new Label(header != null ? header : judul);
        headerLabel.setStyle("-fx-text-fill: #FFF8ED; -fx-font-size: 16px; -fx-font-weight: bold;");
        headerLabel.setWrapText(true);
        titleBox.getChildren().add(headerLabel);

        headerBox.getChildren().addAll(iconCircle, titleBox);

        // Content dialog
        VBox contentBox = new VBox(10);
        contentBox.setPadding(new Insets(20, 24, 20, 24));
        contentBox.setStyle("-fx-background-color: #FFFEFD;");

        Label pesanLabel = new Label(pesan);
        pesanLabel.setWrapText(true);
        pesanLabel.setStyle(
                "-fx-text-fill: #493629;"
                + "-fx-font-size: 13px;"
                + "-fx-line-spacing: 4px;"
        );
        pesanLabel.setMaxWidth(420);
        contentBox.getChildren().add(pesanLabel);

        // Footer / Button bar
        HBox buttonBar = new HBox(12);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);
        buttonBar.setPadding(new Insets(14, 22, 16, 22));
        buttonBar.setStyle(
                "-fx-background-color: #F8F3ED;"
                + "-fx-border-color: #EFE6DC transparent transparent transparent;"
                + "-fx-border-width: 1;"
        );

        if (teksTombolSekunder != null) {
            Button secondaryButton = new Button(teksTombolSekunder);
            secondaryButton.setStyle(
                    "-fx-background-color: white;"
                    + "-fx-text-fill: #6D5A4D;"
                    + "-fx-font-weight: bold;"
                    + "-fx-font-size: 13px;"
                    + "-fx-background-radius: 8;"
                    + "-fx-border-color: #D8C7B8;"
                    + "-fx-border-radius: 8;"
                    + "-fx-padding: 8 20;"
                    + "-fx-cursor: hand;"
            );
            secondaryButton.setOnAction(e -> {
                result[0] = false;
                dialogStage.close();
            });
            buttonBar.getChildren().add(secondaryButton);
        }

        Button primaryButton = new Button(teksTombolPrimer != null ? teksTombolPrimer : "OK");
        primaryButton.setStyle(
                "-fx-background-color: #70452F;"
                + "-fx-text-fill: white;"
                + "-fx-font-weight: bold;"
                + "-fx-font-size: 13px;"
                + "-fx-background-radius: 8;"
                + "-fx-padding: 8 22;"
                + "-fx-cursor: hand;"
        );
        primaryButton.setOnAction(e -> {
            result[0] = true;
            dialogStage.close();
        });
        buttonBar.getChildren().add(primaryButton);

        VBox root = new VBox(headerBox, contentBox, buttonBar);
        root.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 12;"
                + "-fx-border-color: #E8D8C8;"
                + "-fx-border-radius: 12;"
        );

        Scene scene = new Scene(root);
        dialogStage.setScene(scene);
        dialogStage.setResizable(false);
        dialogStage.setMinWidth(380);
        dialogStage.setMaxWidth(480);
        dialogStage.centerOnScreen();

        dialogStage.showAndWait();

        return result[0];
    }
}
