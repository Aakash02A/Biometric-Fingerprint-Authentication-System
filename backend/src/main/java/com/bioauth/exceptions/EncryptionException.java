package com.bioauth.exceptions;

/**
 * Exception thrown when encryption/decryption fails
 */
public class EncryptionException extends BiometricException {
    public EncryptionException(String message) {
        super("ENCRYPTION_ERROR", message);
    }

    public EncryptionException(String message, Throwable cause) {
        super("ENCRYPTION_ERROR", message, cause);
    }
}
