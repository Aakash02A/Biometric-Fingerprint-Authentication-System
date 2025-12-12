package com.bioauth.biometric;

import com.bioauth.exceptions.SensorException;

/**
 * Interface for fingerprint sensor integration
 * Allows different sensor implementations
 */
public interface FingerprintSensor {
    /**
     * Capture fingerprint from sensor
     * @return Raw fingerprint image/data as byte array
     */
    byte[] captureFingerprintImage() throws SensorException;

    /**
     * Get quality of last captured fingerprint
     * @return Quality score 0-100
     */
    int getLastCaptureQuality() throws SensorException;

    /**
     * Check if sensor is connected and ready
     */
    boolean isReady() throws SensorException;

    /**
     * Initialize sensor connection
     */
    void initialize() throws SensorException;

    /**
     * Close sensor connection
     */
    void shutdown() throws SensorException;

    /**
     * Get sensor information
     */
    String getSensorInfo() throws SensorException;
}
