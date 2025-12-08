/* =============================================
   BIOMETRIC FINGERPRINT AUTHENTICATION SYSTEM
   JavaScript for UI Interactions
   ============================================= */

// ========================
// 1. GLOBAL UTILITIES
// ========================

const UI = {
  // Modal functions
  openModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
      modal.classList.add('show');
      document.body.style.overflow = 'hidden';
    }
  },

  closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
      modal.classList.remove('show');
      document.body.style.overflow = 'auto';
    }
  },

  // Navigate between pages
  navigate(page) {
    window.location.href = page;
  },

  // Show alert/notification
  showAlert(message, type = 'info', duration = 5000) {
    const alertHTML = `
      <div class="alert alert-${type}">
        <span>${message}</span>
        <button class="alert-close" onclick="this.parentElement.remove()">&times;</button>
      </div>
    `;
    const alertContainer = document.getElementById('alert-container') || document.body;
    alertContainer.insertAdjacentHTML('beforeend', alertHTML);

    if (duration > 0) {
      setTimeout(() => {
        const alert = alertContainer.querySelector('.alert');
        if (alert) alert.remove();
      }, duration);
    }
  },

  // Update active nav item
  setActiveNav(selector) {
    document.querySelectorAll('.nav-menu a').forEach(link => {
      link.classList.remove('active');
    });
    const activeLink = document.querySelector(selector);
    if (activeLink) {
      activeLink.classList.add('active');
    }
  },

  // Format date
  formatDate(date) {
    return new Date(date).toLocaleString('en-US', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit',
    });
  },

  // Generate random ID
  generateID() {
    return Math.random().toString(36).substr(2, 9).toUpperCase();
  },

  // Validate email
  isValidEmail(email) {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
  },
};

// ========================
// 2. LOGIN PAGE FUNCTIONS
// ========================

const LoginPage = {
  scannerState: 'idle', // idle, scanning, success, error
  scanProgress: 0,
  maxScanTime: 5000,

  init() {
    this.setupEventListeners();
  },

  setupEventListeners() {
    const startScanBtn = document.getElementById('start-scan-btn');
    const retryBtn = document.getElementById('retry-btn');
    const manualLoginBtn = document.getElementById('manual-login-btn');

    if (startScanBtn) startScanBtn.addEventListener('click', () => this.startScanning());
    if (retryBtn) retryBtn.addEventListener('click', () => this.resetScanner());
    if (manualLoginBtn) manualLoginBtn.addEventListener('click', () => this.showManualLogin());
  },

  startScanning() {
    this.scannerState = 'scanning';
    this.scanProgress = 0;
    this.updateUI();

    // Simulate scanning
    const scanInterval = setInterval(() => {
      this.scanProgress += Math.random() * 25;

      if (this.scanProgress >= 100) {
        this.scanProgress = 100;
        clearInterval(scanInterval);
        this.completeScanning();
      }

      this.updateUI();
    }, 400);

    // Timeout after maxScanTime
    setTimeout(() => {
      if (this.scannerState === 'scanning') {
        clearInterval(scanInterval);
        this.failScanning();
      }
    }, this.maxScanTime);
  },

  completeScanning() {
    // Randomly succeed or fail (80% success)
    if (Math.random() < 0.8) {
      this.scannerState = 'success';
      this.updateUI();
      UI.showAlert('Fingerprint matched! Redirecting...', 'success');

      // Redirect after 2 seconds
      setTimeout(() => {
        window.location.href = 'dashboard.html';
      }, 2000);
    } else {
      this.failScanning();
    }
  },

  failScanning() {
    this.scannerState = 'error';
    this.updateUI();
    UI.showAlert('Fingerprint not recognized. Please try again.', 'danger');

    setTimeout(() => {
      this.resetScanner();
    }, 2000);
  },

  resetScanner() {
    this.scannerState = 'idle';
    this.scanProgress = 0;
    this.updateUI();
  },

  updateUI() {
    const icon = document.getElementById('fingerprint-icon');
    const status = document.getElementById('scanner-status');
    const qualityBar = document.getElementById('quality-fill');
    const startBtn = document.getElementById('start-scan-btn');
    const retryBtn = document.getElementById('retry-btn');

    if (icon) {
      icon.className = `fingerprint-icon ${this.scannerState}`;
    }

    if (status) {
      const statusText = {
        idle: 'Place your finger on the sensor',
        scanning: 'Scanning... Please hold still',
        success: 'Fingerprint Matched!',
        error: 'Fingerprint Not Recognized',
      };
      status.className = `scanner-status ${this.scannerState}`;
      status.textContent = statusText[this.scannerState];
    }

    if (qualityBar) {
      qualityBar.style.width = `${this.scanProgress}%`;
    }

    if (startBtn) {
      startBtn.style.display = this.scannerState === 'idle' ? 'inline-flex' : 'none';
    }

    if (retryBtn) {
      retryBtn.style.display = this.scannerState === 'error' ? 'inline-flex' : 'none';
    }
  },

  showManualLogin() {
    UI.openModal('manual-login-modal');
  },
};

