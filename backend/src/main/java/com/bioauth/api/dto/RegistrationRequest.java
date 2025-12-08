package com.bioauth.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user registration request
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrationRequest {
    private String userId;
    private String username;
    private String email;
    private String department;
    private byte[] fingerprintImage;
    private int qualityScore;
}
