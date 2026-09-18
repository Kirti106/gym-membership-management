package com.gymmanagement.service;

import com.gymmanagement.dao.*;
import com.gymmanagement.exception.*;
import com.gymmanagement.model.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class MembershipService {

    private final MembershipDAO membershipDAO;
    private final MemberDAO memberDAO;
    private final MembershipPlanDAO planDAO;

    public MembershipService() {
        this.membershipDAO = new MembershipDAOImpl();
        this.memberDAO = new MemberDAOImpl();
        this.planDAO = new MembershipPlanDAOImpl();
    }

    public MembershipService(MembershipDAO membershipDAO, MemberDAO memberDAO, MembershipPlanDAO planDAO) {
        this.membershipDAO = membershipDAO;
        this.memberDAO = memberDAO;
        this.planDAO = planDAO;
    }

    public Membership enrollMember(int memberId, int planId, LocalDate startDate) throws GymManagementException {
        Member member = memberDAO.getMemberById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Member with ID " + memberId + " not found."));

        MembershipPlan plan = planDAO.getPlanById(planId)
                .orElseThrow(() -> new PlanNotFoundException("Plan with ID " + planId + " not found."));

        if (startDate == null) {
            startDate = LocalDate.now();
        }

        LocalDate endDate = startDate.plusMonths(plan.getDurationMonths());
        Membership membership = new Membership(memberId, planId, startDate, endDate, MembershipStatus.ACTIVE);

        boolean success = membershipDAO.enrollMember(membership);
        if (!success) {
            throw new DatabaseException("Failed to record membership enrollment.");
        }

        // Update member status to ACTIVE
        memberDAO.updateMemberStatus(memberId, MembershipStatus.ACTIVE.name());

        return membership;
    }

    public Membership renewMembership(int memberId, int planId) throws GymManagementException {
        Member member = memberDAO.getMemberById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Member with ID " + memberId + " not found."));

        MembershipPlan plan = planDAO.getPlanById(planId)
                .orElseThrow(() -> new PlanNotFoundException("Plan with ID " + planId + " not found."));

        LocalDate startDate = LocalDate.now();
        Optional<Membership> activeOpt = membershipDAO.getActiveMembershipByMemberId(memberId);
        if (activeOpt.isPresent()) {
            Membership active = activeOpt.get();
            if (active.getEndDate().isAfter(startDate)) {
                startDate = active.getEndDate(); // extend seamlessly from current expiry
            }
        }

        LocalDate endDate = startDate.plusMonths(plan.getDurationMonths());
        Membership newMembership = new Membership(memberId, planId, startDate, endDate, MembershipStatus.ACTIVE);

        boolean success = membershipDAO.enrollMember(newMembership);
        if (!success) {
            throw new DatabaseException("Failed to renew membership.");
        }

        memberDAO.updateMemberStatus(memberId, MembershipStatus.ACTIVE.name());
        return newMembership;
    }

    public Optional<Membership> getActiveMembership(int memberId) throws DatabaseException {
        return membershipDAO.getActiveMembershipByMemberId(memberId);
    }

    public List<Membership> getAllMemberships() throws DatabaseException {
        return membershipDAO.getAllMemberships();
    }

    public List<Membership> getExpiredMemberships() throws DatabaseException {
        return membershipDAO.getExpiredMemberships();
    }

    public List<Membership> getUpcomingExpiringMemberships(int days) throws DatabaseException {
        return membershipDAO.getUpcomingExpiringMemberships(days);
    }

    public int runAutoExpirySync() throws DatabaseException {
        return membershipDAO.autoExpireMemberships();
    }
}
