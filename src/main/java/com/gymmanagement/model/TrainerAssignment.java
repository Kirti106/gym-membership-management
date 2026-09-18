package com.gymmanagement.model;

import java.time.LocalDate;

public class TrainerAssignment {
    private int assignmentId;
    private int trainerId;
    private int memberId;
    private LocalDate assignedDate;
    private String status; // ACTIVE, COMPLETED, CANCELLED

    private String trainerName;
    private String memberName;

    public TrainerAssignment() {}

    public TrainerAssignment(int assignmentId, int trainerId, int memberId, LocalDate assignedDate, String status) {
        this.assignmentId = assignmentId;
        this.trainerId = trainerId;
        this.memberId = memberId;
        this.assignedDate = assignedDate;
        this.status = status;
    }

    public TrainerAssignment(int trainerId, int memberId, LocalDate assignedDate, String status) {
        this.trainerId = trainerId;
        this.memberId = memberId;
        this.assignedDate = assignedDate;
        this.status = status;
    }

    public int getAssignmentId() { return assignmentId; }
    public void setAssignmentId(int assignmentId) { this.assignmentId = assignmentId; }

    public int getTrainerId() { return trainerId; }
    public void setTrainerId(int trainerId) { this.trainerId = trainerId; }

    public int getMemberId() { return memberId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }

    public LocalDate getAssignedDate() { return assignedDate; }
    public void setAssignedDate(LocalDate assignedDate) { this.assignedDate = assignedDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTrainerName() { return trainerName; }
    public void setTrainerName(String trainerName) { this.trainerName = trainerName; }

    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }

    @Override
    public String toString() {
        return "TrainerAssignment{" +
                "assignmentId=" + assignmentId +
                ", trainerId=" + trainerId +
                ", memberId=" + memberId +
                ", assignedDate=" + assignedDate +
                ", status='" + status + '\'' +
                '}';
    }
}
