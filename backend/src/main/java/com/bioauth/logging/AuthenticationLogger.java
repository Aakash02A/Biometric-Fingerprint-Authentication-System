package com.bioauth.logging;

import com.bioauth.exceptions.StorageException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Service for logging and retrieving authentication audit logs
 */
public class AuthenticationLogger {
    private final Map<String, AuditLog> logStore = new ConcurrentHashMap<>();
    private int logCounter = 0;

    /**
     * Log successful authentication
     */
    public void logSuccessfulAuthentication(String userId, int matchScore, long processingTime) {
        String logId = generateLogId();
        AuditLog log = new AuditLog(logId, "LOGIN_SUCCESS", userId);
        log.setMatchScore(matchScore);
        log.setResult("SUCCESS");
        log.setProcessingTime(processingTime);
        logStore.put(logId, log);
    }

    /**
     * Log failed authentication
     */
    public void logFailedAuthentication(String userId, String reason, int matchScore) {
        String logId = generateLogId();
        AuditLog log = new AuditLog(logId, "LOGIN_FAILURE", userId);
        log.setDetails(reason);
        log.setMatchScore(matchScore);
        log.setResult("FAILURE");
        logStore.put(logId, log);
    }

    /**
     * Log user registration
     */
    public void logRegistration(String userId, String username, String details) {
        String logId = generateLogId();
        AuditLog log = new AuditLog(logId, "REGISTRATION", userId);
        log.setUsername(username);
        log.setDetails(details);
        log.setResult("SUCCESS");
        logStore.put(logId, log);
    }

    /**
     * Log fingerprint capture
     */
    public void logCapture(String userId, int quality, String details) {
        String logId = generateLogId();
        AuditLog log = new AuditLog(logId, "CAPTURE", userId);
        log.setMatchScore(quality);
        log.setDetails(details);
        log.setResult("SUCCESS");
        logStore.put(logId, log);
    }

    /**
     * Log admin actions
     */
    public void logAdminAction(String details) {
        String logId = generateLogId();
        AuditLog log = new AuditLog(logId, "ADMIN_ACTION", "SYSTEM");
        log.setDetails(details);
        log.setResult("SUCCESS");
        logStore.put(logId, log);
    }

    /**
     * Get log by ID
     */
    public Optional<AuditLog> getLogById(String logId) {
        return Optional.ofNullable(logStore.get(logId));
    }

    /**
     * Get logs by user ID
     */
    public List<AuditLog> getLogsByUserId(String userId) {
        return logStore.values().stream()
                .filter(log -> log.getUserId().equals(userId))
                .sorted(Comparator.comparingLong(AuditLog::getTimestamp).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Get logs by event type
     */
    public List<AuditLog> getLogsByEventType(String eventType) {
        return logStore.values().stream()
                .filter(log -> log.getEventType().equals(eventType))
                .sorted(Comparator.comparingLong(AuditLog::getTimestamp).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Get logs by result (SUCCESS, FAILURE, ERROR)
     */
    public List<AuditLog> getLogsByResult(String result) {
        return logStore.values().stream()
                .filter(log -> log.getResult().equals(result))
                .sorted(Comparator.comparingLong(AuditLog::getTimestamp).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Get logs within time range
     */
    public List<AuditLog> getLogsBetween(long startTime, long endTime) {
        return logStore.values().stream()
                .filter(log -> log.getTimestamp() >= startTime && log.getTimestamp() <= endTime)
                .sorted(Comparator.comparingLong(AuditLog::getTimestamp).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Get recent logs
     */
    public List<AuditLog> getRecentLogs(int count) {
        return logStore.values().stream()
                .sorted(Comparator.comparingLong(AuditLog::getTimestamp).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    /**
     * Get all logs
     */
    public List<AuditLog> getAllLogs() {
        return new ArrayList<>(logStore.values());
    }

    /**
     * Get failure statistics
     */
    public Map<String, Integer> getFailureStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        logStore.values().stream()
                .filter(log -> "FAILURE".equals(log.getResult()))
                .forEach(log -> stats.merge(log.getUserId(), 1, Integer::sum));
        return stats;
    }

    /**
     * Get authentication statistics
     */
    public Map<String, Object> getAuthenticationStats() {
        Map<String, Object> stats = new HashMap<>();
        
        long totalAttempts = logStore.values().stream()
                .filter(log -> log.getEventType().matches("LOGIN_SUCCESS|LOGIN_FAILURE"))
                .count();
        
        long successCount = logStore.values().stream()
                .filter(log -> "LOGIN_SUCCESS".equals(log.getEventType()))
                .count();
        
        long failureCount = logStore.values().stream()
                .filter(log -> "LOGIN_FAILURE".equals(log.getEventType()))
                .count();

        stats.put("totalAttempts", totalAttempts);
        stats.put("successCount", successCount);
        stats.put("failureCount", failureCount);
        stats.put("successRate", totalAttempts > 0 ? (successCount * 100) / totalAttempts : 0);

        return stats;
    }

    /**
     * Clear logs (for testing)
     */
    public void clearLogs() {
        logStore.clear();
        logCounter = 0;
    }

    /**
     * Generate unique log ID
     */
    private synchronized String generateLogId() {
        return String.format("LOG%06d", ++logCounter);
    }

    /**
     * Get total log count
     */
    public int getLogCount() {
        return logStore.size();
    }
}
