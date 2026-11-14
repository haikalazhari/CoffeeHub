package com.voidsyntax.coffeehub.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.voidsyntax.coffeehub.enums.OrderStatus;
import com.voidsyntax.coffeehub.enums.PaymentMethod;
import com.voidsyntax.coffeehub.enums.PaymentStatus;
import com.voidsyntax.coffeehub.interfaces.Identifiable;

public class Order implements Identifiable {
    private int id;
    private String orderCode;
    private User pelanggan;
    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private OrderStatus orderStatus;
    private List<OrderDetail> orderDetails;

    public Order(int id, String orderCode, User pelanggan,
                 LocalDateTime orderDate, BigDecimal totalAmount,
                 PaymentMethod paymentMethod, PaymentStatus paymentStatus,
                 OrderStatus orderStatus) {

        this.id = id;
        this.orderCode = orderCode;
        this.pelanggan = pelanggan;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.orderStatus = orderStatus;
        this.orderDetails = new ArrayList<>();
    }

    @Override
    public int getId() {
        return id;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public User getPelanggan() {
        return pelanggan;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public void tambahDetail(OrderDetail detail) {
        orderDetails.add(detail);
    }

    public void tampilkanInfo() {
        System.out.println("=== INFORMASI PESANAN ===");
        System.out.println("ID Pesanan       : " + id);
        System.out.println("Kode Pesanan     : " + orderCode);
        System.out.println("Pelanggan        : " + pelanggan.getNama());
        System.out.println("Tanggal          : " + orderDate);
        System.out.println("Total            : Rp" + totalAmount);
        System.out.println("Metode Pembayaran: " + paymentMethod);
        System.out.println("Status Pembayaran: " + paymentStatus);
        System.out.println("Status Pesanan   : " + orderStatus);
    }
}
