package com.gymmanagement.dao;

import com.gymmanagement.exception.DatabaseException;
import com.gymmanagement.model.Gender;
import com.gymmanagement.model.Member;
import com.gymmanagement.model.MembershipStatus;
import com.gymmanagement.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MemberDAOImpl implements MemberDAO {

    @Override
    public boolean addMember(Member member) throws DatabaseException {
        String sql = "INSERT INTO members (name, age, gender, phone, email, join_date, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, member.getName());
            stmt.setInt(2, member.getAge());
            stmt.setString(3, member.getGender().name());
            stmt.setString(4, member.getPhone());
            stmt.setString(5, member.getEmail());
            stmt.setDate(6, Date.valueOf(member.getJoinDate()));
            stmt.setString(7, member.getStatus() != null ? member.getStatus().name() : "ACTIVE");

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        member.setMemberId(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new DatabaseException("Error adding member: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Member> getAllMembers() throws DatabaseException {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT member_id, name, age, gender, phone, email, join_date, status FROM members ORDER BY member_id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                members.add(mapResultSetToMember(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching members list", e);
        }
        return members;
    }

    @Override
    public Optional<Member> getMemberById(int memberId) throws DatabaseException {
        String sql = "SELECT member_id, name, age, gender, phone, email, join_date, status FROM members WHERE member_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, memberId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToMember(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching member with ID: " + memberId, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Member> searchMembersByName(String name) throws DatabaseException {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT member_id, name, age, gender, phone, email, join_date, status FROM members WHERE LOWER(name) LIKE ? ORDER BY name ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + name.toLowerCase().trim() + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    members.add(mapResultSetToMember(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error searching member by name", e);
        }
        return members;
    }

    @Override
    public Optional<Member> getMemberByPhone(String phone) throws DatabaseException {
        String sql = "SELECT member_id, name, age, gender, phone, email, join_date, status FROM members WHERE phone = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, phone.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToMember(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error searching member by phone", e);
        }
        return Optional.empty();
    }

    @Override
    public boolean updateMember(Member member) throws DatabaseException {
        String sql = "UPDATE members SET name = ?, age = ?, gender = ?, phone = ?, email = ?, status = ? WHERE member_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, member.getName());
            stmt.setInt(2, member.getAge());
            stmt.setString(3, member.getGender().name());
            stmt.setString(4, member.getPhone());
            stmt.setString(5, member.getEmail());
            stmt.setString(6, member.getStatus().name());
            stmt.setInt(7, member.getMemberId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error updating member details", e);
        }
    }

    @Override
    public boolean deleteMember(int memberId) throws DatabaseException {
        String sql = "DELETE FROM members WHERE member_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, memberId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting member with ID: " + memberId, e);
        }
    }

    @Override
    public boolean updateMemberStatus(int memberId, String status) throws DatabaseException {
        String sql = "UPDATE members SET status = ? WHERE member_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, memberId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error updating member status", e);
        }
    }

    @Override
    public int getTotalMembersCount() throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM members";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            throw new DatabaseException("Error counting total members", e);
        }
        return 0;
    }

    @Override
    public int getActiveMembersCount() throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM members WHERE status = 'ACTIVE'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            throw new DatabaseException("Error counting active members", e);
        }
        return 0;
    }

    private Member mapResultSetToMember(ResultSet rs) throws SQLException {
        Date joinDateSql = rs.getDate("join_date");
        return new Member(
                rs.getInt("member_id"),
                rs.getString("name"),
                rs.getInt("age"),
                Gender.fromString(rs.getString("gender")),
                rs.getString("phone"),
                rs.getString("email"),
                joinDateSql != null ? joinDateSql.toLocalDate() : null,
                MembershipStatus.fromString(rs.getString("status"))
        );
    }
}
