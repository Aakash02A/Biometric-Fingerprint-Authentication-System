package com.bioauth.logging;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Model for audit log entries
 */
public class AuditLog implements Serializable {
    private static final long serialVersionUID = 1L;

    private String logId;
    private long timestamp;
    private String eventType;      // LOGIN_SUCCESS, LOGIN_FAILURE, REGISTRATION, CAPTURE, ADMIN_ACTION
    private String userId;
    private String username;
    private int matchScore;        // For authentication events
    private String details;
    private String ipAddress;
    private String result;         // SUCCESS, FAILURE, ERROR
    private long processingTime;   // In milliseconds

    public AuditLog(String logId, String eventType, String userId) {
        this.logId = logId;
        this.timestamp = System.currentTimeMillis();
        this.eventType = eventType;
        this.userId = userId;
        this.matchScore = 0;
        this.processingTime = 0;
    }

    // Getters and Setters
    public String getLogId() {
        return logId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getEventType() {
        return eventType;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(int matchScore) {
        this.matchScore = matchScore;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public long getProcessingTime() {
        return processingTime;
    }

    public void setProcessingTime(long processingTime) {
        this.processingTime = processingTime;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return String.format("[%s] %s - Event: %s | User: %s | Result: %s | Score: %d | Time: %dms",
                sdf.format(new Date(timestamp)), logId, eventType, userId, result, matchScore, processingTime);
    }
}
