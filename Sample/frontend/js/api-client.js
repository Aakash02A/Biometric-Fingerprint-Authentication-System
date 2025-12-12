/**
 * API Client for Biometric Authentication REST API
 * Handles all communication with Spring Boot backend
 */

const API = {
  BASE_URL: 'http://localhost:8080/v1',
  TOKEN_KEY: 'bioauth_token',
  USER_KEY: 'bioauth_user',

  /**
   * Set authorization token
   */
  setToken(token) {
    if (token) {
      localStorage.setItem(this.TOKEN_KEY, token);
    }
  },

  /**
   * Get authorization token
   */
  getToken() {
    return localStorage.getItem(this.TOKEN_KEY);
  },

  /**
   * Clear stored token and user
   */
  logout() {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
    window.location.href = '/frontend/index.html';
  },

  /**
   * Get default headers with authorization
   */
  getHeaders() {
    const headers = {
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    };
    const token = this.getToken();
    if (token) {
      headers['Authorization'] = 'Bearer ' + token;
    }
    return headers;
  },

  /**
   * Make API request
   */
  async request(method, endpoint, data = null) {
    try {
      const url = this.BASE_URL + endpoint;
      const config = {
        method: method,
        headers: this.getHeaders()
      };

      if (data && (method === 'POST' || method === 'PUT')) {
        config.body = JSON.stringify(data);
      }

      const response = await fetch(url, config);
      
      if (!response.ok) {
        const error = await response.json();
        throw new Error(error.error || error.message || `HTTP ${response.status}`);
      }

      return await response.json();

    } catch (error) {
      console.error('API Error:', error);
      throw error;
    }
  },

  /**
   * USERS API METHODS
   */

  /**
   * Register new user
   */
  async registerUser(userData) {
    const payload = {
      userId: userData.userId,
      username: userData.username,
      email: userData.email,
      department: userData.department,
      fingerprintImage: userData.fingerprintImage, // Base64 or byte[]
      qualityScore: userData.qualityScore || 85
    };
    return this.request('POST', '/users/register', payload);
  },

  /**
   * Get user by ID
   */
  async getUser(userId) {
    return this.request('GET', `/users/${userId}`);
  },

  /**
   * Get all users
   */
  async getAllUsers() {
    return this.request('GET', '/users');
  },

  /**
   * Lock user account
   */
  async lockUser(userId) {
    return this.request('POST', `/users/${userId}/lock`);
  },

  /**
   * Unlock user account
   */
  async unlockUser(userId) {
    return this.request('POST', `/users/${userId}/unlock`);
  },

  /**
   * Delete user
   */
  async deleteUser(userId) {
    return this.request('DELETE', `/users/${userId}`);
  },

  /**
   * Get user statistics
   */
  async getUserStats() {
    return this.request('GET', '/users/stats/overview');
  },

  /**
   * AUTHENTICATION API METHODS
   */

  /**
   * Authenticate user with fingerprint
   */
  async authenticate(userId, fingerprintData) {
    const payload = {
      userId: userId,
      fingerprintData: fingerprintData // Base64 or byte[]
    };
    return this.request('POST', '/auth/authenticate', payload);
  },

  /**
   * Verify authentication status
   */
  async verify() {
    return this.request('GET', '/auth/verify');
  },

  /**
   * AUDIT LOGS API METHODS
   */

  /**
   * Get recent logs
   */
  async getRecentLogs(count = 10) {
    return this.request('GET', `/logs/recent?count=${count}`);
  },

  /**
   * Get logs by user
   */
  async getLogsByUser(userId) {
    return this.request('GET', `/logs/user/${userId}`);
  },

  /**
   * Get logs by event type
   */
  async getLogsByEventType(eventType) {
    return this.request('GET', `/logs/events/${eventType}`);
  },

  /**
   * Get all logs
   */
  async getAllLogs() {
    return this.request('GET', '/logs/all');
  },

  /**
   * Get log statistics
   */
  async getLogStats() {
    return this.request('GET', '/logs/stats');
  },

  /**
   * Convert File to Base64
   */
  fileToBase64(file) {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = () => {
        const base64 = reader.result.split(',')[1]; // Remove data:image/png;base64, prefix
        resolve(base64);
      };
      reader.onerror = reject;
      reader.readAsDataURL(file);
    });
  },

  /**
   * Convert byte array to Base64
   */
  bytesToBase64(bytes) {
    let binary = '';
    for (let i = 0; i < bytes.length; i++) {
      binary += String.fromCharCode(bytes[i]);
    }
    return btoa(binary);
  },

  /**
   * Convert Base64 to byte array
   */
  base64ToBytes(base64) {
    const binary = atob(base64);
    const bytes = new Uint8Array(binary.length);
    for (let i = 0; i < binary.length; i++) {
      bytes[i] = binary.charCodeAt(i);
    }
    return bytes;
  }
};

/**
 * Export for use in modules
 */
if (typeof module !== 'undefined' && module.exports) {
  module.exports = API;
}
