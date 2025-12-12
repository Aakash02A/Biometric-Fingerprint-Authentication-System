package com.bioauth;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

/**
 * Simple test to check Windows Biometric Framework connectivity
 */
public class SimpleFingerprintTest {
    
    public interface WinBio extends Library {
        WinBio INSTANCE = Platform.isWindows() ? 
            Native.load("winbio", WinBio.class) : null;
            
        // Simplified WinBioOpenSession function
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
        
        // Simplified WinBioCloseSession function
        int WinBioCloseSession(Pointer sessionHandle);
    }
    
    // Windows Biometric Framework constants
    public static final int WINBIO_TYPE_FINGERPRINT = 0x00000008;
    public static final int WINBIO_POOL_SYSTEM = 1;
    public static final int WINBIO_SUCCESS = 0;
    
    public static void main(String[] args) {
        System.out.println("=== Simple Fingerprint Framework Test ===");
        System.out.println("OS: " + System.getProperty("os.name"));
        System.out.println("JNA Version: " + com.sun.jna.Native.VERSION);
        
        // Check if we can load the library
        if (WinBio.INSTANCE == null) {
            System.out.println("❌ Failed to load Windows Biometric Framework library");
            return;
        }
        
        System.out.println("✓ Windows Biometric Framework library loaded successfully");
        
        // Try to open a session
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
            
            if (result == WINBIO_SUCCESS) {
                System.out.println("✓ Successfully opened biometric session!");
                Pointer sessionHandle = sessionHandleRef.getValue();
                
                // Close the session
                System.out.println("Closing session...");
                int closeResult = WinBio.INSTANCE.WinBioCloseSession(sessionHandle);
                if (closeResult == WINBIO_SUCCESS) {
                    System.out.println("✓ Session closed successfully!");
                } else {
                    System.out.println("⚠ Warning: Failed to close session (code: " + closeResult + ")");
                }
                
                System.out.println("\n🎉 Fingerprint scanner is ready for use!");
                System.out.println("Your FocalTech fingerprint reader should work with the full application.");
                
            } else {
                System.out.println("❌ Failed to open biometric session (code: " + result + ")");
                System.out.println("This might indicate:");
                System.out.println("  - Windows Biometric Service is not running");
                System.out.println("  - Insufficient permissions (try running as administrator)");
                System.out.println("  - Driver issues with your fingerprint scanner");
            }
            
        } catch (UnsatisfiedLinkError e) {
            System.out.println("❌ Link error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n=== Test Complete ===");
    }
}