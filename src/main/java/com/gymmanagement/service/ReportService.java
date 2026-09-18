package com.gymmanagement.service;

import com.gymmanagement.dao.*;
import com.gymmanagement.exception.DatabaseException;
import com.gymmanagement.model.Membership;

import java.util.List;
import java.util.Map;

public class ReportService {

    private final MemberDAO memberDAO;
    private final MembershipDAO membershipDAO;
    private final PaymentDAO paymentDAO;
    private final AttendanceDAO attendanceDAO;
    private final TrainerDAO trainerDAO;

    public ReportService() {
        this.memberDAO = new MemberDAOImpl();
        this.membershipDAO = new MembershipDAOImpl();
        this.paymentDAO = new PaymentDAOImpl();
        this.attendanceDAO = new AttendanceDAOImpl();
        this.trainerDAO = new TrainerDAOImpl();
    }

    public ReportService(MemberDAO memberDAO, MembershipDAO membershipDAO, PaymentDAO paymentDAO, AttendanceDAO attendanceDAO, TrainerDAO trainerDAO) {
        this.memberDAO = memberDAO;
        this.membershipDAO = membershipDAO;
        this.paymentDAO = paymentDAO;
        this.attendanceDAO = attendanceDAO;
        this.trainerDAO = trainerDAO;
    }

    public int getTotalMembersCount() throws DatabaseException {
        return memberDAO.getTotalMembersCount();
    }

    public int getActiveMembersCount() throws DatabaseException {
        return memberDAO.getActiveMembersCount();
    }

    public List<Membership> getExpiredMemberships() throws DatabaseException {
        return membershipDAO.getExpiredMemberships();
    }

    public List<Membership> getUpcomingExpiries(int days) throws DatabaseException {
        return membershipDAO.getUpcomingExpiringMemberships(days);
    }

    public double getTotalRevenue() throws DatabaseException {
        return paymentDAO.getTotalRevenue();
    }

    public Map<String, Double> getMonthlyRevenue() throws DatabaseException {
        return paymentDAO.getMonthlyRevenue();
    }

    public Map<String, Integer> getMostActiveMembers(int limit) throws DatabaseException {
        return attendanceDAO.getMostActiveMembers(limit);
    }

    public Map<String, Integer> getTrainerMemberCounts() throws DatabaseException {
        return trainerDAO.getTrainerMemberCounts();
    }
}
