package com.bioauth.api.service;

import com.bioauth.api.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Authentication service with fingerprint matching and security checks
 */
@Service
@Slf4j
public class AuthenticationService {
    @Autowired
    private UserService userService;

    @Autowired
    private EncryptionService encryptionService;

    @Autowired
    private AuditLogService auditLogService;

    @Value("${bioauth.authentication.match-threshold:85}")
    private int matchThreshold;

    @Value("${bioauth.authentication.max-attempts:3}")
    private int maxAttempts;

    @Value("${bioauth.authentication.lockout-duration:300}")
    private long lockoutDurationSeconds;

    /**
     * Authenticate user with fingerprint
     */
    public AuthenticationResponse authenticate(String userId, byte[] fingerprintData, String ipAddress) {
        long startTime = System.currentTimeMillis();

        try {
            // Get user
            UserEntity user = userService.getUserById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found: " + userId));

            // Check if user is active
            if (!user.getIsActive()) {
                auditLogService.logEvent("LOGIN_FAILURE", userId, user.getUsername(), "authenticate",
                        "FAILURE", 0, "User account is inactive", ipAddress);
                return AuthenticationResponse.builder()
                        .success(false)
                        .message("User account is inactive")
                        .build();
            }

            // Check lockout
            if (isUserLockedOut(user)) {
                long remainingSeconds = getRemainingLockoutTime(user);
                auditLogService.logEvent("LOGIN_FAILURE", userId, user.getUsername(), "authenticate",
                        "FAILURE", 0, "Account locked. Remaining time: " + remainingSeconds + "s", ipAddress);
                return AuthenticationResponse.builder()
                        .success(false)
                        .message("Account locked. Try again in " + remainingSeconds + " seconds")
                        .remainingSeconds(remainingSeconds)
                        .build();
            }

            // Decrypt stored template
            byte[] storedTemplate = encryptionService.decrypt(user.getFingerprintTemplate());

            // Match fingerprints
            int matchScore = matchFingerprints(fingerprintData, storedTemplate);

            // Check if match passes threshold
            if (matchScore >= matchThreshold) {
                // Success
                user.setLastLoginDate(LocalDateTime.now());
                user.setLastLoginIp(ipAddress);
                user.setFailedAuthAttempts(0);
                userService.updateUser(user);

                long duration = System.currentTimeMillis() - startTime;
                auditLogService.logEvent("LOGIN_SUCCESS", userId, user.getUsername(), "authenticate",
                        "SUCCESS", matchScore, "Authentication successful", ipAddress);

                log.info("User authenticated successfully: {} (score: {})", userId, matchScore);
                return AuthenticationResponse.builder()
                        .success(true)
                        .message("Authentication successful")
                        .matchScore(matchScore)
                        .userId(userId)
                        .username(user.getUsername())
                        .processingTimeMs(duration)
                        .build();
            } else {
                // Failure
                userService.incrementFailedAttempts(userId);
                int remainingAttempts = maxAttempts - user.getFailedAuthAttempts();

                auditLogService.logEvent("LOGIN_FAILURE", userId, user.getUsername(), "authenticate",
                        "FAILURE", matchScore, "Match score: " + matchScore + "%. Attempts remaining: " + remainingAttempts, ipAddress);

                log.warn("Authentication failed for user: {} (score: {})", userId, matchScore);
                return AuthenticationResponse.builder()
                        .success(false)
                        .message("Fingerprint does not match")
                        .matchScore(matchScore)
                        .remainingAttempts(remainingAttempts)
                        .build();
            }

        } catch (Exception e) {
            auditLogService.logEvent("LOGIN_FAILURE", userId, null, "authenticate",
                    "ERROR", 0, "Error: " + e.getMessage(), ipAddress);
            log.error("Authentication error for user {}: {}", userId, e.getMessage());
            return AuthenticationResponse.builder()
                    .success(false)
                    .message("Authentication failed: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Match two fingerprint templates
     */
    private int matchFingerprints(byte[] template1, byte[] template2) {
        if (template1 == null || template2 == null) {
            return 0;
        }

        int minLength = Math.min(template1.length, template2.length);
        int matchingBytes = 0;

        for (int i = 0; i < minLength; i++) {
            if (template1[i] == template2[i]) {
                matchingBytes++;
            }
        }

        return (matchingBytes * 100) / minLength;
    }

    /**
     * Check if user is locked out
     */
    private boolean isUserLockedOut(UserEntity user) {
        if (user.getFailedAuthAttempts() < maxAttempts) {
            return false;
        }

        if (user.getLastAuthAttemptTime() == null) {
            return false;
        }

        long secondsElapsed = ChronoUnit.SECONDS.between(user.getLastAuthAttemptTime(), LocalDateTime.now());
        return secondsElapsed < lockoutDurationSeconds;
    }

    /**
     * Get remaining lockout time in seconds
     */
    private long getRemainingLockoutTime(UserEntity user) {
        if (!isUserLockedOut(user)) {
            return 0;
        }

        long secondsElapsed = ChronoUnit.SECONDS.between(user.getLastAuthAttemptTime(), LocalDateTime.now());
        return lockoutDurationSeconds - secondsElapsed;
    }
}
