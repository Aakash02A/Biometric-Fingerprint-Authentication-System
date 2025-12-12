package com.bioauth;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.W32APIOptions;

/**
 * Alternative test using W32APIOptions for better Windows integration
 */
public class AlternativeFingerprintTest {
    
    public interface WinBio extends Library {
        WinBio INSTANCE = Platform.isWindows() ? 
            (WinBio) Native.load("winbio", WinBio.class, W32APIOptions.DEFAULT_OPTIONS) : null;
            
        // WinBioOpenSession with proper signature
        int WinBioOpenSession(
            int factor,
            int poolType,
            int flags,
            Pointer unitArray,
            int unitCount,
            Pointer databaseIds,
            int databaseCount,
            PointerByReference sessionHandle
        );
        
        int WinBioCloseSession(Pointer sessionHandle);
    }
    
    // Constants
    public static final int WINBIO_TYPE_FINGERPRINT = 0x00000008;
    public static final int WINBIO_POOL_SYSTEM = 1;
    public static final int WINBIO_SUCCESS = 0;
    
    public static void main(String[] args) {
        System.out.println("=== Alternative Fingerprint Framework Test ===");
        System.out.println("OS: " + System.getProperty("os.name"));
        System.out.println("JNA Version: " + com.sun.jna.Native.VERSION);
        
        if (WinBio.INSTANCE == null) {
            System.out.println("❌ Failed to load Windows Biometric Framework library");
            return;
        }
        
        System.out.println("✓ Windows Biometric Framework library loaded successfully");
        System.out.println("Using W32APIOptions for better Windows integration");
        
        try {
            PointerByReference sessionHandleRef = new PointerByReference();
            
            System.out.println("Attempting to open biometric session...");
            int result = WinBio.INSTANCE.WinBioOpenSession(
                WINBIO_TYPE_FINGERPRINT,
                WINBIO_POOL_SYSTEM,
                0,
                null,  // UnitArray
                0,     // UnitCount
                null,  // DatabaseIds
                0,     // DatabaseCount
                sessionHandleRef
            );
            
            System.out.println("WinBioOpenSession returned: " + result);
            
            if (result == WINBIO_SUCCESS) {
                System.out.println("✓ Successfully opened biometric session!");
                Pointer sessionHandle = sessionHandleRef.getValue();
                
                System.out.println("Closing session...");
                int closeResult = WinBio.INSTANCE.WinBioCloseSession(sessionHandle);
                System.out.println("WinBioCloseSession returned: " + closeResult);
                
                if (closeResult == WINBIO_SUCCESS) {
                    System.out.println("✓ Session closed successfully!");
                }
                
                System.out.println("\n🎉 Fingerprint scanner connection test PASSED!");
                System.out.println("Your FocalTech fingerprint reader is ready for real-time scanning.");
                
            } else {
                System.out.println("❌ Failed to open biometric session");
                System.out.println("Error code: " + result);
                System.out.println("This might be due to permissions or service issues.");
                
                // Try to provide more specific error information
                if (result == -2147467261) { // 0x80004005
                    System.out.println("This is a general failure (E_FAIL).");
                    System.out.println("Try running as Administrator.");
                } else if (result == -2147024891) { // 0x80070005
                    System.out.println("This is an access denied error (E_ACCESSDENIED).");
                    System.out.println("Try running as Administrator.");
                }
            }
            
        } catch (UnsatisfiedLinkError e) {
            System.out.println("❌ Link error: " + e.getMessage());
            System.out.println("This suggests the winbio.dll function signatures may be incorrect.");
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n=== Test Complete ===");
    }
}