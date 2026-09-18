package com.gymmanagement.service;

import com.gymmanagement.dao.*;
import com.gymmanagement.exception.*;
import com.gymmanagement.model.Attendance;
import com.gymmanagement.model.Member;
import com.gymmanagement.model.Membership;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AttendanceService {

    private final AttendanceDAO attendanceDAO;
    private final MemberDAO memberDAO;
    private final MembershipDAO membershipDAO;

    public AttendanceService() {
        this.attendanceDAO = new AttendanceDAOImpl();
        this.memberDAO = new MemberDAOImpl();
        this.membershipDAO = new MembershipDAOImpl();
    }

    public AttendanceService(AttendanceDAO attendanceDAO, MemberDAO memberDAO, MembershipDAO membershipDAO) {
        this.attendanceDAO = attendanceDAO;
        this.memberDAO = memberDAO;
        this.membershipDAO = membershipDAO;
    }

    public Attendance checkInMember(int memberId) throws GymManagementException {
        Member member = memberDAO.getMemberById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Member with ID " + memberId + " not found."));

        Optional<Membership> activeMembership = membershipDAO.getActiveMembershipByMemberId(memberId);
        if (activeMembership.isEmpty()) {
            throw new MembershipExpiredException("Cannot check in: Member ID " + memberId + " (" + member.getName() + ") has no active membership.");
        }

        LocalDate today = LocalDate.now();
        if (attendanceDAO.isAttendanceMarked(memberId, today)) {
            throw new DuplicateAttendanceException("Member " + member.getName() + " (ID: " + memberId + ") has already checked in today (" + today + ").");
        }

        Attendance attendance = new Attendance(memberId, today, LocalDateTime.now());
        attendance.setMemberName(member.getName());

        boolean success = attendanceDAO.markAttendance(attendance);
        if (!success) {
            throw new DatabaseException("Failed to record attendance check-in.");
        }
        return attendance;
    }

    public List<Attendance> getMemberAttendanceHistory(int memberId) throws MemberNotFoundException, DatabaseException {
        memberDAO.getMemberById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Member with ID " + memberId + " not found."));
        return attendanceDAO.getAttendanceByMemberId(memberId);
    }

    public List<Attendance> getDailyAttendanceLog(LocalDate date) throws DatabaseException {
        if (date == null) date = LocalDate.now();
        return attendanceDAO.getAttendanceByDate(date);
    }

    public int getAttendanceCountForMember(int memberId) throws MemberNotFoundException, DatabaseException {
        memberDAO.getMemberById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Member with ID " + memberId + " not found."));
        return attendanceDAO.getAttendanceCountForMember(memberId);
    }

    public Map<String, Integer> getMostActiveMembers(int limit) throws DatabaseException {
        return attendanceDAO.getMostActiveMembers(limit > 0 ? limit : 5);
    }
}
