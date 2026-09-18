package com.gymmanagement.service;

import com.gymmanagement.dao.AdminDAO;
import com.gymmanagement.dao.AdminDAOImpl;
import com.gymmanagement.exception.DatabaseException;
import com.gymmanagement.exception.EmptyFieldException;
import com.gymmanagement.model.Admin;
import com.gymmanagement.util.InputValidator;

import java.util.Optional;

public class AdminService {

    private final AdminDAO adminDAO;

    public AdminService() {
        this.adminDAO = new AdminDAOImpl();
    }

    public AdminService(AdminDAO adminDAO) {
        this.adminDAO = adminDAO;
    }

    public boolean login(String username, String password) throws DatabaseException, EmptyFieldException {
        InputValidator.validateNonEmpty(username, "Username");
        InputValidator.validateNonEmpty(password, "Password");
        return adminDAO.authenticate(username, password);
    }

    public Optional<Admin> getAdminDetails(String username) throws DatabaseException {
        return adminDAO.findByUsername(username);
    }

    public boolean createAdminAccount(String username, String password, String fullName, String email) throws Exception {
        InputValidator.validateNonEmpty(username, "Username");
        InputValidator.validateNonEmpty(password, "Password");
        InputValidator.validateNonEmpty(fullName, "Full Name");
        InputValidator.validateEmail(email);

        Admin admin = new Admin(username, password, fullName, email);
        return adminDAO.createAdmin(admin);
    }
}
