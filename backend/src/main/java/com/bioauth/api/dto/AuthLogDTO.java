package com.bioauth.api.dto;

import java.time.LocalDateTime;

public class AuthLogDTO {
    private Long id;
    private Long userId;
    private LocalDateTime timestamp;
    private String outcome;
    private Double confidenceScore;
    private String ipAddress;
    private String deviceInfo;
    private String authenticationMethod;

    public AuthLogDTO() {}

    public AuthLogDTO(Long id, Long userId, LocalDateTime timestamp, String outcome, 
                     Double confidenceScore, String ipAddress, String deviceInfo, String authenticationMethod) {
        this.id = id;
        this.userId = userId;
        this.timestamp = timestamp;
        this.outcome = outcome;
        this.confidenceScore = confidenceScore;
        this.ipAddress = ipAddress;
        this.deviceInfo = deviceInfo;
        this.authenticationMethod = authenticationMethod;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public Double getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(Double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public String getAuthenticationMethod() {
        return authenticationMethod;
    }

    public void setAuthenticationMethod(String authenticationMethod) {
        this.authenticationMethod = authenticationMethod;
    }
}
