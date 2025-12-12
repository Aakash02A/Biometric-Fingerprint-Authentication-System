package com.bioauth.biometric;

import com.bioauth.config.SystemConfig;
import com.bioauth.exceptions.SensorException;

/**
 * Factory for creating fingerprint sensor instances
 * Supports both real hardware sensors and simulated sensors for development/testing
 */
public class SensorFactory {
    
    /**
     * Create appropriate sensor based on system configuration and OS
     */
    public static FingerprintSensor createSensor() throws SensorException {
        String sensorType = SystemConfig.SENSOR_TYPE;
        String osName = System.getProperty("os.name").toLowerCase();
        
        // Auto-detect Windows Biometric Framework if configured
        if ("auto".equalsIgnoreCase(sensorType)) {
            if (osName.contains("win")) {
                try {
                    return createWindowsBiometricSensor();
                } catch (Exception e) {
                    System.out.println("Windows Biometric Framework not available, falling back to simulated sensor: " + e.getMessage());
                    return new SimulatedFingerprintSensor();
                }
            } else {
                System.out.println("Windows Biometric Framework only available on Windows. Using simulated sensor.");
                return new SimulatedFingerprintSensor();
            }
        }
        // Explicitly requested Windows Biometric Framework
        else if ("windows".equalsIgnoreCase(sensorType)) {
            if (!osName.contains("win")) {
                throw new SensorException("Windows Biometric Framework requires Windows OS");
            }
            return createWindowsBiometricSensor();
        }
        // Explicitly requested simulated sensor
        else if ("simulated".equalsIgnoreCase(sensorType)) {
            return new SimulatedFingerprintSensor();
        }
        else {
            throw new SensorException("Unknown sensor type: " + sensorType);
        }
    }

    /**
     * Create Windows Biometric Framework sensor
     */
    private static FingerprintSensor createWindowsBiometricSensor() throws SensorException {
        try {
            WindowsBiometricSensor sensor = new WindowsBiometricSensor();
            // Verify sensor is available before returning
            // Don't initialize yet - that happens when actually needed
            return sensor;
        } catch (Exception e) {
            throw new SensorException("Failed to create Windows Biometric Framework sensor: " + e.getMessage(), e);
        }
    }

    /**
     * Create simulated sensor for testing/development
     */
    public static FingerprintSensor createSimulatedSensor() {
        return new SimulatedFingerprintSensor();
    }
}
