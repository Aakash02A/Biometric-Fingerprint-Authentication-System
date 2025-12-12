package com.bioauth.api.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Authentication response DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthenticationResponse {
    private boolean success;
    private String message;
    private String userId;
    private String username;
    private Integer matchScore;
    private Integer remainingAttempts;
    private Long remainingSeconds;
    private Long processingTimeMs;
    private String token;
}
