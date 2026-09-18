package com.gymmanagement;

import com.gymmanagement.dao.MemberDAO;
import com.gymmanagement.dao.PaymentDAO;
import com.gymmanagement.exception.InvalidPaymentException;
import com.gymmanagement.model.Gender;
import com.gymmanagement.model.Member;
import com.gymmanagement.model.MembershipStatus;
import com.gymmanagement.model.Payment;
import com.gymmanagement.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentServiceTest {

    private PaymentService paymentService;

    @BeforeEach
    public void setUp() throws Exception {
        MemberDAO memberDAO = new MockMemberDAO();
        PaymentDAO paymentDAO = new MockPaymentDAO();

        memberDAO.addMember(new Member(1, "Alice Smith", 28, Gender.FEMALE, "9123456780", "alice@example.com", LocalDate.now(), MembershipStatus.ACTIVE));

        paymentService = new PaymentService(paymentDAO, memberDAO);
    }

    @Test
    public void testProcessPaymentSuccess() throws Exception {
        Payment p = paymentService.processPayment(1, 1, 149.99, "UPI", "Paid for quarterly membership");
        assertNotNull(p);
        assertEquals(149.99, p.getAmount());
        assertEquals("UPI", p.getPaymentMethod().name());
    }

    @Test
    public void testNegativePaymentThrowsException() {
        assertThrows(InvalidPaymentException.class, () ->
                paymentService.processPayment(1, 1, -50.00, "CASH", "Invalid payment"));
    }

    private static class MockMemberDAO implements MemberDAO {
        private final List<Member> members = new ArrayList<>();
        @Override public boolean addMember(Member m) { members.add(m); return true; }
        @Override public List<Member> getAllMembers() { return members; }
        @Override public Optional<Member> getMemberById(int id) { return members.stream().filter(m -> m.getMemberId() == id).findFirst(); }
        @Override public List<Member> searchMembersByName(String name) { return members; }
        @Override public Optional<Member> getMemberByPhone(String phone) { return Optional.empty(); }
        @Override public boolean updateMember(Member m) { return true; }
        @Override public boolean deleteMember(int id) { return true; }
        @Override public boolean updateMemberStatus(int id, String status) { return true; }
        @Override public int getTotalMembersCount() { return members.size(); }
        @Override public int getActiveMembersCount() { return members.size(); }
    }

    private static class MockPaymentDAO implements PaymentDAO {
        private final List<Payment> payments = new ArrayList<>();
        @Override public boolean recordPayment(Payment p) { p.setPaymentId(1); payments.add(p); return true; }
        @Override public Optional<Payment> getPaymentById(int id) { return payments.stream().filter(p -> p.getPaymentId() == id).findFirst(); }
        @Override public List<Payment> getPaymentsByMemberId(int id) { return payments; }
        @Override public List<Payment> getAllPayments() { return payments; }
        @Override public double getTotalRevenue() { return payments.stream().mapToDouble(Payment::getAmount).sum(); }
        @Override public Map<String, Double> getMonthlyRevenue() { return Map.of("2026-03", 149.99); }
    }
}
