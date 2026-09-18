package com.gymmanagement.util;

import com.gymmanagement.exception.DatabaseException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/gym_management?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = ""; // Fallback if DB_PASSWORD is not set in environment

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL Driver not found: " + e.getMessage());
        }
    }

    private DBConnection() {
        // Private constructor to prevent instantiation
    }

    public static Connection getConnection() throws DatabaseException {
        String dbUrl = System.getenv("DB_URL");
        if (dbUrl == null || dbUrl.trim().isEmpty()) {
            dbUrl = DEFAULT_URL;
        }

        String dbUser = System.getenv("DB_USER");
        if (dbUser == null || dbUser.trim().isEmpty()) {
            dbUser = DEFAULT_USER;
        }

        String dbPassword = System.getenv("DB_PASSWORD");
        if (dbPassword == null) {
            dbPassword = DEFAULT_PASSWORD;
        }

        try {
            return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to connect to MySQL database. Please verify MySQL service is running and DB credentials are set.", e);
        }
    }

    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (Exception e) {
            return false;
        }
    }
}
