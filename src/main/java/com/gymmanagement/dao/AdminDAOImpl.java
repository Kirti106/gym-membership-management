package com.gymmanagement.dao;

import com.gymmanagement.exception.DatabaseException;
import com.gymmanagement.model.Admin;
import com.gymmanagement.util.DBConnection;

import java.sql.*;
import java.util.Optional;

public class AdminDAOImpl implements AdminDAO {

    @Override
    public Optional<Admin> findByUsername(String username) throws DatabaseException {
        String sql = "SELECT admin_id, username, password, full_name, email, created_at FROM admins WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Admin admin = mapResultSetToAdmin(rs);
                    return Optional.of(admin);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving admin by username", e);
        }
        return Optional.empty();
    }

    @Override
    public boolean authenticate(String username, String password) throws DatabaseException {
        String sql = "SELECT admin_id FROM admins WHERE username = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database error during admin authentication", e);
        }
    }

    @Override
    public boolean createAdmin(Admin admin) throws DatabaseException {
        String sql = "INSERT INTO admins (username, password, full_name, email) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, admin.getUsername());
            stmt.setString(2, admin.getPassword());
            stmt.setString(3, admin.getFullName());
            stmt.setString(4, admin.getEmail());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        admin.setAdminId(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new DatabaseException("Error creating admin account", e);
        }
    }

    private Admin mapResultSetToAdmin(ResultSet rs) throws SQLException {
        Timestamp ts = rs.getTimestamp("created_at");
        return new Admin(
                rs.getInt("admin_id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("full_name"),
                rs.getString("email"),
                ts != null ? ts.toLocalDateTime() : null
        );
    }
}