// ========================
// 3. REGISTRATION PAGE FUNCTIONS
// ========================

const RegistrationPage = {
  captureState: 'idle', // idle, capturing, success
  captureQuality: 0,
  capturedFingerprints: [],

  init() {
    this.setupEventListeners();
  },

  setupEventListeners() {
    const captureBtn = document.getElementById('capture-btn');
    const registerBtn = document.getElementById('register-btn');
    const cancelBtn = document.getElementById('cancel-btn');
    const userIdInput = document.getElementById('user-id');

    if (captureBtn) captureBtn.addEventListener('click', () => this.captureFingerprint());
    if (registerBtn) registerBtn.addEventListener('click', () => this.registerUser());
    if (cancelBtn) cancelBtn.addEventListener('click', () => this.cancel());

    if (userIdInput) {
      userIdInput.addEventListener('input', (e) => {
        this.validateInput(e.target);
      });
    }
  },

  captureFingerprint() {
    this.captureState = 'capturing';
    this.updateCaptureUI();

    // Simulate fingerprint capture
    let quality = 0;
    const captureInterval = setInterval(() => {
      quality += Math.random() * 30;

      if (quality >= 100) {
        quality = 100;
        clearInterval(captureInterval);
        this.captureQuality = quality;
        this.completCapture();
      }

      this.updateQualityBar(quality);
    }, 300);

    // Timeout after 6 seconds
    setTimeout(() => {
      if (this.captureState === 'capturing') {
        clearInterval(captureInterval);
        this.captureState = 'idle';
        this.updateCaptureUI();
        UI.showAlert('Capture timeout. Please try again.', 'warning');
      }
    }, 6000);
  },

  completCapture() {
    const quality = this.captureQuality;
    const qualityText = quality >= 85 ? 'Excellent' : quality >= 70 ? 'Good' : 'Poor';

    this.capturedFingerprints.push({
      id: this.capturedFingerprints.length + 1,
      quality: quality,
      timestamp: new Date(),
    });

    this.captureState = 'idle';
    this.updateCaptureUI();

    const message =
      quality >= 70
        ? `Fingerprint captured with ${qualityText} quality!`
        : 'Quality is low. Please try again.';

    UI.showAlert(message, quality >= 70 ? 'success' : 'warning');

    if (quality >= 70) {
      this.updateFingerprintList();
    }
  },

  updateQualityBar(quality) {
    const qualityBar = document.getElementById('quality-fill');
    if (qualityBar) {
      qualityBar.style.width = `${quality}%`;
    }

    const qualityText = document.getElementById('quality-text');
    if (qualityText) {
      const text = quality >= 85 ? 'Excellent' : quality >= 70 ? 'Good' : 'Poor';
      qualityText.textContent = `Quality: ${text} (${Math.round(quality)}%)`;
    }
  },

  updateCaptureUI() {
    const captureBtn = document.getElementById('capture-btn');
    if (captureBtn) {
      captureBtn.disabled = this.captureState === 'capturing';
      captureBtn.textContent =
        this.captureState === 'capturing' ? 'Capturing...' : 'Capture Fingerprint';
    }
  },

  updateFingerprintList() {
    const list = document.getElementById('fingerprint-list');
    if (list) {
      list.innerHTML = this.capturedFingerprints
        .map(
          (fp, idx) => `
        <div class="user-card">
          <div class="user-info">
            <div class="user-name">Fingerprint #${fp.id}</div>
            <div class="user-detail">Quality: ${fp.quality >= 85 ? 'Excellent' : fp.quality >= 70 ? 'Good' : 'Poor'} | ${UI.formatDate(fp.timestamp)}</div>
          </div>
          <button class="icon-btn danger" onclick="RegistrationPage.removeFingerprint(${idx})" title="Remove">🗑️</button>
        </div>
      `,
        )
        .join('');
    }
  },

  removeFingerprint(index) {
    this.capturedFingerprints.splice(index, 1);
    this.updateFingerprintList();
    UI.showAlert('Fingerprint removed.', 'info');
  },

  registerUser() {
    const userIdInput = document.getElementById('user-id');
    const userNameInput = document.getElementById('user-name');

    if (!userIdInput.value.trim()) {
      UI.showAlert('Please enter User ID.', 'warning');
      return;
    }

    if (!userNameInput.value.trim()) {
      UI.showAlert('Please enter User Name.', 'warning');
      return;
    }

    if (this.capturedFingerprints.length === 0) {
      UI.showAlert('Please capture at least one fingerprint.', 'warning');
      return;
    }

    // Show success modal
    UI.openModal('registration-success-modal');
    UI.showAlert('User registered successfully!', 'success');

    // Clear form after 3 seconds
    setTimeout(() => {
      UI.closeModal('registration-success-modal');
      userIdInput.value = '';
      userNameInput.value = '';
      this.capturedFingerprints = [];
      this.updateFingerprintList();
    }, 3000);
  },

  cancel() {
    if (confirm('Are you sure you want to cancel registration?')) {
      this.capturedFingerprints = [];
      document.getElementById('user-id').value = '';
      document.getElementById('user-name').value = '';
      this.updateFingerprintList();
      UI.navigate('dashboard.html');
    }
  },

  validateInput(input) {
    if (input.id === 'user-id') {
      input.value = input.value.toUpperCase();
    }
  },
};

