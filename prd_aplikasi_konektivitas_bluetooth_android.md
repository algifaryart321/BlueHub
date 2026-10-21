# Product Requirement Document (PRD)

## Aplikasi Android: Universal Bluetooth Controller & IoT Hub

## 1. Informasi Dokumen

* **Nama Produk (Sementara):** BlueHub / BT Connect Master

* **Platform:** Android (Android 8.0 API 26 hingga Android 14+ API 34+)

* **Versi Dokumen:** 1.1

* **Status:** Draft / Proposal (Updated with IoT & Universal Device Scope)

## 2. Gambaran Umum Produk (Executive Summary)

Aplikasi **Universal Bluetooth Controller & IoT Hub** adalah platform Android serbaguna yang dirancang untuk menghubungkan, mengontrol, dan memantau berbagai jenis perangkat Bluetooth (Bluetooth Classic & Bluetooth Low Energy / BLE).

Aplikasi ini bertindak sebagai *hub* serba ada dengan dukungan khusus untuk:

1. **Perangkat IoT & Smart Devices:** Pengambilan data sensor (suhu, kelembapan, detak jantung, timbangan pintar, OBD2 kendaraan, dll.), kontrol aktuator (sakelar, relay, lampu LED), dan pemantauan telemetri real-time.

2. **Printer Termal:** Pencetakan struk, label, cetak gambar, dan pengiriman perintah ESC/POS.

3. **Input Devices (Gamepad, Joystick, Keyboard, Mouse):** Pemetaan tombol (key mapping), tes input/latensi, dan manajemen profil kontroler.

4. **Perangkat Serial & Kustom:** Terminal data raw (SPP/GATT) untuk komunikasi langsung dengan mikrokontroler (ESP32, Arduino, HC-05/06) dan modul kustom lainnya.

## 3. Masalah & Tujuan (Problem & Goals)

### Masalah Pengguna:

* Fragmentasi aplikasi: Pengguna harus menginstal aplikasi terpisah untuk printer termal, gamepad mapping, dan kontrol perangkat IoT/mikrokontroler.

* Sulitnya memantau data dari modul IoT kustom atau alat kesehatan/olahraga Bluetooth tanpa aplikasi bawaan vendor.

* Kesulitan pengguna dalam mendiagnosis masalah koneksi atau melakukan *debugging* data transmisi Bluetooth.

### Tujuan Utama:

* Menyediakan **satu platform terpadu** untuk semua ekosistem periferal dan perangkat IoT Bluetooth.

* Menyediakan dasbor visualisasi data telemetri IoT yang dapat disesuaikan (grafik, *gauge*, sakelar).

* Menjamin stabilitas koneksi dengan fitur *auto-reconnect*, pemindaian latar belakang, dan pengisian ulang profil otomatis.

## 4. Target Pengguna (Target Audience)

1. **Pengembang IoT & Hobbyist Elektronika:** Membutuhkan alat pengujian, dasbor data sensor, dan pemantau serial (Arduino/ESP32).

2. **Pelaku UMKM / Kasir:** Membutuhkan kemudahan cetak struk via printer termal Bluetooth.

3. **Gamer Mobile:** Menggunakan gamepad, joystick, atau keyboard/mouse Bluetooth.

4. **Pengguna Smart Home & Health Tech:** Memantau sensor lingkungan, alat kesehatan BLE (heart rate monitor, pulse oximeter), atau adaptor kendaraan OBD2.

## 5. Persyaratan Fitur Utama (Functional Requirements)

### 5.1. Modul Manajemen Bluetooth Universal

* **Device Discovery & Scanner:**

  * Pemindaian simultan untuk Bluetooth Classic dan BLE.

  * Filter berdasarkan kategori perangkat (Printer, HID/Input, IoT/Sensor, Audio, Unknown/Custom).

  * Indikator kekuatan sinyal real-time (RSSI) dan statistik latensi.

* **Pairing & Connection Manager:**

  * *Pairing* instan dari dalam aplikasi.

  * Manajemen profil koneksi (*Auto-Reconnect* saat dalam jangkauan).

  * Pemantauan status baterai perangkat (Bluetooth Battery Service).

### 5.2. Modul IoT & Smart Devices (Baru)

* **Dashboard Telemetri Sensor:**

  * Komponen visual: Grafik garis real-time, *gauge meter*, indikator numerik, dan status lampu/relay.

  * Dukungan profil standar BLE: *Heart Rate Service*, *Health Thermometer*, *Battery Service*, *Environmental Sensing*.

* **Kontrol Aktuator & Smart Switch:**

  * Tombol sakelar (*toggle button*), *slider* (kontrol reduksi/kecepatan), dan *joystick UI* untuk mengendalikan perangkat IoT secara langsung.

