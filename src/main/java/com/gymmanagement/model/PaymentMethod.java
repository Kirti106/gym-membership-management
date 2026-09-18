package com.gymmanagement.model;

public enum PaymentMethod {
    CASH,
    CARD,
    CREDIT_CARD,
    DEBIT_CARD,
    UPI,
    NET_BANKING;

    public static PaymentMethod fromString(String value) {
        if (value == null) return CASH;
        String formatted = value.trim().toUpperCase().replace(" ", "_");
        try {
            return PaymentMethod.valueOf(formatted);
        } catch (IllegalArgumentException e) {
            return CASH;
        }
    }
}