// ========================
// 4. USER MANAGEMENT FUNCTIONS
// ========================

const UserManagementPage = {
  users: [
    { id: 'USR001', name: 'John Doe', email: 'john@example.com', registered: '2024-01-15', status: 'Active' },
    { id: 'USR002', name: 'Jane Smith', email: 'jane@example.com', registered: '2024-02-20', status: 'Active' },
    { id: 'USR003', name: 'Mike Johnson', email: 'mike@example.com', registered: '2024-03-10', status: 'Inactive' },
    { id: 'USR004', name: 'Sarah Williams', email: 'sarah@example.com', registered: '2024-04-05', status: 'Active' },
  ],

  init() {
    this.setupEventListeners();
    this.renderUsers();
  },

  setupEventListeners() {
    const searchInput = document.getElementById('search-users');
    const statusFilter = document.getElementById('status-filter');

    if (searchInput) {
      searchInput.addEventListener('input', (e) => this.filterUsers());
    }

    if (statusFilter) {
      statusFilter.addEventListener('change', () => this.filterUsers());
    }
  },

  renderUsers(usersToRender = this.users) {
    const usersList = document.getElementById('users-list');
    if (!usersList) return;

    if (usersToRender.length === 0) {
      usersList.innerHTML = '<div class="text-center p-30">No users found.</div>';
      return;
    }

    usersList.innerHTML = usersToRender
      .map(
        (user) => `
      <div class="user-card">
        <div class="user-info" onclick="UserManagementPage.viewUser('${user.id}')">
          <div class="user-name">👤 ${user.name}</div>
          <div class="user-detail">${user.email} | Registered: ${user.registered}</div>
          <div class="user-detail">
            <span class="status-badge status-${user.status === 'Active' ? 'success' : 'warning'}">${user.status}</span>
          </div>
        </div>
        <div class="user-actions">
          <button class="icon-btn" onclick="UserManagementPage.editUser('${user.id}')" title="Edit">✏️</button>
          <button class="icon-btn danger" onclick="UserManagementPage.deleteUser('${user.id}')" title="Delete">🗑️</button>
        </div>
      </div>
    `,
      )
      .join('');
  },

  filterUsers() {
    const searchTerm = (document.getElementById('search-users')?.value || '').toLowerCase();
    const statusFilter = document.getElementById('status-filter')?.value || 'all';

    const filtered = this.users.filter((user) => {
      const matchesSearch =
        user.name.toLowerCase().includes(searchTerm) ||
        user.email.toLowerCase().includes(searchTerm) ||
        user.id.toLowerCase().includes(searchTerm);

      const matchesStatus = statusFilter === 'all' || user.status === statusFilter;

      return matchesSearch && matchesStatus;
    });

    this.renderUsers(filtered);
  },

  viewUser(userId) {
    const user = this.users.find((u) => u.id === userId);
    if (!user) return;

    const modal = document.getElementById('user-detail-modal');
    if (modal) {
      modal.innerHTML = `
        <div class="modal-content">
          <div class="modal-header">
            <h2 class="modal-title">User Details</h2>
            <button class="modal-close" onclick="UI.closeModal('user-detail-modal')">&times;</button>
          </div>
          <div class="modal-body">
            <p><strong>User ID:</strong> ${user.id}</p>
            <p><strong>Name:</strong> ${user.name}</p>
            <p><strong>Email:</strong> ${user.email}</p>
            <p><strong>Registered:</strong> ${user.registered}</p>
            <p><strong>Status:</strong> <span class="status-badge status-${user.status === 'Active' ? 'success' : 'warning'}">${user.status}</span></p>
            <p><strong>Fingerprints:</strong> 2 captured</p>
          </div>
          <div class="modal-footer">
            <button class="btn btn-secondary" onclick="UI.closeModal('user-detail-modal')">Close</button>
          </div>
        </div>
      `;
    }

    UI.openModal('user-detail-modal');
  },

  editUser(userId) {
    const user = this.users.find((u) => u.id === userId);
    if (!user) return;

    const newName = prompt('Enter new name:', user.name);
    if (newName) {
      user.name = newName;
      this.renderUsers();
      UI.showAlert('User updated successfully!', 'success');
    }
  },

  deleteUser(userId) {
    if (confirm('Are you sure you want to delete this user?')) {
      this.users = this.users.filter((u) => u.id !== userId);
      this.renderUsers();
      UI.showAlert('User deleted successfully!', 'success');
    }
  },
};

