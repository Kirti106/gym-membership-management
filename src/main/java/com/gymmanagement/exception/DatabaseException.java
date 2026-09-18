package com.gymmanagement.exception;

public class DatabaseException extends GymManagementException {
    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
