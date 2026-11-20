package com.voidsyntax.coffeehub.enums;

public enum PaymentMethod {
    TUNAI("Tunai (Cash)"),
    QRIS("QRIS"),
    TRANSFER_BCA("Transfer Bank BCA");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
