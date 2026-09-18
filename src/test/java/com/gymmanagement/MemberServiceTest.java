package com.gymmanagement;

import com.gymmanagement.dao.MemberDAO;
import com.gymmanagement.exception.MemberNotFoundException;
import com.gymmanagement.exception.ValidationException;
import com.gymmanagement.model.Gender;
import com.gymmanagement.model.Member;
import com.gymmanagement.model.MembershipStatus;
import com.gymmanagement.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class MemberServiceTest {

    private MemberDAO mockMemberDAO;
    private MemberService memberService;

    @BeforeEach
    public void setUp() {
        mockMemberDAO = new MockMemberDAO();
        memberService = new MemberService(mockMemberDAO);
    }

    @Test
    public void testRegisterMemberSuccess() throws Exception {
        Member m = memberService.registerMember("Test User", 25, "MALE", "9999988888", "test@example.com");
        assertNotNull(m);
        assertEquals("Test User", m.getName());
        assertEquals(25, m.getAge());
    }

    @Test
    public void testRegisterMemberInvalidAge() {
        assertThrows(ValidationException.class, () ->
                memberService.registerMember("Young Kid", 5, "MALE", "9999988888", "kid@example.com"));
    }

    @Test
    public void testGetMemberByIdNotFound() {
        assertThrows(MemberNotFoundException.class, () -> memberService.getMemberById(999));
    }

    // Simple Mock Implementation for Unit Testing without DB dependency
    private static class MockMemberDAO implements MemberDAO {
        private final List<Member> list = new ArrayList<>();
        private int idCounter = 1;

        @Override
        public boolean addMember(Member member) {
            member.setMemberId(idCounter++);
            list.add(member);
            return true;
        }

        @Override
        public List<Member> getAllMembers() { return list; }

        @Override
        public Optional<Member> getMemberById(int memberId) {
            return list.stream().filter(m -> m.getMemberId() == memberId).findFirst();
        }

        @Override
        public List<Member> searchMembersByName(String name) {
            return list.stream().filter(m -> m.getName().toLowerCase().contains(name.toLowerCase())).toList();
        }

        @Override
        public Optional<Member> getMemberByPhone(String phone) {
            return list.stream().filter(m -> m.getPhone().equals(phone)).findFirst();
        }

        @Override
        public boolean updateMember(Member member) { return true; }

        @Override
        public boolean deleteMember(int memberId) { return list.removeIf(m -> m.getMemberId() == memberId); }

        @Override
        public boolean updateMemberStatus(int memberId, String status) {
            getMemberById(memberId).ifPresent(m -> m.setStatus(MembershipStatus.fromString(status)));
            return true;
        }

        @Override
        public int getTotalMembersCount() { return list.size(); }

        @Override
        public int getActiveMembersCount() {
            return (int) list.stream().filter(m -> m.getStatus() == MembershipStatus.ACTIVE).count();
        }
    }
}
