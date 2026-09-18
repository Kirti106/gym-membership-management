package com.gymmanagement.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Attendance {
    private int attendanceId;
    private int memberId;
    private LocalDate attendanceDate;
    private LocalDateTime checkInTime;

    private String memberName;

    public Attendance() {}

    public Attendance(int attendanceId, int memberId, LocalDate attendanceDate, LocalDateTime checkInTime) {
        this.attendanceId = attendanceId;
        this.memberId = memberId;
        this.attendanceDate = attendanceDate;
        this.checkInTime = checkInTime;
    }

    public Attendance(int memberId, LocalDate attendanceDate, LocalDateTime checkInTime) {
        this.memberId = memberId;
        this.attendanceDate = attendanceDate;
        this.checkInTime = checkInTime;
    }

    public int getAttendanceId() { return attendanceId; }
    public void setAttendanceId(int attendanceId) { this.attendanceId = attendanceId; }

    public int getMemberId() { return memberId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }

    public LocalDate getAttendanceDate() { return attendanceDate; }
    public void setAttendanceDate(LocalDate attendanceDate) { this.attendanceDate = attendanceDate; }

    public LocalDateTime getCheckInTime() { return checkInTime; }
    public void setCheckInTime(LocalDateTime checkInTime) { this.checkInTime = checkInTime; }

    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }

    @Override
    public String toString() {
        return "Attendance{" +
                "attendanceId=" + attendanceId +
                ", memberId=" + memberId +
                ", date=" + attendanceDate +
                ", time=" + checkInTime +
                '}';
    }
}
