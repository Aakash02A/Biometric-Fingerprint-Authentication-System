package com.bioauth.api.repository;

import com.bioauth.api.entity.AuditLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for Audit Log database operations
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLogEntity, Long> {
    List<AuditLogEntity> findByUserId(String userId);
    List<AuditLogEntity> findByEventType(String eventType);
    List<AuditLogEntity> findByResult(String result);
    List<AuditLogEntity> findByEventTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    @Query("SELECT a FROM AuditLogEntity a ORDER BY a.eventTime DESC LIMIT ?1")
    List<AuditLogEntity> findRecentLogs(int count);
}
