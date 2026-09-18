package com.gymmanagement.dao;

import com.gymmanagement.exception.DatabaseException;
import com.gymmanagement.model.Attendance;
import com.gymmanagement.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AttendanceDAOImpl implements AttendanceDAO {

    @Override
    public boolean markAttendance(Attendance attendance) throws DatabaseException {
        String sql = "INSERT INTO attendance (member_id, attendance_date, check_in_time) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, attendance.getMemberId());
            stmt.setDate(2, Date.valueOf(attendance.getAttendanceDate()));
            stmt.setTimestamp(3, Timestamp.valueOf(attendance.getCheckInTime()));

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        attendance.setAttendanceId(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062 || (e.getMessage() != null && e.getMessage().contains("unique_daily_attendance"))) {
                throw new DatabaseException("Attendance already marked for member ID " + attendance.getMemberId() + " on " + attendance.getAttendanceDate(), e);
            }
            throw new DatabaseException("Error marking attendance", e);
        }
    }

    @Override
    public boolean isAttendanceMarked(int memberId, LocalDate date) throws DatabaseException {
        String sql = "SELECT attendance_id FROM attendance WHERE member_id = ? AND attendance_date = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, memberId);
            stmt.setDate(2, Date.valueOf(date));
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error checking daily attendance status", e);
        }
    }

    @Override
    public List<Attendance> getAttendanceByMemberId(int memberId) throws DatabaseException {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT a.attendance_id, a.member_id, a.attendance_date, a.check_in_time, m.name AS member_name " +
                     "FROM attendance a JOIN members m ON a.member_id = m.member_id " +
                     "WHERE a.member_id = ? ORDER BY a.attendance_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, memberId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToAttendance(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving attendance history for member ID: " + memberId, e);
        }
        return list;
    }

    @Override
    public List<Attendance> getAttendanceByDate(LocalDate date) throws DatabaseException {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT a.attendance_id, a.member_id, a.attendance_date, a.check_in_time, m.name AS member_name " +
                     "FROM attendance a JOIN members m ON a.member_id = m.member_id " +
                     "WHERE a.attendance_date = ? ORDER BY a.check_in_time ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(date));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToAttendance(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving attendance log for date: " + date, e);
        }
        return list;
    }

    @Override
    public int getAttendanceCountForMember(int memberId) throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM attendance WHERE member_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, memberId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error getting attendance count for member ID: " + memberId, e);
        }
        return 0;
    }

    @Override
    public Map<String, Integer> getMostActiveMembers(int limit) throws DatabaseException {
        Map<String, Integer> activeMap = new LinkedHashMap<>();
        String sql = "SELECT m.name, COUNT(a.attendance_id) AS total_visits " +
                     "FROM attendance a JOIN members m ON a.member_id = m.member_id " +
                     "GROUP BY a.member_id, m.name ORDER BY total_visits DESC LIMIT ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    activeMap.put(rs.getString("name"), rs.getInt("total_visits"));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error generating most active members report", e);
        }
        return activeMap;
    }

    private Attendance mapResultSetToAttendance(ResultSet rs) throws SQLException {
        Date attDate = rs.getDate("attendance_date");
        Timestamp checkIn = rs.getTimestamp("check_in_time");
        Attendance a = new Attendance(
                rs.getInt("attendance_id"),
                rs.getInt("member_id"),
                attDate != null ? attDate.toLocalDate() : null,
                checkIn != null ? checkIn.toLocalDateTime() : null
        );

        try {
            a.setMemberName(rs.getString("member_name"));
        } catch (SQLException ignored) {}

        return a;
    }
}
