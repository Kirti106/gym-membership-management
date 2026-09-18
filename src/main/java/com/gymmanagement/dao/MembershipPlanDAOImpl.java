package com.gymmanagement.dao;

import com.gymmanagement.exception.DatabaseException;
import com.gymmanagement.model.MembershipPlan;
import com.gymmanagement.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MembershipPlanDAOImpl implements MembershipPlanDAO {

    @Override
    public boolean addPlan(MembershipPlan plan) throws DatabaseException {
        String sql = "INSERT INTO membership_plans (plan_name, duration_months, price, description) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, plan.getPlanName());
            stmt.setInt(2, plan.getDurationMonths());
            stmt.setDouble(3, plan.getPrice());
            stmt.setString(4, plan.getDescription());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        plan.setPlanId(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new DatabaseException("Error adding membership plan", e);
        }
    }

    @Override
    public List<MembershipPlan> getAllPlans() throws DatabaseException {
        List<MembershipPlan> plans = new ArrayList<>();
        String sql = "SELECT plan_id, plan_name, duration_months, price, description FROM membership_plans ORDER BY duration_months ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                plans.add(mapResultSetToPlan(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving membership plans", e);
        }
        return plans;
    }

    @Override
    public Optional<MembershipPlan> getPlanById(int planId) throws DatabaseException {
        String sql = "SELECT plan_id, plan_name, duration_months, price, description FROM membership_plans WHERE plan_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, planId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToPlan(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving plan with ID: " + planId, e);
        }
        return Optional.empty();
    }

    @Override
    public boolean updatePlan(MembershipPlan plan) throws DatabaseException {
        String sql = "UPDATE membership_plans SET plan_name = ?, duration_months = ?, price = ?, description = ? WHERE plan_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, plan.getPlanName());
            stmt.setInt(2, plan.getDurationMonths());
            stmt.setDouble(3, plan.getPrice());
            stmt.setString(4, plan.getDescription());
            stmt.setInt(5, plan.getPlanId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error updating membership plan", e);
        }
    }

    @Override
    public boolean deletePlan(int planId) throws DatabaseException {
        String sql = "DELETE FROM membership_plans WHERE plan_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, planId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting membership plan with ID: " + planId, e);
        }
    }

    private MembershipPlan mapResultSetToPlan(ResultSet rs) throws SQLException {
        return new MembershipPlan(
                rs.getInt("plan_id"),
                rs.getString("plan_name"),
                rs.getInt("duration_months"),
                rs.getDouble("price"),
                rs.getString("description")
        );
    }
}
