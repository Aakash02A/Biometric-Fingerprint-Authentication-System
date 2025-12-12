package com.bioauth.api.utils;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class SecurityUtil {

    public static String hashPassword(String password) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(hash);
    }

    public static boolean verifyPassword(String password, String hash) throws Exception {
        String passwordHash = hashPassword(password);
        return passwordHash.equals(hash);
    }

    public static String generateRandomToken(int length) {
        SecureRandom random = new SecureRandom();
        byte[] values = new byte[length];
        random.nextBytes(values);
        return Base64.getEncoder().encodeToString(values);
    }

    public static String hashData(String data) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(data.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(hash);
    }

    public static Double calculateSimilarity(String template1, String template2) {
        if (template1 == null || template2 == null) {
            return 0.0;
        }
        
        // Simulate fingerprint matching algorithm
        int matches = 0;
        int minLength = Math.min(template1.length(), template2.length());
        
        for (int i = 0; i < minLength; i++) {
            if (template1.charAt(i) == template2.charAt(i)) {
                matches++;
            }
        }
        
        return (double) matches / minLength;
    }
}
