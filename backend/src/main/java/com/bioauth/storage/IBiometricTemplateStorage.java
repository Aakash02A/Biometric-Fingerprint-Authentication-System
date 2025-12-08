package com.bioauth.storage;

import com.bioauth.exceptions.StorageException;
import java.util.Map;
import java.util.Optional;

/**
 * Interface for biometric template storage operations
 */
public interface IBiometricTemplateStorage {
    void storeTemplate(String userId, byte[] encryptedTemplate) throws StorageException;
    Optional<byte[]> retrieveTemplate(String userId) throws StorageException;
    void deleteTemplate(String userId) throws StorageException;
    boolean templateExists(String userId) throws StorageException;
    int getTemplateCount() throws StorageException;
    void backup(String backupPath) throws StorageException;
    void restore(String backupPath) throws StorageException;
}
