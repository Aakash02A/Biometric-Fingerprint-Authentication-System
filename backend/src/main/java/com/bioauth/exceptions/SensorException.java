package com.bioauth.exceptions;

/**
 * Exception thrown when sensor is not available or fails
 */
public class SensorException extends BiometricException {
    public SensorException(String message) {
        super("SENSOR_ERROR", message);
    }

    public SensorException(String message, Throwable cause) {
        super("SENSOR_ERROR", message, cause);
    }
}
