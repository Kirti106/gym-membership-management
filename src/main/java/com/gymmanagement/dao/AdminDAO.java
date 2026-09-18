package com.gymmanagement.dao;

import com.gymmanagement.exception.DatabaseException;
import com.gymmanagement.model.Admin;

import java.util.Optional;

public interface AdminDAO {
    Optional<Admin> findByUsername(String username) throws DatabaseException;
    boolean authenticate(String username, String password) throws DatabaseException;
    boolean createAdmin(Admin admin) throws DatabaseException;
}
