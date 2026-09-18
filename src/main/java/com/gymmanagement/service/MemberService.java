package com.gymmanagement.service;

import com.gymmanagement.dao.MemberDAO;
import com.gymmanagement.dao.MemberDAOImpl;
import com.gymmanagement.exception.*;
import com.gymmanagement.model.Gender;
import com.gymmanagement.model.Member;
import com.gymmanagement.model.MembershipStatus;
import com.gymmanagement.util.InputValidator;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class MemberService {

    private final MemberDAO memberDAO;

    public MemberService() {
        this.memberDAO = new MemberDAOImpl();
    }

    public MemberService(MemberDAO memberDAO) {
        this.memberDAO = memberDAO;
    }

    public Member registerMember(String name, int age, String genderStr, String phone, String email) throws GymManagementException {
        InputValidator.validateNonEmpty(name, "Member Name");
        if (age < 12 || age > 100) {
            throw new ValidationException("Age must be between 12 and 100.");
        }
        String validPhone = InputValidator.validatePhone(phone);
        String validEmail = InputValidator.validateEmail(email);

        Optional<Member> existing = memberDAO.getMemberByPhone(validPhone);
        if (existing.isPresent()) {
            throw new ValidationException("A member with phone number " + validPhone + " already exists.");
        }

        Gender gender = Gender.fromString(genderStr);
        Member member = new Member(name, age, gender, validPhone, validEmail, LocalDate.now(), MembershipStatus.ACTIVE);

        boolean success = memberDAO.addMember(member);
        if (!success) {
            throw new DatabaseException("Failed to register new member.");
        }
        return member;
    }

    public List<Member> getAllMembers() throws DatabaseException {
        return memberDAO.getAllMembers();
    }

    public Member getMemberById(int memberId) throws MemberNotFoundException, DatabaseException {
        return memberDAO.getMemberById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("No member found with ID: " + memberId));
    }

    public List<Member> searchMembersByName(String name) throws DatabaseException, EmptyFieldException {
        InputValidator.validateNonEmpty(name, "Search Name");
        return memberDAO.searchMembersByName(name);
    }

    public boolean updateMemberDetails(int memberId, String name, int age, String genderStr, String phone, String email, String statusStr) throws GymManagementException {
        Member member = getMemberById(memberId);

        if (name != null && !name.trim().isEmpty()) member.setName(name.trim());
        if (age >= 12 && age <= 100) member.setAge(age);
        if (genderStr != null && !genderStr.trim().isEmpty()) member.setGender(Gender.fromString(genderStr));
        if (phone != null && !phone.trim().isEmpty()) member.setPhone(InputValidator.validatePhone(phone));
        if (email != null && !email.trim().isEmpty()) member.setEmail(InputValidator.validateEmail(email));
        if (statusStr != null && !statusStr.trim().isEmpty()) member.setStatus(MembershipStatus.fromString(statusStr));

        return memberDAO.updateMember(member);
    }

    public boolean deactivateMember(int memberId) throws MemberNotFoundException, DatabaseException {
        getMemberById(memberId); // verify existence
        return memberDAO.updateMemberStatus(memberId, MembershipStatus.INACTIVE.name());
    }

    public boolean deleteMember(int memberId) throws MemberNotFoundException, DatabaseException {
        getMemberById(memberId); // verify existence
        return memberDAO.deleteMember(memberId);
    }
}
