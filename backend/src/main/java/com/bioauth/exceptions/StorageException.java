package com.bioauth.exceptions;

/**
 * Exception thrown when storage operations fail
 */
public class StorageException extends BiometricException {
    public StorageException(String message) {
        super("STORAGE_ERROR", message);
    }

    public StorageException(String message, Throwable cause) {
        super("STORAGE_ERROR", message, cause);
    }
}
