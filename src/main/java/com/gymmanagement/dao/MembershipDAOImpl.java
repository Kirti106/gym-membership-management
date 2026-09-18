package com.gymmanagement.dao;

import com.gymmanagement.exception.DatabaseException;
import com.gymmanagement.model.Membership;
import com.gymmanagement.model.MembershipStatus;
import com.gymmanagement.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MembershipDAOImpl implements MembershipDAO {

    @Override
    public boolean enrollMember(Membership membership) throws DatabaseException {
        String sql = "INSERT INTO memberships (member_id, plan_id, start_date, end_date, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, membership.getMemberId());
            stmt.setInt(2, membership.getPlanId());
            stmt.setDate(3, Date.valueOf(membership.getStartDate()));
            stmt.setDate(4, Date.valueOf(membership.getEndDate()));
            stmt.setString(5, membership.getStatus() != null ? membership.getStatus().name() : "ACTIVE");

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        membership.setMembershipId(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new DatabaseException("Error enrolling member in membership plan", e);
        }
    }

    @Override
    public Optional<Membership> getActiveMembershipByMemberId(int memberId) throws DatabaseException {
        autoExpireMemberships();
        String sql = "SELECT ms.membership_id, ms.member_id, ms.plan_id, ms.start_date, ms.end_date, ms.status, " +
                     "m.name AS member_name, p.plan_name " +
                     "FROM memberships ms " +
                     "JOIN members m ON ms.member_id = m.member_id " +
                     "JOIN membership_plans p ON ms.plan_id = p.plan_id " +
                     "WHERE ms.member_id = ? AND ms.status = 'ACTIVE' AND ms.end_date >= CURRENT_DATE() " +
                     "ORDER BY ms.end_date DESC LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, memberId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToMembership(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving active membership for member ID: " + memberId, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Membership> getMembershipsByMemberId(int memberId) throws DatabaseException {
        autoExpireMemberships();
        List<Membership> memberships = new ArrayList<>();
        String sql = "SELECT ms.membership_id, ms.member_id, ms.plan_id, ms.start_date, ms.end_date, ms.status, " +
                     "m.name AS member_name, p.plan_name " +
                     "FROM memberships ms " +
                     "JOIN members m ON ms.member_id = m.member_id " +
                     "JOIN membership_plans p ON ms.plan_id = p.plan_id " +
                     "WHERE ms.member_id = ? ORDER BY ms.start_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, memberId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    memberships.add(mapResultSetToMembership(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving membership history for member ID: " + memberId, e);
        }
        return memberships;
    }

    @Override
    public List<Membership> getAllMemberships() throws DatabaseException {
        autoExpireMemberships();
        List<Membership> memberships = new ArrayList<>();
        String sql = "SELECT ms.membership_id, ms.member_id, ms.plan_id, ms.start_date, ms.end_date, ms.status, " +
                     "m.name AS member_name, p.plan_name " +
                     "FROM memberships ms " +
                     "JOIN members m ON ms.member_id = m.member_id " +
                     "JOIN membership_plans p ON ms.plan_id = p.plan_id " +
                     "ORDER BY ms.membership_id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                memberships.add(mapResultSetToMembership(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving all memberships", e);
        }
        return memberships;
    }

    @Override
    public List<Membership> getExpiredMemberships() throws DatabaseException {
        autoExpireMemberships();
        List<Membership> list = new ArrayList<>();
        String sql = "SELECT ms.membership_id, ms.member_id, ms.plan_id, ms.start_date, ms.end_date, ms.status, " +
                     "m.name AS member_name, p.plan_name " +
                     "FROM memberships ms " +
                     "JOIN members m ON ms.member_id = m.member_id " +
                     "JOIN membership_plans p ON ms.plan_id = p.plan_id " +
                     "WHERE ms.status = 'EXPIRED' OR ms.end_date < CURRENT_DATE() " +
                     "ORDER BY ms.end_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToMembership(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving expired memberships", e);
        }
        return list;
    }

    @Override
    public List<Membership> getUpcomingExpiringMemberships(int days) throws DatabaseException {
        autoExpireMemberships();
        List<Membership> list = new ArrayList<>();
        String sql = "SELECT ms.membership_id, ms.member_id, ms.plan_id, ms.start_date, ms.end_date, ms.status, " +
                     "m.name AS member_name, p.plan_name " +
                     "FROM memberships ms " +
                     "JOIN members m ON ms.member_id = m.member_id " +
                     "JOIN membership_plans p ON ms.plan_id = p.plan_id " +
                     "WHERE ms.status = 'ACTIVE' AND ms.end_date >= CURRENT_DATE() " +
                     "AND ms.end_date <= DATE_ADD(CURRENT_DATE(), INTERVAL ? DAY) " +
                     "ORDER BY ms.end_date ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, days);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToMembership(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving upcoming expiring memberships", e);
        }
        return list;
    }

    @Override
    public boolean updateMembership(Membership membership) throws DatabaseException {
        String sql = "UPDATE memberships SET plan_id = ?, start_date = ?, end_date = ?, status = ? WHERE membership_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, membership.getPlanId());
            stmt.setDate(2, Date.valueOf(membership.getStartDate()));
            stmt.setDate(3, Date.valueOf(membership.getEndDate()));
            stmt.setString(4, membership.getStatus().name());
            stmt.setInt(5, membership.getMembershipId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error updating membership record", e);
        }
    }

    @Override
    public boolean cancelMembership(int membershipId) throws DatabaseException {
        String sql = "UPDATE memberships SET status = 'CANCELLED' WHERE membership_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, membershipId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error cancelling membership", e);
        }
    }

    @Override
    public int autoExpireMemberships() throws DatabaseException {
        String sql1 = "UPDATE memberships SET status = 'EXPIRED' WHERE end_date < CURRENT_DATE() AND status = 'ACTIVE'";
        String sql2 = "UPDATE members SET status = 'EXPIRED' WHERE member_id IN (SELECT member_id FROM memberships WHERE status = 'EXPIRED')";
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (Statement stmt1 = conn.createStatement();
                 Statement stmt2 = conn.createStatement()) {

                int expiredCount = stmt1.executeUpdate(sql1);
                stmt2.executeUpdate(sql2);
                conn.commit();
                return expiredCount;
            } catch (SQLException e) {
                conn.rollback();
                throw new DatabaseException("Error auto-expiring memberships", e);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Transaction error while updating expired memberships", e);
        }
    }

    private Membership mapResultSetToMembership(ResultSet rs) throws SQLException {
        Date start = rs.getDate("start_date");
        Date end = rs.getDate("end_date");
        Membership m = new Membership(
                rs.getInt("membership_id"),
                rs.getInt("member_id"),
                rs.getInt("plan_id"),
                start != null ? start.toLocalDate() : null,
                end != null ? end.toLocalDate() : null,
                MembershipStatus.fromString(rs.getString("status"))
        );

        try {
            m.setMemberName(rs.getString("member_name"));
        } catch (SQLException ignored) {}
        try {
            m.setPlanName(rs.getString("plan_name"));
        } catch (SQLException ignored) {}

        return m;
    }
}
