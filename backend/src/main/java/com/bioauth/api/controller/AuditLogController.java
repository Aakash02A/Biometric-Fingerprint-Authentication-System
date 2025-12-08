package com.bioauth.api.controller;

import com.bioauth.api.entity.AuditLogEntity;
import com.bioauth.api.repository.AuditLogRepository;
import com.bioauth.api.service.AuditLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for audit log endpoints
 */
@RestController
@RequestMapping("/v1/logs")
@Slf4j
@CrossOrigin(origins = "*")
public class AuditLogController {
    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private AuditLogRepository auditLogRepository;

    /**
     * Get recent logs
     * GET /api/v1/logs/recent?count=10
     */
    @GetMapping("/recent")
    public ResponseEntity<?> getRecentLogs(@RequestParam(defaultValue = "10") int count) {
        try {
            List<AuditLogEntity> logs = auditLogService.getRecentLogs(count);
            Map<String, Object> response = new HashMap<>();
            response.put("count", logs.size());
            response.put("logs", logs);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching logs: {}", e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * Get logs by user
     * GET /api/v1/logs/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getLogsByUser(@PathVariable String userId) {
        try {
            List<AuditLogEntity> logs = auditLogService.getLogsByUser(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("count", logs.size());
            response.put("logs", logs);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * Get logs by event type
     * GET /api/v1/logs/events/{eventType}
     */
    @GetMapping("/events/{eventType}")
    public ResponseEntity<?> getLogsByEventType(@PathVariable String eventType) {
        try {
            List<AuditLogEntity> logs = auditLogService.getLogsByEventType(eventType);
            Map<String, Object> response = new HashMap<>();
            response.put("eventType", eventType);
            response.put("count", logs.size());
            response.put("logs", logs);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * Get all logs
     * GET /api/v1/logs/all
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllLogs() {
        try {
            List<AuditLogEntity> logs = auditLogRepository.findAll();
            Map<String, Object> response = new HashMap<>();
            response.put("totalLogs", logs.size());
            response.put("logs", logs);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * Get log statistics
     * GET /api/v1/logs/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getLogStats() {
        try {
            long totalLogs = auditLogService.getTotalLogs();
            List<AuditLogEntity> successLogs = auditLogService.getLogsByEventType("LOGIN_SUCCESS");
            List<AuditLogEntity> failureLogs = auditLogService.getLogsByEventType("LOGIN_FAILURE");

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalLogs", totalLogs);
            stats.put("successfulLogins", successLogs.size());
            stats.put("failedLogins", failureLogs.size());
            stats.put("successRate", totalLogs > 0 ? (successLogs.size() * 100) / totalLogs : 0);

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
}
