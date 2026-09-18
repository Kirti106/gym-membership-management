package com.gymmanagement.service;

import com.gymmanagement.dao.MemberDAO;
import com.gymmanagement.dao.MemberDAOImpl;
import com.gymmanagement.dao.PaymentDAO;
import com.gymmanagement.dao.PaymentDAOImpl;
import com.gymmanagement.exception.*;
import com.gymmanagement.model.Member;
import com.gymmanagement.model.Payment;
import com.gymmanagement.model.PaymentMethod;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class PaymentService {

    private final PaymentDAO paymentDAO;
    private final MemberDAO memberDAO;

    public PaymentService() {
        this.paymentDAO = new PaymentDAOImpl();
        this.memberDAO = new MemberDAOImpl();
    }

    public PaymentService(PaymentDAO paymentDAO, MemberDAO memberDAO) {
        this.paymentDAO = paymentDAO;
        this.memberDAO = memberDAO;
    }

    public Payment processPayment(int membershipId, int memberId, double amount, String paymentMethodStr, String notes) throws GymManagementException {
        Member member = memberDAO.getMemberById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Member with ID " + memberId + " not found."));

        if (amount <= 0) {
            throw new InvalidPaymentException("Payment amount must be greater than zero. Received: $" + amount);
        }

        PaymentMethod method = PaymentMethod.fromString(paymentMethodStr);
        Payment payment = new Payment(membershipId, memberId, amount, LocalDateTime.now(), method, notes);
        payment.setMemberName(member.getName());

        boolean success = paymentDAO.recordPayment(payment);
        if (!success) {
            throw new DatabaseException("Failed to record payment in database.");
        }

        return payment;
    }

    public List<Payment> getPaymentsByMember(int memberId) throws MemberNotFoundException, DatabaseException {
        memberDAO.getMemberById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Member with ID " + memberId + " not found."));
        return paymentDAO.getPaymentsByMemberId(memberId);
    }

    public Payment getPaymentReceipt(int paymentId) throws GymManagementException {
        return paymentDAO.getPaymentById(paymentId)
                .orElseThrow(() -> new InvalidPaymentException("No payment receipt found with ID: " + paymentId));
    }

    public List<Payment> getAllPayments() throws DatabaseException {
        return paymentDAO.getAllPayments();
    }

    public double getTotalRevenue() throws DatabaseException {
        return paymentDAO.getTotalRevenue();
    }

    public Map<String, Double> getMonthlyRevenue() throws DatabaseException {
        return paymentDAO.getMonthlyRevenue();
    }
}
