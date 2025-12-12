package com.bioauth.api.service;

import com.bioauth.api.entity.UserEntity;
import com.bioauth.api.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for user management with database operations
 */
@Service
@Transactional
@Slf4j
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EncryptionService encryptionService;

    @Autowired
    private AuditLogService auditLogService;

    /**
     * Register new user with fingerprint
     */
    public UserEntity registerUser(String userId, String username, String email, 
                                   String department, byte[] fingerprintImage, 
                                   int qualityScore, String ipAddress) {
        try {
            // Check if user already exists
            if (userRepository.findByUserId(userId).isPresent()) {
                throw new RuntimeException("User ID already exists: " + userId);
            }
            if (userRepository.findByUsername(username).isPresent()) {
                throw new RuntimeException("Username already exists: " + username);
            }
            if (userRepository.findByEmail(email).isPresent()) {
                throw new RuntimeException("Email already exists: " + email);
            }

            // Encrypt fingerprint template
            byte[] encryptedTemplate = encryptionService.encrypt(fingerprintImage);

            // Create user entity
            UserEntity user = UserEntity.builder()
                    .userId(userId)
                    .username(username)
                    .email(email)
                    .department(department)
                    .fingerprintTemplate(encryptedTemplate)
                    .fingerprintImage(fingerprintImage)
                    .qualityScore(qualityScore)
                    .isActive(true)
                    .failedAuthAttempts(0)
                    .registrationDate(LocalDateTime.now())
                    .registrationIp(ipAddress)
                    .build();

            // Save user
            UserEntity savedUser = userRepository.save(user);

            // Log registration
            auditLogService.logEvent("REGISTRATION", userId, username, "register", 
                                   "SUCCESS", null, null, ipAddress);

            log.info("User registered successfully: {}", userId);
            return savedUser;

        } catch (Exception e) {
            auditLogService.logEvent("REGISTRATION", userId, username, "register", 
                                   "FAILURE", null, e.getMessage(), ipAddress);
            throw new RuntimeException("Registration failed: " + e.getMessage(), e);
        }
    }

    /**
     * Get user by ID
     */
    public Optional<UserEntity> getUserById(String userId) {
        return userRepository.findByUserId(userId);
    }

    /**
     * Get user by username
     */
    public Optional<UserEntity> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Update user
     */
    public UserEntity updateUser(UserEntity user) {
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    /**
     * Get all users
     */
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Get active users
     */
    public List<UserEntity> getActiveUsers() {
        return userRepository.findByIsActive(true);
    }

    /**
     * Lock user account
     */
    public void lockUser(String userId) {
        Optional<UserEntity> userOpt = userRepository.findByUserId(userId);
        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();
            user.setIsActive(false);
            userRepository.save(user);
            log.info("User locked: {}", userId);
        }
    }

    /**
     * Unlock user account
     */
    public void unlockUser(String userId) {
        Optional<UserEntity> userOpt = userRepository.findByUserId(userId);
        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();
            user.setIsActive(true);
            user.setFailedAuthAttempts(0);
            userRepository.save(user);
            log.info("User unlocked: {}", userId);
        }
    }

    /**
     * Increment failed auth attempts
     */
    public void incrementFailedAttempts(String userId) {
        Optional<UserEntity> userOpt = userRepository.findByUserId(userId);
        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();
            user.setFailedAuthAttempts(user.getFailedAuthAttempts() + 1);
            user.setLastAuthAttemptTime(LocalDateTime.now());
            userRepository.save(user);
        }
    }

    /**
     * Reset failed auth attempts
     */
    public void resetFailedAttempts(String userId) {
        Optional<UserEntity> userOpt = userRepository.findByUserId(userId);
        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();
            user.setFailedAuthAttempts(0);
            userRepository.save(user);
        }
    }

    /**
     * Get total user count
     */
    public long getTotalUsers() {
        return userRepository.count();
    }

    /**
     * Delete user
     */
    public void deleteUser(String userId) {
        Optional<UserEntity> userOpt = userRepository.findByUserId(userId);
        if (userOpt.isPresent()) {
            userRepository.delete(userOpt.get());
            log.info("User deleted: {}", userId);
        }
    }
}
