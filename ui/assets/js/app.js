// ============= GLOBAL APP STATE =============

const AppState = {
  currentUser: {
    id: 'USR001',
    name: 'John Doe',
    email: 'john.doe@bioauth.com',
    avatar: 'JD'
  },
  scanAttempts: 0,
  maxAttempts: 5,
  isLocked: false,
  isAuthenticated: false,
  loginType: 'user',
  mockUsers: [
    { 
      id: 1, 
      name: 'Alice Johnson', 
      email: 'alice.johnson@company.com', 
      status: 'active', 
      enrolled: '2024-01-15', 
      lastLogin: '2024-12-12 14:30:00',
      username: 'alice', 
      password: 'alice123', 
      role: 'user',
      department: 'Engineering',
      phone: '+1-555-0101'
    },
    { 
      id: 2, 
      name: 'Bob Smith', 
      email: 'bob.smith@company.com', 
      status: 'active', 
      enrolled: '2024-02-20', 
      lastLogin: '2024-12-12 13:15:00',
      username: 'bob', 
      password: 'bob123', 
      role: 'user',
      department: 'Product',
      phone: '+1-555-0102'
    },
    { 
      id: 3, 
      name: 'Carol White', 
      email: 'carol.white@company.com', 
      status: 'inactive', 
      enrolled: '2024-03-10', 
      lastLogin: '2024-12-01 09:45:00',
      username: 'carol', 
      password: 'carol123', 
      role: 'user',
      department: 'Marketing',
      phone: '+1-555-0103'
    },
    { 
      id: 4, 
      name: 'David Brown', 
      email: 'david.brown@company.com', 
      status: 'active', 
      enrolled: '2024-04-05', 
      lastLogin: '2024-12-12 12:00:00',
      username: 'david', 
      password: 'david123', 
      role: 'user',
      department: 'Sales',
      phone: '+1-555-0104'
    },
    { 
      id: 5, 
      name: 'Eve Davis', 
      email: 'eve.davis@company.com', 
      status: 'active', 
      enrolled: '2024-05-12', 
      lastLogin: '2024-12-11 16:20:00',
      username: 'eve', 
      password: 'eve123', 
      role: 'user',
      department: 'Finance',
      phone: '+1-555-0105'
    },
    { 
      id: 100, 
      name: 'Admin User', 
      email: 'admin@bioauth.com', 
      status: 'active', 
      enrolled: '2024-01-01', 
      lastLogin: '2024-12-12 15:45:00',
      username: 'admin', 
      password: 'admin123', 
      role: 'admin',
      department: 'Administration',
      phone: '+1-555-0100'
    }
  ],
  mockLogs: [
    { id: 'LOG001', user: 'Alice Johnson', timestamp: '2024-12-12 14:30:15', result: 'success', device: 'Windows Desktop', location: 'New York Office' },
    { id: 'LOG002', user: 'Bob Smith', timestamp: '2024-12-12 13:15:42', result: 'success', device: 'MacBook Pro', location: 'Remote' },
    { id: 'LOG003', user: 'Carol White', timestamp: '2024-12-11 13:45:20', result: 'failed', device: 'Mobile', location: 'San Francisco' },
    { id: 'LOG004', user: 'David Brown', timestamp: '2024-12-12 12:00:08', result: 'success', device: 'Windows Laptop', location: 'Boston Office' },
    { id: 'LOG005', user: 'Eve Davis', timestamp: '2024-12-11 16:20:33', result: 'success', device: 'iPad', location: 'Remote' },
    { id: 'LOG006', user: 'Alice Johnson', timestamp: '2024-12-11 12:15:45', result: 'failed', device: 'Mobile', location: 'New York Office' },
    { id: 'LOG007', user: 'Bob Smith', timestamp: '2024-12-12 11:30:20', result: 'success', device: 'Windows Desktop', location: 'Chicago Office' },
    { id: 'LOG008', user: 'David Brown', timestamp: '2024-12-12 10:45:12', result: 'success', device: 'MacBook Pro', location: 'Remote' }
  ]
};

// ============= LOGIN PAGE FUNCTIONS =============

function initLoginPage() {
  checkExistingSession();
}

function checkExistingSession() {
  const sessionToken = sessionStorage.getItem('bioauth_token');
  const sessionUser = sessionStorage.getItem('bioauth_user');
  
  if (sessionToken && sessionUser) {
    const user = JSON.parse(sessionUser);
    redirectToDashboard(user.role);
  }
}

