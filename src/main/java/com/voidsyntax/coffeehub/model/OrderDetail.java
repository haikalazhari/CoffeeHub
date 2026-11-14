package com.voidsyntax.coffeehub.model;

import java.math.BigDecimal;

public class OrderDetail {
    private int id;
    private Product produk;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;

    public OrderDetail(int id, Product produk, int quantity,
                       BigDecimal unitPrice) {
        this.id = id;
        this.produk = produk;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = hitungSubtotal();
    }

    public int getId() {
        return id;
    }

    public Product getProduk() {
        return produk;
    }

    public void setProduk(Product produk) {
        this.produk = produk;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.subtotal = hitungSubtotal();
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        this.subtotal = hitungSubtotal();
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public BigDecimal hitungSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public void tampilkanInfo() {
        System.out.println("=== DETAIL PESANAN ===");
        System.out.println("Produk   : " + produk.getNama());
        System.out.println("Jumlah   : " + quantity);
        System.out.println("Harga    : Rp" + unitPrice);
        System.out.println("Subtotal : Rp" + subtotal);
    }
}
