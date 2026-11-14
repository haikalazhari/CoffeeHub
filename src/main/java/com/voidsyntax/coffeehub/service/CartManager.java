package com.voidsyntax.coffeehub.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.voidsyntax.coffeehub.model.CartItem;
import com.voidsyntax.coffeehub.model.Product;

public class CartManager {

    // Setiap user memiliki keranjang masing-masing.
    private static final Map<Integer, List<CartItem>> carts = new HashMap<>();

    private CartManager() {
        // Mencegah pembuatan objek CartManager.
    }

    public static List<CartItem> getItems(int userId) {
        return Collections.unmodifiableList(
                carts.getOrDefault(userId, Collections.emptyList())
        );
    }

    public static void tambahProduk(int userId, Product product, int quantity) {
        if (userId <= 0) {
            throw new IllegalArgumentException("ID pengguna tidak valid.");
        }

        if (product == null) {
            throw new IllegalArgumentException("Produk tidak boleh null.");
        }

        if (!product.isTersedia() || product.getStok() <= 0) {
            throw new IllegalArgumentException("Produk sedang tidak tersedia.");
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException("Jumlah pembelian harus lebih dari 0.");
        }

        List<CartItem> cartItems = carts.computeIfAbsent(
                userId, id -> new ArrayList<>()
        );

        CartItem existingItem = cariItem(userId, product.getId());

        int totalQuantity = quantity;

        if (existingItem != null) {
            totalQuantity += existingItem.getQuantity();
        }

        if (totalQuantity > product.getStok()) {
            throw new IllegalArgumentException(
                    "Jumlah pembelian melebihi stok yang tersedia."
            );
        }

        if (existingItem != null) {
            existingItem.setQuantity(totalQuantity);
        } else {
            cartItems.add(new CartItem(product, quantity));
        }
    }

    public static void ubahJumlah(int userId, int productId, int quantity) {
        CartItem item = cariItem(userId, productId);

        if (item == null) {
            throw new IllegalArgumentException(
                    "Produk tidak ditemukan di keranjang."
            );
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException("Jumlah harus lebih dari 0.");
        }

        if (quantity > item.getProduct().getStok()) {
            throw new IllegalArgumentException(
                    "Jumlah pembelian melebihi stok yang tersedia."
            );
        }

        item.setQuantity(quantity);
    }

    public static void hapusProduk(int userId, int productId) {
        List<CartItem> cartItems = carts.get(userId);

        if (cartItems != null) {
            cartItems.removeIf(
                    item -> item.getProduct().getId() == productId
            );
        }
    }

    public static void kosongkanKeranjang(int userId) {
        List<CartItem> cartItems = carts.get(userId);

        if (cartItems != null) {
            cartItems.clear();
        }
    }

    public static BigDecimal hitungTotal(int userId) {
        BigDecimal total = BigDecimal.ZERO;

        for (CartItem item : getItems(userId)) {
            total = total.add(item.getSubtotal());
        }

        return total;
    }

    public static int hitungJumlahItem(int userId) {
        return getItems(userId).stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    public static boolean isEmpty(int userId) {
        return getItems(userId).isEmpty();
    }

    private static CartItem cariItem(int userId, int productId) {
        return getItems(userId).stream()
                .filter(item -> item.getProduct().getId() == productId)
                .findFirst()
                .orElse(null);
    }
}
