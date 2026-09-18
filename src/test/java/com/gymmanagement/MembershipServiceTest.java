package com.gymmanagement;

import com.gymmanagement.dao.MemberDAO;
import com.gymmanagement.dao.MembershipDAO;
import com.gymmanagement.dao.MembershipPlanDAO;
import com.gymmanagement.model.*;
import com.gymmanagement.service.MembershipService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class MembershipServiceTest {

    private MembershipService membershipService;

    @BeforeEach
    public void setUp() throws Exception {
        MemberDAO memberDAO = new MockMemberDAO();
        MembershipPlanDAO planDAO = new MockPlanDAO();
        MembershipDAO membershipDAO = new MockMembershipDAO();

        memberDAO.addMember(new Member(1, "John Doe", 30, Gender.MALE, "9999911111", "john@example.com", LocalDate.now(), MembershipStatus.ACTIVE));
        planDAO.addPlan(new MembershipPlan(1, "Annual VIP", 12, 399.99, "Full VIP Access"));

        membershipService = new MembershipService(membershipDAO, memberDAO, planDAO);
    }

    @Test
    public void testEnrollMemberCalculatesEndDate() throws Exception {
        Membership ms = membershipService.enrollMember(1, 1, LocalDate.of(2026, 1, 1));
        assertNotNull(ms);
        assertEquals(LocalDate.of(2026, 1, 1), ms.getStartDate());
        assertEquals(LocalDate.of(2027, 1, 1), ms.getEndDate());
        assertEquals(MembershipStatus.ACTIVE, ms.getStatus());
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

    private static class MockPlanDAO implements MembershipPlanDAO {
        private final List<MembershipPlan> plans = new ArrayList<>();
        @Override public boolean addPlan(MembershipPlan p) { plans.add(p); return true; }
        @Override public List<MembershipPlan> getAllPlans() { return plans; }
        @Override public Optional<MembershipPlan> getPlanById(int id) { return plans.stream().filter(p -> p.getPlanId() == id).findFirst(); }
        @Override public boolean updatePlan(MembershipPlan p) { return true; }
        @Override public boolean deletePlan(int id) { return true; }
    }

    private static class MockMembershipDAO implements MembershipDAO {
        private final List<Membership> list = new ArrayList<>();
        @Override public boolean enrollMember(Membership m) { m.setMembershipId(1); list.add(m); return true; }
        @Override public Optional<Membership> getActiveMembershipByMemberId(int memberId) { return list.stream().filter(m -> m.getMemberId() == memberId).findFirst(); }
        @Override public List<Membership> getMembershipsByMemberId(int memberId) { return list; }
        @Override public List<Membership> getAllMemberships() { return list; }
        @Override public List<Membership> getExpiredMemberships() { return list.stream().filter(m -> m.getStatus() == MembershipStatus.EXPIRED).toList(); }
        @Override public List<Membership> getUpcomingExpiringMemberships(int days) { return list; }
        @Override public boolean updateMembership(Membership m) { return true; }
        @Override public boolean cancelMembership(int id) { return true; }
        @Override public int autoExpireMemberships() { return 0; }
    }
}
