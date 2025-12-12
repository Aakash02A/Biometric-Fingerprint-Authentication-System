package com.bioauth.api.repository;

import com.bioauth.api.model.AuthLog;
import com.bioauth.api.model.AuthLog.AuthOutcome;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuthLogRepository extends JpaRepository<AuthLog, Long> {
    List<AuthLog> findByUserId(Long userId);
    List<AuthLog> findByUserIdOrderByTimestampDesc(Long userId);
    List<AuthLog> findByOutcome(AuthOutcome outcome);
    List<AuthLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT al FROM AuthLog al WHERE al.userId = :userId AND al.timestamp BETWEEN :start AND :end")
    List<AuthLog> findUserLogsInDateRange(@Param("userId") Long userId, 
                                          @Param("start") LocalDateTime start, 
                                          @Param("end") LocalDateTime end);
    
    @Query("SELECT COUNT(al) FROM AuthLog al WHERE al.userId = :userId AND al.outcome = 'FAILED' AND al.timestamp BETWEEN :start AND :end")
    Long countFailedAttemptsInRange(@Param("userId") Long userId, 
                                    @Param("start") LocalDateTime start, 
                                    @Param("end") LocalDateTime end);
}
