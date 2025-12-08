package com.bioauth;

import com.bioauth.auth.AuthenticationResult;
import com.bioauth.exceptions.*;
import com.bioauth.logging.AuditLog;
import com.bioauth.user.User;
import java.util.List;
import java.util.Map;

/**
 * Demo application showcasing the Biometric Authentication System
 */
public class SystemDemo {

    public static void main(String[] args) {
        try {
            demo();
        } catch (Exception e) {
            System.err.println("Demo failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void demo() throws Exception {
        System.out.println("========================================");
        System.out.println("BIOMETRIC FINGERPRINT AUTHENTICATION");
        System.out.println("System Demo");
        System.out.println("========================================\n");

        // Initialize system
        BiometricAuthenticationSystem system = new BiometricAuthenticationSystem();
        System.out.println();

        // Demo 1: Register users
        System.out.println("--- DEMO 1: User Registration ---");
        demoRegistration(system);
        System.out.println();

        // Demo 2: Authentication
        System.out.println("--- DEMO 2: User Authentication ---");
        demoAuthentication(system);
        System.out.println();

        // Demo 3: Failed authentication and lockout
        System.out.println("--- DEMO 3: Failed Authentication & Lockout ---");
        demoFailedAuth(system);
        System.out.println();

        // Demo 4: Audit logs
        System.out.println("--- DEMO 4: Audit Logs ---");
        demoAuditLogs(system);
        System.out.println();

        // Demo 5: Statistics
        System.out.println("--- DEMO 5: System Statistics ---");
        demoStatistics(system);
        System.out.println();

        // Display system info
        System.out.println("--- System Information ---");
        System.out.println(system.getSystemInfo());
        System.out.println();

        // Cleanup
        system.shutdown();
    }

    private static void demoRegistration(BiometricAuthenticationSystem system) throws Exception {
        try {
            // Simulate fingerprint capture
            byte[] fingerprint1 = system.captureFingerprintFromSensor();

            // Register user
            User user1 = system.registerUser("USR001", "alice_johnson", 
                                           "alice@company.com", "IT Department", 
                                           fingerprint1);
            
            System.out.println("✓ Registered: " + user1.getUsername() 
                             + " (Quality: " + user1.getQualityScore() + "%)");

            // Register second user
            byte[] fingerprint2 = system.captureFingerprintFromSensor();
            User user2 = system.registerUser("USR002", "bob_smith", 
                                           "bob@company.com", "HR Department", 
                                           fingerprint2);
            
            System.out.println("✓ Registered: " + user2.getUsername() 
                             + " (Quality: " + user2.getQualityScore() + "%)");

            System.out.println("Total registered users: " + system.getTotalUsers());

        } catch (Exception e) {
            System.out.println("✗ Registration failed: " + e.getMessage());
            throw e;
        }
    }

    private static void demoAuthentication(BiometricAuthenticationSystem system) throws Exception {
        try {
            // Capture fingerprint (same user re-registering their fingerprint for auth)
            byte[] fingerprintAuth = system.captureFingerprintFromSensor();

            // Authenticate user USR001
            AuthenticationResult result = system.authenticate("USR001", fingerprintAuth);
            
            if (result.isSuccess()) {
                System.out.println("✓ Authentication successful for: " + result.getUserId());
                System.out.println("  Match Score: " + result.getMatchScore() + "%");
                System.out.println("  Message: " + result.getMessage());
            } else {
                System.out.println("✗ Authentication failed: " + result.getMessage());
            }

        } catch (AuthenticationException e) {
            System.out.println("✗ Authentication error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("✗ Unexpected error: " + e.getMessage());
        }
    }

    private static void demoFailedAuth(BiometricAuthenticationSystem system) throws Exception {
        try {
            // Simulate multiple failed authentication attempts
            byte[] wrongFingerprint = new byte[256];
            java.util.Arrays.fill(wrongFingerprint, (byte) 0xFF);

            for (int i = 1; i <= 3; i++) {
                try {
                    system.authenticate("USR002", wrongFingerprint);
                } catch (AuthenticationException e) {
                    System.out.println("✗ Attempt " + i + " failed: " + e.getMessage());
                }
            }

            // Try to authenticate after lockout
            try {
                system.authenticate("USR002", wrongFingerprint);
            } catch (AuthenticationException e) {
                System.out.println("✓ Account locked as expected: " + e.getMessage());
            }

        } catch (Exception e) {
            System.out.println("Error in failed auth demo: " + e.getMessage());
        }
    }

    private static void demoAuditLogs(BiometricAuthenticationSystem system) {
        try {
            List<AuditLog> recentLogs = system.getAuditLogs(10);
            System.out.println("Recent audit logs (last " + recentLogs.size() + "):"); 
            
            for (AuditLog log : recentLogs) {
                System.out.println("  " + log);
            }

        } catch (Exception e) {
            System.out.println("Error retrieving logs: " + e.getMessage());
        }
    }

    private static void demoStatistics(BiometricAuthenticationSystem system) {
        try {
            Map<String, Object> stats = system.getAuthenticationStats();
            System.out.println("Authentication Statistics:");
            System.out.println("  Total Attempts: " + stats.get("totalAttempts"));
            System.out.println("  Successful: " + stats.get("successCount"));
            System.out.println("  Failed: " + stats.get("failureCount"));
            System.out.println("  Success Rate: " + stats.get("successRate") + "%");

        } catch (Exception e) {
            System.out.println("Error getting statistics: " + e.getMessage());
        }
    }
}
