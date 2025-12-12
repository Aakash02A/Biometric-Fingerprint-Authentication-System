package com.bioauth.api.dto;

public class VerifyRequest {
    private Long userId;
    private String scanId;
    private String encryptedScanData;
    private String ipAddress;
    private String deviceInfo;

    public VerifyRequest() {}

    public VerifyRequest(Long userId, String scanId, String encryptedScanData, String ipAddress, String deviceInfo) {
        this.userId = userId;
        this.scanId = scanId;
        this.encryptedScanData = encryptedScanData;
        this.ipAddress = ipAddress;
        this.deviceInfo = deviceInfo;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getScanId() {
        return scanId;
    }

    public void setScanId(String scanId) {
        this.scanId = scanId;
    }

    public String getEncryptedScanData() {
        return encryptedScanData;
    }

    public void setEncryptedScanData(String encryptedScanData) {
        this.encryptedScanData = encryptedScanData;
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
}
