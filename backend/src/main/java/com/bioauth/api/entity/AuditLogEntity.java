package com.bioauth.api.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Audit log entity for tracking all authentication events
 */
@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String eventType;  // LOGIN_SUCCESS, LOGIN_FAILURE, REGISTRATION, etc.

    @Column
    private String userId;

    @Column
    private String username;

    @Column
    private String action;  // register, authenticate, update_profile, etc.

    @Column
    private String result;  // SUCCESS, FAILURE, ERROR

    @Column
    private Integer matchScore;

    @Column
    private String details;

    @Column
    private String ipAddress;

    @Column
    private String deviceInfo;

    @Column
    private Long processingTimeMs;

    @Column
    private LocalDateTime eventTime;

    @Column
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (eventTime == null) {
            eventTime = LocalDateTime.now();
        }
    }
}
