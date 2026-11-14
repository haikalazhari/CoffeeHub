package com.voidsyntax.coffeehub.model;

import java.math.BigDecimal;

public class CartItem {

    private Product product;
    private int quantity;

    public CartItem(Product product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Produk tidak boleh null.");
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException("Jumlah pembelian harus lebih dari 0.");
        }

        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Produk tidak boleh null.");
        }

        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Jumlah pembelian harus lebih dari 0.");
        }

        this.quantity = quantity;
    }

    public BigDecimal getSubtotal() {
        return product.getHarga().multiply(BigDecimal.valueOf(quantity));
    }
}
