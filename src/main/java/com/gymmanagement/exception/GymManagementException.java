package com.gymmanagement.exception;

public class GymManagementException extends Exception {
    public GymManagementException(String message) {
        super(message);
    }

    public GymManagementException(String message, Throwable cause) {
        super(message, cause);
    }
}
