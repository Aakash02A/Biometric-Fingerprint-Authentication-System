package com.bioauth;

import com.sun.jna.Platform;
import com.sun.jna.Native;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Diagnostic tool to check biometric sensor availability and Windows Biometric Framework status
 */
public class BiometricDiagnostic {
    
    public static void main(String[] args) {
        System.out.println("=== Biometric System Diagnostic Tool ===");
        System.out.println();
        
        // Check OS
        checkOperatingSystem();
        
        // Check Windows Biometric Framework
        checkWindowsBiometricFramework();
        
        // Check biometric devices
        checkBiometricDevices();
        
        // Check JNA availability
        checkJNA();
        
        System.out.println("\n=== Diagnostic Complete ===");
    }
    
    private static void checkOperatingSystem() {
        System.out.println("1. Operating System Check:");
        String osName = System.getProperty("os.name");
        String osVersion = System.getProperty("os.version");
        System.out.println("   OS Name: " + osName);
        System.out.println("   OS Version: " + osVersion);
        System.out.println("   Is Windows: " + Platform.isWindows());
        System.out.println();
    }
    
    private static void checkWindowsBiometricFramework() {
        System.out.println("2. Windows Biometric Framework Check:");
        
        try {
            // Check if winbio.dll exists
            boolean winbioExists = checkFileExists("C:\\Windows\\System32\\winbio.dll");
            System.out.println("   WinBio DLL exists: " + (winbioExists ? "YES" : "NO"));
            
            if (winbioExists) {
                System.out.println("   Windows Biometric Framework: AVAILABLE");
            } else {
                System.out.println("   Windows Biometric Framework: NOT FOUND");
                System.out.println("   Note: This is required for fingerprint scanning");
            }
        } catch (Exception e) {
            System.out.println("   Error checking WinBio framework: " + e.getMessage());
        }
        System.out.println();
    }
    
    private static void checkBiometricDevices() {
        System.out.println("3. Biometric Devices Check:");
        
        try {
            // Use PowerShell to check for biometric devices
            ProcessBuilder pb = new ProcessBuilder("powershell", "-Command", 
                "Get-PnpDevice | Where-Object {$_.Class -eq 'Biometric'} | Select-Object Name, Manufacturer, Status | Format-List");
            Process process = pb.start();
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            boolean foundDevices = false;
            
            System.out.println("   Detected biometric devices:");
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    System.out.println("     " + line);
                    foundDevices = true;
                }
            }
            
            if (!foundDevices) {
                System.out.println("     No biometric devices found");
            }
            
            process.waitFor();
        } catch (Exception e) {
            System.out.println("   Error checking biometric devices: " + e.getMessage());
        }
        System.out.println();
    }
    
    private static void checkJNA() {
        System.out.println("4. JNA (Java Native Access) Check:");
        
        try {
            System.out.println("   JNA Version: " + com.sun.jna.Native.VERSION);
            System.out.println("   Platform: " + (Platform.isWindows() ? "Windows" : 
                              Platform.isLinux() ? "Linux" : 
                              Platform.isMac() ? "macOS" : "Other"));
            
            // Try to load a simple Windows library
            if (Platform.isWindows()) {
                try {
                    com.sun.jna.Library kernel32 = Native.load("kernel32", com.sun.jna.Library.class);
                    System.out.println("   JNA Kernel32 binding: SUCCESS");
                } catch (Exception e) {
                    System.out.println("   JNA Kernel32 binding: FAILED - " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("   Error checking JNA: " + e.getMessage());
        }
        System.out.println();
    }
    
    private static boolean checkFileExists(String filePath) {
        try {
            return new java.io.File(filePath).exists();
        } catch (Exception e) {
            return false;
        }
    }
}