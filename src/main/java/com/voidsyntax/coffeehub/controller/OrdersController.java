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
import com.voidsyntax.coffeehub.util.AlertUtil;
import com.voidsyntax.coffeehub.util.ToastUtil;

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

        totalColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-font-weight: bold; -fx-text-fill: #70452F;");
                }
            }
        });

        paymentColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getPaymentStatus().name()
                )
        );

        paymentColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Label badge = new Label();
                    if ("LUNAS".equals(item)) {
                        badge.setText("✓ Lunas");
                        badge.setStyle(
                                "-fx-background-color: #ECFDF5;" +
                                "-fx-text-fill: #047857;" +
                                "-fx-border-color: #A7F3D0;" +
                                "-fx-border-radius: 12;" +
                                "-fx-background-radius: 12;" +
                                "-fx-padding: 3 10;" +
                                "-fx-font-weight: bold;" +
                                "-fx-font-size: 11px;"
                        );
                    } else {
                        badge.setText("⏳ Belum Bayar");
                        badge.setStyle(
                                "-fx-background-color: #FFF7ED;" +
                                "-fx-text-fill: #C2410C;" +
                                "-fx-border-color: #FED7AA;" +
                                "-fx-border-radius: 12;" +
                                "-fx-background-radius: 12;" +
                                "-fx-padding: 3 10;" +
                                "-fx-font-weight: bold;" +
                                "-fx-font-size: 11px;"
                        );
                    }
                    setGraphic(badge);
                    setText(null);
                }
            }
        });

        statusColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getOrderStatus().name()
                )
        );

        statusColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Label badge = new Label();
                    switch (item) {
                        case "MENUNGGU" -> {
                            badge.setText("🕒 Menunggu");
                            badge.setStyle(
                                    "-fx-background-color: #FEFCE8;" +
                                    "-fx-text-fill: #A16207;" +
                                    "-fx-border-color: #FEF08A;" +
                                    "-fx-border-radius: 12;" +
                                    "-fx-background-radius: 12;" +
                                    "-fx-padding: 3 10;" +
                                    "-fx-font-weight: bold;" +
                                    "-fx-font-size: 11px;"
                            );
                        }
                        case "DIPROSES" -> {
                            badge.setText("⚙️ Diproses");
                            badge.setStyle(
                                    "-fx-background-color: #EFF6FF;" +
                                    "-fx-text-fill: #1D4ED8;" +
                                    "-fx-border-color: #BFDBFE;" +
                                    "-fx-border-radius: 12;" +
                                    "-fx-background-radius: 12;" +
                                    "-fx-padding: 3 10;" +
                                    "-fx-font-weight: bold;" +
                                    "-fx-font-size: 11px;"
                            );
                        }
                        case "SIAP" -> {
                            badge.setText("📦 Siap");
                            badge.setStyle(
                                    "-fx-background-color: #FAF5FF;" +
                                    "-fx-text-fill: #7E22CE;" +
                                    "-fx-border-color: #E9D5FF;" +
                                    "-fx-border-radius: 12;" +
                                    "-fx-background-radius: 12;" +
                                    "-fx-padding: 3 10;" +
                                    "-fx-font-weight: bold;" +
                                    "-fx-font-size: 11px;"
                            );
                        }
                        case "SELESAI" -> {
                            badge.setText("✓ Selesai");
                            badge.setStyle(
                                    "-fx-background-color: #ECFDF5;" +
                                    "-fx-text-fill: #047857;" +
                                    "-fx-border-color: #A7F3D0;" +
                                    "-fx-border-radius: 12;" +
                                    "-fx-background-radius: 12;" +
                                    "-fx-padding: 3 10;" +
                                    "-fx-font-weight: bold;" +
                                    "-fx-font-size: 11px;"
                            );
                        }
                        default -> {
                            badge.setText("✕ Dibatalkan");
                            badge.setStyle(
                                    "-fx-background-color: #FEF2F2;" +
                                    "-fx-text-fill: #B91C1C;" +
                                    "-fx-border-color: #FECACA;" +
                                    "-fx-border-radius: 12;" +
                                    "-fx-background-radius: 12;" +
                                    "-fx-padding: 3 10;" +
                                    "-fx-font-weight: bold;" +
                                    "-fx-font-size: 11px;"
                            );
                        }
                    }
                    setGraphic(badge);
                    setText(null);
                }
            }
        });
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
                        "-fx-background-color: #70452F;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 12px;" +
                        "-fx-background-radius: 6;" +
                        "-fx-padding: 5 12;" +
                        "-fx-cursor: hand;"
                );

                statusButton.setStyle(
                        "-fx-background-color: #EFE2D3;" +
                        "-fx-text-fill: #56301F;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 12px;" +
                        "-fx-background-radius: 6;" +
                        "-fx-border-color: #D8C2AD;" +
                        "-fx-border-radius: 6;" +
                        "-fx-padding: 5 12;" +
                        "-fx-cursor: hand;"
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

                    HBox buttons = new HBox(8);
                    buttons.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
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
            ToastUtil.showInfo(orderTable, "Pesanan " + order.getOrderCode() + " sudah lunas.");
            return;
        }

        String catatanInfo = (order.getNotes() != null && !order.getNotes().isBlank())
                ? "\nCatatan Pesanan: " + order.getNotes()
                : "";

        boolean setuju = AlertUtil.konfirmasi(
                orderTable.getScene().getWindow(),
                "Konfirmasi Pembayaran",
                "Konfirmasi Pembayaran " + order.getOrderCode(),
                "Total Pembayaran: Rp" + order.getTotalAmount()
                        + "\nMetode: " + order.getPaymentMethod().getDisplayName()
                        + catatanInfo
                        + "\n\nPastikan pembayaran tunai telah diterima dengan benar.",
                "Konfirmasi Lunas",
                "Batal"
        );

        if (setuju) {
            boolean berhasil = orderDAO.ubahStatusPembayaran(
                    order.getId(),
                    PaymentStatus.LUNAS
            );

            if (berhasil) {
                ToastUtil.showSuccess(
                        orderTable,
                        "Pembayaran " + order.getOrderCode() + " berhasil dikonfirmasi (LUNAS)!"
                );
                muatDataPesanan();

            } else {
                ToastUtil.showWarning(
                        orderTable,
                        "Gagal mengonfirmasi pembayaran."
                );
            }
        }
    }

    // Mengubah status pesanan
    private void ubahStatusPesanan(Order order) {

        OrderStatus statusBaru = AlertUtil.pilihStatusPesanan(
                orderTable.getScene().getWindow(),
                order.getOrderCode(),
                order.getOrderStatus()
        );

        if (statusBaru == null || statusBaru == order.getOrderStatus()) {
            return;
        }

        boolean berhasil = orderDAO.ubahStatusPesanan(
                order.getId(),
                statusBaru
        );

        if (berhasil) {
            ToastUtil.showSuccess(
                    orderTable,
                    "Status pesanan " + order.getOrderCode() + " berhasil diubah ke " + statusBaru.name() + "!"
            );
            muatDataPesanan();

        } else {
            ToastUtil.showWarning(
                    orderTable,
                    "Gagal memperbarui status pesanan."
            );
        }
    }

    // Refresh tabel pesanan
    @FXML
    private void handleRefresh() {
        muatDataPesanan();
        ToastUtil.showInfo(orderTable, "Data pesanan berhasil dimuat ulang.");
    }

    private void tampilkanInformasi(String pesan) {
        ToastUtil.showInfo(orderTable, pesan);
    }

    private void tampilkanError(String pesan) {
        ToastUtil.showWarning(orderTable, pesan);
    }
}
