package com.bioauth.biometric;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.PointerByReference;
import java.util.Arrays;
import java.util.List;

/**
 * JNA bindings for Windows Biometric Framework (WinBio API)
 * Provides low-level access to Windows fingerprint sensor hardware
 */
public class WinBioAPI {
    
    // Define Windows Biometric API constants
    public static final int WINBIO_SUCCESS = 0;
    public static final int WINBIO_SENSOR_READY = 0;
    public static final int WINBIO_TYPE_FINGERPRINT = 0x00000008;
    public static final int WINBIO_POOL_SYSTEM = 1;
    public static final int WINBIO_POOL_PRIVATE = 2;
    public static final int WINBIO_POOL_UNASSIGNED = 4;
    public static final int WINBIO_FLAG_RAW = 0x00000001;
    public static final int WINBIO_DATA_TYPE_RAW = 0x00000001;
    public static final int WINBIO_DATA_TYPE_TEMPLATE = 0x00000008;
    
    // Timeout constants (in milliseconds)
    public static final int WINBIO_TIMEOUT_DEFAULT = 10000;

    /**
     * JNA interface to Windows Biometric Framework DLL
     */
    public interface WinBio extends Library {
        WinBio INSTANCE = Platform.isWindows() ? 
            Native.load("winbio", WinBio.class) : null;

        /**
         * Opens a connection to the Windows Biometric Framework
         * WinBioOpenSession(
         *   WINBIO_BIOMETRIC_TYPE Factor,
         *   WINBIO_POOL_TYPE PoolType,
         *   WINBIO_SESSION_FLAGS Flags,
         *   WINBIO_UNIT_ID *UnitArray,
         *   SIZE_T UnitCount,
         *   WINBIO_DATABASE_ID *DatabaseIds,
         *   SIZE_T DatabaseCount,
         *   WINBIO_SESSION_HANDLE *SessionHandle
         * )
         */
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

        /**
         * Closes a session with the Windows Biometric Framework
         * WinBioCloseSession(WINBIO_SESSION_HANDLE SessionHandle)
         */
        int WinBioCloseSession(Pointer sessionHandle);

        /**
         * Captures a fingerprint sample
         * WinBioCaptureSample(
         *   WINBIO_SESSION_HANDLE SessionHandle,
         *   WINBIO_BIR_PURPOSE Purpose,
         *   WINBIO_BIR_DATA_FLAG Flags,
         *   PWINBIO_BIR *Sample,
         *   PSIZE_T SampleSize,
         *   PWINBIO_REJECT_DETAIL RejectDetail
         * )
         */
        int WinBioCaptureSample(
            Pointer sessionHandle,
            int purpose,
            int flags,
            PointerByReference sample,
            PointerByReference sampleSize,
            PointerByReference rejectDetail
        );

        /**
         * Gets the current sensor status
         * WinBioGetSensorStatus(
         *   WINBIO_SESSION_HANDLE SessionHandle,
         *   PWINBIO_SENSOR_STATUS SensorStatus
         * )
         */
        int WinBioGetSensorStatus(
            Pointer sessionHandle,
            PointerByReference sensorStatus
        );

        /**
         * Enumerates biometric units (devices)
         * WinBioEnumBiometricUnits(
         *   WINBIO_BIOMETRIC_TYPE Factor,
         *   PWINBIO_UNIT_SCHEMA *UnitSchemaArray,
         *   PSIZE_T UnitCount
         * )
         */
        int WinBioEnumBiometricUnits(
            int factor,
            PointerByReference unitSchemaArray,
            PointerByReference unitCount
        );

        /**
         * Frees allocated memory
         * WinBioFree(PVOID Address)
         */
        int WinBioFree(Pointer address);

        /**
         * Converts a WINBIO error code to a string
         */
        int WinBioHresultToString(int hr, byte[] buffer, int bufferSize);
    }

    /**
     * WINBIO_UNIT_SCHEMA structure
     */
    public static class WINBIO_UNIT_SCHEMA extends Structure {
        public int UnitId;
        public int PoolType;
        public int BiometricFactor;
        public int SensorStatus;
        public byte[] DeviceInstanceId = new byte[256];
        public byte[] SerialNumber = new byte[256];
        public byte[] ManufacturerName = new byte[256];
        public byte[] ModelName = new byte[256];

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(
                "UnitId", "PoolType", "BiometricFactor", "SensorStatus",
                "DeviceInstanceId", "SerialNumber", "ManufacturerName", "ModelName"
            );
        }
    }

    /**
     * Check if Windows Biometric Framework is available
     */
    public static boolean isAvailable() {
        try {
            return Platform.isWindows() && WinBio.INSTANCE != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get last error message from WinBio API
     */
    public static String getErrorMessage(int errorCode) {
        try {
            if (WinBio.INSTANCE == null) {
                return "Windows Biometric Framework not available";
            }
            
            // Use WinBioHresultToString instead
            byte[] buffer = new byte[256];
            int result = WinBio.INSTANCE.WinBioHresultToString(errorCode, buffer, buffer.length);
            if (result == 0) { // S_OK
                // Convert byte array to string
                int len = 0;
                while (len < buffer.length && buffer[len] != 0) len++;
                return new String(buffer, 0, len);
            }
        } catch (Exception e) {
            // Ignore
        }
        return "Unknown error (" + errorCode + ")";
    }
}
