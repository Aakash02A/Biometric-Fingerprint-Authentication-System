# Windows Fingerprint Scanner Setup Guide

## Step-by-Step Setup

### Step 1: Check Your Laptop for Built-in Fingerprint Scanner

**Open PowerShell as Administrator** and run:

```powershell
# Check for fingerprint scanner devices
Get-PnpDevice | Where-Object {$_.Class -eq "Biometric"} | Select-Object Name, Manufacturer, Status
```

**Sample Output:**
```
Name                                Manufacturer      Status
----                                ------------      ------
Synaptics Fingerprint Sensor        Synaptics         OK
```

If you see a fingerprint device listed → **Go to Step 3**

If not → **Go to Step 2**

---

### Step 2: Install Fingerprint Scanner Drivers (If Needed)

#### For Dell Laptops:
1. Visit [Dell Support](https://www.dell.com/support)
2. Enter your service tag or model number
3. Search for "Fingerprint Reader" or "Biometric"
4. Download and install the driver
5. Restart your computer

#### For HP Laptops:
1. Visit [HP Support](https://support.hp.com)
2. Find your model
3. Download "Biometric" or "Fingerprint" driver
4. Install and restart

#### For Lenovo Laptops:
1. Visit [Lenovo Support](https://support.lenovo.com)
2. Find your ThinkPad/IdeaPad model
3. Download "Fingerprint" driver
4. Install and restart

#### For ASUS Laptops:
1. Visit [ASUS Support](https://www.asus.com/support)
2. Enter your model
3. Look for "Biometric" drivers
4. Install and restart

#### For External USB Scanner:
1. Plug in the USB fingerprint scanner
2. Windows may auto-install drivers
3. If not, visit manufacturer website (SecuGen, Nitgen, etc.)
4. Download and install drivers

---

### Step 3: Verify Installation

**Run PowerShell command again:**

```powershell
Get-PnpDevice | Where-Object {$_.Class -eq "Biometric"} | Select-Object Name, Manufacturer, Status
```

**Status should show: OK** ✅

---

### Step 4: Build the System

**Open Command Prompt or PowerShell** in the project directory:

```bash
cd C:\Z\GITHUB\Biometric-Fingerprint-Authentication-System\backend

# Build with Maven
mvn clean install -DskipTests
```

**Expected output:**
```
[INFO] BUILD SUCCESS
```

---

### Step 5: Run the System

**From the backend directory:**

```bash
# Option 1: Run compiled JAR
java -jar target/biometric-fingerprint-auth-1.0.0-jar-with-dependencies.jar

# Option 2: Run with Maven
mvn exec:java -Dexec.mainClass="com.bioauth.SystemDemo"
```

**Look for output:**
```
Biometric Authentication System initialized successfully
Sensor: Windows Biometric Framework Sensor (Fingerprint) - Devices: 1
Available fingerprint devices: 1
```

If you see this → **Your real fingerprint scanner is connected!** ✅

---

### Step 6: Test Fingerprint Capture

When prompted in the application:

1. **Place your finger on the scanner**
2. Keep your finger steady (don't press too hard)
3. Wait for the scanner to vibrate or beep
4. Remove your finger

**System output:**
```
Fingerprint captured. Quality score: 87
```

Quality score 70+ = Good ✅

---

## Configuration

### Force Real Sensor (Recommended)

Edit `backend/src/main/java/com/bioauth/config/SystemConfig.java`:

```java
public static final String SENSOR_TYPE = "windows";  // Force Windows Biometric Framework
```

Then rebuild: `mvn clean install`

### Auto-Detection (Default)

```java
public static final String SENSOR_TYPE = "auto";  // Auto-detect (current setting)
```

System will:
- ✅ Use real sensor if Windows + scanner detected
- ✅ Fall back to simulated if not available

### Force Simulated (Testing Only)

```java
public static final String SENSOR_TYPE = "simulated";  // Testing only
```

---

## Troubleshooting

### Issue: "No fingerprint sensors detected"

**Solutions:**

1. **Check driver installation:**
   ```powershell
   Get-PnpDevice | Where-Object {$_.Class -eq "Biometric"}
   ```
   
2. **Reinstall drivers:**
   - Uninstall from Device Manager
   - Restart
   - Windows will auto-reinstall or you manually install

3. **Check Windows Update:**
   - Settings → Windows Update → Check for updates
   - Often includes biometric driver updates

4. **Test with built-in apps:**
   - Windows Hello should work if scanner is functioning
   - Settings → Accounts → Sign-in options → Facial recognition/Fingerprint

---

### Issue: "Failed to open biometric session"

**Solutions:**

1. **Run as Administrator:**
   ```powershell
   # Right-click Command Prompt → Run as Administrator
   java -jar target/biometric-fingerprint-auth-1.0.0-jar-with-dependencies.jar
   ```

2. **Close conflicting apps:**
   - Windows Hello
   - Fingerprint authentication services
   - Any other biometric apps

3. **Restart computer and try again**

---

### Issue: "Fingerprint quality too low"

**Solutions:**

1. **Clean the scanner:**
   - Use soft, dry cloth
   - Remove any dust or smudges

2. **Better finger contact:**
   - Place entire fingerprint on scanner
   - Don't press too hard
   - Keep finger centered

3. **Retry capture:**
   - Wait 2 seconds between attempts
   - Use different finger if needed

---

### Issue: System uses Simulated Sensor

**Check:**
```powershell
# Verify fingerprint device is showing as OK
Get-PnpDevice | Where-Object {$_.Class -eq "Biometric"} | Select-Object Status
```

**If Status is not "OK":**
- Driver not installed properly
- Device is disabled
- Device connection issue (USB)

**Fix:**
1. In Device Manager, right-click on Biometric device
2. Click "Update driver"
3. Choose "Search automatically for drivers"
4. Restart and try again

---

## Commands Reference

```powershell
# List all biometric devices
Get-PnpDevice | Where-Object {$_.Class -eq "Biometric"} | Select-Object Name, Manufacturer, Status

# List fingerprint devices specifically
Get-WmiObject -Class Win32_PnPDevice | Where-Object {$_.Description -like "*fingerprint*"}

# Check Windows Biometric Framework DLL
Test-Path C:\Windows\System32\winbio.dll

# Rebuild project
mvn clean install -DskipTests

# Run application
java -jar target/biometric-fingerprint-auth-1.0.0-jar-with-dependencies.jar
```

---

## Quick Test Script

Save as `test-fingerprint.bat`:

```batch
@echo off
echo Checking for fingerprint scanner...
powershell -Command "Get-PnpDevice | Where-Object {$_.Class -eq 'Biometric'} | Select-Object Name, Manufacturer, Status"

echo.
echo Checking Windows Biometric Framework...
powershell -Command "Test-Path C:\Windows\System32\winbio.dll"

echo.
echo Building project...
cd backend
mvn clean install -DskipTests

echo.
echo Running application...
java -jar target/biometric-fingerprint-auth-1.0.0-jar-with-dependencies.jar

pause
```

---

## Success Checklist

✅ Fingerprint device shows in Device Manager  
✅ Device status is "OK"  
✅ System detects device during startup  
✅ Fingerprint captures without errors  
✅ Quality score is 70+  
✅ Registration completes successfully  
✅ Authentication works with captured fingerprint  

---

## Support

For issues:
1. Check **WINDOWS_BIOMETRIC_INTEGRATION.md** (detailed docs)
2. Review PowerShell output for device status
3. Verify driver is from official manufacturer
4. Run as Administrator
5. Restart system and try again

## Next Steps

1. ✅ Run the setup guide
2. ✅ Verify fingerprint scanner detection
3. ✅ Build and run the application
4. ✅ Test fingerprint capture
5. ✅ Register and authenticate with real fingerprints

Good luck! 🎯
