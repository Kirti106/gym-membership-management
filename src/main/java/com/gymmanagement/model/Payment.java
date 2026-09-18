package com.gymmanagement.model;

import java.time.LocalDateTime;

public class Payment {
    private int paymentId;
    private int membershipId;
    private int memberId;
    private double amount;
    private LocalDateTime paymentDate;
    private PaymentMethod paymentMethod;
    private String notes;

    private String memberName;

    public Payment() {}

    public Payment(int paymentId, int membershipId, int memberId, double amount, LocalDateTime paymentDate, PaymentMethod paymentMethod, String notes) {
        this.paymentId = paymentId;
        this.membershipId = membershipId;
        this.memberId = memberId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.notes = notes;
    }

    public Payment(int membershipId, int memberId, double amount, LocalDateTime paymentDate, PaymentMethod paymentMethod, String notes) {
        this.membershipId = membershipId;
        this.memberId = memberId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.notes = notes;
    }

    public int getPaymentId() { return paymentId; }
    public void setPaymentId(int paymentId) { this.paymentId = paymentId; }

    public int getMembershipId() { return membershipId; }
    public void setMembershipId(int membershipId) { this.membershipId = membershipId; }

    public int getMemberId() { return memberId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentId=" + paymentId +
                ", membershipId=" + membershipId +
                ", memberId=" + memberId +
                ", amount=$" + amount +
                ", paymentDate=" + paymentDate +
                ", paymentMethod=" + paymentMethod +
                '}';
    }
}
