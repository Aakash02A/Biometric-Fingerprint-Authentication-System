/**
 * Fingerprint Scanner Integration Module
 * Interfaces with browser-based fingerprint scanning
 */

const FingerprintScanner = {
  
  // Scanner configuration
  config: {
    quality_threshold: 70,
    timeout: 5000,
    resolution: '500x500',
    device: null
  },

  // Scanner state
  state: {
    isScanning: false,
    lastCapture: null,
    scanCount: 0
  },

  /**
   * Initialize scanner
   */
  async initialize() {
    try {
      console.log('Initializing fingerprint scanner...');
      
      // Check if browser supports WebRTC for camera access
      if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
        console.warn('WebRTC not supported - will use file upload instead');
        return false;
      }

      // Try to detect fingerprint scanner via USB
      if (navigator.usb) {
        try {
          const devices = await navigator.usb.getDevices();
          console.log('USB devices found:', devices.length);
          
          // Check for common fingerprint scanner vendor IDs
          const fingerprintDevices = devices.filter(device => {
            const vendorId = device.vendorId;
            // Common fingerprint scanner vendor IDs
            const SECUGEN_ID = 0x1162;
            const NITGEN_ID = 0x0483;
            const CROSSMATCH_ID = 0x1c79;
            return vendorId === SECUGEN_ID || vendorId === NITGEN_ID || vendorId === CROSSMATCH_ID;
          });

          if (fingerprintDevices.length > 0) {
            console.log('Fingerprint scanner detected!');
            this.config.device = fingerprintDevices[0];
            return true;
          }
        } catch (error) {
          console.warn('Error checking USB devices:', error);
        }
      }

      console.log('Scanner initialized in file capture mode');
      return true;

    } catch (error) {
      console.error('Scanner initialization error:', error);
      return false;
    }
  },

  /**
   * Request fingerprint scan from user
   */
  async scanFingerprint() {
    if (this.state.isScanning) {
      console.warn('Already scanning...');
      return null;
    }

    this.state.isScanning = true;
    this.state.scanCount++;

    try {
      // If USB device available, use it
      if (this.config.device) {
        return await this.scanFromUSBDevice();
      }

      // Fall back to camera/file capture
      return await this.scanFromCamera();

    } catch (error) {
      console.error('Scan error:', error);
      throw error;
    } finally {
      this.state.isScanning = false;
    }
  },

  /**
   * Scan from USB fingerprint device
   */
  async scanFromUSBDevice() {
    try {
      const device = this.config.device;
      
      // Open USB connection
      await device.open();
      
      // Select configuration and claim interface
      await device.selectConfiguration(1);
      await device.claimInterface(0);

      // Send scan command to device
      const scanCommand = new Uint8Array([0x01, 0x00, 0x10]); // Generic scan command
      
      await device.controlTransferOut({
        requestType: 'vendor',
        recipient: 'device',
        request: 0x01,
        value: 0,
        index: 0
      }, scanCommand);

      // Read fingerprint data
      const result = await device.transferIn(0x81, 256);
      const fingerprintData = result.data.buffer;

      // Close connection
      await device.close();

      this.state.lastCapture = fingerprintData;
      return fingerprintData;

    } catch (error) {
      console.error('USB scan error:', error);
      throw new Error('Failed to scan from USB device: ' + error.message);
    }
  },

  /**
   * Scan from camera (fallback method)
   */
  async scanFromCamera() {
    return new Promise((resolve, reject) => {
      // Create modal for camera capture
      const modal = document.createElement('div');
      modal.id = 'camera-scan-modal';
      modal.style.cssText = `
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: rgba(0, 0, 0, 0.8);
        display: flex;
        justify-content: center;
        align-items: center;
        z-index: 10000;
      `;

      const container = document.createElement('div');
      container.style.cssText = `
        background: white;
        border-radius: 12px;
        padding: 30px;
        width: 90%;
        max-width: 500px;
        text-align: center;
      `;

      container.innerHTML = `
        <h3>Fingerprint Scan</h3>
        <p style="color: #666; margin: 15px 0;">Place your finger on the scanner</p>
        
        <video id="camera-preview" style="
          width: 100%;
          border-radius: 8px;
          background: #f0f0f0;
          margin: 20px 0;
          display: none;
        "></video>

        <div id="scan-instructions" style="
          background: #f5f5f5;
          padding: 20px;
          border-radius: 8px;
          margin: 20px 0;
          color: #666;
        ">
          <p>📸 Camera access needed</p>
          <p style="font-size: 12px; margin-top: 10px;">
            For better results, use an external fingerprint scanner connected via USB
          </p>
        </div>

        <div style="display: flex; gap: 10px; justify-content: center;">
          <button id="capture-from-camera" class="btn btn-primary">Capture Fingerprint</button>
          <button id="upload-fingerprint" class="btn btn-secondary">Upload Image</button>
          <button id="cancel-scan" class="btn btn-outline">Cancel</button>
        </div>

        <input type="file" id="fingerprint-file-input" accept="image/*" style="display: none;">
      `;

      modal.appendChild(container);
      document.body.appendChild(modal);

      // Capture from camera button
      document.getElementById('capture-from-camera').onclick = async () => {
        try {
          const stream = await navigator.mediaDevices.getUserMedia({ 
            video: { 
              facingMode: 'environment',
              width: { ideal: 500 },
              height: { ideal: 500 }
            }
          });

          const video = document.getElementById('camera-preview');
          video.srcObject = stream;
          video.style.display = 'block';
          document.getElementById('scan-instructions').style.display = 'none';

          // Capture after 2 seconds
          setTimeout(() => {
            const canvas = document.createElement('canvas');
            canvas.width = video.videoWidth;
            canvas.height = video.videoHeight;
            const ctx = canvas.getContext('2d');
            ctx.drawImage(video, 0, 0);

            // Stop stream
            stream.getTracks().forEach(track => track.stop());

            // Get image data
            canvas.toBlob(blob => {
              const reader = new FileReader();
              reader.onload = (e) => {
                const base64 = e.target.result;
                modal.remove();
                this.state.lastCapture = base64;
                resolve({
                  data: base64,
                  type: 'camera',
                  timestamp: new Date().toISOString()
                });
              };
              reader.readAsDataURL(blob);
            });
          }, 2000);

        } catch (error) {
          console.error('Camera error:', error);
          document.getElementById('scan-instructions').innerHTML = 
            '<p style="color: red;">❌ Camera access denied</p>' +
            '<p style="font-size: 12px; margin-top: 10px;">Use "Upload Image" or connect USB scanner</p>';
        }
      };

      // Upload image button
      document.getElementById('upload-fingerprint').onclick = () => {
        document.getElementById('fingerprint-file-input').click();
      };

      document.getElementById('fingerprint-file-input').onchange = (e) => {
        const file = e.target.files[0];
        if (file) {
          const reader = new FileReader();
          reader.onload = (event) => {
            modal.remove();
            this.state.lastCapture = event.target.result;
            resolve({
              data: event.target.result,
              type: 'file',
              filename: file.name,
              timestamp: new Date().toISOString()
            });
          };
          reader.readAsDataURL(file);
        }
      };

      // Cancel button
      document.getElementById('cancel-scan').onclick = () => {
        modal.remove();
        reject(new Error('Scan cancelled by user'));
      };
    });
  },

  /**
   * Get quality score of captured fingerprint
   */
  async getQualityScore(fingerprintData) {
    try {
      // In real implementation, use computer vision to assess quality
      // For now, return estimated quality based on data size
      if (!fingerprintData) return 0;

      if (typeof fingerprintData === 'string' && fingerprintData.startsWith('data:')) {
        // For base64 data, estimate quality as 75-95%
        return 75 + Math.random() * 20;
      } else if (fingerprintData instanceof ArrayBuffer) {
        // For raw scanner data, estimate based on size
        const size = fingerprintData.byteLength;
        return Math.min(100, (size / 65536) * 100);
      }

      return 70;

    } catch (error) {
      console.error('Quality score error:', error);
      return 0;
    }
  },

  /**
   * Display scan status
   */
  displayStatus(message, type = 'info') {
    const statusEl = document.getElementById('scan-status');
    if (statusEl) {
      statusEl.textContent = message;
      statusEl.className = `status status-${type}`;
    } else {
      console.log(`[${type.toUpperCase()}] ${message}`);
    }
  },

  /**
   * Get scanner info
   */
  getScannerInfo() {
    if (this.config.device) {
      return {
        type: 'USB Fingerprint Scanner',
        status: 'Connected',
        vendorId: `0x${this.config.device.vendorId.toString(16)}`,
        productId: `0x${this.config.device.productId.toString(16)}`
      };
    }
    return {
      type: 'File/Camera Capture',
      status: 'Ready',
      resolution: this.config.resolution
    };
  }
};

// Auto-initialize on page load
window.addEventListener('load', () => {
  FingerprintScanner.initialize().then(success => {
    if (success) {
      console.log('✅ Fingerprint scanner initialized');
      console.log('Scanner Info:', FingerprintScanner.getScannerInfo());
    }
  });
});
