package com.bioauth;

import com.bioauth.auth.*;
import com.bioauth.biometric.*;
import com.bioauth.logging.AuditLog;
import com.bioauth.user.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import static org.junit.Assert.*;

/**
 * Comprehensive test suite for Biometric Authentication System
 */
public class BiometricAuthenticationSystemTest {
    private BiometricAuthenticationSystem system;

    @Before
    public void setUp() throws Exception {
        system = new BiometricAuthenticationSystem();
    }

    @After
    public void tearDown() throws Exception {
        system.shutdown();
    }

    // ============ User Registration Tests ============

    @Test
    public void testSuccessfulUserRegistration() throws Exception {
        byte[] fingerprint = system.captureFingerprintFromSensor();
        User user = system.registerUser("USR001", "testuser", "test@company.com", "IT", fingerprint);
        
        assertNotNull(user);
        assertEquals("USR001", user.getUserId());
        assertEquals("testuser", user.getUsername());
        assertTrue(user.isActive());
        assertNotNull(user.getFingerprintTemplate());
    }

    @Test(expected = com.bioauth.exceptions.UserException.class)
    public void testDuplicateUserIdRegistration() throws Exception {
        byte[] fingerprint = system.captureFingerprintFromSensor();
        system.registerUser("USR001", "user1", "user1@company.com", "IT", fingerprint);
        
        // Try to register same user ID
        system.registerUser("USR001", "user2", "user2@company.com", "IT", fingerprint);
    }

    @Test(expected = com.bioauth.exceptions.LowQualityException.class)
    public void testLowQualityFingerprintRegistration() throws Exception {
        byte[] lowQualityFingerprint = new byte[1];  // Too small, will fail quality check
        system.registerUser("USR001", "testuser", "test@company.com", "IT", lowQualityFingerprint);
    }

    // ============ Authentication Tests ============

    @Test
    public void testSuccessfulAuthentication() throws Exception {
        // Register user
        byte[] fingerprint = system.captureFingerprintFromSensor();
        system.registerUser("USR001", "alice", "alice@company.com", "IT", fingerprint);

        // Authenticate with same fingerprint (in real system would be different capture)
        AuthenticationResult result = system.authenticate("USR001", fingerprint);
        assertTrue(result.isSuccess());
        assertGreaterThanOrEqual(result.getMatchScore(), 85);
    }

    @Test(expected = com.bioauth.exceptions.AuthenticationException.class)
    public void testAuthenticationWithInvalidUserId() throws Exception {
        byte[] fingerprint = system.captureFingerprintFromSensor();
        system.authenticate("INVALID", fingerprint);
    }

    // ============ Retry Limit Tests ============

    @Test
    public void testFailedAuthenticationAttempts() throws Exception {
        byte[] fingerprint = system.captureFingerprintFromSensor();
        system.registerUser("USR001", "testuser", "test@company.com", "IT", fingerprint);

        // Create wrong fingerprint
        byte[] wrongFingerprint = new byte[256];
        java.util.Arrays.fill(wrongFingerprint, (byte) 255);

        // Attempt multiple failed authentications
        int failureCount = 0;
        for (int i = 0; i < 3; i++) {
            try {
                system.authenticate("USR001", wrongFingerprint);
            } catch (com.bioauth.exceptions.AuthenticationException e) {
                failureCount++;
            }
        }

        assertEquals(3, failureCount);
    }

    // ============ User Management Tests ============

    @Test
    public void testGetUserById() throws Exception {
        byte[] fingerprint = system.captureFingerprintFromSensor();
        system.registerUser("USR001", "alice", "alice@company.com", "IT", fingerprint);

        User user = system.getUser("USR001");
        assertNotNull(user);
        assertEquals("alice", user.getUsername());
    }

    @Test
    public void testLockUser() throws Exception {
        byte[] fingerprint = system.captureFingerprintFromSensor();
        system.registerUser("USR001", "alice", "alice@company.com", "IT", fingerprint);

        system.lockUser("USR001");
        User user = system.getUser("USR001");
        assertFalse(user.isActive());
    }

    @Test
    public void testUnlockUser() throws Exception {
        byte[] fingerprint = system.captureFingerprintFromSensor();
        system.registerUser("USR001", "alice", "alice@company.com", "IT", fingerprint);

        system.lockUser("USR001");
        system.unlockUser("USR001");
        
        User user = system.getUser("USR001");
        assertTrue(user.isActive());
    }

    // ============ Audit Log Tests ============

    @Test
    public void testAuditLogGeneration() throws Exception {
        byte[] fingerprint = system.captureFingerprintFromSensor();
        system.registerUser("USR001", "alice", "alice@company.com", "IT", fingerprint);

        java.util.List<AuditLog> logs = system.getAuditLogs(10);
        assertTrue(logs.size() > 0);
        assertEquals("REGISTRATION", logs.get(0).getEventType());
    }

    @Test
    public void testMultipleAuthenticationLogs() throws Exception {
        byte[] fingerprint = system.captureFingerprintFromSensor();
        system.registerUser("USR001", "alice", "alice@company.com", "IT", fingerprint);

        // Successful auth
        system.authenticate("USR001", fingerprint);

        java.util.List<AuditLog> logs = system.getAuditLogs(20);
        long authLogs = logs.stream()
                .filter(log -> "LOGIN_SUCCESS".equals(log.getEventType()))
                .count();

        assertGreaterThanOrEqual(authLogs, 1);
    }

    // ============ System Statistics Tests ============

    @Test
    public void testAuthenticationStatistics() throws Exception {
        byte[] fingerprint = system.captureFingerprintFromSensor();
        system.registerUser("USR001", "alice", "alice@company.com", "IT", fingerprint);
        system.authenticate("USR001", fingerprint);

        java.util.Map<String, Object> stats = system.getAuthenticationStats();
        assertTrue(stats.containsKey("totalAttempts"));
        assertTrue(stats.containsKey("successCount"));
        assertTrue((Long) stats.get("successCount") >= 1);
    }

    // ============ Encryption Tests ============

    @Test
    public void testEncryptionAndDecryption() throws Exception {
        byte[] fingerprint = system.captureFingerprintFromSensor();
        system.registerUser("USR001", "alice", "alice@company.com", "IT", fingerprint);

        // Template should be encrypted
        User user = system.getUser("USR001");
        byte[] encryptedTemplate = user.getFingerprintTemplate();
        
        // Should be different from original (encrypted)
        assertNotEquals(fingerprint, encryptedTemplate);
    }

    // ============ Multi-user Tests ============

    @Test
    public void testMultipleUsersManagement() throws Exception {
        for (int i = 1; i <= 3; i++) {
            byte[] fingerprint = system.captureFingerprintFromSensor();
            system.registerUser("USR00" + i, "user" + i, "user" + i + "@company.com", "IT", fingerprint);
        }

        assertEquals(3, system.getTotalUsers());
    }

    // ============ Helper Methods ============

    private void assertGreaterThanOrEqual(int actual, int expected) {
        assertTrue("Expected " + actual + " >= " + expected, actual >= expected);
    }

    private void assertNotEquals(byte[] unexpected, byte[] actual) {
        assertFalse("Arrays should not be equal", 
                java.util.Arrays.equals(unexpected, actual));
    }
}