// ========================
// 5. CAPTURE MODULE FUNCTIONS
// ========================

const CaptureModule = {
  captureQuality: 0,
  isCapturing: false,

  init() {
    this.setupEventListeners();
  },

  setupEventListeners() {
    const captureBtn = document.getElementById('capture-button');
    const clearBtn = document.getElementById('clear-button');

    if (captureBtn) captureBtn.addEventListener('click', () => this.startCapture());
    if (clearBtn) clearBtn.addEventListener('click', () => this.clearCapture());
  },

  startCapture() {
    if (this.isCapturing) return;

    this.isCapturing = true;
    this.captureQuality = 0;
    this.updateUI();

    let quality = 0;
    const interval = setInterval(() => {
      quality += Math.random() * 25;

      if (quality >= 100) {
        quality = 100;
        clearInterval(interval);
        this.completeCapture();
      }

      this.updateQuality(quality);
    }, 400);

    setTimeout(() => {
      if (this.isCapturing) {
        clearInterval(interval);
        this.isCapturing = false;
        this.updateUI();
        UI.showAlert('Capture timeout.', 'warning');
      }
    }, 6000);
  },

  updateQuality(quality) {
    const bar = document.getElementById('capture-quality-bar');
    if (bar) {
      bar.style.width = `${quality}%`;
    }

    const text = document.getElementById('capture-quality-text');
    if (text) {
      const status = quality >= 85 ? 'Excellent' : quality >= 70 ? 'Good' : 'Poor';
      text.textContent = `Quality: ${status} (${Math.round(quality)}%)`;
    }
  },

  completeCapture() {
    this.captureQuality = Math.round(Math.random() * 30 + 70);
    this.isCapturing = false;
    this.updateQuality(this.captureQuality);
    this.updateUI();
    UI.showAlert(`Capture successful! Quality: ${this.captureQuality}%`, 'success');
  },

  updateUI() {
    const btn = document.getElementById('capture-button');
    if (btn) {
      btn.disabled = this.isCapturing;
      btn.textContent = this.isCapturing ? 'Capturing...' : 'Start Capture';
    }
  },

  clearCapture() {
    this.captureQuality = 0;
    this.isCapturing = false;
    const bar = document.getElementById('capture-quality-bar');
    if (bar) bar.style.width = '0%';
    const text = document.getElementById('capture-quality-text');
    if (text) text.textContent = 'Quality: -';
    UI.showAlert('Capture cleared.', 'info');
  },
};

