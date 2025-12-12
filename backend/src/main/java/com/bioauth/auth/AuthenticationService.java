package com.bioauth.auth;

import com.bioauth.exceptions.AuthenticationException;
import com.bioauth.exceptions.UserException;
import com.bioauth.config.SystemConfig;
import com.bioauth.user.User;
import com.bioauth.user.UserService;
import com.bioauth.storage.EncryptionService;
import com.bioauth.logging.AuthenticationLogger;

/**
 * Main authentication service coordinating the authentication process
 */
public class AuthenticationService {
    private final UserService userService;
    private final FingerprintMatcher matcher;
    private final EncryptionService encryptionService;
    private final RetryLimitManager retryManager;
    private final AuthenticationLogger logger;

    public AuthenticationService(UserService userService, FingerprintMatcher matcher,
                               EncryptionService encryptionService, RetryLimitManager retryManager,
                               AuthenticationLogger logger) {
        this.userService = userService;
        this.matcher = matcher;
        this.encryptionService = encryptionService;
        this.retryManager = retryManager;
        this.logger = logger;
    }

    /**
     * Authenticate user with fingerprint
     */
    public AuthenticationResult authenticate(String userId, byte[] fingerprintData) 
            throws AuthenticationException, UserException {
        
        long startTime = System.currentTimeMillis();
        
        try {
            // Validate input
            if (userId == null || userId.isEmpty()) {
                throw new AuthenticationException("User ID cannot be empty", 0);
            }
            if (fingerprintData == null || fingerprintData.length == 0) {
                throw new AuthenticationException("Fingerprint data cannot be empty", 0);
            }

            // Get user
            User user = userService.getUserById(userId);

            // Check if user is locked out
            if (userService.isUserLockedOut(user, SystemConfig.LOCKOUT_DURATION)) {
                String msg = "User account temporarily locked";
                logger.logFailedAuthentication(userId, msg, 0);
                throw new AuthenticationException(msg, 0);
            }

            // Get stored template
            byte[] storedTemplate = userService.getDecryptedTemplate(userId);

            // Match fingerprints
            int matchScore = matcher.calculateAdvancedSimilarity(fingerprintData, storedTemplate);

            // Check if match passes threshold
            boolean isSuccess = matchScore >= SystemConfig.MATCH_THRESHOLD;

            // Handle result
            if (isSuccess) {
                userService.resetFailedAttempts(user);
                long duration = System.currentTimeMillis() - startTime;
                logger.logSuccessfulAuthentication(userId, matchScore, duration);
                return new AuthenticationResult(true, userId, matchScore, "Authentication successful");
            } else {
                userService.incrementFailedAttempts(user);
                int remainingAttempts = SystemConfig.MAX_ATTEMPTS - user.getFailedAuthAttempts();
                String msg = "Fingerprint does not match. Attempts remaining: " + remainingAttempts;
                logger.logFailedAuthentication(userId, msg, matchScore);
                throw new AuthenticationException(msg, matchScore);
            }

        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            logger.logFailedAuthentication(userId, "Authentication error: " + e.getMessage(), 0);
            throw new AuthenticationException("Authentication process failed: " + e.getMessage(), 0, e);
        }
    }

    /**
     * Authenticate user by username (convenience method)
     */
    public AuthenticationResult authenticateByUsername(String username, byte[] fingerprintData)
            throws AuthenticationException, UserException {
        
        User user = userService.getUserByUsername(username);
        return authenticate(user.getUserId(), fingerprintData);
    }

    /**
     * Verify if user can attempt authentication
     */
    public boolean canAttemptAuthentication(String userId) throws UserException {
        User user = userService.getUserById(userId);
        return !userService.isUserLockedOut(user, SystemConfig.LOCKOUT_DURATION) &&
               user.isActive();
    }

    /**
     * Reset failed attempts for user (admin function)
     */
    public void resetFailedAttempts(String userId) throws UserException {
        User user = userService.getUserById(userId);
        userService.resetFailedAttempts(user);
        logger.logAdminAction("Reset failed attempts for user: " + userId);
    }

    /**
     * Lock user account (admin function)
     */
    public void lockUser(String userId) throws UserException {
        User user = userService.getUserById(userId);
        user.setActive(false);
        userService.updateUser(user);
        logger.logAdminAction("Locked user account: " + userId);
    }

    /**
     * Unlock user account (admin function)
     */
    public void unlockUser(String userId) throws UserException {
        User user = userService.getUserById(userId);
        user.setActive(true);
        user.setFailedAuthAttempts(0);
        userService.updateUser(user);
        logger.logAdminAction("Unlocked user account: " + userId);
    }
}
