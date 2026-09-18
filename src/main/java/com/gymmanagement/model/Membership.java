package com.gymmanagement.model;

import java.time.LocalDate;

public class Membership {
    private int membershipId;
    private int memberId;
    private int planId;
    private LocalDate startDate;
    private LocalDate endDate;
    private MembershipStatus status;

    private String memberName;
    private String planName;

    public Membership() {}

    public Membership(int membershipId, int memberId, int planId, LocalDate startDate, LocalDate endDate, MembershipStatus status) {
        this.membershipId = membershipId;
        this.memberId = memberId;
        this.planId = planId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public Membership(int memberId, int planId, LocalDate startDate, LocalDate endDate, MembershipStatus status) {
        this.memberId = memberId;
        this.planId = planId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public int getMembershipId() { return membershipId; }
    public void setMembershipId(int membershipId) { this.membershipId = membershipId; }

    public int getMemberId() { return memberId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }

    public int getPlanId() { return planId; }
    public void setPlanId(int planId) { this.planId = planId; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public MembershipStatus getStatus() { return status; }
    public void setStatus(MembershipStatus status) { this.status = status; }

    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }

    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }

    public boolean isExpired() {
        return endDate != null && endDate.isBefore(LocalDate.now());
    }

    @Override
    public String toString() {
        return "Membership{" +
                "membershipId=" + membershipId +
                ", memberId=" + memberId +
                ", planId=" + planId +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", status=" + status +
                '}';
    }
}
