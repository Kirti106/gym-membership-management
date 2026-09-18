package com.gymmanagement.dao;

import com.gymmanagement.exception.DatabaseException;
import com.gymmanagement.model.Payment;
import com.gymmanagement.model.PaymentMethod;
import com.gymmanagement.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PaymentDAOImpl implements PaymentDAO {

    @Override
    public boolean recordPayment(Payment payment) throws DatabaseException {
        String sql = "INSERT INTO payments (membership_id, member_id, amount, payment_date, payment_method, notes) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, payment.getMembershipId());
            stmt.setInt(2, payment.getMemberId());
            stmt.setDouble(3, payment.getAmount());
            stmt.setTimestamp(4, Timestamp.valueOf(payment.getPaymentDate()));
            stmt.setString(5, payment.getPaymentMethod().name());
            stmt.setString(6, payment.getNotes());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        payment.setPaymentId(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new DatabaseException("Error recording payment", e);
        }
    }

    @Override
    public Optional<Payment> getPaymentById(int paymentId) throws DatabaseException {
        String sql = "SELECT p.payment_id, p.membership_id, p.member_id, p.amount, p.payment_date, p.payment_method, p.notes, " +
                     "m.name AS member_name " +
                     "FROM payments p JOIN members m ON p.member_id = m.member_id " +
                     "WHERE p.payment_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, paymentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToPayment(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching payment record with ID: " + paymentId, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Payment> getPaymentsByMemberId(int memberId) throws DatabaseException {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT p.payment_id, p.membership_id, p.member_id, p.amount, p.payment_date, p.payment_method, p.notes, " +
                     "m.name AS member_name " +
                     "FROM payments p JOIN members m ON p.member_id = m.member_id " +
                     "WHERE p.member_id = ? ORDER BY p.payment_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, memberId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToPayment(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching payment history for member ID: " + memberId, e);
        }
        return list;
    }

    @Override
    public List<Payment> getAllPayments() throws DatabaseException {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT p.payment_id, p.membership_id, p.member_id, p.amount, p.payment_date, p.payment_method, p.notes, " +
                     "m.name AS member_name " +
                     "FROM payments p JOIN members m ON p.member_id = m.member_id " +
                     "ORDER BY p.payment_id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToPayment(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching all payments list", e);
        }
        return list;
    }

    @Override
    public double getTotalRevenue() throws DatabaseException {
        String sql = "SELECT SUM(amount) FROM payments";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error calculating total revenue", e);
        }
        return 0.0;
    }

    @Override
    public Map<String, Double> getMonthlyRevenue() throws DatabaseException {
        Map<String, Double> monthlyMap = new LinkedHashMap<>();
        String sql = "SELECT DATE_FORMAT(payment_date, '%Y-%m') AS month, SUM(amount) AS total " +
                     "FROM payments GROUP BY DATE_FORMAT(payment_date, '%Y-%m') ORDER BY month DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                monthlyMap.put(rs.getString("month"), rs.getDouble("total"));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error calculating monthly revenue report", e);
        }
        return monthlyMap;
    }

    private Payment mapResultSetToPayment(ResultSet rs) throws SQLException {
        Timestamp ts = rs.getTimestamp("payment_date");
        Payment p = new Payment(
                rs.getInt("payment_id"),
                rs.getInt("membership_id"),
                rs.getInt("member_id"),
                rs.getDouble("amount"),
                ts != null ? ts.toLocalDateTime() : null,
                PaymentMethod.fromString(rs.getString("payment_method")),
                rs.getString("notes")
        );

        try {
            p.setMemberName(rs.getString("member_name"));
        } catch (SQLException ignored) {}

        return p;
    }
}
