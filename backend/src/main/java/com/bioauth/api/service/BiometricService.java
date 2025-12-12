package com.bioauth.api.service;

import com.bioauth.api.dto.ScanResponse;
import com.bioauth.api.dto.VerifyResponse;
import com.bioauth.api.model.BiometricTemplate;
import com.bioauth.api.model.User;
import com.bioauth.api.repository.BiometricTemplateRepository;
import com.bioauth.api.repository.UserRepository;
import com.bioauth.api.utils.AESEncryptionUtil;
import com.bioauth.api.utils.BiometricSensorAPI;
import com.bioauth.api.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class BiometricService {

    @Autowired
    private BiometricTemplateRepository biometricTemplateRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthLogService authLogService;

    public ScanResponse scanFingerprint(Long userId, String deviceInfo, String ipAddress) throws Exception {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            throw new IllegalArgumentException("User not found");
        }

        User user = userOpt.get();
        if (user.getStatus() == User.UserStatus.LOCKED || user.getStatus() == User.UserStatus.INACTIVE) {
            throw new IllegalStateException("User account is locked or inactive");
        }

        // Generate fake fingerprint scan
        String fakeScan = BiometricSensorAPI.generateFakeScan();
        Integer qualityScore = BiometricSensorAPI.generateQualityScore();

        // Encrypt the scan data
        String encryptedScan = AESEncryptionUtil.encrypt(fakeScan);

        // Generate scan ID
        String scanId = "SCAN-" + System.nanoTime();

        ScanResponse response = new ScanResponse();
        response.setScanId(scanId);
        response.setEncryptedScanData(encryptedScan);
        response.setQualityScore(qualityScore);
        response.setSuccess(BiometricSensorAPI.isHighQualityScan(qualityScore));
        response.setMessage(response.getSuccess() ? 
            "High quality scan captured" : "Low quality scan, please try again");

        return response;
    }

    public VerifyResponse verifyFingerprint(Long userId, String encryptedScanData) throws Exception {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            return createFailureResponse(null, "User not found");
        }

        User user = userOpt.get();

        if (user.getStatus() == User.UserStatus.LOCKED) {
            return createFailureResponse(userId, "User account is locked");
        }

        if (user.getStatus() == User.UserStatus.INACTIVE) {
            return createFailureResponse(userId, "User account is inactive");
        }

        if (user.getEncryptedBiometricTemplate() == null || user.getEncryptedBiometricTemplate().isEmpty()) {
            return createFailureResponse(userId, "No biometric template enrolled for this user");
        }

        // Decrypt stored template
        String storedTemplate = AESEncryptionUtil.decrypt(user.getEncryptedBiometricTemplate());
        
        // Decrypt scanned template
        String scannedTemplate = AESEncryptionUtil.decrypt(encryptedScanData);

        // Perform matching
        Double confidenceScore = BiometricSensorAPI.matchTemplates(storedTemplate, scannedTemplate);

        VerifyResponse response = new VerifyResponse();
        response.setUserId(userId);
        response.setConfidenceScore(confidenceScore);

        if (BiometricSensorAPI.isMatchConfident(confidenceScore)) {
            response.setMatched(true);
            response.setMessage("Fingerprint matched successfully");
            // Generate JWT token on successful authentication
            // Token generation would be handled by AuthController
        } else {
            response.setMatched(false);
            response.setMessage("Fingerprint did not match");
        }

        return response;
    }

    public void enrollBiometricTemplate(Long userId, String templateData) throws Exception {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            throw new IllegalArgumentException("User not found");
        }

        // Deactivate old templates
        biometricTemplateRepository.findByUserIdAndIsActiveTrue(userId)
                .forEach(template -> {
                    template.setIsActive(false);
                    biometricTemplateRepository.save(template);
                });

        // Encrypt and hash the template
        String encryptedTemplate = AESEncryptionUtil.encrypt(templateData);
        String templateHash = SecurityUtil.hashData(templateData);

        // Save encrypted template to user
        User user = userOpt.get();
        user.setEncryptedBiometricTemplate(encryptedTemplate);
        user.setBiometricTemplateUpdatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        // Create BiometricTemplate record
        BiometricTemplate template = new BiometricTemplate();
        template.setUserId(userId);
        template.setEncryptedTemplateData(encryptedTemplate);
        template.setTemplateHash(templateHash);
        template.setQualityScore(BiometricSensorAPI.generateQualityScore());
        template.setMetadata(BiometricSensorAPI.generateScanMetadata("ENROLLMENT", "LOCAL"));
        template.setIsActive(true);
        template.setCreatedAt(LocalDateTime.now());
        
        biometricTemplateRepository.save(template);
    }

    private VerifyResponse createFailureResponse(Long userId, String message) {
        VerifyResponse response = new VerifyResponse();
        response.setUserId(userId);
        response.setMatched(false);
        response.setMessage(message);
        response.setConfidenceScore(0.0);
        return response;
    }
}
