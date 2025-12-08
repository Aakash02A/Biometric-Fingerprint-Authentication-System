package com.bioauth.exceptions;

/**
 * Exception thrown when user is not found or invalid
 */
public class UserException extends BiometricException {
    public UserException(String message) {
        super("USER_ERROR", message);
    }

    public UserException(String message, Throwable cause) {
        super("USER_ERROR", message, cause);
    }
}
