package com.bioauth.config;

/**
 * Security and encryption related configuration
 */
public class SecurityConfig {
    // Master encryption key (should be loaded from secure key management system)
    private static byte[] masterKey;
    
    // Salt for key derivation
    public static final String MASTER_KEY_SALT = "bioauth_master_salt_2024";
    
    // Key derivation iterations
    public static final int KEY_DERIVATION_ITERATIONS = 10000;
    
    // Cipher configuration
    public static final String CIPHER_TRANSFORMATION = "AES/CBC/PKCS5Padding";
    public static final int IV_SIZE = 16;  // bytes
    
    // Hash algorithm for fingerprint matching
    public static final String HASH_ALGORITHM = "SHA-256";
    
    // Template encryption algorithm
    public static final String TEMPLATE_ENCRYPTION = "AES/ECB/PKCS5Padding";
    
    // Enable two-factor authentication
    public static final boolean ENABLE_2FA = false;
    
    // Enable rate limiting
    public static final boolean ENABLE_RATE_LIMITING = true;
    
    // Session configuration
    public static final long SESSION_TIMEOUT = 1800000;  // 30 minutes in milliseconds
    
    // Password/PIN configuration (for backup authentication)
    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final boolean REQUIRE_SPECIAL_CHARS = true;
    public static final boolean REQUIRE_NUMBERS = true;
    
    public static byte[] getMasterKey() {
        if (masterKey == null) {
            // Generate or load master key
            // This is a placeholder - in production, load from secure key store
            masterKey = new byte[32];  // 256 bits
        }
        return masterKey;
    }
    
    public static void setMasterKey(byte[] key) {
        SecurityConfig.masterKey = key;
    }
    
    private SecurityConfig() {
        // Private constructor to prevent instantiation
    }
}
