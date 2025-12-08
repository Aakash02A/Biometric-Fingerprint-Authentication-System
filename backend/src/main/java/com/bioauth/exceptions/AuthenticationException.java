package com.bioauth.exceptions;

/**
 * Exception thrown when authentication fails or fingerprint doesn't match
 */
public class AuthenticationException extends BiometricException {
    private int matchScore;

    public AuthenticationException(String message) {
        super("AUTH_ERROR", message);
        this.matchScore = 0;
    }

    public AuthenticationException(String message, Throwable cause) {
        super("AUTH_ERROR", message, cause);
        this.matchScore = 0;
    }

    public AuthenticationException(String message, int matchScore) {
        super("AUTH_ERROR", message);
        this.matchScore = matchScore;
    }

    public int getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(int matchScore) {
        this.matchScore = matchScore;
    }
}
