package com.bioauth.api.controller;

import com.bioauth.api.dto.*;
import com.bioauth.api.model.ApiResponse;
import com.bioauth.api.model.AuthLog.AuthOutcome;
import com.bioauth.api.model.User;
import com.bioauth.api.security.JwtTokenProvider;
import com.bioauth.api.service.AuthLogService;
import com.bioauth.api.service.BiometricService;
import com.bioauth.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private BiometricService biometricService;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthLogService authLogService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/scan")
    public ResponseEntity<ApiResponse<ScanResponse>> scanFingerprint(
            @RequestBody ScanRequest request,
            HttpServletRequest httpRequest) {
        try {
            String ipAddress = getClientIpAddress(httpRequest);
            String deviceInfo = request.getDeviceInfo() != null ? request.getDeviceInfo() : "UNKNOWN";

            ScanResponse scanResponse = biometricService.scanFingerprint(
                    request.getUserId(),
                    deviceInfo,
                    ipAddress
            );

            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Fingerprint scan completed successfully",
                    scanResponse
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null, "BAD_REQUEST"));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(false, e.getMessage(), null, "FORBIDDEN"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Scan failed: " + e.getMessage(), null, "SCAN_ERROR"));
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<VerifyResponse>> verifyFingerprint(
            @RequestBody VerifyRequest request,
            HttpServletRequest httpRequest) {
        try {
            String ipAddress = getClientIpAddress(httpRequest);
            String deviceInfo = request.getDeviceInfo() != null ? request.getDeviceInfo() : "UNKNOWN";

            // Get user
            Optional<User> userOpt = userService.getUserById(request.getUserId());
            if (!userOpt.isPresent()) {
                authLogService.logFailedAuthentication(request.getUserId(), ipAddress, deviceInfo);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "User not found", null, "USER_NOT_FOUND"));
            }

            User user = userOpt.get();

            // Verify fingerprint
            VerifyResponse verifyResponse = biometricService.verifyFingerprint(
                    request.getUserId(),
                    request.getEncryptedScanData()
            );

            if (verifyResponse.getMatched()) {
                // Log successful authentication
                authLogService.logSuccessfulAuthentication(
                        request.getUserId(),
                        verifyResponse.getConfidenceScore(),
                        ipAddress,
                        deviceInfo
                );

                // Generate JWT token
                String jwtToken = jwtTokenProvider.generateToken(
                        user.getId(),
                        user.getUsername(),
                        user.getRole().toString()
                );
                verifyResponse.setJwtToken(jwtToken);

                return ResponseEntity.ok(new ApiResponse<>(
                        true,
                        "Fingerprint verified successfully",
                        verifyResponse
                ));
            } else {
                // Log failed authentication
                authLogService.logFailedAuthentication(request.getUserId(), ipAddress, deviceInfo);

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(
                                false,
                                "Fingerprint verification failed",
                                verifyResponse,
                                "VERIFICATION_FAILED"
                        ));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null, "BAD_REQUEST"));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(false, e.getMessage(), null, "FORBIDDEN"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Verification failed: " + e.getMessage(), null, "VERIFICATION_ERROR"));
        }
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
