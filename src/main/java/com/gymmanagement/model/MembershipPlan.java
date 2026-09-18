package com.gymmanagement.model;

public class MembershipPlan {
    private int planId;
    private String planName;
    private int durationMonths;
    private double price;
    private String description;

    public MembershipPlan() {}

    public MembershipPlan(int planId, String planName, int durationMonths, double price, String description) {
        this.planId = planId;
        this.planName = planName;
        this.durationMonths = durationMonths;
        this.price = price;
        this.description = description;
    }

    public MembershipPlan(String planName, int durationMonths, double price, String description) {
        this.planName = planName;
        this.durationMonths = durationMonths;
        this.price = price;
        this.description = description;
    }

    public int getPlanId() { return planId; }
    public void setPlanId(int planId) { this.planId = planId; }

    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }

    public int getDurationMonths() { return durationMonths; }
    public void setDurationMonths(int durationMonths) { this.durationMonths = durationMonths; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return "MembershipPlan{" +
                "planId=" + planId +
                ", planName='" + planName + '\'' +
                ", durationMonths=" + durationMonths +
                ", price=$" + price +
                ", description='" + description + '\'' +
                '}';
    }
}
