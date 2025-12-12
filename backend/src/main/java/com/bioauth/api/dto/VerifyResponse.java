package com.bioauth.api.dto;

public class VerifyResponse {
    private Boolean matched;
    private Double confidenceScore;
    private String message;
    private String jwtToken;
    private Long userId;

    public VerifyResponse() {}

    public VerifyResponse(Boolean matched, Double confidenceScore, String message, String jwtToken, Long userId) {
        this.matched = matched;
        this.confidenceScore = confidenceScore;
        this.message = message;
        this.jwtToken = jwtToken;
        this.userId = userId;
    }

    public Boolean getMatched() {
        return matched;
    }

    public void setMatched(Boolean matched) {
        this.matched = matched;
    }

    public Double getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(Double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