function redirectToDashboard(role) {
  if (role === 'admin') {
    window.location.href = '/views/adminDashboard.html';
  } else {
    window.location.href = '/views/userDashboard.html';
  }
}

function simulateFingerprintScan() {
  const scanner = document.getElementById('fingerprintScanner');
  const icon = scanner.querySelector('.fingerprint-icon');

  scanner.style.opacity = '0.5';
  scanner.style.pointerEvents = 'none';
  icon.style.animation = 'none';
  icon.textContent = 'Scanning...';

  setTimeout(() => {
    // 70% success rate
    if (Math.random() < 0.7) {
      handleScanSuccess();
    } else {
      handleScanFailure();
    }
  }, 2000);
}

function handleScanSuccess() {
  // Set authentication
  AppState.isAuthenticated = true;
  const token = 'jwt_token_' + Date.now();
  
  sessionStorage.setItem('bioauth_token', token);
  sessionStorage.setItem('bioauth_user', JSON.stringify(AppState.currentUser));

  showStatus('✓ Authentication successful! Redirecting...', 'success');

  setTimeout(() => {
    window.location.href = 'views/userDashboard.html';
  }, 1000);
}

function handleScanFailure() {
  const scanner = document.getElementById('fingerprintScanner');
  const icon = scanner.querySelector('.fingerprint-icon');

  scanner.style.opacity = '1';
  scanner.style.pointerEvents = 'auto';
  icon.textContent = '❌';
  icon.style.animation = 'shake 0.5s ease-in-out';

  showStatus('✗ Fingerprint verification failed. Please try again.', 'error');

  setTimeout(() => {
    icon.textContent = '👆';
    icon.style.animation = 'pulse 1.5s ease-in-out infinite';
  }, 1500);
}

function showStatus(message, type) {
  const statusMessage = document.querySelector('.status-message');

  if (statusMessage) {
    statusMessage.textContent = message;
    statusMessage.className = `status-message show ${type}`;
  }
}

// ============= USER DASHBOARD FUNCTIONS =============

function initUserDashboard() {
  loadUserSession();
  populateProfileSummary();
  populateAuthenticationHistory();
  populateDeviceInfo();
  attachDashboardListeners();
}

function loadUserSession() {
  const sessionUser = sessionStorage.getItem('bioauth_user');
  if (sessionUser) {
    const user = JSON.parse(sessionUser);
    AppState.currentUser = user;
    
    // Update UI with logged-in user
    const userAvatar = document.getElementById('userAvatar');
    const userName = document.getElementById('userName');
    const userEmail = document.getElementById('userEmail');
    
    if (userAvatar) userAvatar.textContent = user.avatar || user.name.substring(0, 2).toUpperCase();
    if (userName) userName.textContent = user.name;
    if (userEmail) userEmail.textContent = user.email;
  } else {
    // Redirect to login if not authenticated
    window.location.href = 'views/login.html';
  }
}

function populateProfileSummary() {
  const profileContainer = document.querySelector('.profile-summary');
  if (!profileContainer) return;

  const user = AppState.currentUser;
  const profileHTML = `
    <div class="profile-pic">${user.avatar || 'JD'}</div>
    <div class="profile-details">
      <div class="value">${user.name}</div>
      <div class="label">Full Name</div>
      <div class="value">${user.email}</div>
      <div class="label">Email Address</div>
      <div class="value">${user.id}</div>
      <div class="label">User ID</div>
    </div>
  `;
  profileContainer.innerHTML = profileHTML;
}

function populateAuthenticationHistory() {
  const historyContainer = document.getElementById('authHistory');
  if (!historyContainer) return;

  const mockHistory = [
    { timestamp: '2024-12-11 14:32:15', status: 'success', device: 'Windows Desktop' },
    { timestamp: '2024-12-11 09:15:42', status: 'success', device: 'Laptop' },
    { timestamp: '2024-12-10 16:45:20', status: 'failed', device: 'Mobile' },
    { timestamp: '2024-12-10 11:22:08', status: 'success', device: 'Windows Desktop' },
    { timestamp: '2024-12-09 15:58:33', status: 'success', device: 'Laptop' }
  ];

  let html = '';
  mockHistory.forEach(item => {
    const statusClass = item.status === 'success' ? 'success' : 'failed';
    const statusText = item.status === 'success' ? 'Success' : 'Failed';
    html += `
      <div class="history-item">
        <div>
          <div class="history-time">${item.timestamp}</div>
          <div class="history-time">${item.device}</div>
        </div>
        <span class="history-status ${statusClass}">${statusText}</span>
      </div>
    `;
  });

  historyContainer.innerHTML = html;
}

