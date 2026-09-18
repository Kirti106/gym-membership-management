package com.gymmanagement.dao;

import com.gymmanagement.exception.DatabaseException;
import com.gymmanagement.model.MembershipPlan;

import java.util.List;
import java.util.Optional;

public interface MembershipPlanDAO {
    boolean addPlan(MembershipPlan plan) throws DatabaseException;
    List<MembershipPlan> getAllPlans() throws DatabaseException;
    Optional<MembershipPlan> getPlanById(int planId) throws DatabaseException;
    boolean updatePlan(MembershipPlan plan) throws DatabaseException;
    boolean deletePlan(int planId) throws DatabaseException;
}
