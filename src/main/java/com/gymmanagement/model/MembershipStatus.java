package com.gymmanagement.model;

public enum MembershipStatus {
    ACTIVE,
    INACTIVE,
    EXPIRED,
    CANCELLED;

    public static MembershipStatus fromString(String value) {
        if (value == null) return INACTIVE;
        try {
            return MembershipStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return INACTIVE;
        }
    }
}
