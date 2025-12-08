package com.bioauth.exceptions;

/**
 * Exception thrown when fingerprint quality is below threshold
 */
public class LowQualityException extends BiometricException {
    private int qualityScore;

    public LowQualityException(String message, int qualityScore) {
        super("LOW_QUALITY", message);
        this.qualityScore = qualityScore;
    }

    public int getQualityScore() {
        return qualityScore;
    }
}
