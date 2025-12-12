package com.bioauth.storage;

import com.bioauth.exceptions.EncryptionException;

/**
 * Interface for encryption/decryption operations
 */
public interface EncryptionService {
    byte[] encrypt(byte[] plaintext) throws EncryptionException;
    byte[] decrypt(byte[] ciphertext) throws EncryptionException;
    void generateNewMasterKey() throws EncryptionException;
}
