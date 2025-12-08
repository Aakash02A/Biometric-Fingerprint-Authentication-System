package com.bioauth;

import com.bioauth.auth.*;
import com.bioauth.biometric.*;
import com.bioauth.config.SystemConfig;
import com.bioauth.exceptions.*;
import com.bioauth.logging.AuthenticationLogger;
import com.bioauth.storage.AESEncryptionService;
import com.bioauth.storage.BiometricTemplateStorage;
import com.bioauth.user.User;
import com.bioauth.user.UserRepository;
import com.bioauth.user.UserService;

/**
 * Main application class for Biometric Fingerprint Authentication System
 * Coordinates all services and provides public API
 */
public class BiometricAuthenticationSystem {
    private final UserService userService;
    private final AuthenticationService authService;
    private final FingerprintCaptureService captureService;
    private final FeatureExtractionService featureExtractor;
    private final FingerprintMatcher matcher;
    private final AESEncryptionService encryptionService;
    private final BiometricTemplateStorage templateStorage;
    private final AuthenticationLogger logger;
    private final FingerprintSensor sensor;

    /**
     * Initialize the system with all services
     */
    public BiometricAuthenticationSystem() throws Exception {
        // Initialize encryption
        this.encryptionService = new AESEncryptionService();
        
        // Initialize storage
        this.templateStorage = new BiometricTemplateStorage();
        
        // Initialize user service
        UserRepository userRepository = new UserRepository();
        this.userService = new UserService(userRepository, encryptionService);
        
        // Initialize biometric services
        this.sensor = new SimulatedFingerprintSensor();
        this.featureExtractor = new FeatureExtractionService();
        this.captureService = new FingerprintCaptureService(sensor, featureExtractor);
        
        // Initialize matcher and retry manager
        this.matcher = new FingerprintMatcher();
        RetryLimitManager retryManager = new RetryLimitManager();
        
        // Initialize logger
        this.logger = new AuthenticationLogger();
        
        // Initialize authentication service
        this.authService = new AuthenticationService(userService, matcher, 
                                                     encryptionService, retryManager, logger);
        
        System.out.println("Biometric Authentication System initialized successfully");
        System.out.println("Sensor: " + sensor.getSensorInfo());
    }

    /**
     * Register new user with fingerprint
     */
    public User registerUser(String userId, String username, String email, String department,
                            byte[] fingerprintData) throws Exception {
        try {
            // Extract features from fingerprint
            byte[] features = featureExtractor.extractFeatures(fingerprintData);
            int quality = featureExtractor.getFeatureQuality(features);
            
            if (quality < SystemConfig.GOOD_QUALITY) {
                throw new LowQualityException("Fingerprint quality insufficient: " + quality, quality);
            }

            // Register user with encryption
            User user = userService.registerUser(userId, username, email, features, quality);

            // Store encrypted template
            templateStorage.storeTemplate(userId, user.getFingerprintTemplate());

            // Log registration
            logger.logRegistration(userId, username, "Department: " + department);

            System.out.println("User registered successfully: " + userId);
            return user;

        } catch (Exception e) {
            logger.logFailedAuthentication(userId, "Registration failed: " + e.getMessage(), 0);
            throw e;
        }
    }

    /**
     * Authenticate user with fingerprint
     */
    public AuthenticationResult authenticate(String userId, byte[] fingerprintData) 
            throws Exception {
        try {
            // Check if user can attempt authentication
            if (!authService.canAttemptAuthentication(userId)) {
                throw new AuthenticationException("User account is locked or inactive", 0);
            }

            // Extract features from captured fingerprint
            byte[] capturedFeatures = featureExtractor.extractFeatures(fingerprintData);

            // Perform authentication
            AuthenticationResult result = authService.authenticate(userId, capturedFeatures);

            return result;

        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            throw new AuthenticationException("Authentication failed: " + e.getMessage(), 0, e);
        }
    }

    /**
     * Capture fingerprint from sensor
     */
    public byte[] captureFingerprintFromSensor() throws Exception {
        try {
            sensor.initialize();
            byte[] fingerprintImage = captureService.captureAndProcess();
            return fingerprintImage;
        } finally {
            sensor.shutdown();
        }
    }

    /**
     * Capture multiple fingerprint samples (for better accuracy)
     */
    public byte[] captureMultipleSamples(int numSamples) throws Exception {
        try {
            sensor.initialize();
            byte[] fusedTemplate = captureService.captureMultipleSamples(numSamples);
            return fusedTemplate;
        } finally {
            sensor.shutdown();
        }
    }

    /**
     * Get user information
     */
    public User getUser(String userId) throws UserException {
        return userService.getUserById(userId);
    }

    /**
     * Get total registered users
     */
    public int getTotalUsers() throws UserException {
        return userService.getTotalUsers();
    }

    /**
     * Lock user account (admin)
     */
    public void lockUser(String userId) throws UserException {
        authService.lockUser(userId);
    }

    /**
     * Unlock user account (admin)
     */
    public void unlockUser(String userId) throws UserException {
        authService.unlockUser(userId);
    }

    /**
     * Get audit logs
     */
    public java.util.List<com.bioauth.logging.AuditLog> getAuditLogs(int count) {
        return logger.getRecentLogs(count);
    }

    /**
     * Get authentication statistics
     */
    public java.util.Map<String, Object> getAuthenticationStats() {
        return logger.getAuthenticationStats();
    }

    /**
     * Get system information
     */
    public String getSystemInfo() {
        return String.format("Biometric Authentication System v1.0\n" +
                "Total Users: %d\n" +
                "Total Logs: %d\n" +
                "Match Threshold: %d%%\n" +
                "Good Quality: %d%%\n" +
                "Max Auth Attempts: %d\n" +
                "Encryption: AES-%d",
                getTotalUsers(), logger.getLogCount(), SystemConfig.MATCH_THRESHOLD,
                SystemConfig.GOOD_QUALITY, SystemConfig.MAX_ATTEMPTS, SystemConfig.KEY_SIZE);
    }

    /**
     * Shutdown system
     */
    public void shutdown() throws Exception {
        sensor.shutdown();
        System.out.println("Biometric Authentication System shutdown");
    }

    // ============ Getters for Services ============

    public UserService getUserService() {
        return userService;
    }

    public AuthenticationService getAuthenticationService() {
        return authService;
    }

    public FingerprintCaptureService getCaptureService() {
        return captureService;
    }

    public AuthenticationLogger getLogger() {
        return logger;
    }

    public AESEncryptionService getEncryptionService() {
        return encryptionService;
    }

    public BiometricTemplateStorage getTemplateStorage() {
        return templateStorage;
    }
}
