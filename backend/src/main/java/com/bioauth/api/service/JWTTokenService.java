package com.bioauth.api.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * JWT Token Service for authentication token generation and validation
 */
@Service
@Slf4j
public class JWTTokenService {
    
    @Autowired
    private EncryptionService encryptionService;

    private static final String TOKEN_SEPARATOR = ".";
    private static final long TOKEN_EXPIRATION_MS = 3600000; // 1 hour

    /**
     * Generate JWT token for authenticated user
     * 
     * @param userId User ID
     * @param username Username
     * @return JWT token
     */
    public String generateToken(String userId, String username) {
        try {
            long issuedAt = System.currentTimeMillis();
            long expiresAt = issuedAt + TOKEN_EXPIRATION_MS;

            // Token format: header.payload.signature
            String header = encodeBase64(createHeader());
            String payload = encodeBase64(createPayload(userId, username, issuedAt, expiresAt));
            String signature = createSignature(header, payload);

            String token = header + TOKEN_SEPARATOR + payload + TOKEN_SEPARATOR + signature;
            log.info("JWT token generated for user: {}", userId);
            return token;

        } catch (Exception e) {
            log.error("Error generating JWT token: {}", e.getMessage());
            throw new RuntimeException("Token generation failed: " + e.getMessage());
        }
    }

    /**
     * Validate JWT token
     * 
     * @param token JWT token
     * @return true if token is valid
     */
    public boolean validateToken(String token) {
        try {
            if (token == null || token.isEmpty()) {
                return false;
            }

            String[] parts = token.split("\\" + TOKEN_SEPARATOR);
            if (parts.length != 3) {
                log.warn("Invalid token format");
                return false;
            }

            String header = parts[0];
            String payload = parts[1];
            String signature = parts[2];

            // Verify signature
            String expectedSignature = createSignature(header, payload);
            if (!signature.equals(expectedSignature)) {
                log.warn("Invalid token signature");
                return false;
            }

            // Verify expiration
            String payloadJson = decodeBase64(payload);
            if (isTokenExpired(payloadJson)) {
                log.warn("Token expired");
                return false;
            }

            return true;

        } catch (Exception e) {
            log.error("Error validating token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Extract user ID from token
     * 
     * @param token JWT token
     * @return User ID
     */
    public String extractUserId(String token) {
        try {
            String[] parts = token.split("\\" + TOKEN_SEPARATOR);
            if (parts.length != 3) {
                return null;
            }

            String payloadJson = decodeBase64(parts[1]);
            // In production, parse JSON to extract userId
            // For now, extract from payload string
            int startIdx = payloadJson.indexOf("\"userId\":\"") + 10;
            int endIdx = payloadJson.indexOf("\"", startIdx);
            return payloadJson.substring(startIdx, endIdx);

        } catch (Exception e) {
            log.error("Error extracting userId from token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Extract username from token
     * 
     * @param token JWT token
     * @return Username
     */
    public String extractUsername(String token) {
        try {
            String[] parts = token.split("\\" + TOKEN_SEPARATOR);
            if (parts.length != 3) {
                return null;
            }

            String payloadJson = decodeBase64(parts[1]);
            int startIdx = payloadJson.indexOf("\"username\":\"") + 12;
            int endIdx = payloadJson.indexOf("\"", startIdx);
            return payloadJson.substring(startIdx, endIdx);

        } catch (Exception e) {
            log.error("Error extracting username from token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Create JWT header
     */
    private String createHeader() {
        return "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
    }

    /**
     * Create JWT payload
     */
    private String createPayload(String userId, String username, long issuedAt, long expiresAt) {
        return "{\"userId\":\"" + userId + "\",\"username\":\"" + username 
                + "\",\"iat\":" + issuedAt + ",\"exp\":" + expiresAt + "}";
    }

    /**
     * Create JWT signature
     */
    private String createSignature(String header, String payload) {
        try {
            String data = header + TOKEN_SEPARATOR + payload;
            // Simple HMAC-like signature (in production, use proper JWT library)
            return encodeBase64(encryptionService.encrypt(data));
        } catch (Exception e) {
            throw new RuntimeException("Error creating signature: " + e.getMessage());
        }
    }

    /**
     * Check if token is expired
     */
    private boolean isTokenExpired(String payloadJson) {
        try {
            int startIdx = payloadJson.indexOf("\"exp\":") + 6;
            int endIdx = payloadJson.indexOf("}", startIdx);
            long expiresAt = Long.parseLong(payloadJson.substring(startIdx, endIdx));
            return System.currentTimeMillis() > expiresAt;
        } catch (Exception e) {
            log.error("Error checking token expiration: {}", e.getMessage());
            return true; // Assume expired on error
        }
    }

    /**
     * Encode to Base64
     */
    private String encodeBase64(String text) {
        return java.util.Base64.getEncoder().encodeToString(text.getBytes());
    }

    /**
     * Decode from Base64
     */
    private String decodeBase64(String text) {
        return new String(java.util.Base64.getDecoder().decode(text));
    }
}
