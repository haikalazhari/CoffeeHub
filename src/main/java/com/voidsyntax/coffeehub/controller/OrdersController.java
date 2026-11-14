package com.voidsyntax.coffeehub.controller;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import com.voidsyntax.coffeehub.enums.OrderStatus;
import com.voidsyntax.coffeehub.enums.PaymentStatus;
import com.voidsyntax.coffeehub.model.Order;
import com.voidsyntax.coffeehub.repository.OrderDAO;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;

public class OrdersController {

    @FXML
    private Label totalOrdersLabel;

    @FXML
    private Label pendingOrdersLabel;

    @FXML
    private Label completedOrdersLabel;

    @FXML
    private TableView<Order> orderTable;

    @FXML
    private TableColumn<Order, Integer> idColumn;

    @FXML
    private TableColumn<Order, String> codeColumn;

    @FXML
    private TableColumn<Order, String> customerColumn;

    @FXML
    private TableColumn<Order, String> dateColumn;

    @FXML
    private TableColumn<Order, String> totalColumn;

    @FXML
    private TableColumn<Order, String> paymentColumn;

    @FXML
    private TableColumn<Order, String> statusColumn;

    @FXML
    private TableColumn<Order, Void> actionColumn;

    private final OrderDAO orderDAO = new OrderDAO();

    private final ObservableList<Order> daftarOrder =
            FXCollections.observableArrayList();

    private final DateTimeFormatter dateFormatter =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    @FXML
    public void initialize() {
        aturKolom();
        aturTombolAksi();
        muatDataPesanan();
    }

    // Mengatur kolom tabel
    private void aturKolom() {

        idColumn.setCellValueFactory(data ->
                new SimpleIntegerProperty(
                        data.getValue().getId()
                ).asObject()
        );

        codeColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getOrderCode()
                )
        );

        customerColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getPelanggan().getNama()
                )
        );

        dateColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getOrderDate()
                                .format(dateFormatter)
                )
        );

        totalColumn.setCellValueFactory(data -> {

            BigDecimal total = data.getValue().getTotalAmount();

            NumberFormat format =
                    NumberFormat.getCurrencyInstance(
                            Locale.forLanguageTag("id-ID")
                    );

            format.setMaximumFractionDigits(0);

            return new SimpleStringProperty(
                    format.format(total)
            );
        });

        paymentColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getPaymentStatus().name()
                )
        );

        statusColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getOrderStatus().name()
                )
        );
    }

    // Mengatur tombol aksi
    private void aturTombolAksi() {

        actionColumn.setCellFactory(column ->
                new TableCell<>() {

            private final Button paymentButton =
                    new Button("Payment");

            private final Button statusButton =
                    new Button("Status");

            {
                paymentButton.setStyle(
                        "-fx-background-color: #8B6048;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 5;"
                );

                statusButton.setStyle(
                        "-fx-background-color: #D8C7B8;" +
                        "-fx-text-fill: #5C3825;" +
                        "-fx-background-radius: 5;"
                );

                paymentButton.setOnAction(event -> {
                    Order order = getTableView()
                            .getItems()
                            .get(getIndex());

                    konfirmasiPembayaran(order);
                });

                statusButton.setOnAction(event -> {
                    Order order = getTableView()
                            .getItems()
                            .get(getIndex());

                    ubahStatusPesanan(order);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    paymentButton.setDisable(
                            getTableView().getItems()
                                    .get(getIndex())
                                    .getPaymentStatus() == PaymentStatus.LUNAS
                    );

                    HBox buttons = new HBox(5);
                    buttons.getChildren().addAll(
                            paymentButton,
                            statusButton
                    );

                    setGraphic(buttons);
                }
            }
        });
    }

    // Memuat seluruh pesanan
    private void muatDataPesanan() {

        List<Order> orders = orderDAO.tampilkanSemua();

        daftarOrder.setAll(orders);
        orderTable.setItems(daftarOrder);

        hitungRingkasanPesanan(orders);
    }

    // Menghitung ringkasan pesanan
    private void hitungRingkasanPesanan(List<Order> orders) {

        int total = orders.size();
        int menunggu = 0;
        int selesai = 0;

        for (Order order : orders) {

            if (order.getOrderStatus() == OrderStatus.MENUNGGU) {
                menunggu++;
            }

            if (order.getOrderStatus() == OrderStatus.SELESAI) {
                selesai++;
            }
        }

        totalOrdersLabel.setText(String.valueOf(total));
        pendingOrdersLabel.setText(String.valueOf(menunggu));
        completedOrdersLabel.setText(String.valueOf(selesai));
    }

    // Mengonfirmasi pembayaran tunai
    private void konfirmasiPembayaran(Order order) {

        if (order.getPaymentStatus() == PaymentStatus.LUNAS) {
            tampilkanInformasi("Pesanan ini sudah dibayar.");
            return;
        }

        Alert confirmation = new Alert(
                Alert.AlertType.CONFIRMATION
        );

        confirmation.setTitle("Confirm Cash Payment");
        confirmation.setHeaderText(
                "Confirm payment for " + order.getOrderCode() + "?"
        );

        confirmation.setContentText(
                "Total payment: Rp" + order.getTotalAmount()
                        + "\nPayment method: TUNAI"
        );

        Optional<ButtonType> result = confirmation.showAndWait();

        if (result.isPresent()
                && result.get() == ButtonType.OK) {

            boolean berhasil = orderDAO.ubahStatusPembayaran(
                    order.getId(),
                    PaymentStatus.LUNAS
            );

            if (berhasil) {
                tampilkanInformasi(
                        "Pembayaran berhasil dikonfirmasi."
                );

                muatDataPesanan();

            } else {
                tampilkanError(
                        "Gagal mengonfirmasi pembayaran."
                );
            }
        }
    }

    // Mengubah status pesanan
    private void ubahStatusPesanan(Order order) {

        ChoiceDialog<OrderStatus> dialog =
                new ChoiceDialog<>(
                        order.getOrderStatus(),
                        OrderStatus.values()
                );

        dialog.setTitle("Update Order Status");
        dialog.setHeaderText(
                "Update status for " + order.getOrderCode()
        );

        dialog.setContentText("Select order status:");

        Optional<OrderStatus> result = dialog.showAndWait();

        result.ifPresent(statusBaru -> {

            if (statusBaru == order.getOrderStatus()) {
                return;
            }

            boolean berhasil = orderDAO.ubahStatusPesanan(
                    order.getId(),
                    statusBaru
            );

            if (berhasil) {
                tampilkanInformasi(
                        "Status pesanan berhasil diperbarui."
                );

                muatDataPesanan();

            } else {
                tampilkanError(
                        "Gagal memperbarui status pesanan."
                );
            }
        });
    }

    // Refresh tabel pesanan
    @FXML
    private void handleRefresh() {
        muatDataPesanan();
        tampilkanInformasi("Data pesanan berhasil dimuat ulang.");
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
