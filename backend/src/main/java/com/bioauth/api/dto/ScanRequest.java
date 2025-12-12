package com.bioauth.api.dto;

public class ScanRequest {
    private Long userId;
    private String scanData;
    private String deviceInfo;
    private String ipAddress;

    public ScanRequest() {}

    public ScanRequest(Long userId, String scanData, String deviceInfo, String ipAddress) {
        this.userId = userId;
        this.scanData = scanData;
        this.deviceInfo = deviceInfo;
        this.ipAddress = ipAddress;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getScanData() {
        return scanData;
    }

    public void setScanData(String scanData) {
        this.scanData = scanData;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
