package com.gymmanagement.dao;

import com.gymmanagement.exception.DatabaseException;
import com.gymmanagement.model.Trainer;
import com.gymmanagement.model.TrainerAssignment;
import com.gymmanagement.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TrainerDAOImpl implements TrainerDAO {

    @Override
    public boolean addTrainer(Trainer trainer) throws DatabaseException {
        String sql = "INSERT INTO trainers (name, specialization, phone, email, is_available) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, trainer.getName());
            stmt.setString(2, trainer.getSpecialization());
            stmt.setString(3, trainer.getPhone());
            stmt.setString(4, trainer.getEmail());
            stmt.setBoolean(5, trainer.isAvailable());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        trainer.setTrainerId(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new DatabaseException("Error adding trainer record", e);
        }
    }

    @Override
    public List<Trainer> getAllTrainers() throws DatabaseException {
        List<Trainer> list = new ArrayList<>();
        String sql = "SELECT trainer_id, name, specialization, phone, email, is_available FROM trainers ORDER BY trainer_id ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToTrainer(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching trainers list", e);
        }
        return list;
    }

    @Override
    public Optional<Trainer> getTrainerById(int trainerId) throws DatabaseException {
        String sql = "SELECT trainer_id, name, specialization, phone, email, is_available FROM trainers WHERE trainer_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, trainerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTrainer(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching trainer with ID: " + trainerId, e);
        }
        return Optional.empty();
    }

    @Override
    public boolean updateTrainer(Trainer trainer) throws DatabaseException {
        String sql = "UPDATE trainers SET name = ?, specialization = ?, phone = ?, email = ?, is_available = ? WHERE trainer_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, trainer.getName());
            stmt.setString(2, trainer.getSpecialization());
            stmt.setString(3, trainer.getPhone());
            stmt.setString(4, trainer.getEmail());
            stmt.setBoolean(5, trainer.isAvailable());
            stmt.setInt(6, trainer.getTrainerId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error updating trainer profile", e);
        }
    }

    @Override
    public boolean updateAvailability(int trainerId, boolean available) throws DatabaseException {
        String sql = "UPDATE trainers SET is_available = ? WHERE trainer_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, available);
            stmt.setInt(2, trainerId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error updating trainer availability", e);
        }
    }

    @Override
    public boolean assignTrainerToMember(TrainerAssignment assignment) throws DatabaseException {
        String sql = "INSERT INTO trainer_assignments (trainer_id, member_id, assigned_date, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, assignment.getTrainerId());
            stmt.setInt(2, assignment.getMemberId());
            stmt.setDate(3, Date.valueOf(assignment.getAssignedDate()));
            stmt.setString(4, assignment.getStatus() != null ? assignment.getStatus() : "ACTIVE");

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        assignment.setAssignmentId(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new DatabaseException("Error assigning trainer to member", e);
        }
    }

    @Override
    public boolean removeTrainerAssignment(int assignmentId) throws DatabaseException {
        String sql = "UPDATE trainer_assignments SET status = 'CANCELLED' WHERE assignment_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, assignmentId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error removing trainer assignment", e);
        }
    }

    @Override
    public List<TrainerAssignment> getActiveAssignments() throws DatabaseException {
        List<TrainerAssignment> list = new ArrayList<>();
        String sql = "SELECT ta.assignment_id, ta.trainer_id, ta.member_id, ta.assigned_date, ta.status, " +
                     "t.name AS trainer_name, m.name AS member_name " +
                     "FROM trainer_assignments ta " +
                     "JOIN trainers t ON ta.trainer_id = t.trainer_id " +
                     "JOIN members m ON ta.member_id = m.member_id " +
                     "WHERE ta.status = 'ACTIVE' ORDER BY ta.assigned_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToAssignment(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching active trainer assignments", e);
        }
        return list;
    }

    @Override
    public Optional<TrainerAssignment> getAssignmentForMember(int memberId) throws DatabaseException {
        String sql = "SELECT ta.assignment_id, ta.trainer_id, ta.member_id, ta.assigned_date, ta.status, " +
                     "t.name AS trainer_name, m.name AS member_name " +
                     "FROM trainer_assignments ta " +
                     "JOIN trainers t ON ta.trainer_id = t.trainer_id " +
                     "JOIN members m ON ta.member_id = m.member_id " +
                     "WHERE ta.member_id = ? AND ta.status = 'ACTIVE' ORDER BY ta.assigned_date DESC LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, memberId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToAssignment(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error checking trainer assignment for member ID: " + memberId, e);
        }
        return Optional.empty();
    }

    @Override
    public Map<String, Integer> getTrainerMemberCounts() throws DatabaseException {
        Map<String, Integer> countMap = new LinkedHashMap<>();
        String sql = "SELECT t.name AS trainer_name, COUNT(ta.assignment_id) AS member_count " +
                     "FROM trainers t LEFT JOIN trainer_assignments ta ON t.trainer_id = ta.trainer_id AND ta.status = 'ACTIVE' " +
                     "GROUP BY t.trainer_id, t.name ORDER BY member_count DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                countMap.put(rs.getString("trainer_name"), rs.getInt("member_count"));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error generating trainer workload report", e);
        }
        return countMap;
    }

    private Trainer mapResultSetToTrainer(ResultSet rs) throws SQLException {
        return new Trainer(
                rs.getInt("trainer_id"),
                rs.getString("name"),
                rs.getString("specialization"),
                rs.getString("phone"),
                rs.getString("email"),
                rs.getBoolean("is_available")
        );
    }

    private TrainerAssignment mapResultSetToAssignment(ResultSet rs) throws SQLException {
        Date aDate = rs.getDate("assigned_date");
        TrainerAssignment ta = new TrainerAssignment(
                rs.getInt("assignment_id"),
                rs.getInt("trainer_id"),
                rs.getInt("member_id"),
                aDate != null ? aDate.toLocalDate() : null,
                rs.getString("status")
        );

        try {
            ta.setTrainerName(rs.getString("trainer_name"));
        } catch (SQLException ignored) {}
        try {
            ta.setMemberName(rs.getString("member_name"));
        } catch (SQLException ignored) {}

        return ta;
    }
}
