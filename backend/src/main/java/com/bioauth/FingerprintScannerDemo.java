package com.bioauth;

import com.bioauth.biometric.FingerprintSensor;
import com.bioauth.biometric.SensorFactory;
import com.bioauth.biometric.WindowsBiometricSensor;
import com.bioauth.exceptions.SensorException;

/**
 * Enhanced demo application to demonstrate real-time fingerprint scanning
 * with error handling for various scanner states
 */
public class FingerprintScannerDemo {
    
    public static void main(String[] args) {
        System.out.println("=== Biometric Fingerprint Scanner Demo ===");
        System.out.println("System: " + System.getProperty("os.name"));
        System.out.println();
        
        FingerprintSensor sensor = null;
        
        try {
            System.out.println("Step 1: Creating fingerprint sensor...");
            sensor = SensorFactory.createSensor();
            System.out.println("✓ Sensor created: " + sensor.getClass().getSimpleName());
            
            try {
                String sensorInfo = sensor.getSensorInfo();
                System.out.println("Sensor info: " + sensorInfo);
            } catch (Exception e) {
                System.out.println("Sensor info: Not available (" + e.getMessage() + ")");
            }
            
            System.out.println("\nStep 2: Initializing sensor connection...");
            try {
                sensor.initialize();
                System.out.println("✓ Sensor initialized successfully!");
            } catch (Exception e) {
                System.out.println("⚠ Sensor initialization failed: " + e.getMessage());
                System.out.println("This may happen if:");
                System.out.println("  - Your fingerprint scanner is not properly connected");
                System.out.println("  - Windows Biometric Service is not running");
                System.out.println("  - Drivers are not properly installed");
                System.out.println("  - You're not running with administrator privileges");
                return;
            }
            
            System.out.println("\nStep 3: Checking sensor status...");
            try {
                boolean isReady = sensor.isReady();
                System.out.println("Sensor ready: " + (isReady ? "YES" : "NO"));
                
                if (!isReady) {
                    System.out.println("⚠ Sensor is not ready for scanning.");
                    System.out.println("Please check:");
                    System.out.println("  - Fingerprint scanner is properly connected");
                    System.out.println("  - Windows Biometric Service is enabled");
                    System.out.println("  - No other application is using the scanner");
                    return;
                }
            } catch (Exception e) {
                System.out.println("⚠ Could not check sensor status: " + e.getMessage());
                return;
            }
            
            System.out.println("\n=== Real-Time Fingerprint Scanning ===");
            System.out.println("Place your finger on the scanner now...");
            System.out.println("(Press Ctrl+C to exit)");
            
            // Continuous scanning loop
            int scanCount = 0;
            while (scanCount < 3) {  // Limit to 3 scans for demo
                try {
                    System.out.println("\nWaiting for fingerprint #" + (scanCount + 1) + "...");
                    
                    // Capture fingerprint
                    byte[] fingerprintData = sensor.captureFingerprintImage();
                    scanCount++;
                    
                    System.out.println("✓ Fingerprint captured successfully!");
                    System.out.println("  Data size: " + fingerprintData.length + " bytes");
                    
                    // Get quality score
                    int quality = sensor.getLastCaptureQuality();
                    System.out.println("  Quality score: " + quality + "/100");
                    
                    // Interpret quality
                    if (quality >= 85) {
                        System.out.println("  Quality rating: EXCELLENT ✅");
                    } else if (quality >= 70) {
                        System.out.println("  Quality rating: GOOD ✅");
                    } else if (quality >= 50) {
                        System.out.println("  Quality rating: FAIR ⚠️");
                    } else {
                        System.out.println("  Quality rating: POOR ❌ - Try again");
                    }
                    
                    // Brief pause between scans
                    Thread.sleep(2000);
                    
                } catch (Exception e) {
                    System.out.println("⚠ Scan failed: " + e.getMessage());
                    // Continue with next scan attempt
                }
            }
            
            System.out.println("\n=== Demo Complete ===");
            System.out.println("Successfully performed " + scanCount + " fingerprint scans.");
            
        } catch (Exception e) {
            System.err.println("❌ Fatal error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Clean up
            if (sensor != null) {
                try {
                    System.out.println("\nCleaning up...");
                    sensor.shutdown();
                    System.out.println("✓ Scanner disconnected.");
                } catch (Exception e) {
                    System.err.println("⚠ Error during cleanup: " + e.getMessage());
                }
            }
        }
        
        System.out.println("\nThank you for using the Biometric Fingerprint Scanner Demo!");
    }
}