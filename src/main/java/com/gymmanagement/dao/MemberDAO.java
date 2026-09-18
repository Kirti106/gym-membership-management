package com.gymmanagement.dao;

import com.gymmanagement.exception.DatabaseException;
import com.gymmanagement.model.Member;

import java.util.List;
import java.util.Optional;

public interface MemberDAO {
    boolean addMember(Member member) throws DatabaseException;
    List<Member> getAllMembers() throws DatabaseException;
    Optional<Member> getMemberById(int memberId) throws DatabaseException;
    List<Member> searchMembersByName(String name) throws DatabaseException;
    Optional<Member> getMemberByPhone(String phone) throws DatabaseException;
    boolean updateMember(Member member) throws DatabaseException;
    boolean deleteMember(int memberId) throws DatabaseException;
    boolean updateMemberStatus(int memberId, String status) throws DatabaseException;
    int getTotalMembersCount() throws DatabaseException;
    int getActiveMembersCount() throws DatabaseException;
}
