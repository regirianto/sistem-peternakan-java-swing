# Input dan Output Sistem

Dokumen ini menjelaskan data yang dimasukkan ke dalam aplikasi **Sistem Manajemen Penjualan Hewan Ternak (Berkah Aamiin Farm)** dan data yang dihasilkan kembali kepada pengguna.

---

## 1. Input

Semua data berikut diisi oleh pengguna (admin atau kasir) melalui halaman-halaman yang tersedia di aplikasi, lalu disimpan sebagai arsip data agar dapat digunakan kembali di kemudian hari.

| No. | Jenis Data | Keterangan Isian |
|---|---|---|
| 1 | **Data Login dan Pengguna** | Nama pengguna, nama lengkap, dan kata sandi untuk masuk ke aplikasi |
| 2 | **Data Pelanggan** | Nama pelanggan, nomor telepon, dan alamat |
| 3 | **Data Supplier** | Nama supplier, nomor telepon, dan alamat |
| 4 | **Data Kambing** | Kode kambing, asal supplier, jenis kelamin, berat awal, harga modal, kondisi kambing, dan tanggal masuk |
| 5 | **Data Riwayat Kesehatan** | Kambing yang diperiksa, tanggal pemeriksaan, keterangan atau tindakan, dan berat saat pemeriksaan |
| 6 | **Data Pemesanan dan Transaksi** | Nama pelanggan, nama kasir yang melayani, nomor nota, tanggal transaksi, metode pembayaran, jumlah yang dibayar, serta daftar kambing yang dibeli beserta harga jualnya |
| 7 | **Data Pembayaran** | Nomor nota transaksi, tanggal pembayaran, jumlah yang dibayar, dan keterangan pembayaran |
| 8 | **Data Laporan** | Periode laporan yang akan ditampilkan pada halaman cetak laporan |

### Penjelasan singkat

- **Data Login dan Pengguna** dipakai untuk menjaga aplikasi agar tidak bisa dibuka oleh orang yang tidak berhak. Data awal sudah disediakan sistem, sehingga pengguna dapat masuk tanpa perlu mendaftar lebih dulu.
- **Data Pelanggan dan Supplier** merupakan data utama yang wajib diisi sebelum melakukan transaksi. Data pelanggan menyimpan siapa yang membeli, sedangkan data supplier menyimpan dari mana kambing diperoleh.
- **Data Kambing** berisi informasi tentang setiap ekor kambing yang dimiliki peternakan, mulai dari berat dan harga beli dari supplier hingga kondisi kesehatannya saat ini.
- **Data Riwayat Kesehatan** digunakan untuk mencatat pemeriksaan kesehatan yang dilakukan dari waktu ke waktu, sehingga riwayat kesehatan seekor kambing tetap dapat dilihat.
- **Data Pemesanan dan Transaksi** diisi pada halaman transaksi penjualan. Pengguna memilih pelanggan, memasukkan kambing yang dibeli, lalu sistem menghitungkan total harga secara otomatis.
- **Data Pembayaran** digunakan ketika pembayaran dilakukan secara bertahap. Setiap pembayaran yang masuk akan tercatat beserta tanggal dan jumlahnya, sehingga sistem dapat mengetahui berapa sisa yang belum dibayar.
- **Data Laporan** diisi pada saat pengguna menekan tombol cetak laporan. Periode yang dipilih akan menentukan data mana saja yang dimunculkan pada laporan.

---

## 2. Proses

```
Input Data  →  Pemeriksaan Kelengkapan Data  →  Penyimpanan Data
Tampil di Daftar  →  Cetak Laporan
```

- Setiap data yang masuk diperiksa terlebih dahulu. Jika ada isian yang kosong atau tidak sesuai, sistem akan menampilkan pemberitahuan dan data belum tersimpan.
- Data yang sudah dinyatakan benar akan disimpan dan langsung muncul pada halaman daftar, sehingga pengguna tidak perlu memasukkan data yang sama dua kali.
- Pada saat transaksi disimpan, sistem otomatis mencatat transaksi, mencatat pembayaran yang pertama, dan mengubah kondisi kambing yang dibeli menjadi "terjual".

---

## 3. Output

Data atau informasi yang dihasilkan sistem sebagai hasil dari masukan pengguna.

| No. | Jenis Output | Keterangan |
|---|---|---|
| 1 | **Informasi Data Pelanggan** | Menampilkan daftar pelanggan yang sudah terdaftar beserta nomor telepon dan alamatnya |
| 2 | **Informasi Data Supplier** | Menampilkan daftar supplier beserta nomor telepon dan alamatnya |
| 3 | **Informasi Data Kambing** | Menampilkan daftar kambing beserta kode, asal supplier, jenis kelamin, berat, harga modal, kondisi, dan tanggal masuk |
| 4 | **Informasi Riwayat Kesehatan** | Menampilkan riwayat pemeriksaan kesehatan seekor kambing beserta tanggal, keterangan, dan berat saat pemeriksaan |
| 5 | **Bukti Transaksi atau Nota** | Menampilkan rincian pembelian pelanggan berupa nomor nota, tanggal, daftar kambing yang dibeli, total harga, dan sisa pembayaran |
| 6 | **Informasi Pembayaran** | Menampilkan riwayat setiap pembayaran yang pernah dilakukan beserta tanggal, jumlah, dan keterangannya |
| 7 | **Laporan Penjualan** | Menampilkan rekap seluruh transaksi yang terjadi pada periode yang dipilih |
| 8 | **Laporan Data Kambing** | Menampilkan rekap jumlah dan kondisi kambing yang tersedia |
| 9 | **Laporan Kesehatan** | Menampilkan rekap riwayat kesehatan kambing pada periode yang dipilih |

---

## 4. Output Berupa Berkas PDF

Selain daftar tabel di dalam aplikasi, sistem juga menghasilkan keluaran berupa **berkas PDF**. Bagian ini merupakan output dokumen yang dapat dicetak, disimpan, maupun dikirimkan kepada pihak lain.

### 4.1 Daftar berkas PDF yang dihasilkan

| No. | Nama Berkas | Isi | Kapan Dihasilkan |
|---|---|---|---|
| 1 | **Nota Pembelian** | Rincian belanja pelanggan: nomor nota, tanggal, nama pelanggan, daftar kambing yang dibeli, harga jual, total, dan sisa pembayaran yang harus dilunasi | Dari halaman Data Transaksi, setelah memilih salah satu transaksi |
| 2 | **Laporan Penjualan** | Rekap seluruh transaksi penjualan, nama pelanggan, dan total pembayaran pada periode yang dipilih | Dari halaman Data Transaksi, setelah memilih rentang tanggal |
| 3 | **Laporan Data Kambing** | Rekap jumlah dan kondisi kambing yang tersedia | Dari halaman Data Kambing |
| 4 | **Laporan Kesehatan** | Rekap riwayat pemeriksaan kesehatan kambing pada periode yang dipilih | Dari halaman Riwayat Kesehatan, setelah memilih kode kambing dan periode |

### 4.2 Kegunaan berkas PDF

- Dicetak sebagai arsip atau bukti transaksi penjualan
- Disimpan di dalam komputer sebagai cadangan data
- Dikirim kepada pelanggan atau pihak lain melalui aplikasi pesan singkat atau email
- Dipakai sebagai bahan lampiran laporan atau tugas akhir

