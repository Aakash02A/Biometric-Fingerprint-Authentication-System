package com.bioauth.api.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * User entity mapped to SQLite database
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String userId;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column
    private String department;

    @Lob
    @Column(columnDefinition = "BLOB")
    private byte[] fingerprintTemplate;

    @Lob
    @Column(columnDefinition = "BLOB")
    private byte[] fingerprintImage;

    @Column
    private Integer qualityScore;

    @Column
    private Boolean isActive = true;

    @Column
    private Integer failedAuthAttempts = 0;

    @Column
    private LocalDateTime lastAuthAttemptTime;

    @Column
    private LocalDateTime registrationDate;

    @Column
    private LocalDateTime lastLoginDate;

    @Column
    private String registrationIp;

    @Column
    private String lastLoginIp;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        registrationDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