* **Rule Engine & Automasi Sederhana:**

  * Opsi pemicu otomatis, contoh: "Jika nilai sensor suhu $> 30^\circ\text{C}$, kirim perintah matikan via Bluetooth".

* **Integrasi OBD2 & Vehicle Diagnostic (Optional Extension):**

  * Pembacaan kode error dasar dan parameter mesin kendaraan via adaptor Bluetooth ELM327.

### 5.3. Modul Printer Termal (ESC/POS)

* **Kompatibilitas:** Driver standar ESC/POS untuk ukuran kertas 58mm dan 80mm.

* **Fitur Cetak Struk & Dokumen:**

  * Editor teks langsung (ukuran, format cetak, opsi tebal).

  * Konversi gambar ke cetakan monokrom (*dithering*).

  * Cetak dokumen PDF, halaman web, serta pembuatan QR Code / Barcode instan.

* **Template Struk Kustom & Diagnostik:** Template kasir standar dan tombol *Self-test print*.

### 5.4. Modul Input Device (Gamepad, Joystick, Keyboard, Mouse)

* **Visual Input Tester:**

  * Tes visual untuk tombol gamepad (A, B, X, Y, Trigger, D-Pad, Analog Stick).

  * Tes pergerakan kursor mouse, *polling rate*, serta deteksi *Keycode* keyboard.

* **Key Mapper & Sensitivity:**

  * Opsi pemetaan tombol kustom dan penyesuaian *deadzone* analog joystick.

* **Pengujian Latensi:** Pengukuran *delay* transmisi input dalam milidetik ($ms$).

### 5.5. Modul Terminal Bluetooth Serial & BLE GATT

* **Terminal SPP (Serial Port Profile):**

  * Pengiriman dan penerimaan data raw (ASCII/HEX).

  * Pengaturan Karakter Akhir (CR/LF).

* **BLE GATT Browser & Inspector:**

  * Penjelajah hirarki Service, Characteristic, dan Descriptor.

  * Fitur Read, Write, dan Notify/Indicate pada nilai Karakteristik.

* **Export Log:** Ekspor riwayat komunikasi data ke format `.txt`, `.csv`, atau `.json`.

## 6. Persyaratan Non-Fungsional (Non-Functional Requirements)

* **Performa:**

  * Penundaan transmisi (latency) input gamepad < $20\text{ ms}$.

  * *Refresh rate* grafik telemetri IoT hingga $10\text{ Hz}$ tanpa lag UI.

* **Efisiensi Daya:** Penggunaan daya rendah pada background service pemantauan IoT.

* **Keamanan:**

  * Pemrosesan data secara lokal pada perangkat tanpa pengiriman data sensor ke server pihak ketiga tanpa izin.

  * Penanganan izin Bluetooth & Lokasi sesuai panduan privasi Android.

* **Ukuran Aplikasi:** Ringan, target < $35\text{ MB}$.

## 7. Kebutuhan Teknis & Izin Android (Technical & Permissions)

### Minimal & Target SDK:

* **Min SDK:** API 26 (Android 8.0 Oreo)

* **Target SDK:** API 34+ (Android 14)

### Draf Izin Manifest (Android Permissions):

* `BLUETOOTH` & `BLUETOOTH_ADMIN` (Compat Android < 12)

* `BLUETOOTH_SCAN` (`neverForLocation` jika memungkinkan)

* `BLUETOOTH_CONNECT`

* `BLUETOOTH_ADVERTISE`

* `ACCESS_FINE_LOCATION` / `ACCESS_COARSE_LOCATION`

* `FOREGROUND_SERVICE` & `FOREGROUND_SERVICE_CONNECTED_DEVICE` (Untuk koneksi IoT latar belakang di Android 14)

## 8. Desain Antarmuka (UI/UX)

* **Dashboard Utama:**

  * Bottom Navigation Bar: **Devices**, **IoT Dashboard**, **Printer**, **Gamepad/HID**, **Terminal**.

  * Status koneksi aktif dalam bentuk *persistent banner* di atas layar.

* **Pengalaman Pengguna (UX):**

  * Drag-and-Drop widget pada dasbor IoT untuk mempermudah kustomisasi tampilan sensor.

  * Dukungan Tema Gelap (Dark Mode) dan Terang (Light Mode)

|  | 
| ----- | 

## 10. Indikator Keberhasilan (KPIs)

* Tingkat keberhasilan *pairing* dan stabilitas koneksi terhubung > $95\%$.

* Penilaian aplikasi di Play Store $\ge 4.5$.

* Tingkat *crash* aplikasi < $0.1\%$.