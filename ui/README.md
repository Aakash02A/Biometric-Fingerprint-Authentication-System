# BioAuth - Biometric Authentication System UI

Production-ready front-end UI for biometric fingerprint authentication web application.

## Structure

```
ui/
├── index.html                          # Landing page
├── assets/
│   ├── css/
│   │   └── style.css                  # Main stylesheet (dark theme, animations)
│   └── js/
│       └── app.js                     # JavaScript functionality (simulated events)
└── views/
    ├── login.html                     # Login screen with fingerprint scan
    ├── userDashboard.html             # User dashboard
    └── adminDashboard.html            # Admin dashboard
```

## Features

### Login Screen
- Modern biometric theme with fingerprint animation and glow effects
- Dynamic UI states:
  - **Scanning**: Animation with loading spinner
  - **Match Success**: Green glow animation + auto-redirect to dashboard
  - **Match Failed**: Red shake animation with retry counter
  - **Locked Out**: Account lock after 5 failed attempts
- Simulated fingerprint recognition (70% success rate for demo)
- Fully responsive design

### User Dashboard
- Profile summary with user information
- Authentication history with success/failure status
- Device information and last login details
- Biometric re-enrollment button (modal dialog)
- Responsive card layout
- Logout functionality

### Admin Dashboard
- Sidebar navigation
- Quick stats cards (total users, online users, auth attempts, failures)
- User management table with enable/disable actions
- System-wide authentication logs
- Real-time status indicator showing online/offline users
- Alerts panel for system warnings and critical events
- System health status (database, API, disk usage, memory)
- Fully functional mock data

## UI/UX Details

### Theme
- **Dark mode** inspired with neon accents
- **Primary Colors**:
  - Neon Green (#00ff88) - Success, primary actions
  - Neon Blue (#00d4ff) - Secondary accents
  - Error Red (#ff006e) - Failures, dangers
  - Warning Yellow (#ffd60a) - Warnings

### Animations
- Fingerprint scan pulse animation
- Success/failure glow effects
- Shake animation for failed attempts
- Smooth slide-up entrance animations
- Hover effects on cards and buttons
- Real-time pulsing status indicators

### Responsive Design
- Mobile-first approach
- Tablets: 768px breakpoint
- Mobile: 480px breakpoint
- Sidebar collapses on mobile in admin dashboard
- Touch-friendly button sizes

## Getting Started

1. **Open Landing Page**:
   ```
   Open index.html in a web browser
   ```

2. **User Login**:
   - Navigate to "User Login" button
   - Enter credentials (any username/password)
   - Click "Scan Fingerprint"
   - Wait for scan simulation (2 seconds)
   - Success redirects to user dashboard

3. **Admin Panel**:
   - Click "Admin Panel" button
   - View all admin features
   - Manage users, view logs, check alerts

## JavaScript Functions

### Login Page
- `simulateFingerprintScan()` - Simulates fingerprint scan with delay
- `handleScanSuccess()` - Handles successful match with animation
- `handleScanFailure()` - Handles failed match with retry counter
- `updateRetryCounter()` - Updates attempt counter UI

### User Dashboard
- `populateProfileSummary()` - Loads user profile data
- `populateAuthenticationHistory()` - Displays auth history
- `populateDeviceInfo()` - Shows device details
- `openReenrollModal()` - Opens re-enrollment modal

### Admin Dashboard
- `populateUserManagementTable()` - Populates user table
- `populateAuthenticationLogsTable()` - Displays auth logs
- `populateAlertsPanel()` - Shows system alerts
- `updateQuickStats()` - Updates statistics cards
- `toggleUserStatus()` - Toggles user active/inactive
- `disableUser()` - Disables a user account

## Mock Data

All data is simulated with realistic mock values:
- 5 sample users with status, enrollment, and login dates
- 8 authentication logs with timestamps and results
- 5 system alerts (critical, warning, info levels)
- Quick stats showing system activity

## Browser Compatibility

- Chrome 90+
- Firefox 88+
- Safari 14+
- Edge 90+

## No External Dependencies

- Pure HTML5
- CSS3 (with animations and gradients)
- Vanilla JavaScript (no frameworks)
- No CDN dependencies
- Completely self-contained

## Customization

### Colors
Edit `:root` variables in `style.css`:
```css
:root {
  --accent-neon: #00ff88;
  --error-red: #ff006e;
  /* ... more variables */
}
```

### Animation Speed
Adjust animation timings in `style.css`:
```css
--transition-smooth: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
```

### Fingerprint Success Rate
In `app.js`, modify the probability in `simulateFingerprintScan()`:
```javascript
const isMatch = Math.random() < 0.7; // 70% success
```

## Production Ready

✓ Smooth animations and transitions
✓ Accessible modal dialogs
✓ Keyboard shortcuts (ESC to close modals)
✓ Mobile responsive
✓ Cross-browser compatible
✓ Performance optimized
✓ Security-minded UI
✓ Professional styling
✓ Fully wired with simulated events

## License

Part of Biometric Fingerprint Authentication System
