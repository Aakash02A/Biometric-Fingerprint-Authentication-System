package com.bioauth.biometric;

import com.bioauth.exceptions.SensorException;
import com.bioauth.config.SystemConfig;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Windows Biometric Framework (WBF) fingerprint sensor integration
 * Uses Windows native biometric APIs via JNA for real fingerprint capture
 * Requires Windows 7+ with fingerprint sensor hardware installed
 */
public class WindowsBiometricSensor implements FingerprintSensor {
    
    private boolean isConnected = false;
    private int lastQuality = 0;
    private Pointer sessionHandle = null;
    private byte[] lastCapturedImage = null;
    private int deviceCount = 0;

    /**
     * Initialize connection to Windows Biometric Framework
     */
    @Override
    public void initialize() throws SensorException {
        if (isConnected) {
            return;
        }

        try {
            // Check if Windows Biometric Framework is available
            if (!WinBioAPI.isAvailable()) {
                throw new SensorException(
                    "Windows Biometric Framework not available. " +
                    "Requires Windows 7+ with compatible fingerprint sensor hardware installed.");
            }

            // Check for available biometric devices
            if (!checkForBiometricDevices()) {
                throw new SensorException(
                    "No fingerprint sensors detected. Please ensure a compatible " +
                    "fingerprint scanner is installed and connected.");
            }

            // Open biometric session
            sessionHandle = openBiometricSession();
            if (sessionHandle == null) {
                throw new SensorException("Failed to open biometric session");
            }

            isConnected = true;
            System.out.println("Windows Biometric Framework sensor initialized");
            System.out.println("Available fingerprint devices: " + deviceCount);

        } catch (SensorException e) {
            throw e;
        } catch (Exception e) {
            throw new SensorException("Sensor initialization failed: " + e.getMessage(), e);
        }
    }

    /**
     * Capture fingerprint from Windows Biometric Framework
     */
    @Override
    public byte[] captureFingerprintImage() throws SensorException {
        if (!isConnected) {
            throw new SensorException("Sensor not initialized. Call initialize() first.");
        }

        try {
            // Perform fingerprint capture
            byte[] fingerprintData = captureBiometricData(sessionHandle);
            
            if (fingerprintData == null || fingerprintData.length == 0) {
                throw new SensorException("No fingerprint data captured");
            }

            this.lastCapturedImage = fingerprintData;
            
            // Calculate quality from captured data
            this.lastQuality = calculateQualityScore(fingerprintData);

            System.out.println("Fingerprint captured. Quality score: " + lastQuality);
            return fingerprintData;

        } catch (SensorException e) {
            throw e;
        } catch (Exception e) {
            throw new SensorException("Fingerprint capture failed: " + e.getMessage(), e);
        }
    }

    /**
     * Get quality of last captured fingerprint
     */
    @Override
    public int getLastCaptureQuality() throws SensorException {
        if (!isConnected) {
            throw new SensorException("Sensor not initialized");
        }
        return lastQuality;
    }

    /**
     * Check if sensor is ready
     */
    @Override
    public boolean isReady() throws SensorException {
        if (!isConnected) {
            return false;
        }
        
        try {
            return checkSensorStatus(sessionHandle);
        } catch (Exception e) {
            throw new SensorException("Error checking sensor status: " + e.getMessage(), e);
        }
    }

