package com.bioauth.user;

import java.io.Serializable;
import java.util.Base64;

/**
 * User model class representing a registered user
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userId;
    private String username;
    private String email;
    private String department;
    private byte[] fingerprintTemplate;  // Encrypted biometric template
    private long registrationDate;
    private boolean isActive;
    private int failedAuthAttempts;
    private long lastAuthAttemptTime;
    private int qualityScore;

    /**
     * Constructor for creating a new user
     */
    public User(String userId, String username, String email) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.registrationDate = System.currentTimeMillis();
        this.isActive = true;
        this.failedAuthAttempts = 0;
        this.qualityScore = 0;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public byte[] getFingerprintTemplate() {
        return fingerprintTemplate;
    }

    public void setFingerprintTemplate(byte[] fingerprintTemplate) {
        this.fingerprintTemplate = fingerprintTemplate;
    }

    public long getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(long registrationDate) {
        this.registrationDate = registrationDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getFailedAuthAttempts() {
        return failedAuthAttempts;
    }

    public void setFailedAuthAttempts(int failedAuthAttempts) {
        this.failedAuthAttempts = failedAuthAttempts;
    }

    public long getLastAuthAttemptTime() {
        return lastAuthAttemptTime;
    }

    public void setLastAuthAttemptTime(long lastAuthAttemptTime) {
        this.lastAuthAttemptTime = lastAuthAttemptTime;
    }

    public int getQualityScore() {
        return qualityScore;
    }

    public void setQualityScore(int qualityScore) {
        this.qualityScore = qualityScore;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", department='" + department + '\'' +
                ", registrationDate=" + registrationDate +
                ", isActive=" + isActive +
                ", failedAuthAttempts=" + failedAuthAttempts +
                ", qualityScore=" + qualityScore +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;
        return userId.equals(user.userId);
    }

    @Override
    public int hashCode() {
        return userId.hashCode();
    }
}
