package com.gymmanagement.service;

import com.gymmanagement.dao.MembershipPlanDAO;
import com.gymmanagement.dao.MembershipPlanDAOImpl;
import com.gymmanagement.exception.DatabaseException;
import com.gymmanagement.exception.GymManagementException;
import com.gymmanagement.exception.PlanNotFoundException;
import com.gymmanagement.exception.ValidationException;
import com.gymmanagement.model.MembershipPlan;
import com.gymmanagement.util.InputValidator;

import java.util.List;

public class MembershipPlanService {

    private final MembershipPlanDAO planDAO;

    public MembershipPlanService() {
        this.planDAO = new MembershipPlanDAOImpl();
    }

    public MembershipPlanService(MembershipPlanDAO planDAO) {
        this.planDAO = planDAO;
    }

    public MembershipPlan createPlan(String name, int durationMonths, double price, String description) throws GymManagementException {
        InputValidator.validateNonEmpty(name, "Plan Name");
        if (durationMonths <= 0) {
            throw new ValidationException("Duration months must be at least 1.");
        }
        if (price <= 0) {
            throw new ValidationException("Price must be greater than zero.");
        }

        MembershipPlan plan = new MembershipPlan(name, durationMonths, price, description);
        boolean success = planDAO.addPlan(plan);
        if (!success) {
            throw new DatabaseException("Failed to save membership plan.");
        }
        return plan;
    }

    public List<MembershipPlan> getAllPlans() throws DatabaseException {
        return planDAO.getAllPlans();
    }

    public MembershipPlan getPlanById(int planId) throws PlanNotFoundException, DatabaseException {
        return planDAO.getPlanById(planId)
                .orElseThrow(() -> new PlanNotFoundException("No membership plan found with ID: " + planId));
    }

    public boolean updatePlan(int planId, String name, int durationMonths, double price, String description) throws GymManagementException {
        MembershipPlan plan = getPlanById(planId);

        if (name != null && !name.trim().isEmpty()) plan.setPlanName(name.trim());
        if (durationMonths > 0) plan.setDurationMonths(durationMonths);
        if (price > 0) plan.setPrice(price);
        if (description != null) plan.setDescription(description.trim());

        return planDAO.updatePlan(plan);
    }

    public boolean deletePlan(int planId) throws PlanNotFoundException, DatabaseException {
        getPlanById(planId); // verify exists
        return planDAO.deletePlan(planId);
    }
}
