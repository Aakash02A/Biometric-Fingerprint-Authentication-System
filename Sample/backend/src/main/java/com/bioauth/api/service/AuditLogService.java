package com.bioauth.api.service;

import com.bioauth.api.entity.AuditLogEntity;
import com.bioauth.api.repository.AuditLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for audit logging
 */
@Service
@Transactional
@Slf4j
public class AuditLogService {
    @Autowired
    private AuditLogRepository auditLogRepository;

    /**
     * Log an event
     */
    public void logEvent(String eventType, String userId, String username, String action,
                        String result, Integer matchScore, String details, String ipAddress) {
        try {
            AuditLogEntity log = AuditLogEntity.builder()
                    .eventType(eventType)
                    .userId(userId)
                    .username(username)
                    .action(action)
                    .result(result)
                    .matchScore(matchScore)
                    .details(details)
                    .ipAddress(ipAddress)
                    .eventTime(LocalDateTime.now())
                    .build();

            auditLogRepository.save(log);
        } catch (Exception e) {
            log.error("Error logging event: {}", e.getMessage());
        }
    }

    /**
     * Get logs by user
     */
    public List<AuditLogEntity> getLogsByUser(String userId) {
        return auditLogRepository.findByUserId(userId);
    }

    /**
     * Get logs by event type
     */
    public List<AuditLogEntity> getLogsByEventType(String eventType) {
        return auditLogRepository.findByEventType(eventType);
    }

    /**
     * Get recent logs
     */
    public List<AuditLogEntity> getRecentLogs(int count) {
        return auditLogRepository.findRecentLogs(count);
    }

    /**
     * Get total log count
     */
    public long getTotalLogs() {
        return auditLogRepository.count();
    }
}
