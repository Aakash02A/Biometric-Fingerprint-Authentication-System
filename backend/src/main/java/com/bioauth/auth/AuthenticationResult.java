package com.bioauth.auth;

/**
 * Result object for authentication attempts
 */
public class AuthenticationResult {
    private boolean success;
    private String userId;
    private int matchScore;
    private String message;
    private long timestamp;

    public AuthenticationResult(boolean success, String userId, int matchScore, String message) {
        this.success = success;
        this.userId = userId;
        this.matchScore = matchScore;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters
    public boolean isSuccess() {
        return success;
    }

    public String getUserId() {
        return userId;
    }

    public int getMatchScore() {
        return matchScore;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "AuthenticationResult{" +
                "success=" + success +
                ", userId='" + userId + '\'' +
                ", matchScore=" + matchScore +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
