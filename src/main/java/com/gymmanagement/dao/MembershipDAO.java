package com.gymmanagement.dao;

import com.gymmanagement.exception.DatabaseException;
import com.gymmanagement.model.Membership;

import java.util.List;
import java.util.Optional;

public interface MembershipDAO {
    boolean enrollMember(Membership membership) throws DatabaseException;
    Optional<Membership> getActiveMembershipByMemberId(int memberId) throws DatabaseException;
    List<Membership> getMembershipsByMemberId(int memberId) throws DatabaseException;
    List<Membership> getAllMemberships() throws DatabaseException;
    List<Membership> getExpiredMemberships() throws DatabaseException;
    List<Membership> getUpcomingExpiringMemberships(int days) throws DatabaseException;
    boolean updateMembership(Membership membership) throws DatabaseException;
    boolean cancelMembership(int membershipId) throws DatabaseException;
    int autoExpireMemberships() throws DatabaseException;
}