// ========================
// 6. LOGS/AUDIT FUNCTIONS
// ========================

const LogsPage = {
  logs: [
    {
      id: 1,
      timestamp: new Date(Date.now() - 3600000),
      userId: 'USR001',
      status: 'Success',
      message: 'Fingerprint matched',
    },
    {
      id: 2,
      timestamp: new Date(Date.now() - 7200000),
      userId: 'USR002',
      status: 'Success',
      message: 'Fingerprint matched',
    },
    {
      id: 3,
      timestamp: new Date(Date.now() - 10800000),
      userId: 'USR003',
      status: 'Failed',
      message: 'Fingerprint not recognized',
    },
    {
      id: 4,
      timestamp: new Date(Date.now() - 14400000),
      userId: 'USR001',
      status: 'Success',
      message: 'Fingerprint matched',
    },
    {
      id: 5,
      timestamp: new Date(Date.now() - 18000000),
      userId: 'USR004',
      status: 'Failed',
      message: 'Quality threshold not met',
    },
  ],

  init() {
    this.setupEventListeners();
    this.renderLogs();
  },

  setupEventListeners() {
    const dateFilter = document.getElementById('log-date-filter');
    const statusFilter = document.getElementById('log-status-filter');
    const userFilter = document.getElementById('log-user-filter');
    const downloadBtn = document.getElementById('download-logs-btn');

    if (dateFilter) dateFilter.addEventListener('change', () => this.filterLogs());
    if (statusFilter) statusFilter.addEventListener('change', () => this.filterLogs());
    if (userFilter) userFilter.addEventListener('change', () => this.filterLogs());
    if (downloadBtn) downloadBtn.addEventListener('click', () => this.downloadLogs());
  },

  renderLogs(logsToRender = this.logs) {
    const tbody = document.querySelector('#logs-table tbody');
    if (!tbody) return;

    if (logsToRender.length === 0) {
      tbody.innerHTML =
        '<tr><td colspan="5" class="text-center p-20">No logs found.</td></tr>';
      return;
    }

    tbody.innerHTML = logsToRender
      .map(
        (log) => `
      <tr>
        <td>${UI.formatDate(log.timestamp)}</td>
        <td>${log.userId}</td>
        <td>
          <span class="status-badge status-${log.status === 'Success' ? 'success' : 'danger'}">
            ${log.status}
          </span>
        </td>
        <td>${log.message}</td>
      </tr>
    `,
      )
      .join('');
  },

  filterLogs() {
    const dateValue = document.getElementById('log-date-filter')?.value || 'all';
    const statusValue = document.getElementById('log-status-filter')?.value || 'all';
    const userValue = document.getElementById('log-user-filter')?.value || 'all';

    const now = new Date();
    const filtered = this.logs.filter((log) => {
      // Date filter
      let matchesDate = true;
      if (dateValue !== 'all') {
        const logDate = new Date(log.timestamp);
        const today = new Date();
        today.setHours(0, 0, 0, 0);

        if (dateValue === 'today') {
          matchesDate =
            logDate.getDate() === today.getDate() &&
            logDate.getMonth() === today.getMonth() &&
            logDate.getFullYear() === today.getFullYear();
        } else if (dateValue === 'week') {
          const weekAgo = new Date(today);
          weekAgo.setDate(weekAgo.getDate() - 7);
          matchesDate = logDate >= weekAgo;
        } else if (dateValue === 'month') {
          const monthAgo = new Date(today);
          monthAgo.setMonth(monthAgo.getMonth() - 1);
          matchesDate = logDate >= monthAgo;
        }
      }

      const matchesStatus = statusValue === 'all' || log.status === statusValue;
      const matchesUser = userValue === 'all' || log.userId === userValue;

      return matchesDate && matchesStatus && matchesUser;
    });

    this.renderLogs(filtered);
  },

  downloadLogs() {
    const csv =
      'Timestamp,User ID,Status,Message\n' +
      this.logs
        .map((log) => `${UI.formatDate(log.timestamp)},${log.userId},${log.status},"${log.message}"`)
        .join('\n');

    const blob = new Blob([csv], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `logs_${new Date().toISOString().split('T')[0]}.csv`;
    a.click();
    window.URL.revokeObjectURL(url);

    UI.showAlert('Logs downloaded successfully!', 'success');
  },
};

// ========================
// 7. DASHBOARD FUNCTIONS
// ========================

const DashboardPage = {
  init() {
    this.setupEventListeners();
    this.updateGreeting();
  },

  setupEventListeners() {
    const cards = document.querySelectorAll('.dashboard-card');
    cards.forEach((card) => {
      card.addEventListener('click', (e) => {
        const action = card.dataset.action;
        if (action) this.handleCardClick(action);
      });
    });

    const logoutBtn = document.getElementById('logout-btn');
    if (logoutBtn) {
      logoutBtn.addEventListener('click', () => this.logout());
    }
  },

  updateGreeting() {
    const greeting = document.getElementById('greeting-message');
    if (greeting) {
      const hour = new Date().getHours();
      const greetingText =
        hour < 12 ? 'Good Morning' : hour < 18 ? 'Good Afternoon' : 'Good Evening';
      greeting.textContent = `${greetingText}, John Doe! 👋`;
    }
  },

  handleCardClick(action) {
    const actions = {
      register: 'register.html',
      manage: 'users.html',
      logs: 'logs.html',
      security: 'security.html',
      capture: 'capture.html',
    };

    if (actions[action]) {
      UI.navigate(actions[action]);
    }
  },

  logout() {
    if (confirm('Are you sure you want to logout?')) {
      window.location.href = 'index.html';
    }
  },
};

// ========================
// 8. FORM VALIDATION
// ========================

const FormValidator = {
  validateEmail(email) {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
  },

  validatePassword(password) {
    return password.length >= 8;
  },

  validateUsername(username) {
    return username.length >= 3 && /^[a-zA-Z0-9_]+$/.test(username);
  },

  showError(inputElement, message) {
    inputElement.classList.add('error');
    const errorDiv = document.createElement('div');
    errorDiv.className = 'form-error';
    errorDiv.textContent = message;
    inputElement.parentElement.appendChild(errorDiv);
  },

  clearError(inputElement) {
    inputElement.classList.remove('error');
    const errorDiv = inputElement.parentElement.querySelector('.form-error');
    if (errorDiv) errorDiv.remove();
  },
};

// ========================
// 9. MODAL HANDLERS
// ========================

function setupModalHandlers() {
  // Close modal on backdrop click
  document.querySelectorAll('.modal').forEach((modal) => {
    modal.addEventListener('click', (e) => {
      if (e.target === modal) {
        UI.closeModal(modal.id);
      }
    });
  });

  // Close modal on close button
  document.querySelectorAll('.modal-close').forEach((btn) => {
    btn.addEventListener('click', (e) => {
      const modal = e.target.closest('.modal');
      if (modal) UI.closeModal(modal.id);
    });
  });
}

// ========================
// 10. MANUAL LOGIN FORM
// ========================

function setupManualLoginForm() {
  const form = document.getElementById('manual-login-form');
  if (!form) return;

  form.addEventListener('submit', (e) => {
    e.preventDefault();

    const username = document.getElementById('login-username').value;
    const password = document.getElementById('login-password').value;

    if (!username || !password) {
      UI.showAlert('Please fill in all fields.', 'warning');
      return;
    }

    UI.showAlert('Logging in...', 'info');

    // Simulate login
    setTimeout(() => {
      if (username === 'admin' && password === 'password') {
        UI.showAlert('Login successful!', 'success');
        setTimeout(() => {
          window.location.href = 'dashboard.html';
        }, 1000);
      } else {
        UI.showAlert('Invalid username or password.', 'danger');
      }
    }, 1000);
  });
}

// ========================
// 11. REGISTRATION FORM
// ========================

function setupRegistrationForm() {
  const form = document.getElementById('registration-form');
  if (!form) return;

  form.addEventListener('submit', (e) => {
    e.preventDefault();

    const userId = document.getElementById('user-id').value;
    const userName = document.getElementById('user-name').value;
    const userEmail = document.getElementById('user-email').value;

    if (!userId || !userName || !userEmail) {
      UI.showAlert('Please fill in all fields.', 'warning');
      return;
    }

    if (!FormValidator.validateEmail(userEmail)) {
      UI.showAlert('Please enter a valid email.', 'warning');
      return;
    }

    UI.showAlert('Registration form submitted!', 'success');
  });
}

// ========================
// 12. PAGE INITIALIZATION
// ========================

document.addEventListener('DOMContentLoaded', () => {
  setupModalHandlers();
  setupManualLoginForm();
  setupRegistrationForm();

  // Initialize page-specific scripts
  const page = document.body.id;

  if (page === 'login-page') LoginPage.init();
  if (page === 'registration-page') RegistrationPage.init();
  if (page === 'user-management-page') UserManagementPage.init();
  if (page === 'capture-page') CaptureModule.init();
  if (page === 'logs-page') LogsPage.init();
  if (page === 'dashboard-page') DashboardPage.init();
});

// ========================
// 13. KEYBOARD SHORTCUTS
// ========================

document.addEventListener('keydown', (e) => {
  // Escape to close modal
  if (e.key === 'Escape') {
    document.querySelectorAll('.modal.show').forEach((modal) => {
      UI.closeModal(modal.id);
    });
  }
});

// ========================
// 14. UTILITY FUNCTIONS
// ========================

function toggleSidebar() {
  const sidebar = document.querySelector('.sidebar');
  if (sidebar) {
    sidebar.classList.toggle('collapsed');
  }
}

function scrollToTop() {
  window.scrollTo({ top: 0, behavior: 'smooth' });
}
