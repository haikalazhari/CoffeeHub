package com.voidsyntax.coffeehub.controller;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import com.voidsyntax.coffeehub.enums.OrderStatus;
import com.voidsyntax.coffeehub.enums.PaymentStatus;
import com.voidsyntax.coffeehub.model.Order;
import com.voidsyntax.coffeehub.model.OrderDetail;
import com.voidsyntax.coffeehub.model.User;
import com.voidsyntax.coffeehub.repository.OrderDAO;
import com.voidsyntax.coffeehub.service.SessionManager;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class CustomerOrdersController {

    @FXML
    private VBox ordersContainer;

    private final OrderDAO orderDAO = new OrderDAO();

    private final NumberFormat currencyFormat =
            NumberFormat.getCurrencyInstance(
                    Locale.forLanguageTag("id-ID")
            );

    private final DateTimeFormatter dateFormatter =
            DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    @FXML
    private void initialize() {
        muatRiwayatPesanan();
    }

    private void muatRiwayatPesanan() {
        ordersContainer.getChildren().clear();

        User pelanggan = SessionManager.getCurrentUser();

        if (!SessionManager.isPelanggan() || pelanggan == null) {
            tampilkanPesan(
                    Alert.AlertType.ERROR,
                    "Sesi Tidak Valid",
                    "Silakan login kembali."
            );
            return;
        }

        List<Order> daftarPesanan =
                orderDAO.tampilkanBerdasarkanPelanggan(
                        pelanggan.getId()
                );

        if (daftarPesanan.isEmpty()) {
            Label emptyLabel = new Label(
                    "Anda belum memiliki riwayat pesanan."
            );

            emptyLabel.setStyle(
                    "-fx-font-size: 14px;"
                    + "-fx-text-fill: #8B7562;"
            );

            ordersContainer.getChildren().add(emptyLabel);
            return;
        }

        for (Order order : daftarPesanan) {
            ordersContainer.getChildren().add(
                    buatKartuPesanan(order)
            );
        }
    }

    private VBox buatKartuPesanan(Order order) {
        VBox card = new VBox(14);
        card.setPadding(new Insets(20));
        card.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 14;"
                + "-fx-border-color: #E8D8C8;"
                + "-fx-border-radius: 14;"
                + "-fx-effect: dropshadow(three-pass-box, rgba(73, 54, 41, 0.05), 10, 0, 0, 4);"
        );

        // Header: Order Code & Date di kiri, Status Badges di kanan
        HBox headerBox = new HBox(12);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        VBox orderInfoBox = new VBox(3);
        Label orderCodeLabel = new Label("Order Code: " + order.getOrderCode());
        orderCodeLabel.setStyle(
                "-fx-font-size: 16px;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: #493629;"
        );

        Label dateLabel = new Label(order.getOrderDate().format(dateFormatter));
        dateLabel.setStyle(
                "-fx-font-size: 12px;"
                + "-fx-text-fill: #9C8573;"
        );
        orderInfoBox.getChildren().addAll(orderCodeLabel, dateLabel);

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        // Badges Status yang modern
        Label paymentStatusBadge = buatPaymentStatusBadge(order.getPaymentStatus());
        Label orderStatusBadge = buatOrderStatusBadge(order.getOrderStatus());

        HBox badgesBox = new HBox(8);
        badgesBox.setAlignment(Pos.CENTER_RIGHT);
        badgesBox.getChildren().addAll(paymentStatusBadge, orderStatusBadge);

        headerBox.getChildren().addAll(orderInfoBox, headerSpacer, badgesBox);

        // Garis Pembatas
        Region divider1 = new Region();
        divider1.setPrefHeight(1);
        divider1.setStyle("-fx-background-color: #F0E6DC;");

        // Detail produk pesanan
        VBox detailsBox = new VBox(8);
        Label detailsTitle = new Label("Order Details");
        detailsTitle.setStyle(
                "-fx-font-size: 13px;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: #8B7562;"
        );
        detailsBox.getChildren().add(detailsTitle);

        for (OrderDetail detail : order.getOrderDetails()) {
            HBox itemRow = new HBox(10);
            itemRow.setAlignment(Pos.CENTER_LEFT);

            Label productName = new Label(detail.getProduk().getNama());
            productName.setStyle(
                    "-fx-font-weight: bold;"
                    + "-fx-font-size: 14px;"
                    + "-fx-text-fill: #493629;"
            );

            Label productQty = new Label("x" + detail.getQuantity());
            productQty.setStyle(
                    "-fx-font-size: 11px;"
                    + "-fx-font-weight: bold;"
                    + "-fx-text-fill: #8B7562;"
                    + "-fx-background-color: #F5EFE8;"
                    + "-fx-background-radius: 6;"
                    + "-fx-padding: 2 7 2 7;"
            );

            Region itemSpacer = new Region();
            HBox.setHgrow(itemSpacer, Priority.ALWAYS);

            Label subtotalLabel = new Label(currencyFormat.format(detail.getSubtotal()));
            subtotalLabel.setStyle(
                    "-fx-font-size: 13px;"
                    + "-fx-font-weight: bold;"
                    + "-fx-text-fill: #795548;"
            );

            itemRow.getChildren().addAll(productName, productQty, itemSpacer, subtotalLabel);
            detailsBox.getChildren().add(itemRow);
        }

        // Notes pesanan (jika ada)
        HBox notesBox = null;
        if (order.getNotes() != null && !order.getNotes().isBlank()) {
            notesBox = new HBox(8);
            notesBox.setAlignment(Pos.CENTER_LEFT);
            notesBox.setStyle(
                    "-fx-background-color: #FFF9E6;" +
                    "-fx-background-radius: 8;" +
                    "-fx-padding: 8 12 8 12;" +
                    "-fx-border-color: #FFE082;" +
                    "-fx-border-radius: 8;"
            );

            Label noteIcon = new Label("📝");
            noteIcon.setStyle("-fx-font-size: 13px;");

            Label noteText = new Label("Catatan: " + order.getNotes());
            noteText.setWrapText(true);
            noteText.setStyle(
                    "-fx-font-size: 12px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-text-fill: #8D5B10;"
            );

            notesBox.getChildren().addAll(noteIcon, noteText);
        }

        // Garis Pembatas
        Region divider2 = new Region();
        divider2.setPrefHeight(1);
        divider2.setStyle("-fx-background-color: #F0E6DC;");

        // Footer: Info Pembayaran di kiri, Total di kanan
        HBox footerBox = new HBox(12);
        footerBox.setAlignment(Pos.CENTER_LEFT);

        String paymentDisplay = switch (order.getPaymentMethod()) {
            case QRIS -> "📱 QRIS (NMID: ID1020304050607)";
            case TRANSFER_BCA -> "💳 BCA 8831 2345 6789 (a/n CoffeeHub)";
            case TUNAI -> "💵 Tunai di Kasir";
        };

        Label paymentMethodLabel = new Label(paymentDisplay);
        paymentMethodLabel.setStyle(
                "-fx-font-size: 12px;"
                + "-fx-text-fill: #70452F;"
                + "-fx-background-color: #FAF4ED;"
                + "-fx-background-radius: 8;"
                + "-fx-padding: 5 12 5 12;"
                + "-fx-border-color: #E8D8C8;"
                + "-fx-border-radius: 8;"
        );

        Region footerSpacer = new Region();
        HBox.setHgrow(footerSpacer, Priority.ALWAYS);

        HBox totalBox = new HBox(6);
        totalBox.setAlignment(Pos.CENTER_RIGHT);

        Label totalTextLabel = new Label("Total:");
        totalTextLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #8B7562;");

        Label totalAmountLabel = new Label(currencyFormat.format(order.getTotalAmount()));
        totalAmountLabel.setStyle(
                "-fx-font-size: 17px;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: #56301F;"
        );
        totalBox.getChildren().addAll(totalTextLabel, totalAmountLabel);

        footerBox.getChildren().addAll(paymentMethodLabel, footerSpacer, totalBox);

        card.getChildren().addAll(
                headerBox,
                divider1,
                detailsBox
        );

        if (notesBox != null) {
            card.getChildren().add(notesBox);
        }

        card.getChildren().addAll(
                divider2,
                footerBox
        );

        return card;
    }

    private Label buatPaymentStatusBadge(PaymentStatus status) {
        Label badge = new Label();
        if (status == PaymentStatus.LUNAS) {
            badge.setText("✓ Lunas");
            badge.setStyle(
                    "-fx-background-color: #ECFDF5;"
                    + "-fx-text-fill: #047857;"
                    + "-fx-border-color: #A7F3D0;"
                    + "-fx-border-radius: 20;"
                    + "-fx-background-radius: 20;"
                    + "-fx-padding: 4 12 4 12;"
                    + "-fx-font-size: 12px;"
                    + "-fx-font-weight: bold;"
            );
        } else {
            badge.setText("⏳ Belum Bayar");
            badge.setStyle(
                    "-fx-background-color: #FFF7ED;"
                    + "-fx-text-fill: #C2410C;"
                    + "-fx-border-color: #FED7AA;"
                    + "-fx-border-radius: 20;"
                    + "-fx-background-radius: 20;"
                    + "-fx-padding: 4 12 4 12;"
                    + "-fx-font-size: 12px;"
                    + "-fx-font-weight: bold;"
            );
        }
        return badge;
    }

    private Label buatOrderStatusBadge(OrderStatus status) {
        Label badge = new Label();
        String text;
        String style;

        switch (status) {
            case DIPROSES -> {
                text = "⚙️ Diproses";
                style = "-fx-background-color: #E0F2FE; -fx-text-fill: #0284C7; -fx-border-color: #BAE6FD;";
            }
            case SIAP -> {
                text = "☕ Siap Diambil";
                style = "-fx-background-color: #F3E8FF; -fx-text-fill: #7E22CE; -fx-border-color: #E9D5FF;";
            }
            case SELESAI -> {
                text = "✓ Selesai";
                style = "-fx-background-color: #DCFCE7; -fx-text-fill: #15803D; -fx-border-color: #BBF7D0;";
            }
            case DIBATALKAN -> {
                text = "✕ Dibatalkan";
                style = "-fx-background-color: #FEE2E2; -fx-text-fill: #B91C1C; -fx-border-color: #FECACA;";
            }
            default -> { // MENUNGGU
                text = "🕒 Menunggu";
                style = "-fx-background-color: #FEF3C7; -fx-text-fill: #B45309; -fx-border-color: #FDE68A;";
            }
        }

        badge.setText(text);
        badge.setStyle(
                style
                + "-fx-border-radius: 20;"
                + "-fx-background-radius: 20;"
                + "-fx-padding: 4 12 4 12;"
                + "-fx-font-size: 12px;"
                + "-fx-font-weight: bold;"
        );
        return badge;
    }


    private void tampilkanPesan(
            Alert.AlertType tipe,
            String judul,
            String pesan
    ) {
        Alert alert = new Alert(tipe);
        alert.setTitle(judul);
        alert.setHeaderText(null);
        alert.setContentText(pesan);
        alert.showAndWait();
    }
}
