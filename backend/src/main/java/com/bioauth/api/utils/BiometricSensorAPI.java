package com.bioauth.api.utils;

import java.security.SecureRandom;
import java.util.Base64;

public class BiometricSensorAPI {

    private static final SecureRandom random = new SecureRandom();
    private static final int TEMPLATE_LENGTH = 256;

    public static String generateFakeScan() {
        byte[] scanData = new byte[TEMPLATE_LENGTH];
        random.nextBytes(scanData);
        return Base64.getEncoder().encodeToString(scanData);
    }

    public static String generateTemplateFromScan(String scanData) throws Exception {
        if (scanData == null || scanData.isEmpty()) {
            throw new IllegalArgumentException("Scan data cannot be null or empty");
        }
        
        byte[] decodedScan = Base64.getDecoder().decode(scanData);
        byte[] templateData = new byte[256];
        System.arraycopy(decodedScan, 0, templateData, 0, Math.min(decodedScan.length, 256));
        
        return Base64.getEncoder().encodeToString(templateData);
    }

    public static Double matchTemplates(String storedTemplate, String scannedTemplate) throws Exception {
        if (storedTemplate == null || scannedTemplate == null) {
            return 0.0;
        }

        try {
            byte[] stored = Base64.getDecoder().decode(storedTemplate);
            byte[] scanned = Base64.getDecoder().decode(scannedTemplate);

            int matches = 0;
            int minLength = Math.min(stored.length, scanned.length);

            for (int i = 0; i < minLength; i++) {
                if (stored[i] == scanned[i]) {
                    matches++;
                }
            }

            double similarity = (double) matches / minLength;
            
            // Add random variance to make it more realistic (±10%)
            double variance = (random.nextDouble() - 0.5) * 0.2;
            similarity = Math.max(0, Math.min(1, similarity + variance));
            
            return similarity;
        } catch (IllegalArgumentException e) {
            return 0.0;
        }
    }

    public static Integer generateQualityScore() {
        // Random quality score between 60-99
        return 60 + random.nextInt(40);
    }

    public static boolean isHighQualityScan(Integer qualityScore) {
        return qualityScore != null && qualityScore >= 75;
    }

    public static boolean isMatchConfident(Double confidence) {
        return confidence != null && confidence >= 0.85;
    }

    public static String generateScanMetadata(String deviceInfo, String ipAddress) {
        return String.format(
            "{\"device\":\"%s\",\"ip\":\"%s\",\"timestamp\":%d}",
            deviceInfo != null ? deviceInfo : "UNKNOWN",
            ipAddress != null ? ipAddress : "0.0.0.0",
            System.currentTimeMillis()
        );
    }
}
