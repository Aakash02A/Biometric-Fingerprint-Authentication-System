# Login & Authentication Guide

## Overview

The BioAuth system now features complete user and admin login with username/password credentials and biometric fingerprint verification.

## Login Features

### Two Login Types
1. **User Login** - Regular users with standard access
2. **Admin Login** - Administrators with full system access

### Two-Factor Authentication
1. **Step 1**: Username & Password Validation
2. **Step 2**: Biometric Fingerprint Scan

## Test Credentials

### User Accounts
```
Username: alice        Password: alice123
Username: bob          Password: bob123
Username: carol        Password: carol123
Username: david        Password: david123
Username: eve          Password: eve123
```

### Admin Account
```
Username: admin        Password: admin123
```

## Login Flow

### 1. Select Login Type
Choose between "User" or "Admin" login type at the bottom of the login form.

### 2. Enter Credentials
- Enter username in the "Username" field
- Enter password in the "Password" field

### 3. Validate Credentials
- Click "Validate Credentials" button
- System checks username/password against database
- If invalid: Error message shown, can retry
- If valid: Form is disabled, scan button appears

### 4. Fingerprint Scan
- Click "Scan Fingerprint" button
- Wait for fingerprint scan simulation (2 seconds)
- 70% success rate (for demo purposes)
- On success: Auto-redirects to appropriate dashboard
- On failure: Retry counter increments (max 5 attempts)

### 5. Dashboard Redirect
- **User Login** → User Dashboard
- **Admin Login** → Admin Dashboard

## Security Features

### Client-Side
- Session storage for authentication state
- Auto-redirect to login if session expires
- Role-based dashboard access (admin login cannot access user dashboard)
- Form validation before biometric scan

### Session Management
- `bioauth_token` - Authentication token (stored in sessionStorage)
- `bioauth_user` - User information (stored in sessionStorage)
- Automatically cleared on logout

## User Dashboard

### Features
- View profile information (name, email, ID)
- Authentication history
- Device information
- Biometric re-enrollment
- Logout button

### Access Control
- Only accessible after successful login and biometric verification
- Automatic redirect to login if not authenticated
- Displays logged-in user's information

## Admin Dashboard

### Features
- User management table (enable/disable users)
- Authentication logs
- System statistics
- Current online users status
- System alerts
- System health metrics
- Logout button

### Access Control
- Only accessible with admin credentials
- User login cannot access admin dashboard
- Automatic redirect to login if not admin
- Full system management capabilities

## Logout

### User Dashboard
Click "Logout" button in top-right corner:
- Clears session storage
- Redirects to login page
- Can login again with different credentials

### Admin Dashboard
Click "Logout" button in top-right corner:
- Clears session storage
- Redirects to login page
- Can login again with different credentials

## Error Handling

### Invalid Credentials
**Message**: "Invalid credentials. Please try again."
- Username/password combination not found
- Clear invalid password and try again

### Role Mismatch
**Message**: "This account does not have admin privileges."
- User account selected for admin login
- Choose "User" login type instead

**Message**: "Please use admin login for this account."
- Admin account selected for user login
- Choose "Admin" login type instead

### Inactive Account
**Message**: "This account is inactive. Contact administrator."
- Account status is INACTIVE
- Admin must enable the account
- Try different credentials

### Fingerprint Verification Failed
**Message**: "✗ Fingerprint did not match. Try again."
- Scanned fingerprint does not match stored template
- Click "Scan Fingerprint" to retry
- Maximum 5 attempts before lockout

### Account Locked
**Message**: "Account locked due to too many failed attempts. Please try again later."
- 5 failed fingerprint scans
- Click "Try Again" to reset form and login again

## Demo Workflow

### 1. User Login
```
1. Open /views/login.html
2. Select "User" login type (default)
3. Enter credentials:
   - Username: alice
   - Password: alice123
4. Click "Validate Credentials"
5. Click "Scan Fingerprint"
6. Wait for success message
7. Automatically redirected to User Dashboard
8. View profile, history, and device info
9. Click "Logout" to return to login
```

### 2. Admin Login
```
1. Open /views/login.html
2. Select "Admin" login type
3. Enter credentials:
   - Username: admin
   - Password: admin123
4. Click "Validate Credentials"
5. Click "Scan Fingerprint"
6. Wait for success message
7. Automatically redirected to Admin Dashboard
8. View all users, logs, and system stats
9. Manage users and view alerts
10. Click "Logout" to return to login
```

## Technical Details

### Session Storage
```javascript
// Stored after successful authentication
sessionStorage.setItem('bioauth_token', 'jwt_token_' + Date.now());
sessionStorage.setItem('bioauth_user', JSON.stringify({
  id: 'USR1',
  name: 'Alice Johnson',
  email: 'alice@bioauth.com',
  avatar: 'AJ',
  username: 'alice',
  role: 'user',
  dbId: 1
}));
```

### Credential Validation
```javascript
const user = AppState.mockUsers.find(u => 
  u.username === username && u.password === password
);
```

### Role Checking
```javascript
if (AppState.loginType === 'admin' && user.role !== 'admin') {
  // Show error: not admin
}
```

### Biometric Simulation
- 70% success rate in `simulateFingerprintScan()`
- Confidence score between 0.0-1.0
- Quality score between 60-99
- Match threshold: 0.85+

## Testing Tips

1. **Try Invalid Credentials First**
   - Tests error handling
   - See "Invalid credentials" message

2. **Test Role Mismatch**
   - Login with user account in admin mode
   - See role validation

3. **Test Failed Fingerprints**
   - Click scan button multiple times
   - See retry counter
   - Test lockout at 5 attempts

4. **Test Logout**
   - Login successfully
   - Click logout
   - Verify redirected to login
   - Verify session cleared

5. **Test Session Persistence**
   - Login successfully
   - Open browser console
   - Check sessionStorage
   - Manual page refresh
   - Verify still logged in (session maintained)

## Password Requirements

Current implementation:
- No password validation rules
- Accepts any length
- Case-sensitive
- Stored in plaintext in mock data (for demo only)

For production:
- Enforce minimum 8 characters
- Require uppercase, lowercase, numbers, special chars
- Hash passwords with bcrypt/Argon2
- Implement password reset

## Future Enhancements

1. **Backend Integration**
   - Connect to Spring Boot REST API
   - Real JWT token generation
   - Database credential validation

2. **Security**
   - Implement password reset flow
   - Two-factor SMS/email verification
   - Rate limiting on login attempts
   - Account lockout after X failures

3. **Features**
   - Remember me functionality
   - Login history per user
   - Device fingerprinting
   - Geographic login restrictions

4. **UI/UX**
   - Loading states
   - Password strength indicator
   - Show/hide password toggle
   - Terms and conditions

## Troubleshooting

### Stuck at Validate Credentials
- Ensure username and password are correct
- Check login type selection
- Try different test user

### Fingerprint Scan Not Progressing
- Click "Scan Fingerprint" button
- Wait 2 seconds for simulation
- If locked out, click "Try Again"

### Redirected Back to Login
- Session may have expired
- Account might be logged out
- Login again with credentials

### Can't Access Admin Dashboard
- Ensure using admin credentials
- Select "Admin" login type
- Admin username: admin, password: admin123

## Support

For issues or questions about the login system, refer to the main README or API documentation.