    /**
     * Shutdown sensor connection
     */
    @Override
    public void shutdown() throws SensorException {
        if (isConnected && sessionHandle != null) {
            try {
                closeBiometricSession(sessionHandle);
                isConnected = false;
                sessionHandle = null;
                System.out.println("Windows Biometric Framework sensor shutdown");
            } catch (Exception e) {
                throw new SensorException("Error during shutdown: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Get sensor information
     */
    @Override
    public String getSensorInfo() throws SensorException {
        try {
            return "Windows Biometric Framework Sensor (Fingerprint) - " +
                    "Devices: " + deviceCount;
        } catch (Exception e) {
            return "Windows Biometric Framework Sensor (Error retrieving info)";
        }
    }

    /**
     * Check if fingerprint sensors are available on this system
     */
    private boolean checkForBiometricDevices() throws SensorException {
        try {
            if (!WinBioAPI.isAvailable()) {
                return false;
            }

            PointerByReference unitSchemaArray = new PointerByReference();
            PointerByReference unitCount = new PointerByReference();

            // Call WinBioEnumBiometricUnits to enumerate devices
            int result = WinBioAPI.WinBio.INSTANCE.WinBioEnumBiometricUnits(
                WinBioAPI.WINBIO_TYPE_FINGERPRINT,
                unitSchemaArray,
                unitCount
            );

            if (result == WinBioAPI.WINBIO_SUCCESS) {
                // Count available devices
                long count = Pointer.nativeValue(unitCount.getValue());
                deviceCount = (int) count;
                
                // Free the enumerated units array
                if (unitSchemaArray.getValue() != null) {
                    WinBioAPI.WinBio.INSTANCE.WinBioFree(unitSchemaArray.getValue());
                }
                
                return deviceCount > 0;
            } else {
                System.err.println("Error enumerating biometric units: " + 
                    WinBioAPI.getErrorMessage(result));
                return false;
            }

        } catch (Exception e) {
            System.err.println("Could not enumerate biometric devices: " + e.getMessage());
            return false;
        }
    }

    /**
     * Open a session with Windows Biometric Framework
     */
    private Pointer openBiometricSession() throws SensorException {
        try {
            PointerByReference sessionHandleRef = new PointerByReference();

            // Call WinBioOpenSession
            int result = WinBioAPI.WinBio.INSTANCE.WinBioOpenSession(
                WinBioAPI.WINBIO_TYPE_FINGERPRINT,
                WinBioAPI.WINBIO_POOL_SYSTEM,
                0,
                sessionHandleRef
            );

            if (result == WinBioAPI.WINBIO_SUCCESS) {
                return sessionHandleRef.getValue();
            } else {
                String errorMsg = WinBioAPI.getErrorMessage(result);
                throw new SensorException("Failed to open biometric session: " + errorMsg);
            }
        } catch (SensorException e) {
            throw e;
        } catch (Exception e) {
            throw new SensorException("Failed to open biometric session: " + e.getMessage(), e);
        }
    }

    /**
     * Capture biometric data from the sensor
     */
    private byte[] captureBiometricData(Pointer sessionHandle) throws SensorException {
        try {
            PointerByReference sample = new PointerByReference();
            PointerByReference sampleSize = new PointerByReference();
            PointerByReference rejectDetail = new PointerByReference();

            // Call WinBioCaptureSample
            int result = WinBioAPI.WinBio.INSTANCE.WinBioCaptureSample(
                sessionHandle,
                0x01,  // Purpose: enrollment
                WinBioAPI.WINBIO_DATA_TYPE_RAW,
                sample,
                sampleSize,
                rejectDetail
            );

            if (result == WinBioAPI.WINBIO_SUCCESS && sample.getValue() != null) {
                // Get sample size
                long size = Pointer.nativeValue(sampleSize.getValue());
                
                // Extract fingerprint data
                byte[] fingerprintData = sample.getValue().getByteArray(0, (int) size);
                
                // Free allocated sample
                WinBioAPI.WinBio.INSTANCE.WinBioFree(sample.getValue());
                
                return fingerprintData;
            } else {
                String errorMsg = WinBioAPI.getErrorMessage(result);
                throw new SensorException("Failed to capture fingerprint: " + errorMsg);
            }

        } catch (SensorException e) {
            throw e;
        } catch (Exception e) {
            throw new SensorException("Fingerprint capture error: " + e.getMessage(), e);
        }
    }

    /**
     * Close biometric session
     */
    private void closeBiometricSession(Pointer sessionHandle) throws SensorException {
        try {
            int result = WinBioAPI.WinBio.INSTANCE.WinBioCloseSession(sessionHandle);
            if (result != WinBioAPI.WINBIO_SUCCESS) {
                System.err.println("Error closing session: " + WinBioAPI.getErrorMessage(result));
            }
        } catch (Exception e) {
            throw new SensorException("Failed to close session: " + e.getMessage(), e);
        }
    }

    /**
     * Check sensor status
     */
    private boolean checkSensorStatus(Pointer sessionHandle) throws SensorException {
        try {
            PointerByReference sensorStatus = new PointerByReference();

            int result = WinBioAPI.WinBio.INSTANCE.WinBioGetSensorStatus(
                sessionHandle,
                sensorStatus
            );

            if (result == WinBioAPI.WINBIO_SUCCESS) {
                int status = sensorStatus.getValue().getInt(0);
                return status == WinBioAPI.WINBIO_SENSOR_READY;
            } else {
                return false;
            }

        } catch (Exception e) {
            throw new SensorException("Failed to check sensor status: " + e.getMessage(), e);
        }
    }

    /**
     * Calculate quality score from fingerprint data
     */
    private int calculateQualityScore(byte[] fingerprintData) {
        if (fingerprintData == null || fingerprintData.length == 0) {
            return 0;
        }

        int qualityScore = 50; // Base score
        
        // Analyze fingerprint data entropy
        int uniqueBytes = 0;
        boolean[] seen = new boolean[256];
        for (byte b : fingerprintData) {
            if (!seen[b & 0xFF]) {
                seen[b & 0xFF] = true;
                uniqueBytes++;
            }
        }
        
        // More entropy = better quality
        qualityScore += (uniqueBytes / 256.0) * 30;
        
        // Check for pattern transitions (indicates ridge patterns)
        int transitionCount = 0;
        for (int i = 1; i < fingerprintData.length; i++) {
            if (fingerprintData[i] != fingerprintData[i - 1]) {
                transitionCount++;
            }
        }
        
        // Good transitions indicate fingerprint ridge patterns
        qualityScore += Math.min(20, (transitionCount / (double) fingerprintData.length) * 100);
        
        return Math.min(100, Math.max(0, qualityScore));
    }
}

