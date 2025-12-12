# Registration & Login System - Complete Testing Guide

## System Overview

The BioAuth system now includes:
1. **User Registration Page** - Create new accounts
2. **Simplified Login Page** - Clean credential-based login
3. **Two-Step Authentication** - Password validation + fingerprint scan
4. **Role-Based Access Control** - User vs Admin differentiation
5. **Session Management** - Persistent login state

---

## Test Credentials

### User Accounts
| Username | Password | Role | Status |
|----------|----------|------|--------|
| alice | alice123 | user | active |
| bob | bob123 | user | active |
| carol | carol123 | user | inactive |
| david | david123 | user | active |
| eve | eve123 | user | active |

### Admin Account
| Username | Password | Role | Status |
|----------|----------|------|--------|
| admin | admin123 | admin | active |

---

## Testing Procedures

### Test 1: New User Registration

**Steps:**
1. Navigate to [http://localhost/views/register.html](http://localhost/views/register.html)
2. Fill in the registration form:
   - First Name: John
   - Last Name: Smith
   - Email: john.smith@example.com
   - Username: johnsmith
   - Password: SecurePass123
   - Confirm Password: SecurePass123
3. Click "Create Account"

**Expected Results:**
- ✓ Password strength indicator shows "Strong password"
- ✓ Success message displays
- ✓ Page redirects to login page with success notification
- ✓ New account is available in mockUsers array

**Success Criteria:**
```javascript
// New user should appear in AppState.mockUsers
const newUser = AppState.mockUsers.find(u => u.username === 'johnsmith');
console.log(newUser); // Should print the newly created user object
```

---

### Test 2: Registration Form Validation

**Test 2.1: Password Mismatch**
1. Fill registration form with mismatched passwords
2. Click Create Account
3. Expected: Error message "Passwords do not match."

**Test 2.2: Duplicate Username**
1. Use username "alice" (existing user)
2. Expected: Error message "Username already exists. Please choose a different one."

**Test 2.3: Duplicate Email**
1. Use email "alice@bioauth.com" (existing user)
2. Expected: Error message "Email already registered. Please login or use a different email."

**Test 2.4: Missing Fields**
1. Leave one or more fields empty
2. Expected: Error message "Please fill in all fields."

**Test 2.5: Short Password**
1. Enter password with less than 6 characters
2. Expected: Error message "Password must be at least 6 characters long."

---

### Test 3: User Login Flow

**Steps:**
1. Navigate to [http://localhost/views/login.html](http://localhost/views/login.html)
2. Enter credentials: alice / alice123
3. Click "Login" button
4. Click fingerprint scanner when it appears
5. Observe scan result

**Expected Results:**

**Stage 1 - Credentials Validation:**
- ✓ Username/password fields accept input
- ✓ Login button is clickable
- ✓ Form validates credentials
- ✓ Message shows "Credentials verified"
- ✓ Biometric section appears

**Stage 2 - Biometric Scan:**
- ✓ Fingerprint scanner interactive area appears
- ✓ 70% success rate (scan succeeds on some attempts, fails on others)
- ✓ On success: Message shows "✓ Authentication successful! Redirecting..."
- ✓ On failure: Message shows "✗ Fingerprint verification failed. Please try again."
- ✓ Can retry scan immediately
- ✓ On success: Redirected to User Dashboard after ~1 second

---

### Test 4: Invalid Login Attempts

**Test 4.1: Wrong Password**
1. Enter username: alice
2. Enter password: wrongpassword
3. Click Login
4. Expected: Error "Invalid username or password."

**Test 4.2: Nonexistent Username**
1. Enter username: nonexistent
2. Enter password: password123
3. Click Login
4. Expected: Error "Invalid username or password."

**Test 4.3: Inactive User (Carol)**
1. Enter username: carol
2. Enter password: carol123
3. Click Login
4. Expected: Error "This account is not active. Please contact support."

**Test 4.4: Empty Fields**
1. Leave username and/or password empty
2. Click Login
3. Expected: Error "Please enter both username and password."

---

### Test 5: Session Management

**Test 5.1: Existing Session Auto-Redirect**
1. Login successfully (alice / alice123 + fingerprint scan)
2. You should be redirected to User Dashboard
3. Manually navigate back to login page: [http://localhost/views/login.html](http://localhost/views/login.html)
4. Expected: Auto-redirect back to User Dashboard (session is still valid)

**Test 5.2: Session Storage Verification**
1. Login successfully
2. Open browser DevTools (F12)
3. Go to Storage → Session Storage
4. Check for keys: `bioauth_token` and `bioauth_user`
5. Expected: Both keys should be present with appropriate values

```javascript
// In DevTools console, check session:
console.log(sessionStorage.getItem('bioauth_token')); // Should show token
console.log(JSON.parse(sessionStorage.getItem('bioauth_user'))); // Should show user object
```

**Test 5.3: Logout Clears Session**
1. Login successfully and reach User Dashboard
2. Click "Logout" button in top right
3. Check browser back button history
4. Expected: Cannot go back to dashboard (session is cleared)
5. Navigate to dashboard URL directly
6. Expected: Redirected to login page

---

### Test 6: User Dashboard Access

**Successful Login to User Dashboard:**
1. Login with alice / alice123
2. Complete fingerprint scan (may take 2-3 attempts)
3. Expected: Redirected to User Dashboard
4. Expected: Dashboard shows:
   - User avatar with initials (AJ for Alice Johnson)
   - User name: "Alice Johnson"
   - User email: "alice@bioauth.com"
   - Authentication history
   - Device information
   - Biometric re-enrollment option
   - Logout button in header

**Profile Information Verification:**
```javascript
// In DevTools console:
const user = JSON.parse(sessionStorage.getItem('bioauth_user'));
console.log(user.name);  // Should show "Alice Johnson"
console.log(user.email); // Should show "alice@bioauth.com"
console.log(user.role);  // Should show "user"
```

---

### Test 7: Admin Dashboard Access

**Admin Login Flow:**
1. Navigate to login page
2. Enter credentials: admin / admin123
3. Click Login
4. Complete fingerprint scan
5. Expected: Redirected to Admin Dashboard (NOT User Dashboard)

**Admin Dashboard Verification:**
1. Admin should see:
   - User management table
   - Authentication logs
   - Online users status
   - System alerts
   - Quick statistics
   - Admin logout button

**Non-Admin Cannot Access Admin Dashboard:**
1. Login as regular user (alice)
2. Manually navigate to: [http://localhost/views/adminDashboard.html](http://localhost/views/adminDashboard.html)
3. Expected: Auto-redirect to login page
4. Expected: Cannot access admin features

---

### Test 8: Keyboard Shortcuts

**Test 8.1: Enter Key Login**
1. Navigate to login page
2. Enter username and press Tab
3. Enter password
4. Press Enter (instead of clicking button)
5. Expected: Login proceeds as normal

**Test 8.2: Enter Key in Registration**
1. Navigate to registration page
2. Fill fields and press Tab to navigate
3. Press Enter in any field
4. Expected: No premature submission

---

### Test 9: Mobile Responsiveness

**Test on Different Screen Sizes:**

**Desktop (1920px):**
1. Login form should be centered in clean container
2. All buttons and fields properly sized
3. Footer links in horizontal layout

**Tablet (768px):**
1. Form should still be centered and readable
2. Buttons should be appropriately sized
3. No horizontal scrolling

**Mobile (375px):**
1. Login form should be full width with padding
2. All inputs easily tappable
3. Buttons full width
4. Footer links should stack vertically
5. Text should be readable without zooming

---

### Test 10: Data Models Integrity

**AppState Model Structure:**
```javascript
// Expected AppState structure:
{
  currentUser: {
    id: string,
    name: string,
    email: string,
    avatar: string,
    username: string,
    role: 'user' | 'admin',
    dbId?: number
  },
  isAuthenticated: boolean,
  mockUsers: [
    {
      id: number,
      name: string,
      email: string,
      status: 'active' | 'inactive',
      enrolled: string (YYYY-MM-DD),
      lastLogin: string,
      username: string,
      password: string,
      role: 'user' | 'admin'
    }
  ]
}
```

**Verification Script:**
```javascript
// Run in DevTools console to verify model integrity:
function verifyModels() {
  console.log('=== AppState Model Verification ===');
  
  // Check mockUsers structure
  AppState.mockUsers.forEach(user => {
    if (!user.username || !user.password || !user.role) {
      console.error('Invalid user:', user);
    }
  });
  
  // Check currentUser structure
  if (AppState.currentUser && !AppState.currentUser.role) {
    console.error('Invalid currentUser:', AppState.currentUser);
  }
  
  console.log('✓ All models verified');
  console.log('Total users:', AppState.mockUsers.length);
}

verifyModels();
```

---

### Test 11: Error Handling

**Test Scenario: Check Error Messages**

| Error Scenario | Expected Message |
|---|---|
| Wrong username | "Invalid username or password." |
| Wrong password | "Invalid username or password." |
| Empty username | "Please enter both username and password." |
| Empty password | "Please enter both username and password." |
| Inactive user | "This account is not active. Please contact support." |
| Duplicate username (registration) | "Username already exists. Please choose a different one." |
| Duplicate email (registration) | "Email already registered. Please login or use a different email." |
| Password mismatch (registration) | "Passwords do not match." |
| Short password (registration) | "Password must be at least 6 characters long." |
| Fingerprint scan failed | "✗ Fingerprint verification failed. Please try again." |

---

## Model Data Flow

### Registration Data Flow:
```
User Input Form
    ↓
JavaScript Validation
    ↓
Duplicate Check (Username & Email)
    ↓
Password Validation
    ↓
Create User Object
    ↓
Add to AppState.mockUsers
    ↓
Redirect to Login with ?new=true
```

### Login Data Flow:
```
User Input (Username + Password)
    ↓
Validate Credentials Against mockUsers
    ↓
Check User Status (active/inactive)
    ↓
Create Session Token
    ↓
Store User in sessionStorage
    ↓
Show Fingerprint Scanner
    ↓
Simulate Biometric Scan (70% success)
    ↓
On Success: Redirect to Dashboard
```

### Dashboard Access Flow:
```
Check sessionStorage for Token + User
    ↓
If Missing: Redirect to Login
    ↓
If Present: Load Dashboard
    ↓
Populate UI with User Data
    ↓
Enforce Role-Based Access (Admin checks)
    ↓
Display Dashboard
```

---

## Performance Metrics

**Expected Performance:**
- Login page load: < 500ms
- Registration form validation: < 100ms
- Fingerprint scan simulation: ~2000ms
- Dashboard load: < 1000ms
- Session check on page load: < 100ms

---

## Troubleshooting

### Issue: Login redirects immediately to dashboard

**Solution:** Clear sessionStorage and try again
```javascript
sessionStorage.clear();
// Then refresh and login again
```

### Issue: New user not appearing in dropdown

**Solution:** Verify user was added to AppState
```javascript
console.log(AppState.mockUsers.find(u => u.username === 'username'));
```

### Issue: Password strength indicator not showing

**Solution:** Check if password input has proper event listener
```javascript
document.getElementById('regPassword').dispatchEvent(new Event('input'));
```

### Issue: Cannot access admin dashboard

**Solution:** Verify user role is 'admin'
```javascript
const user = JSON.parse(sessionStorage.getItem('bioauth_user'));
console.log(user.role); // Should be 'admin'
```

---

## Summary Checklist

- [ ] Registration page loads correctly
- [ ] Can create new user account
- [ ] Duplicate username/email prevention works
- [ ] Password strength indicator shows correctly
- [ ] Login validates credentials properly
- [ ] Fingerprint scanner appears after credential validation
- [ ] Biometric scan succeeds/fails appropriately
- [ ] Session is stored in sessionStorage
- [ ] User dashboard loads with user data
- [ ] Admin dashboard only accessible by admin
- [ ] Logout clears session and redirects
- [ ] Auto-redirect on page visit with active session
- [ ] All error messages display correctly
- [ ] Mobile responsive on all screen sizes
- [ ] Keyboard shortcuts (Enter key) work
- [ ] AppState models contain all required fields

---

**Created:** December 12, 2025  
**System:** BioAuth Fingerprint Authentication  
**Status:** Ready for Testing