function populateDeviceInfo() {
  const deviceContainer = document.getElementById('deviceInfo');
  if (!deviceContainer) return;

  const mockDeviceInfo = [
    { label: 'Device Name', value: 'DESKTOP-ABC123' },
    { label: 'OS', value: 'Windows 11 Pro' },
    { label: 'Browser', value: 'Chrome 120.0' },
    { label: 'IP Address', value: '192.168.1.105' },
    { label: 'Last Enrolled', value: '2024-11-15' }
  ];

  let html = '<ul class="device-info-list">';
  mockDeviceInfo.forEach(item => {
    html += `
      <li>
        <span class="device-label">${item.label}</span>
        <span class="device-value">${item.value}</span>
      </li>
    `;
  });
  html += '</ul>';

  deviceContainer.innerHTML = html;
}

function attachDashboardListeners() {
  const reenrollBtn = document.getElementById('reenrollBtn');
  if (reenrollBtn) {
    reenrollBtn.addEventListener('click', openReenrollModal);
  }

  const logoutBtn = document.getElementById('logoutBtn');
  if (logoutBtn) {
    logoutBtn.addEventListener('click', logoutUser);
  }

  attachModalListeners();
}

function logoutUser() {
  sessionStorage.removeItem('bioauth_token');
  sessionStorage.removeItem('bioauth_user');
  AppState.isAuthenticated = false;
  window.location.href = 'views/login.html';
}

// ============= ADMIN DASHBOARD FUNCTIONS =============

function initAdminDashboard() {
  checkAdminSession();
  populateUserManagementTable();
  populateAuthenticationLogsTable();
  populateAlertsPanel();
  attachAdminListeners();
  updateQuickStats();
}

function checkAdminSession() {
  const sessionUser = sessionStorage.getItem('bioauth_user');
  if (!sessionUser) {
    window.location.href = 'views/login.html';
    return;
  }

  const user = JSON.parse(sessionUser);
  if (user.role !== 'admin') {
    sessionStorage.removeItem('bioauth_token');
    sessionStorage.removeItem('bioauth_user');
    window.location.href = 'views/login.html';
    return;
  }

  AppState.currentUser = user;
  
  const adminLogoutBtn = document.getElementById('adminLogoutBtn');
  if (adminLogoutBtn) {
    adminLogoutBtn.addEventListener('click', () => {
      sessionStorage.removeItem('bioauth_token');
      sessionStorage.removeItem('bioauth_user');
      window.location.href = 'views/login.html';
    });
  }
}

function populateUserManagementTable() {
  const tableBody = document.getElementById('userTableBody');
  if (!tableBody) return;

  let html = '';
  AppState.mockUsers.forEach(user => {
    const statusClass = user.status === 'active' ? 'active' : 'inactive';
    const statusText = user.status === 'active' ? 'Active' : 'Inactive';
    html += `
      <tr>
        <td>${user.name}</td>
        <td>${user.email}</td>
        <td><span class="badge ${statusClass}">${statusText}</span></td>
        <td>${user.enrolled}</td>
        <td>${user.lastLogin}</td>
        <td>
          <button class="action-btn" onclick="toggleUserStatus(${user.id})">Toggle</button>
          <button class="action-btn danger" onclick="disableUser(${user.id})">Disable</button>
        </td>
      </tr>
    `;
  });

  tableBody.innerHTML = html;
}

function populateAuthenticationLogsTable() {
  const tableBody = document.getElementById('logsTableBody');
  if (!tableBody) return;

  let html = '';
  AppState.mockLogs.forEach(log => {
    const resultClass = log.result === 'success' ? 'success' : 'warning';
    const resultText = log.result === 'success' ? 'Success' : 'Failed';
    html += `
      <tr>
        <td>${log.user}</td>
        <td>${log.timestamp}</td>
        <td><span class="badge ${resultClass}">${resultText}</span></td>
        <td>${log.device}</td>
      </tr>
    `;
  });

  tableBody.innerHTML = html;
}

