package com.bioauth.biometric;

import com.bioauth.exceptions.SensorException;
import com.bioauth.config.SystemConfig;
import java.security.SecureRandom;

/**
 * Simulated fingerprint sensor for development and testing
 * In production, this would be replaced with actual sensor API
 */
public class SimulatedFingerprintSensor implements FingerprintSensor {
    private boolean isConnected = false;
    private int lastQuality = 0;
    private final SecureRandom random = new SecureRandom();

    @Override
    public byte[] captureFingerprintImage() throws SensorException {
        if (!isConnected) {
            throw new SensorException("Sensor not initialized. Call initialize() first.");
        }

        // Simulate capture time
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new SensorException("Capture interrupted: " + e.getMessage(), e);
        }

        // Generate simulated quality score
        this.lastQuality = 50 + random.nextInt(50);  // 50-99

        // Generate simulated fingerprint data (256 bytes of random data)
        byte[] simulatedImage = new byte[SystemConfig.FEATURE_VECTOR_SIZE];
        random.nextBytes(simulatedImage);

        return simulatedImage;
    }

    @Override
    public int getLastCaptureQuality() throws SensorException {
        if (!isConnected) {
            throw new SensorException("Sensor not initialized");
        }
        return lastQuality;
    }

    @Override
    public boolean isReady() throws SensorException {
        return isConnected;
    }

    @Override
    public void initialize() throws SensorException {
        try {
            // Simulate initialization delay
            Thread.sleep(200);
            isConnected = true;
        } catch (InterruptedException e) {
            throw new SensorException("Sensor initialization failed: " + e.getMessage(), e);
        }
    }

    @Override
    public void shutdown() throws SensorException {
        isConnected = false;
    }

    @Override
    public String getSensorInfo() throws SensorException {
        return "Simulated Fingerprint Sensor v1.0 (Development Mode)";
    }
}
