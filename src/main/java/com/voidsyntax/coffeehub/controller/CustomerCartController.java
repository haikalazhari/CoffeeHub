package com.voidsyntax.coffeehub.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.voidsyntax.coffeehub.enums.OrderStatus;
import com.voidsyntax.coffeehub.enums.PaymentMethod;
import com.voidsyntax.coffeehub.enums.PaymentStatus;
import com.voidsyntax.coffeehub.model.CartItem;
import com.voidsyntax.coffeehub.model.Order;
import com.voidsyntax.coffeehub.model.OrderDetail;
import com.voidsyntax.coffeehub.model.Pelanggan;
import com.voidsyntax.coffeehub.model.Product;
import com.voidsyntax.coffeehub.model.User;
import com.voidsyntax.coffeehub.repository.OrderDAO;
import com.voidsyntax.coffeehub.repository.ProductDAO;
import com.voidsyntax.coffeehub.service.CartManager;
import com.voidsyntax.coffeehub.service.SessionManager;
import com.voidsyntax.coffeehub.util.AlertUtil;
import com.voidsyntax.coffeehub.util.ToastUtil;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class CustomerCartController {

    @FXML
    private VBox cartItemsContainer;

    @FXML
    private CheckBox selectAllCheckBox;

    @FXML
    private Label totalLabel;

    @FXML
    private Label itemCountLabel;

    @FXML
    private RadioButton tunaiRadio;

    @FXML
    private RadioButton qrisRadio;

    @FXML
    private RadioButton bcaRadio;

    @FXML
    private ToggleGroup paymentToggleGroup;

    @FXML
    private VBox paymentInfoBox;

    @FXML
    private Label paymentTitleLabel;

    @FXML
    private Label paymentDetailLabel;

    @FXML
    private TextArea orderNotesField;

    private final Set<Integer> selectedProductIds = new HashSet<>();
    private boolean updatingSelectAll = false;

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
        int userId = getCurrentUserId();
        for (CartItem item : CartManager.getItems(userId)) {
            selectedProductIds.add(item.getProduct().getId());
        }

        tampilkanKeranjang();

        if (paymentToggleGroup != null) {
            paymentToggleGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
                perbaruiInfoPembayaran();
            });
            perbaruiInfoPembayaran();
        }
    }

    @FXML
    private void handleSelectAll() {
        if (updatingSelectAll || selectAllCheckBox == null) {
            return;
        }

        int userId = getCurrentUserId();
        List<CartItem> items = CartManager.getItems(userId);

        if (selectAllCheckBox.isSelected()) {
            for (CartItem item : items) {
                selectedProductIds.add(item.getProduct().getId());
            }
        } else {
            selectedProductIds.clear();
        }

        tampilkanKeranjang();
    }

    private void perbaruiSelectAllState() {
        if (selectAllCheckBox == null) {
            return;
        }

        int userId = getCurrentUserId();
        List<CartItem> items = CartManager.getItems(userId);

        if (items.isEmpty()) {
            selectAllCheckBox.setSelected(false);
            selectAllCheckBox.setDisable(true);
            return;
        }

        selectAllCheckBox.setDisable(false);
        updatingSelectAll = true;

        List<Integer> allIds = items.stream()
                .map(i -> i.getProduct().getId())
                .toList();

        selectAllCheckBox.setSelected(!allIds.isEmpty() && selectedProductIds.containsAll(allIds));
        updatingSelectAll = false;
    }

    private void perbaruiInfoPembayaran() {
        if (paymentTitleLabel == null || paymentDetailLabel == null) {
            return;
        }

        if (qrisRadio != null && qrisRadio.isSelected()) {
            paymentTitleLabel.setText("📱 Pembayaran via QRIS");
            paymentDetailLabel.setText(
                    "NMID: ID1020304050607\n"
                    + "Merchant: CoffeeHub Official\n"
                    + "Scan QRIS via BCA, Mandiri, GoPay, OVO, Dana, dll."
            );
        } else if (bcaRadio != null && bcaRadio.isSelected()) {
            paymentTitleLabel.setText("💳 Transfer Bank BCA");
            paymentDetailLabel.setText(
                    "No. Rekening: 8831 2345 6789\n"
                    + "Atas Nama: CoffeeHub Indonesia\n"
                    + "Transfer sesuai total belanja & simpan bukti transfer."
            );
        } else {
            paymentTitleLabel.setText("💵 Pembayaran Tunai");
            paymentDetailLabel.setText(
                    "Bayar langsung secara tunai di meja kasir saat mengambil pesanan."
            );
        }
    }

    private PaymentMethod getSelectedPaymentMethod() {
        if (qrisRadio != null && qrisRadio.isSelected()) {
            return PaymentMethod.QRIS;
        } else if (bcaRadio != null && bcaRadio.isSelected()) {
            return PaymentMethod.TRANSFER_BCA;
        }
        return PaymentMethod.TUNAI;
    }

    private void tampilkanKeranjang() {
        cartItemsContainer.getChildren().clear();

        int userId = getCurrentUserId();
        List<CartItem> items = CartManager.getItems(userId);

        Set<Integer> currentProductIds = items.stream()
                .map(i -> i.getProduct().getId())
                .collect(Collectors.toSet());
        selectedProductIds.retainAll(currentProductIds);

        if (items.isEmpty()) {
            Label emptyLabel = new Label("Keranjang belanja masih kosong.");
            emptyLabel.setStyle(
                    "-fx-font-size: 14px;" +
                    "-fx-text-fill: #8B7562;"
            );

            cartItemsContainer.getChildren().add(emptyLabel);
        } else {
            for (CartItem item : items) {
                cartItemsContainer.getChildren().add(buatKartuItem(item));
            }
        }

        perbaruiSelectAllState();
        perbaruiRingkasan();
    }

    private VBox buatKartuItem(CartItem item) {
        int productId = item.getProduct().getId();
        boolean isSelected = selectedProductIds.contains(productId);

        VBox card = new VBox(12);
        card.setPadding(new Insets(16, 18, 16, 18));

        String styleSelected =
                "-fx-background-color: white;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: #C58B55;" +
                "-fx-border-width: 1.5;" +
                "-fx-border-radius: 12;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(112, 69, 47, 0.08), 8, 0, 0, 2);";

        String styleUnselected =
                "-fx-background-color: #FCFAF8;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: #E8D8C8;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 12;" +
                "-fx-opacity: 0.85;";

        card.setStyle(isSelected ? styleSelected : styleUnselected);

        // Header Baris: CheckBox, Nama Produk, Spacer, Harga Satuan
        HBox topRow = new HBox(12);
        topRow.setAlignment(Pos.CENTER_LEFT);

        CheckBox itemCheckBox = new CheckBox();
        itemCheckBox.setSelected(isSelected);
        itemCheckBox.setStyle("-fx-cursor: hand;");
        itemCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                selectedProductIds.add(productId);
                card.setStyle(styleSelected);
            } else {
                selectedProductIds.remove(productId);
                card.setStyle(styleUnselected);
            }
            perbaruiSelectAllState();
            perbaruiRingkasan();
        });

        Label namaLabel = new Label(item.getProduct().getNama());
        namaLabel.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #493629;"
        );

        Region nameSpacer = new Region();
        HBox.setHgrow(nameSpacer, Priority.ALWAYS);

        Label hargaLabel = new Label(
                currencyFormat.format(item.getProduct().getHarga())
        );
        hargaLabel.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #70452F;"
        );

        topRow.getChildren().addAll(itemCheckBox, namaLabel, nameSpacer, hargaLabel);

        // Baris Kontrol Jumlah & Tombol Remove
        HBox bottomRow = new HBox(12);
        bottomRow.setAlignment(Pos.CENTER_LEFT);

        Label quantityLabel = new Label(
                "Jumlah: " + item.getQuantity()
        );
        quantityLabel.setStyle("-fx-text-fill: #493629; -fx-font-size: 13px;");

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
                        productId,
                        quantity - 1
                );

                tampilkanKeranjang();
                ToastUtil.showInfo(cartItemsContainer, "Jumlah " + item.getProduct().getNama() + " dikurangi.");
            }
        });

        increaseButton.setOnAction(event -> {
            try {
                CartManager.ubahJumlah(
                        getCurrentUserId(),
                        productId,
                        item.getQuantity() + 1
                );

                tampilkanKeranjang();
                ToastUtil.showSuccess(cartItemsContainer, "Jumlah " + item.getProduct().getNama() + " ditambah.");

            } catch (IllegalArgumentException e) {
                ToastUtil.showWarning(cartItemsContainer, e.getMessage());
            }
        });

        removeButton.setOnAction(event -> {
            CartManager.hapusProduk(
                    getCurrentUserId(),
                    productId
            );
            selectedProductIds.remove(productId);

            tampilkanKeranjang();
            ToastUtil.showInfo(cartItemsContainer, item.getProduct().getNama() + " dihapus dari keranjang.");
        });

        HBox quantityBox = new HBox(8);
        quantityBox.setAlignment(Pos.CENTER_LEFT);
        quantityBox.getChildren().addAll(
                decreaseButton,
                quantityLabel,
                increaseButton
        );

        Region bottomSpacer = new Region();
        HBox.setHgrow(bottomSpacer, Priority.ALWAYS);

        Label subtotalLabel = new Label(
                "Subtotal: " + currencyFormat.format(item.getSubtotal())
        );
        subtotalLabel.setStyle(
                "-fx-font-weight: bold;" +
                "-fx-font-size: 14px;" +
                "-fx-text-fill: #795548;"
        );

        bottomRow.getChildren().addAll(
                quantityBox,
                removeButton,
                bottomSpacer,
                subtotalLabel
        );

        card.getChildren().addAll(
                topRow,
                bottomRow
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
        int selectedCount = 0;
        BigDecimal selectedTotal = BigDecimal.ZERO;

        for (CartItem item : CartManager.getItems(userId)) {
            if (selectedProductIds.contains(item.getProduct().getId())) {
                selectedCount += item.getQuantity();
                selectedTotal = selectedTotal.add(item.getSubtotal());
            }
        }

        itemCountLabel.setText(selectedCount + " item");
        totalLabel.setText(currencyFormat.format(selectedTotal));
    }

    private void tampilkanPeringatan(String pesan) {
        AlertUtil.peringatan(
                cartItemsContainer.getScene().getWindow(),
                "Peringatan",
                "Perhatian",
                pesan
        );
    }

    private final ProductDAO productDAO = new ProductDAO();
    private final OrderDAO orderDAO = new OrderDAO();

    @FXML
    private void handleCheckout() {
        int userId = getCurrentUserId();
        User user = SessionManager.getCurrentUser();

        List<CartItem> allItems = CartManager.getItems(userId);
        if (allItems.isEmpty()) {
            tampilkanPeringatan(
                    "Keranjang masih kosong. Tambahkan produk terlebih dahulu."
            );
            return;
        }

        List<CartItem> selectedItems = allItems.stream()
                .filter(item -> selectedProductIds.contains(item.getProduct().getId()))
                .toList();

        if (selectedItems.isEmpty()) {
            ToastUtil.showWarning(
                    cartItemsContainer,
                    "Silakan pilih setidaknya satu produk untuk di-checkout."
            );
            return;
        }

        BigDecimal total = BigDecimal.ZERO;
        int totalItem = 0;
        for (CartItem item : selectedItems) {
            totalItem += item.getQuantity();
            total = total.add(item.getSubtotal());
        }

        PaymentMethod paymentMethod = getSelectedPaymentMethod();
        String paymentDesc = switch (paymentMethod) {
            case QRIS -> "QRIS (NMID: ID1020304050607 - CoffeeHub Official)";
            case TRANSFER_BCA -> "Transfer BCA (No. Rek: 8831 2345 6789 a/n CoffeeHub Indonesia)";
            case TUNAI -> "Tunai (Cash di Kasir)";
        };

        String notes = (orderNotesField != null && orderNotesField.getText() != null)
                ? orderNotesField.getText().trim()
                : "";

        String notesDisplay = notes.isBlank() ? "-" : notes;

        boolean lanjut = AlertUtil.konfirmasi(
                cartItemsContainer.getScene().getWindow(),
                "Konfirmasi Checkout",
                "Konfirmasi Pemesanan",
                "Ringkasan Pesanan:\n" +
                "• Total Item: " + totalItem + " item\n" +
                "• Total Biaya: " + currencyFormat.format(total) + "\n" +
                "• Metode Pembayaran: " + paymentDesc + "\n" +
                "• Catatan: " + notesDisplay + "\n\n" +
                "Lanjutkan dan buat pesanan ini?",
                "Pesan Sekarang",
                "Batal"
        );

        if (!lanjut) {
            return;
        }

        // Validasi stok produk terkini untuk item yang dipilih
        List<OrderDetail> orderDetails = new ArrayList<>();
        BigDecimal totalPesanan = BigDecimal.ZERO;

        for (CartItem item : selectedItems) {
            Product product = productDAO.cariBerdasarkanId(
                    item.getProduct().getId()
            );

            if (product == null || !product.isTersedia()) {
                tampilkanPeringatan(
                        "Produk '" + item.getProduct().getNama()
                        + "' saat ini sedang tidak tersedia."
                );
                return;
            }

            if (product.getStok() < item.getQuantity()) {
                tampilkanPeringatan(
                        "Stok untuk '" + product.getNama()
                        + "' tidak mencukupi (Tersisa: "
                        + product.getStok() + ")."
                );
                return;
            }

            BigDecimal unitPrice = product.getHarga();
            OrderDetail detail = new OrderDetail(
                    0,
                    product,
                    item.getQuantity(),
                    unitPrice
            );

            orderDetails.add(detail);
            totalPesanan = totalPesanan.add(detail.getSubtotal());
        }

        // Buat kode pesanan unik
        String orderCode = "CH-"
                + UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase(Locale.ROOT);

        Order order = new Order(
                0,
                orderCode,
                (Pelanggan) user,
                LocalDateTime.now(),
                totalPesanan,
                paymentMethod,
                PaymentStatus.BELUM_BAYAR,
                OrderStatus.MENUNGGU,
                notes.isBlank() ? null : notes
        );

        for (OrderDetail detail : orderDetails) {
            order.tambahDetail(detail);
        }

        boolean berhasil = orderDAO.tambah(order);

        if (berhasil) {
            // Hapus HANYA item yang dipilih dan dicheckout dari keranjang
            for (CartItem item : selectedItems) {
                CartManager.hapusProduk(userId, item.getProduct().getId());
                selectedProductIds.remove(item.getProduct().getId());
            }
            if (orderNotesField != null) {
                orderNotesField.clear();
            }

            String instruksiBayar = switch (paymentMethod) {
                case QRIS -> "Silakan scan QRIS CoffeeHub (NMID: ID1020304050607) sebesar "
                        + currencyFormat.format(totalPesanan) + " dan tunjukkan bukti ke kasir.";
                case TRANSFER_BCA -> "Silakan transfer ke BCA 8831 2345 6789 a/n CoffeeHub Indonesia sebesar "
                        + currencyFormat.format(totalPesanan) + " dan tunjukkan bukti ke kasir.";
                case TUNAI -> "Silakan lakukan pembayaran tunai di kasir saat mengambil pesanan.";
            };

            String catatanInfo = notes.isBlank() ? "" : "\nCatatan: " + notes + "\n";

            AlertUtil.sukses(
                    cartItemsContainer.getScene().getWindow(),
                    "Pesanan Berhasil",
                    "Pesanan Berhasil Dibuat!",
                    "Kode Pesanan: " + orderCode + "\n" +
                    "Total: " + currencyFormat.format(totalPesanan) + "\n" +
                    "Metode: " + paymentDesc + "\n" +
                    catatanInfo + "\n" +
                    instruksiBayar + "\n\n" +
                    "Pesanan Anda akan ditampilkan di riwayat pesanan."
            );

            // Langsung arahkan ke halaman My Orders
            if (CustomerController.getInstance() != null) {
                CustomerController.getInstance().bukaHalamanOrders();
            } else {
                try {
                    FXMLLoader loader = new FXMLLoader(
                            getClass().getResource("/fxml/customer-orders.fxml")
                    );
                    Node ordersPage = loader.load();
                    Parent parent = cartItemsContainer.getParent();
                    while (parent != null && !(parent instanceof StackPane)) {
                        parent = parent.getParent();
                    }
                    if (parent instanceof StackPane contentArea) {
                        contentArea.getChildren().setAll(ordersPage);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } else {
            tampilkanPeringatan(
                    "Pesanan gagal disimpan. Periksa kembali stok produk atau coba lagi."
            );
        }
    }
}
