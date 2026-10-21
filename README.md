# BlueHub: Universal Bluetooth Controller & IoT Hub

Aplikasi Android serbaguna berbasis **Kotlin 2.0+** dan **Jetpack Compose** untuk mengontrol, menghubungkan, dan memantau perangkat Bluetooth (Classic SPP & Bluetooth Low Energy / BLE).

---

## 🚀 Fitur Utama

1. **Bluetooth Discovery & Scanner**:
   * Pemindaian simultan BLE dan Classic Discovery.
   * Filter kategori (IoT/Sensor, Printer Termal, Gamepad/HID, Serial/Kustom).
   * Pemantauan sinyal RSSI real-time dan status pairing.

2. **IoT & Sensor Telemetry Dashboard**:
   * Visualisasi grafik telemetri real-time yang responsif (hingga 10 Hz tanpa lag antarmuka).
   * Kontrol sakelar aktuator (Relay 1, Relay 2, LED Dimmer PWM).
   * Engine aturan automasi nilai ambang batas (*Rule Engine*).

3. **Printer Termal (ESC/POS)**:
   * Dukungan format kertas 58mm & 80mm.
   * Editor teks struk belanja dinamis dengan pratinjau struk nyata.
   * Fitur Self-Test print dan konversi perintah raw ESC/POS.

4. **Gamepad & HID Tester**:
   * Visualisasi pergerakan analog stick ganda (LS/RS) dengan normalisasi deadzone.
   * Deteksi penekanan tombol A, B, X, Y, Triggers (L1/R1/L2/R2), dan D-Pad.
   * Pengukur latensi input ($ms$) dan pembaca KeyCode perangkat keras.

5. **Terminal Serial & GATT Browser**:
   * Monitor data log masuk/keluar secara real-time.
   * Dukungan format ASCII & HEX.
   * Konfigurasi line terminator (CR+LF, LF, CR, None).
   * Tombol makro instan (`AT`, `PING`, `STATUS`, dll.).

---

## 🛠️ Tech Stack & Arsitektur

* **Bahasa:** Kotlin 2.0+
* **UI Framework:** Jetpack Compose + Material 3 Design
* **Arsitektur:** Clean Architecture + MVVM
* **Dependency Injection:** Dagger Hilt
* **Asynchronous:** Kotlin Coroutines & `StateFlow` / `SharedFlow`
* **BLE Core:** Nordic Semiconductor Android BLE Library
* **Serial SPP:** Native Android `BluetoothSocket` dengan Coroutine stream
* **Background Keep-Alive:** Android Foreground Service (`FOREGROUND_SERVICE_CONNECTED_DEVICE`) untuk Android 14+
* **Local Storage:** Room Database & Jetpack DataStore

---

## 📂 Cara Membuka Proyek di Android Studio

1. Buka **Android Studio** (Koala / Ladybug atau versi terbaru yang mendukung AGP 8.7+).
2. Pilih **Open** -> Navigasikan ke folder `D:\PROJECT CODING\BLUEHUB`.
3. Biarkan Android Studio menyelesaikan Gradle Sync pertama kali.
4. Hubungkan perangkat Android fisik atau jalankan di Emulator (disarankan perangkat fisik dengan Bluetooth aktif untuk pengujian perangkat keras).
