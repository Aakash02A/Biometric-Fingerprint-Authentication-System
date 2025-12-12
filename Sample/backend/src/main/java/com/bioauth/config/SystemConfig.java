package com.bioauth.config;

/**
 * System-wide configuration constants and settings
 */
public class SystemConfig {
    // Fingerprint matching thresholds
    public static final int MATCH_THRESHOLD = 85;  // Minimum match score (0-100)
    public static final int EXCELLENT_QUALITY = 85;
    public static final int GOOD_QUALITY = 70;
    public static final int POOR_QUALITY = 50;

    // Sensor configuration
    public static final String SENSOR_TYPE = "auto";  // "auto", "windows", or "simulated"
    public static final int FINGERPRINT_TEMPLATE_SIZE = 512;  // bytes
    public static final int DPI_RESOLUTION = 500;
    public static final int MAX_CAPTURE_TIME = 5000;  // milliseconds
    public static final int MIN_CAPTURE_TIME = 1000;  // milliseconds
    public static final int SENSOR_TIMEOUT = 10000;  // milliseconds - timeout for sensor operations

    // Retry and lockout configuration
    public static final int MAX_AUTHENTICATION_ATTEMPTS = 3;
    public static final long LOCKOUT_DURATION = 300000;  // 5 minutes in milliseconds
    public static final int LOCKOUT_THRESHOLD = 5;  // After 5 failed attempts

    // Storage configuration
    public static final String STORAGE_TYPE = "FILE";  // "FILE" or "DATABASE"
    public static final String DATA_DIRECTORY = "./data";
    public static final String USERS_FILE = "users.json";
    public static final String LOGS_FILE = "audit.log";

    // Encryption configuration
    public static final String ENCRYPTION_ALGORITHM = "AES";
    public static final int KEY_SIZE = 256;  // bits
    public static final String KEY_ALGORITHM = "AES";

    // Logging configuration
    public static final String LOG_LEVEL = "INFO";
    public static final boolean ENABLE_AUDIT_LOGGING = true;
    public static final long LOG_ROTATION_SIZE = 10 * 1024 * 1024;  // 10 MB

    // Feature extraction configuration
    public static final int MINUTIAE_POINTS = 50;  // Number of minutiae points to extract
    public static final int FEATURE_VECTOR_SIZE = 256;  // Size of feature vector

    private SystemConfig() {
        // Private constructor to prevent instantiation
    }
}
