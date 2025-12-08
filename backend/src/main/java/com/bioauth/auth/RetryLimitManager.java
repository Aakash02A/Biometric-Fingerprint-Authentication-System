package com.bioauth.auth;

import com.bioauth.exceptions.AuthenticationException;
import com.bioauth.config.SystemConfig;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages authentication retry limits and lockout periods
 */
public class RetryLimitManager {
    private static class RetryInfo {
        int attempts;
        long lockedUntil;

        RetryInfo(int attempts, long lockedUntil) {
            this.attempts = attempts;
            this.lockedUntil = lockedUntil;
        }
    }

    private final Map<String, RetryInfo> retryMap = new ConcurrentHashMap<>();

    /**
     * Check if user can attempt authentication
     */
    public boolean canAttempt(String userId) throws AuthenticationException {
        if (userId == null || userId.isEmpty()) {
            throw new AuthenticationException("User ID cannot be empty", 0);
        }

        RetryInfo info = retryMap.get(userId);
        if (info == null) {
            return true;  // No retry info, user can attempt
        }

        // Check if lockout period has expired
        if (System.currentTimeMillis() > info.lockedUntil) {
            retryMap.remove(userId);  // Clear lockout
            return true;
        }

        return false;
    }

    /**
     * Record failed attempt
     */
    public void recordFailedAttempt(String userId) throws AuthenticationException {
        if (userId == null || userId.isEmpty()) {
            throw new AuthenticationException("User ID cannot be empty", 0);
        }

        RetryInfo info = retryMap.computeIfAbsent(userId, k -> new RetryInfo(0, 0));
        info.attempts++;

        // Check if max attempts exceeded
        if (info.attempts >= SystemConfig.MAX_ATTEMPTS) {
            // Lock user for lockout duration
            info.lockedUntil = System.currentTimeMillis() + SystemConfig.LOCKOUT_DURATION;
        }
    }

    /**
     * Record successful attempt (reset counter)
     */
    public void recordSuccessfulAttempt(String userId) throws AuthenticationException {
        if (userId == null || userId.isEmpty()) {
            throw new AuthenticationException("User ID cannot be empty", 0);
        }

        retryMap.remove(userId);  // Clear retry info
    }

    /**
     * Get remaining attempts for user
     */
    public int getRemainingAttempts(String userId) throws AuthenticationException {
        if (userId == null || userId.isEmpty()) {
            throw new AuthenticationException("User ID cannot be empty", 0);
        }

        RetryInfo info = retryMap.get(userId);
        if (info == null) {
            return SystemConfig.MAX_ATTEMPTS;
        }

        int remaining = SystemConfig.MAX_ATTEMPTS - info.attempts;
        return Math.max(0, remaining);
    }

    /**
     * Check if user is locked out
     */
    public boolean isLockedOut(String userId) throws AuthenticationException {
        RetryInfo info = retryMap.get(userId);
        if (info == null) {
            return false;
        }

        if (System.currentTimeMillis() > info.lockedUntil) {
            retryMap.remove(userId);
            return false;
        }

        return true;
    }

    /**
     * Get lockout remaining time in milliseconds
     */
    public long getLockoutRemainingTime(String userId) throws AuthenticationException {
        RetryInfo info = retryMap.get(userId);
        if (info == null) {
            return 0;
        }

        long remaining = info.lockedUntil - System.currentTimeMillis();
        return Math.max(0, remaining);
    }

    /**
     * Force unlock user
     */
    public void unlock(String userId) throws AuthenticationException {
        retryMap.remove(userId);
    }

    /**
     * Clear all retry info
     */
    public void clearAll() {
        retryMap.clear();
    }
}