function populateAlertsPanel() {
  const alertsContainer = document.getElementById('alertsContainer');
  if (!alertsContainer) return;

  const mockAlerts = [
    { type: 'critical', message: 'User Bob Smith - 5 failed login attempts', time: '14:32' },
    { type: 'warning', message: 'High authentication failure rate detected', time: '13:45' },
    { type: 'info', message: 'New user Carol White enrolled successfully', time: '12:20' },
    { type: 'critical', message: 'Unusual login time detected for David Brown', time: '11:15' },
    { type: 'warning', message: 'System certificate expiring in 30 days', time: '10:45' }
  ];

  let html = '';
  mockAlerts.forEach(alert => {
    html += `
      <div class="alert-item ${alert.type}">
        <span>${alert.message}</span>
        <span class="alert-time">${alert.time}</span>
      </div>
    `;
  });

  alertsContainer.innerHTML = html;
}

function updateQuickStats() {
  const statsContainer = document.getElementById('statsContainer');
  if (!statsContainer) return;

  const stats = [
    { value: '12', label: 'Total Users' },
    { value: '8', label: 'Currently Online' },
    { value: '47', label: 'Auth Attempts (Today)' },
    { value: '3', label: 'Failed Attempts' }
  ];

  let html = '';
  stats.forEach(stat => {
    html += `
      <div class="stat-card">
        <div class="stat-value">${stat.value}</div>
        <div class="stat-label">${stat.label}</div>
      </div>
    `;
  });

  statsContainer.innerHTML = html;
}

function toggleUserStatus(userId) {
  const user = AppState.mockUsers.find(u => u.id === userId);
  if (user) {
    user.status = user.status === 'active' ? 'inactive' : 'active';
    populateUserManagementTable();
    alert(`User ${user.name} status changed to ${user.status.toUpperCase()}`);
  }
}

function disableUser(userId) {
  const user = AppState.mockUsers.find(u => u.id === userId);
  if (user && confirm(`Disable user ${user.name}?`)) {
    user.status = 'inactive';
    populateUserManagementTable();
    alert(`User ${user.name} has been disabled.`);
  }
}

function attachAdminListeners() {
  const sidebarLinks = document.querySelectorAll('.sidebar-nav a');
  sidebarLinks.forEach(link => {
    link.addEventListener('click', (e) => {
      sidebarLinks.forEach(l => l.classList.remove('active'));
      link.classList.add('active');
    });
  });

  // Set first link as active by default
  if (sidebarLinks.length > 0) {
    sidebarLinks[0].classList.add('active');
  }
}

// ============= MODAL FUNCTIONS =============

function attachModalListeners() {
  // Reenroll Modal
  const reenrollModal = document.getElementById('reenrollModal');
  const reenrollClose = document.getElementById('reenrollClose');
  const reenrollCancel = document.getElementById('reenrollCancel');
  const reenrollConfirm = document.getElementById('reenrollConfirm');

  if (reenrollClose) {
    reenrollClose.addEventListener('click', () => {
      if (reenrollModal) reenrollModal.classList.remove('show');
    });
  }

  if (reenrollCancel) {
    reenrollCancel.addEventListener('click', () => {
      if (reenrollModal) reenrollModal.classList.remove('show');
    });
  }

  if (reenrollConfirm) {
    reenrollConfirm.addEventListener('click', () => {
      alert('Re-enrollment process started. Please follow the on-screen instructions.');
      if (reenrollModal) reenrollModal.classList.remove('show');
    });
  }

  // Close modal when clicking outside
  if (reenrollModal) {
    reenrollModal.addEventListener('click', (e) => {
      if (e.target === reenrollModal) {
        reenrollModal.classList.remove('show');
      }
    });
  }
}

// ============= UTILITY FUNCTIONS =============

function formatDate(date) {
  const options = { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' };
  return new Date(date).toLocaleDateString('en-US', options);
}

function generateMockDeviceId() {
  return 'DEV-' + Math.random().toString(36).substr(2, 9).toUpperCase();
}

// ============= PAGE INITIALIZATION =============

document.addEventListener('DOMContentLoaded', () => {
  const currentPage = window.location.pathname;

  if (currentPage.includes('login.html') || currentPage === '/' || currentPage.endsWith('/')) {
    initLoginPage();
  } else if (currentPage.includes('userDashboard.html')) {
    initUserDashboard();
  } else if (currentPage.includes('adminDashboard.html')) {
    initAdminDashboard();
  }
});

// ============= KEYBOARD SHORTCUTS =============

document.addEventListener('keydown', (e) => {
  if (e.key === 'Escape') {
    const modals = document.querySelectorAll('.modal.show');
    modals.forEach(modal => modal.classList.remove('show'));
  }
});
