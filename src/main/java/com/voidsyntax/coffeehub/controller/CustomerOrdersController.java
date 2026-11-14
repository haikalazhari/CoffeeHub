package com.voidsyntax.coffeehub.controller;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import com.voidsyntax.coffeehub.model.Order;
import com.voidsyntax.coffeehub.model.OrderDetail;
import com.voidsyntax.coffeehub.model.User;
import com.voidsyntax.coffeehub.repository.OrderDAO;
import com.voidsyntax.coffeehub.service.SessionManager;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
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
        VBox card = new VBox(12);

        card.setPadding(new Insets(20));

        card.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 10;"
                + "-fx-border-color: #E8D8C8;"
                + "-fx-border-radius: 10;"
        );

        Label orderCodeLabel = new Label(
                "Order Code: " + order.getOrderCode()
        );

        orderCodeLabel.setStyle(
                "-fx-font-size: 16px;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: #493629;"
        );

        Label dateLabel = new Label(
                order.getOrderDate().format(dateFormatter)
        );

        dateLabel.setStyle(
                "-fx-font-size: 12px;"
                + "-fx-text-fill: #8B7562;"
        );

        // Detail produk pesanan
        VBox detailsBox = new VBox(8);

        Label detailsTitle = new Label("Order Details");

        detailsTitle.setStyle(
                "-fx-font-size: 14px;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: #493629;"
        );

        detailsBox.getChildren().add(detailsTitle);

        for (OrderDetail detail : order.getOrderDetails()) {
                VBox productBox = new VBox(4);

                Label productName = new Label(
                        detail.getProduk().getNama()
                );

                productName.setStyle(
                        "-fx-font-weight: bold;"
                        + "-fx-text-fill: #493629;"
                );

                Label productInfo = new Label(
                        detail.getQuantity()
                        + " x "
                        + currencyFormat.format(detail.getUnitPrice())
                );

                productInfo.setStyle(
                        "-fx-font-size: 12px;"
                        + "-fx-text-fill: #8B7562;"
                );

                Label subtotalLabel = new Label(
                        "Subtotal: "
                        + currencyFormat.format(detail.getSubtotal())
                );

                subtotalLabel.setStyle(
                        "-fx-font-size: 13px;"
                        + "-fx-text-fill: #795548;"
                );

                productBox.getChildren().addAll(
                        productName,
                        productInfo,
                        subtotalLabel
                );

                detailsBox.getChildren().add(productBox);
        }

        Label totalLabel = new Label(
                "Total: " + currencyFormat.format(order.getTotalAmount())
        );

        totalLabel.setStyle(
                "-fx-font-size: 15px;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: #795548;"
        );

        Label paymentMethodLabel = new Label(
                "Payment: " + order.getPaymentMethod()
        );

        Label paymentStatusLabel = new Label(
                "Payment Status: " + order.getPaymentStatus()
        );

        Label orderStatusLabel = new Label(
                "Order Status: " + order.getOrderStatus()
        );

        paymentMethodLabel.setStyle("-fx-text-fill: #6D5A4D;");
        paymentStatusLabel.setStyle("-fx-text-fill: #6D5A4D;");
        orderStatusLabel.setStyle("-fx-text-fill: #6D5A4D;");

        HBox statusBox = new HBox(20);
        statusBox.getChildren().addAll(
                paymentStatusLabel,
                orderStatusLabel
        );

        card.getChildren().addAll(
                orderCodeLabel,
                dateLabel,
                detailsBox,
                totalLabel,
                paymentMethodLabel,
                statusBox
        );

        return card;
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
