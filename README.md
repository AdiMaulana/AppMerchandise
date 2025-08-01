# AppMerchandise

*UAS Mobile Programming 1 - Merchandise App*

---

## Overview

AppMerchandise adalah aplikasi Android yang dibuat sebagai proyek tugas mata kuliah pemrograman mobile. Aplikasi ini menyediakan platform e-commerce sederhana untuk pengguna agar dapat melihat dan membeli merchandise band seperti kaos dan aksesori. Data pengguna, katalog merchandise, serta riwayat transaksi pembelian disimpan menggunakan SQLite secara lokal.

---

## Features

- Registrasi dan login user dengan verifikasi password
- Tampilan daftar merchandise dengan deskripsi, harga, dan stok
- Pembelian merchandise dengan pemilihan jumlah
- Konfirmasi pembelian yang aman melalui input password
- Penyimpanan transaksi pembelian dengan nomor pesanan, tanggal, detail pembeli, dan alamat pengiriman
- Otomatis update stok setelah pembelian berhasil
- Halaman receipt sebagai bukti pembelian
- Implementasi UI menggunakan Kotlin dan Jetpack Compose yang modular

---

## Screenshots

<!--
<div style="display: flex; flex-wrap: wrap; justify-content: space-between;">

  <img src="https://raw.githubusercontent.com/AdiMaulana/AppMerchandise/refs/heads/master/capture/1.registrasi.jpg" alt="Screenshot 1" width="23%" />
  <img src="https://raw.githubusercontent.com/AdiMaulana/AppMerchandise/refs/heads/master/capture/2.%20login.jpg" alt="Screenshot 2" width="23%" />
  <img src="https://raw.githubusercontent.com/AdiMaulana/AppMerchandise/refs/heads/master/capture/3.%20list%20view.jpg" alt="Screenshot 3" width="23%" />
  <img src="https://raw.githubusercontent.com/AdiMaulana/AppMerchandise/refs/heads/master/capture/4.%20detail%20item.jpg" alt="Screenshot 4" width="23%" />

  <img src="https://raw.githubusercontent.com/AdiMaulana/AppMerchandise/refs/heads/master/capture/5.%20beli.jpg" alt="Screenshot 5" width="23%" style="margin-top: 16px;" />
  <img src="https://raw.githubusercontent.com/AdiMaulana/AppMerchandise/refs/heads/master/capture/6.%20konfirmasi.jpg" alt="Screenshot 6" width="23%" style="margin-top: 16px;" />
  <img src="https://raw.githubusercontent.com/AdiMaulana/AppMerchandise/refs/heads/master/capture/7.%20check%20password.jpg" alt="Screenshot 7" width="23%" style="margin-top: 16px;" />
  <img src="https://raw.githubusercontent.com/AdiMaulana/AppMerchandise/refs/heads/master/capture/8.%20resi.jpg" alt="Screenshot 8" width="23%" style="margin-top: 16px;" />

</div>
-->


<div style="display: flex; flex-wrap: nowrap; justify-content: space-between; align-items: center;">
  <img src="https://raw.githubusercontent.com/AdiMaulana/AppMerchandise/refs/heads/master/capture/1.registrasi.jpg" alt="Screenshot 1" width="20%" />
  <img src="https://raw.githubusercontent.com/AdiMaulana/AppMerchandise/refs/heads/master/capture/2.%20login.jpg" alt="Screenshot 2" width="20%" />
  <img src="https://raw.githubusercontent.com/AdiMaulana/AppMerchandise/refs/heads/master/capture/3.%20list%20view.jpg" alt="Screenshot 3" width="20%" />
  <img src="https://raw.githubusercontent.com/AdiMaulana/AppMerchandise/refs/heads/master/capture/4.%20detail%20item.jpg" alt="Screenshot 4" width="20%" />
  <img src="https://raw.githubusercontent.com/AdiMaulana/AppMerchandise/refs/heads/master/capture/5.%20beli.jpg" alt="Screenshot 5" width="20%"/>
  <img src="https://raw.githubusercontent.com/AdiMaulana/AppMerchandise/refs/heads/master/capture/6.%20konfirmasi.jpg" alt="Screenshot 6" width="20%"/>
  <img src="https://raw.githubusercontent.com/AdiMaulana/AppMerchandise/refs/heads/master/capture/7.%20check%20password.jpg" alt="Screenshot 7" width="20%"/>
  <img src="https://raw.githubusercontent.com/AdiMaulana/AppMerchandise/refs/heads/master/capture/8.%20resi.jpg" alt="Screenshot 8" width="20%"/>
</div>

---

## Architecture & Components

- Bahasa Kotlin dan Jetpack Compose untuk UI
- SQLite sebagai database lokal dengan tabel:
  - `m_merchandise` untuk data produk merchandise
  - `m_users` untuk data pengguna
  - `t_transaction` untuk histori transaksi
- `AppDatabaseHelper` sebagai class pengelola database (create, upgrade, CRUD)
- Validasi password sebelum simpan transaksi
- Pemisahan activity sesuai fungsi: `LoginActivity`, `DashboardActivity`, `PurchasePasswordActivity`, `PurchaseReceiptActivity`

---

## Getting Started

### Prerequisites

- Android Studio Bumblebee atau yang lebih baru
- Android SDK dengan minimum API 21 (Lollipop) atau lebih tinggi
- Kotlin versi 1.7+ (atau kompatibel)

### Build & Run

1. Clone repository ini:
   git clone https://github.com/AdiMaulana/AppMerchandise.git
   cd AppMerchandise


2. Buka project di Android Studio

3. Build dan jalankan aplikasinya di emulator atau perangkat fisik

4. Daftar user baru atau login dengan akun yang sudah ada

5. Jelajahi merchandise, lakukan pembelian, dan selesaikan pembelian dengan verifikasi password

---

## Project Structure

- `/app/src/main/java/com/ridexone/appmerchandise`
- `AppDatabaseHelper.kt`: Kelas helper database terpadu mengelola semua tabel
- Aktivitas untuk login, dashboard, input password pembelian, dan tampilan bukti pembelian
- `/app/src/main/res/`
- Resource aplikasi seperti layouts, tema, dan icon

---

## Contributing

Proyek ini dibuat untuk keperluan akademik/mata kuliah.  
Silakan lakukan fork dan ajukan pull request untuk perbaikan atau penambahan fitur.

---

## Contact

Dibuat oleh [Adi Maulana](https://github.com/AdiMaulana)  
Jika ada pertanyaan atau masukan, silakan buka issue di repo ini.


