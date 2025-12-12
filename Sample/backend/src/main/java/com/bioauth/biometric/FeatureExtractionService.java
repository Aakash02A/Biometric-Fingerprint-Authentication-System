package com.bioauth.biometric;

import com.bioauth.exceptions.SensorException;
import com.bioauth.config.SystemConfig;
import java.security.MessageDigest;

/**
 * Service for extracting distinguishing features from fingerprint images
 */
public class FeatureExtractionService {
    
    /**
     * Extract features from raw fingerprint image
     */
    public byte[] extractFeatures(byte[] fingerprintImage) throws SensorException {
        if (fingerprintImage == null || fingerprintImage.length == 0) {
            throw new SensorException("Invalid fingerprint image");
        }

        try {
            // Step 1: Normalize image (simulated)
            byte[] normalized = normalizeImage(fingerprintImage);

            // Step 2: Extract minutiae points (simulated)
            byte[] minutiae = extractMinutiae(normalized);

            // Step 3: Create feature vector from minutiae
            byte[] featureVector = createFeatureVector(minutiae);

            return featureVector;
        } catch (Exception e) {
            throw new SensorException("Feature extraction failed: " + e.getMessage(), e);
        }
    }

    /**
     * Normalize fingerprint image (reduce noise, enhance contrast)
     */
    private byte[] normalizeImage(byte[] image) {
        // Simulated normalization: apply simple hash-based transformation
        byte[] normalized = new byte[image.length];
        for (int i = 0; i < image.length; i++) {
            // Simple normalization: shift and scale
            normalized[i] = (byte) ((image[i] + 128) % 256);
        }
        return normalized;
    }

    /**
     * Extract minutiae points (bifurcations and endings)
     * In real implementation, would use ridge detection algorithms
     */
    private byte[] extractMinutiae(byte[] normalizedImage) {
        byte[] minutiae = new byte[SystemConfig.MINUTIAE_POINTS];
        
        // Simulated minutiae extraction: use hash-based selection
        int step = Math.max(1, normalizedImage.length / SystemConfig.MINUTIAE_POINTS);
        for (int i = 0; i < SystemConfig.MINUTIAE_POINTS; i++) {
            int idx = (i * step) % normalizedImage.length;
            minutiae[i] = normalizedImage[idx];
        }
        
        return minutiae;
    }

    /**
     * Create feature vector from minutiae points
     */
    private byte[] createFeatureVector(byte[] minutiae) throws Exception {
        // Simulated feature vector creation: expand minutiae using hash
        byte[] featureVector = new byte[SystemConfig.FEATURE_VECTOR_SIZE];
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        
        // Use minutiae as seed for feature generation
        md.update(minutiae);
        byte[] hash1 = md.digest();
        
        // Copy and rotate for additional features
        System.arraycopy(hash1, 0, featureVector, 0, Math.min(hash1.length, featureVector.length));
        
        // Fill remaining with secondary hash
        if (featureVector.length > hash1.length) {
            md.update(hash1);
            byte[] hash2 = md.digest();
            System.arraycopy(hash2, 0, featureVector, hash1.length, 
                           Math.min(hash2.length, featureVector.length - hash1.length));
        }
        
        return featureVector;
    }

    /**
     * Get quality score from extracted features
     */
    public int getFeatureQuality(byte[] features) throws SensorException {
        if (features == null || features.length == 0) {
            throw new SensorException("Invalid features");
        }

        // Simulated quality calculation based on feature entropy
        int nonZeroCount = 0;
        for (byte b : features) {
            if (b != 0) {
                nonZeroCount++;
            }
        }

        // Quality score: percentage of non-zero feature bytes
        return (nonZeroCount * 100) / features.length;
    }
}
