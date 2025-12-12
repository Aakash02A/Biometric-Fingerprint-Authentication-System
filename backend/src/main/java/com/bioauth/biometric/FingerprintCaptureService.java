package com.bioauth.biometric;

import com.bioauth.exceptions.SensorException;
import com.bioauth.exceptions.LowQualityException;
import com.bioauth.config.SystemConfig;
import java.security.MessageDigest;
import java.util.Arrays;

/**
 * Service for fingerprint capture and preprocessing
 */
public class FingerprintCaptureService {
    private final FingerprintSensor sensor;
    private final FeatureExtractionService featureExtractor;

    public FingerprintCaptureService(FingerprintSensor sensor, FeatureExtractionService featureExtractor) {
        this.sensor = sensor;
        this.featureExtractor = featureExtractor;
    }

    /**
     * Capture fingerprint and extract features
     */
    public byte[] captureAndProcess() throws SensorException, LowQualityException {
        // Check sensor readiness
        if (!sensor.isReady()) {
            throw new SensorException("Fingerprint sensor not ready");
        }

        // Perform capture
        long captureStartTime = System.currentTimeMillis();
        byte[] capturedImage = sensor.captureFingerprintImage();
        long captureTime = System.currentTimeMillis() - captureStartTime;

        // Validate capture time
        if (captureTime > SystemConfig.MAX_CAPTURE_TIME) {
            throw new SensorException("Capture timeout exceeded: " + captureTime + "ms");
        }

        // Check quality
        int quality = sensor.getLastCaptureQuality();
        if (quality < SystemConfig.POOR_QUALITY) {
            throw new LowQualityException("Fingerprint quality too low: " + quality, quality);
        }

        // Extract features
        byte[] features = featureExtractor.extractFeatures(capturedImage);

        return features;
    }

    /**
     * Multi-sample capture for better reliability
     */
    public byte[] captureMultipleSamples(int numSamples) throws SensorException, LowQualityException {
        if (numSamples < 1 || numSamples > 5) {
            throw new SensorException("Invalid number of samples: " + numSamples);
        }

        byte[][] samples = new byte[numSamples][];
        int goodSamples = 0;

        for (int i = 0; i < numSamples; i++) {
            try {
                samples[i] = captureAndProcess();
                goodSamples++;
            } catch (LowQualityException e) {
                // Low quality capture, try again
                if (i == numSamples - 1 && goodSamples == 0) {
                    throw e;  // No good samples at all
                }
            }
        }

        if (goodSamples == 0) {
            throw new LowQualityException("Could not capture any good quality samples", 0);
        }

        // Fuse samples (simple averaging for now)
        return fuseSamples(Arrays.copyOf(samples, goodSamples));
    }

    /**
     * Fuse multiple fingerprint samples
     */
    private byte[] fuseSamples(byte[][] samples) throws SensorException {
        if (samples.length == 0) {
            throw new SensorException("No samples to fuse");
        }

        if (samples.length == 1) {
            return samples[0];
        }

        // Simple fusion: XOR all samples for redundancy
        byte[] fused = Arrays.copyOf(samples[0], SystemConfig.FEATURE_VECTOR_SIZE);
        for (int i = 1; i < samples.length; i++) {
            for (int j = 0; j < fused.length && j < samples[i].length; j++) {
                fused[j] ^= samples[i][j];
            }
        }

        return fused;
    }

    /**
     * Initialize sensor
     */
    public void initialize() throws SensorException {
        sensor.initialize();
    }

    /**
     * Shutdown sensor
     */
    public void shutdown() throws SensorException {
        sensor.shutdown();
    }

    /**
     * Get sensor information
     */
    public String getSensorInfo() throws SensorException {
        return sensor.getSensorInfo();
    }
}
