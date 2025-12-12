package com.bioauth.storage;

import com.bioauth.exceptions.EncryptionException;
import com.bioauth.config.SecurityConfig;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.SecureRandom;

/**
 * AES-based encryption service for biometric template protection
 */
public class AESEncryptionService implements EncryptionService {
    private SecretKey masterKey;
    private final SecureRandom secureRandom = new SecureRandom();

    public AESEncryptionService() throws EncryptionException {
        generateNewMasterKey();
    }

    @Override
    public byte[] encrypt(byte[] plaintext) throws EncryptionException {
        if (plaintext == null || plaintext.length == 0) {
            throw new EncryptionException("Plaintext cannot be null or empty");
        }

        try {
            Cipher cipher = Cipher.getInstance(SecurityConfig.TEMPLATE_ENCRYPTION);
            cipher.init(Cipher.ENCRYPT_MODE, masterKey);
            return cipher.doFinal(plaintext);
        } catch (Exception e) {
            throw new EncryptionException("Encryption failed: " + e.getMessage(), e);
        }
    }

    @Override
    public byte[] decrypt(byte[] ciphertext) throws EncryptionException {
        if (ciphertext == null || ciphertext.length == 0) {
            throw new EncryptionException("Ciphertext cannot be null or empty");
        }

        try {
            Cipher cipher = Cipher.getInstance(SecurityConfig.TEMPLATE_ENCRYPTION);
            cipher.init(Cipher.DECRYPT_MODE, masterKey);
            return cipher.doFinal(ciphertext);
        } catch (Exception e) {
            throw new EncryptionException("Decryption failed: " + e.getMessage(), e);
        }
    }

    @Override
    public void generateNewMasterKey() throws EncryptionException {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(SecurityConfig.KEY_SIZE, secureRandom);
            this.masterKey = keyGenerator.generateKey();
        } catch (Exception e) {
            throw new EncryptionException("Key generation failed: " + e.getMessage(), e);
        }
    }

    /**
     * Get the current master key (for backup/restore purposes)
     */
    public SecretKey getMasterKey() {
        return masterKey;
    }

    /**
     * Set the master key (for restore from backup)
     */
    public void setMasterKey(SecretKey key) {
        this.masterKey = key;
    }
}
