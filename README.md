# Scan Wizard  

## Overview  
**Scan Wizard** is an intuitive QR code scanning application designed for seamless functionality and user convenience. The app leverages Google’s **ML Kit** for QR code detection and includes enhanced features such as flashlight controls, gallery QR scanning, and a detailed scan history. With robust permission handling and a user-friendly design, Scan Wizard offers an efficient way to scan, process, and manage QR codes.  

---

## Features  

### 1. **Landing Page (Camera Screen)**  
- Accesses the device’s camera to scan QR codes in real time.  
- **Bottom Navigation Bar**:  
  - **Flashlight Control**: Toggle flashlight for low-light conditions.  
  - **Zoom Control**: Adjust camera zoom for better focus.  
  - **Gallery Access**: Select images from the gallery to scan QR codes (requires gallery permissions).  

### 2. **Scanned Page**  
- Displays the content of the scanned QR code.  
- Provides specific actions based on the type of content:  
  - **URLs**: Open directly in the browser.  
  - **Images**: Save to the device.  
  - **Text**: Copy or share the content.  
- Simple options to **share** or **copy** QR code data.  

### 3. **History Page**  
- Maintains a history of all scanned QR codes using SQLite3 for local storage.  
- Allows users to:  
  - **Favorite** scans for quick access.  
  - View, edit, or delete scanned history records.  

---

## Permissions Required  
- **Camera Permission**: To scan QR codes in real time.  
- **Gallery Permission**: To scan QR codes from saved images.  

---

## Technology Stack  
- **Programming Language**: Kotlin  
- **QR Code Detection**: Google ML Kit  
- **Database**: SQLite3  
- **UI Framework**: Jetpack Compose (or XML-based layouts)  

---

## How to Use  
1. **Launch the App**: Grant camera and gallery permissions when prompted.  
2. **Scan QR Codes**:  
   - Use the camera to scan QR codes or select an image from the gallery.  
   - Adjust flashlight or zoom as needed.  
3. **View Scanned Content**: Perform context-specific actions (e.g., open links, save images, copy text).  
4. **Access History**: View all scans in the history tab, mark important scans as favorites, or manage records.  

---

## Future Enhancements  
- **Cloud Sync**: Allow users to sync their scan history across devices.  
- **Dark Mode**: Add a dark theme for better usability in low-light environments.  
- **Multi-Language Support**: Support for additional languages for a global audience.  
- **Barcode Scanning**: Expand capabilities to include barcodes and other formats.  

---

## Installation Instructions  
1. Clone the repository:  
   ```bash
   git clone https://github.com/username/scan-wizard.git
   ```  
2. Open the project in Android Studio.  
3. Build and run the project on your Android device or emulator.  

---

## Acknowledgments  
Special thanks to **Google ML Kit** for providing robust QR code detection tools and to the open-source community for inspiration and support.  

---  
