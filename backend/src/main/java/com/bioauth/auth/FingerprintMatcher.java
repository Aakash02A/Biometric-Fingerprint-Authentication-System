package com.bioauth.auth;

import com.bioauth.exceptions.AuthenticationException;
import com.bioauth.config.SystemConfig;

/**
 * Service for fingerprint matching and similarity calculation
 */
public class FingerprintMatcher {
    
    /**
     * Compare two fingerprint templates
     * @return Similarity score 0-100
     */
    public int matchFingerprints(byte[] template1, byte[] template2) throws AuthenticationException {
        if (template1 == null || template2 == null) {
            throw new AuthenticationException("Templates cannot be null", 0);
        }
        
        if (template1.length == 0 || template2.length == 0) {
            throw new AuthenticationException("Templates cannot be empty", 0);
        }

        // Match templates of different lengths by using the shorter one
        int minLength = Math.min(template1.length, template2.length);
        int matchingBytes = 0;

        // Count matching bytes
        for (int i = 0; i < minLength; i++) {
            if (template1[i] == template2[i]) {
                matchingBytes++;
            }
        }

        // Calculate similarity score (0-100)
        int similarity = (matchingBytes * 100) / minLength;
        
        return similarity;
    }

    /**
     * Check if two templates match above threshold
     */
    public boolean isMatch(byte[] template1, byte[] template2) throws AuthenticationException {
        int similarity = matchFingerprints(template1, template2);
        return similarity >= SystemConfig.MATCH_THRESHOLD;
    }

    /**
     * Calculate advanced similarity using multiple algorithms
     */
    public int calculateAdvancedSimilarity(byte[] template1, byte[] template2) throws AuthenticationException {
        if (template1 == null || template2 == null) {
            throw new AuthenticationException("Templates cannot be null", 0);
        }

        // Algorithm 1: Byte-by-byte matching
        int byteMatching = matchFingerprints(template1, template2);

        // Algorithm 2: Hamming distance
        int hammingDistance = calculateHammingDistance(template1, template2);

        // Algorithm 3: Statistical correlation
        int correlation = calculateCorrelation(template1, template2);

        // Weighted average: 50% byte matching, 25% hamming, 25% correlation
        int similarity = (byteMatching * 50 + hammingDistance * 25 + correlation * 25) / 100;

        return Math.max(0, Math.min(100, similarity));  // Clamp to 0-100
    }

    /**
     * Calculate hamming distance (number of differing bits)
     */
    private int calculateHammingDistance(byte[] template1, byte[] template2) {
        int minLength = Math.min(template1.length, template2.length);
        int hammingDistance = 0;

        for (int i = 0; i < minLength; i++) {
            byte xor = (byte) (template1[i] ^ template2[i]);
            // Count set bits
            for (int j = 0; j < 8; j++) {
                if ((xor & (1 << j)) != 0) {
                    hammingDistance++;
                }
            }
        }

        // Convert to similarity (inverse: lower distance = higher similarity)
        int maxDistance = minLength * 8;
        return 100 - (hammingDistance * 100 / maxDistance);
    }

    /**
     * Calculate statistical correlation between templates
     */
    private int calculateCorrelation(byte[] template1, byte[] template2) {
        int minLength = Math.min(template1.length, template2.length);
        
        if (minLength == 0) return 0;

        // Calculate means
        int sum1 = 0, sum2 = 0;
        for (int i = 0; i < minLength; i++) {
            sum1 += (template1[i] & 0xFF);
            sum2 += (template2[i] & 0xFF);
        }
        double mean1 = sum1.0 / minLength;
        double mean2 = sum2.0 / minLength;

        // Calculate correlation coefficient
        double numerator = 0;
        double denom1 = 0, denom2 = 0;
        for (int i = 0; i < minLength; i++) {
            double diff1 = (template1[i] & 0xFF) - mean1;
            double diff2 = (template2[i] & 0xFF) - mean2;
            numerator += diff1 * diff2;
            denom1 += diff1 * diff1;
            denom2 += diff2 * diff2;
        }

        double denominator = Math.sqrt(denom1 * denom2);
        double correlation = (denominator > 0) ? numerator / denominator : 0;

        // Convert to 0-100 scale
        return (int) ((correlation + 1) * 50);  // Range [-1, 1] -> [0, 100]
    }
}
