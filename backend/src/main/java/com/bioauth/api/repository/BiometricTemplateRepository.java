package com.bioauth.api.repository;

import com.bioauth.api.model.BiometricTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BiometricTemplateRepository extends JpaRepository<BiometricTemplate, Long> {
    Optional<BiometricTemplate> findByUserIdAndIsActive(Long userId, Boolean isActive);
    List<BiometricTemplate> findByUserId(Long userId);
    List<BiometricTemplate> findByUserIdAndIsActiveTrue(Long userId);
}
