package com.bioauth.api.dto;

public class ScanResponse {
    private String scanId;
    private String encryptedScanData;
    private Integer qualityScore;
    private String message;
    private Boolean success;

    public ScanResponse() {}

    public ScanResponse(String scanId, String encryptedScanData, Integer qualityScore, Boolean success, String message) {
        this.scanId = scanId;
        this.encryptedScanData = encryptedScanData;
        this.qualityScore = qualityScore;
        this.success = success;
        this.message = message;
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

    public Integer getQualityScore() {
        return qualityScore;
    }

    public void setQualityScore(Integer qualityScore) {
        this.qualityScore = qualityScore;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }
}
