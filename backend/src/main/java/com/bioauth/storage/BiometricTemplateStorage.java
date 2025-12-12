package com.bioauth.storage;

import com.bioauth.exceptions.StorageException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of biometric template storage
 * Can be replaced with database or file-based implementation
 */
public class BiometricTemplateStorage implements IBiometricTemplateStorage {
    private final Map<String, byte[]> templateStore = new ConcurrentHashMap<>();

    @Override
    public void storeTemplate(String userId, byte[] encryptedTemplate) throws StorageException {
        if (userId == null || userId.isEmpty()) {
            throw new StorageException("User ID cannot be empty");
        }
        if (encryptedTemplate == null || encryptedTemplate.length == 0) {
            throw new StorageException("Template cannot be empty");
        }
        templateStore.put(userId, encryptedTemplate);
    }

    @Override
    public Optional<byte[]> retrieveTemplate(String userId) throws StorageException {
        if (userId == null || userId.isEmpty()) {
            throw new StorageException("User ID cannot be empty");
        }
        return Optional.ofNullable(templateStore.get(userId));
    }

    @Override
    public void deleteTemplate(String userId) throws StorageException {
        if (userId == null || userId.isEmpty()) {
            throw new StorageException("User ID cannot be empty");
        }
        if (!templateStore.containsKey(userId)) {
            throw new StorageException("Template for user " + userId + " not found");
        }
        templateStore.remove(userId);
    }

    @Override
    public boolean templateExists(String userId) throws StorageException {
        if (userId == null || userId.isEmpty()) {
            throw new StorageException("User ID cannot be empty");
        }
        return templateStore.containsKey(userId);
    }

    @Override
    public int getTemplateCount() throws StorageException {
        return templateStore.size();
    }

    @Override
    public void backup(String backupPath) throws StorageException {
        // Implementation would serialize templateStore to file
        // Placeholder for now
    }

    @Override
    public void restore(String backupPath) throws StorageException {
        // Implementation would deserialize templateStore from file
        // Placeholder for now
    }

    /**
     * Clear all templates (for testing)
     */
    public void clear() {
        templateStore.clear();
    }

    /**
     * Get all templates (for testing/admin)
     */
    public Map<String, byte[]> getAllTemplates() {
        return new ConcurrentHashMap<>(templateStore);
    }
}
