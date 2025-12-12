package com.bioauth.user;

import com.bioauth.exceptions.UserException;
import com.bioauth.storage.EncryptionService;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Service layer for user management operations
 */
public class UserService {
    private final IUserRepository userRepository;
    private final EncryptionService encryptionService;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@(.+)$"
    );
    private static final Pattern USER_ID_PATTERN = Pattern.compile("^USR\\d{3,}$");

    public UserService(IUserRepository userRepository, EncryptionService encryptionService) {
        this.userRepository = userRepository;
        this.encryptionService = encryptionService;
    }

    /**
     * Register a new user with biometric data
     */
    public User registerUser(String userId, String username, String email, byte[] fingerprintTemplate, int qualityScore)
            throws UserException {
        
        // Validate input
        validateUserId(userId);
        validateUsername(username);
        validateEmail(email);
        validateFingerprintTemplate(fingerprintTemplate);

        // Check if user already exists
        if (userRepository.exists(userId)) {
            throw new UserException("User with ID " + userId + " already exists");
        }

        // Create new user
        User user = new User(userId, username, email);

        // Encrypt and store fingerprint template
        try {
            byte[] encryptedTemplate = encryptionService.encrypt(fingerprintTemplate);
            user.setFingerprintTemplate(encryptedTemplate);
        } catch (Exception e) {
            throw new UserException("Failed to encrypt fingerprint template: " + e.getMessage(), e);
        }

        user.setQualityScore(qualityScore);
        
        // Save user
        userRepository.save(user);
        
        return user;
    }

    /**
     * Get user by ID
     */
    public User getUserById(String userId) throws UserException {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new UserException("User with ID " + userId + " not found");
        }
        return user.get();
    }

    /**
     * Get user by username
     */
    public User getUserByUsername(String username) throws UserException {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new UserException("User with username " + username + " not found");
        }
        return user.get();
    }

    /**
     * Update user information
     */
    public void updateUser(User user) throws UserException {
        if (user == null) {
            throw new UserException("User cannot be null");
        }
        userRepository.update(user);
    }

    /**
     * Delete user
     */
    public void deleteUser(String userId) throws UserException {
        userRepository.delete(userId);
    }

    /**
     * Get decrypted fingerprint template
     */
    public byte[] getDecryptedTemplate(String userId) throws UserException {
        User user = getUserById(userId);
        if (user.getFingerprintTemplate() == null) {
            throw new UserException("User " + userId + " has no fingerprint template");
        }
        
        try {
            return encryptionService.decrypt(user.getFingerprintTemplate());
        } catch (Exception e) {
            throw new UserException("Failed to decrypt fingerprint template: " + e.getMessage(), e);
        }
    }

    /**
     * Check if user is locked out due to failed attempts
     */
    public boolean isUserLockedOut(User user, long lockoutDuration) {
        if (user.getFailedAuthAttempts() == 0) {
            return false;
        }
        long timeSinceLastAttempt = System.currentTimeMillis() - user.getLastAuthAttemptTime();
        return timeSinceLastAttempt < lockoutDuration;
    }

    /**
     * Increment failed auth attempts
     */
    public void incrementFailedAttempts(User user) throws UserException {
        user.setFailedAuthAttempts(user.getFailedAuthAttempts() + 1);
        user.setLastAuthAttemptTime(System.currentTimeMillis());
        userRepository.update(user);
    }

    /**
     * Reset failed auth attempts
     */
    public void resetFailedAttempts(User user) throws UserException {
        user.setFailedAuthAttempts(0);
        userRepository.update(user);
    }

    /**
     * Get total registered users count
     */
    public int getTotalUsers() throws UserException {
        return userRepository.count();
    }

    // ============ Validation Methods ============

    private void validateUserId(String userId) throws UserException {
        if (userId == null || userId.isEmpty()) {
            throw new UserException("User ID cannot be empty");
        }
        if (!USER_ID_PATTERN.matcher(userId).matches()) {
            throw new UserException("User ID must be in format USRxxx (e.g., USR001)");
        }
    }

    private void validateUsername(String username) throws UserException {
        if (username == null || username.isEmpty()) {
            throw new UserException("Username cannot be empty");
        }
        if (username.length() < 3) {
            throw new UserException("Username must be at least 3 characters");
        }
        if (username.length() > 50) {
            throw new UserException("Username must not exceed 50 characters");
        }
    }

    private void validateEmail(String email) throws UserException {
        if (email == null || email.isEmpty()) {
            throw new UserException("Email cannot be empty");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new UserException("Invalid email format");
        }
    }

    private void validateFingerprintTemplate(byte[] template) throws UserException {
        if (template == null || template.length == 0) {
            throw new UserException("Fingerprint template cannot be empty");
        }
        if (template.length > 10 * 1024) {  // 10 KB max
            throw new UserException("Fingerprint template is too large");
        }
    }
}
