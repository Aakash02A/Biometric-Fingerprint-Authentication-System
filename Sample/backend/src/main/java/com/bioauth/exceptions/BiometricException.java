package com.bioauth.exceptions;

/**
 * Base exception for biometric-related errors
 */
public class BiometricException extends Exception {
    private String errorCode;

    public BiometricException(String message) {
        super(message);
        this.errorCode = "BIOMETRIC_ERROR";
    }

    public BiometricException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "BIOMETRIC_ERROR";
    }

    public BiometricException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BiometricException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
