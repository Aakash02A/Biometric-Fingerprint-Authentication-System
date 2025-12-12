package com.bioauth;

import com.bioauth.biometric.FingerprintSensor;
import com.bioauth.biometric.SensorFactory;
import com.bioauth.biometric.WindowsBiometricSensor;
import com.bioauth.exceptions.SensorException;

/**
 * Simple test application to demonstrate real-time fingerprint scanning
 * with your laptop's biometric fingerprint scanner
 */
public class FingerprintTest {
    
    public static void main(String[] args) {
        FingerprintSensor sensor = null;
        
        try {
            System.out.println("=== Fingerprint Scanner Test ===");
            System.out.println("Initializing fingerprint scanner...");
            
            // Create sensor using factory (will auto-detect your scanner)
            sensor = SensorFactory.createSensor();
            System.out.println("Sensor created: " + sensor.getClass().getSimpleName());
            System.out.println("Sensor info: " + sensor.getSensorInfo());
            
            // Initialize the sensor
            System.out.println("\nConnecting to fingerprint scanner...");
            sensor.initialize();
            System.out.println("✓ Scanner connected successfully!");
            
            // Check if sensor is ready
            boolean isReady = sensor.isReady();
            System.out.println("Sensor ready: " + (isReady ? "YES" : "NO"));
            
            if (isReady) {
                System.out.println("\n=== Real-Time Fingerprint Scanning ===");
                System.out.println("Place your finger on the scanner now...");
                
                // Capture fingerprint
                byte[] fingerprintData = sensor.captureFingerprintImage();
                System.out.println("✓ Fingerprint captured successfully!");
                System.out.println("Data size: " + fingerprintData.length + " bytes");
                
                // Get quality score
                int quality = sensor.getLastCaptureQuality();
                System.out.println("Quality score: " + quality + "/100");
                
                // Interpret quality
                if (quality >= 85) {
                    System.out.println("Quality rating: EXCELLENT");
                } else if (quality >= 70) {
                    System.out.println("Quality rating: GOOD");
                } else if (quality >= 50) {
                    System.out.println("Quality rating: FAIR");
                } else {
                    System.out.println("Quality rating: POOR - Try again");
                }
                
                System.out.println("\n=== Scan Complete ===");
            } else {
                System.out.println("⚠ Sensor is not ready. Check connections and try again.");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Clean up
            if (sensor != null) {
                try {
                    sensor.shutdown();
                    System.out.println("Scanner disconnected.");
                } catch (SensorException e) {
                    System.err.println("Error shutting down sensor: " + e.getMessage());
                }
            }
        }
    }
}