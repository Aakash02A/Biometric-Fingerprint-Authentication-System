package com.bioauth.api.service;

import com.bioauth.api.dto.AuthLogDTO;
import com.bioauth.api.model.AuthLog;
import com.bioauth.api.model.AuthLog.AuthOutcome;
import com.bioauth.api.repository.AuthLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthLogService {

    @Autowired
    private AuthLogRepository authLogRepository;

    public AuthLog logAttempt(Long userId, AuthOutcome outcome, Double confidenceScore, 
                             String ipAddress, String deviceInfo, String authenticationMethod) {
        AuthLog log = new AuthLog();
        log.setUserId(userId);
        log.setOutcome(outcome);
        log.setConfidenceScore(confidenceScore);
        log.setIpAddress(ipAddress);
        log.setDeviceInfo(deviceInfo);
        log.setAuthenticationMethod(authenticationMethod);
        log.setTimestamp(LocalDateTime.now());

        return authLogRepository.save(log);
    }

    public AuthLog logSuccessfulAuthentication(Long userId, Double confidenceScore, 
                                               String ipAddress, String deviceInfo) {
        return logAttempt(userId, AuthOutcome.SUCCESS, confidenceScore, ipAddress, deviceInfo, "BIOMETRIC");
    }

    public AuthLog logFailedAuthentication(Long userId, String ipAddress, String deviceInfo) {
        return logAttempt(userId, AuthOutcome.FAILED, 0.0, ipAddress, deviceInfo, "BIOMETRIC");
    }

    public AuthLog logLockedOut(Long userId, String ipAddress, String deviceInfo) {
        return logAttempt(userId, AuthOutcome.LOCKED_OUT, 0.0, ipAddress, deviceInfo, "BIOMETRIC");
    }

    public List<AuthLogDTO> getUserAuthLogs(Long userId) {
        return authLogRepository.findByUserIdOrderByTimestampDesc(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AuthLogDTO> getAuthLogsByOutcome(AuthOutcome outcome) {
        return authLogRepository.findByOutcome(outcome).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AuthLogDTO> getAuthLogsInDateRange(LocalDateTime start, LocalDateTime end) {
        return authLogRepository.findByTimestampBetween(start, end).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Long getFailedAttemptsCount(Long userId, LocalDateTime start, LocalDateTime end) {
        return authLogRepository.countFailedAttemptsInRange(userId, start, end);
    }

    public List<AuthLogDTO> getAllAuthLogs() {
        return authLogRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private AuthLogDTO convertToDTO(AuthLog log) {
        return new AuthLogDTO(
            log.getId(),
            log.getUserId(),
            log.getTimestamp(),
            log.getOutcome().toString(),
            log.getConfidenceScore(),
            log.getIpAddress(),
            log.getDeviceInfo(),
            log.getAuthenticationMethod()
        );
    }
}
