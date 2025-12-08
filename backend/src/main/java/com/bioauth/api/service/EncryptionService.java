package com.bioauth.api.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

/**
 * Encryption service for biometric template protection
 */
@Service
@Slf4j
public class EncryptionService {
    @Value("${bioauth.encryption.algorithm:AES}")
    private String algorithm;

    @Value("${bioauth.encryption.key-size:256}")
    private int keySize;

    @Value("${bioauth.encryption.enabled:true}")
    private boolean encryptionEnabled;

    private SecretKey masterKey;

    public EncryptionService() {
        initializeMasterKey();
    }

    /**
     * Initialize master encryption key
     */
    private void initializeMasterKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256, new SecureRandom());
            this.masterKey = keyGenerator.generateKey();
            log.info("Master key initialized with {} bits", keySize);
        } catch (Exception e) {
            log.error("Error initializing master key: {}", e.getMessage());
            throw new RuntimeException("Failed to initialize encryption", e);
        }
    }

    /**
     * Encrypt biometric template
     */
    public byte[] encrypt(byte[] plaintext) {
        if (!encryptionEnabled || plaintext == null || plaintext.length == 0) {
            return plaintext;
        }

        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, masterKey);
            return cipher.doFinal(plaintext);
        } catch (Exception e) {
            log.error("Encryption error: {}", e.getMessage());
            throw new RuntimeException("Encryption failed", e);
        }
    }

    /**
     * Decrypt biometric template
     */
    public byte[] decrypt(byte[] ciphertext) {
        if (!encryptionEnabled || ciphertext == null || ciphertext.length == 0) {
            return ciphertext;
        }

        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, masterKey);
            return cipher.doFinal(ciphertext);
        } catch (Exception e) {
            log.error("Decryption error: {}", e.getMessage());
            throw new RuntimeException("Decryption failed", e);
        }
    }
}
