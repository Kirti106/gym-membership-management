package com.gymmanagement.dao;

import com.gymmanagement.exception.DatabaseException;
import com.gymmanagement.model.Payment;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PaymentDAO {
    boolean recordPayment(Payment payment) throws DatabaseException;
    Optional<Payment> getPaymentById(int paymentId) throws DatabaseException;
    List<Payment> getPaymentsByMemberId(int memberId) throws DatabaseException;
    List<Payment> getAllPayments() throws DatabaseException;
    double getTotalRevenue() throws DatabaseException;
    Map<String, Double> getMonthlyRevenue() throws DatabaseException;
}
