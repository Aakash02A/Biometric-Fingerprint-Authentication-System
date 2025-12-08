package com.bioauth.api.controller;

import com.bioauth.api.dto.AuthenticationRequest;
import com.bioauth.api.service.AuthenticationService;
import com.bioauth.api.service.AuthenticationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for authentication endpoints
 */
@RestController
@RequestMapping("/v1/auth")
@Slf4j
@CrossOrigin(origins = "*")
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;

    /**
     * Authenticate user with fingerprint
     * POST /api/v1/auth/authenticate
     */
    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest request, HttpServletRequest httpRequest) {
        try {
            String ipAddress = getClientIpAddress(httpRequest);
            AuthenticationResponse result = authenticationService.authenticate(
                    request.getUserId(),
                    request.getFingerprintData(),
                    ipAddress
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", result.isSuccess());
            response.put("message", result.getMessage());
            response.put("userId", result.getUserId());
            response.put("username", result.getUsername());
            response.put("matchScore", result.getMatchScore());
            response.put("remainingAttempts", result.getRemainingAttempts());
            response.put("processingTimeMs", result.getProcessingTimeMs());

            if (result.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(401).body(response);
            }

        } catch (Exception e) {
            log.error("Authentication error: {}", e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * Verify authentication status
     * GET /api/v1/auth/verify
     */
    @GetMapping("/verify")
    public ResponseEntity<?> verify(@RequestHeader(value = "Authorization", required = false) String token) {
        Map<String, Object> response = new HashMap<>();
        response.put("authenticated", token != null && !token.isEmpty());
        return ResponseEntity.ok(response);
    }

    /**
     * Get client IP address
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor == null || xForwardedFor.isEmpty()) {
            return request.getRemoteAddr();
        }
        return xForwardedFor.split(",")[0].trim();
    }
}
