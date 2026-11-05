# Sistem Manajemen Penjualan Hewan Ternak

Aplikasi desktop untuk mengelola usaha peternakan kambing — mulai dari data master, stok, kesehatan, transaksi penjualan, hingga laporan.

| | |
|---|---|
| Nama Project | `SistemManajemenPenjualanHewanTernak` |
| Branding | **Berkah Aamiin Farm** |
| Jenis | Desktop Application (Java Swing) |
| IDE | Apache NetBeans (NetBeans GUI Form / `.form`) |
| Bahasa | Java (source/target 25) |
| Database | MySQL 8 (`sistem_peternakan4`) via JDBC |
| Build | Apache Ant (`build.xml`) + NetBeans |
| Entry Point | `sistemmanajemenpenjualanhewanternak.SistemManajemenPenjualanHewanTernak#main` |
| Total LOC | ±6.500 baris Java (17 form di `views/`) |

---

## 1. Arsitektur

```
SistemManajemenPenjualanHewanTernak   (entry point → cek DB → LoginForm)
├── Utils/
│   ├── Database.java     (koneksi JDBC + auto-create schema + seed dummy data)
│   └── UserSession.java  (session statis: id & nama user yang login)
└── views/                (17 Swing Form: Login, Dashboard, CRUD, transaksi, laporan)
src/report/               (4 template JasperReports .jrxml)
src/images/               (7 ikon menu dashboard)
```

Pola arsitektur: **prosedural per-form**, query SQL langsung di dalam event handler tiap form. Tidak ada DAO/service layer, tidak ada ORM. `UserSession` bekerja sebagai session holder global antar-form.

---

## 2. Fitur

### 2.1 Autentikasi & Manajemen User
- **Login** (`LoginForm`) — validasi username/password ke tabel `user`
- **Session aktif** (`UserSession`) — nama kasir ditampilkan di footer dashboard
- **Tambah User** (`TambahUserForm`) — registrasi akun baru
- **Logout** dari dashboard, session di-reset

### 2.2 Dashboard Admin
`Dashboard_AaminFarm` — JPanel 1280×720, header oranye, 6 menu kartu klikable:

| Menu | Form yang dibuka |
|---|---|
| Data Kambing | `ViewListKambing` |
| Data Supplier | `ViewListSupplier` |
| Data Pelanggan | `DaftarPelanggan` |
| Data Transaksi | `ViewListTransaksi` |
| Riwayat Kesehatan | `RiwayatKesehatan` |
| Manajemen User | `TambahUserForm` |

Ditambah tombol **LOGOUT** di kanan bawah.

### 2.3 Data Kambing (stok)
- **Daftar & pencarian** (`ViewListKambing`) — live search per `kode_kambing` / nama supplier, filter dropdown status (Tersedia / Terjual / Sakit / Mati), tombol *Refresh Data*
- **Tambah kambing** (`FormInputKambing`) — kode, jenis kelamin, berat awal, harga modal, supplier, tanggal masuk (JCalendar), status
- **Edit** (`FormEditKambing`)
- **Detail** — panel `DETAIL KAMBING` / `INFORMASI KAMBING`
- **Hapus** data terpilih
- Status enum: `Tersedia`, `Terjual`, `Sakit`, `Mati`
- Laporan → `LaporanStatistikKambing.jrxml`

### 2.4 Data Supplier
- **CRUD penuh**: `ViewListSupplier` (daftar) → `FormInputSupplier` (tambah) → `FormEditSupplier` (edit), hapus via `DataSupplier`
- Fields: nama supplier, kontak, alamat
- Relasi ke `kambing.id_supplier` (`ON DELETE SET NULL`)

### 2.5 Data Pelanggan
- **CRUD penuh**: `DaftarPelanggan` → `TambahPelanggan` / `UpdatePelanggan`, hapus data terpilih
- Fields: nama, kontak, alamat
- Dipakai sebagai tujuan penjualan pada transaksi (pembeli aqiqah, cantek sate, panitia qurban)

### 2.6 Transaksi Penjualan
`FormTransaksi` — layar penjualan dengan keranjang:

- Form header: pelanggan (dropdown), kasir/user (dropdown), metode pembayaran
- **Keranjang belanja** — tambah kambing ke daftar, atur jumlah per item, harga jual satuan (default dari `harga_modal`, bisa diubah)
- **Auto-kalkulasi total** dan **DP default 20%**
- Format Rupiah pada kolom harga (`Rp 2.200.000`)
- Validasi uang: DP tidak boleh melebihi total, pelunasan tidak boleh kurang
- Simpan transaksi = 3 langkah dalam 1 proses:
  1. `INSERT` master `transaksi` (+ generate `nomor_invoice`)
  2. `INSERT` `riwayat_pembayaran` pertama
  3. `INSERT` `detail_transaksi` + `UPDATE` status kambing → `Terjual`
- Status pembayaran: `Lunas`, `DP`, `Batal`
- Kambing yang sudah terjual otomatis tidak bisa dipilih lagi

### 2.7 Riwayat Transaksi
`ViewListTransaksi`:

- **Daftar transaksi** dengan cari (nomor invoice / pelanggan) dan reset filter
- **Tambah Pembayaran / Lunasi** — mendukung **cicilan (multiple down payment)**; setiap pembayaran masuk ke tabel `riwayat_pembayaran`, sisa tagihan dihitung realtime dari `SUM(nominal_masuk)`
- **Lihat Detail** transaksi
- **Cetak Invoice** → `InvoicePembelian.jrxml`
- **Cetak Laporan Periode** → `LaporanPenjualan.jrxml` (pilih rentang tanggal via dialog)

### 2.8 Riwayat Kesehatan
- **RiwayatKesehatan** — daftar pemeriksaan kesehatan seekor kambing, pencarian berdasarkan kode kambing, tambah (`FormInputRiwayat`), hapus, *Cetak Laporan* dengan parameter periode
- Fields per pemeriksaan: tanggal pemeriksaan, diagnosa/keterangan/tindakan, berat saat ini
- Status `Sakit` bisa disinkronkan dari riwayat kesehatan
- Laporan → `LaporanKesehatan.jrxml`

### 2.9 Laporan (JasperReports)
4 template di `src/report/`, di-render langsung dari form via `JasperCompileManager` + `JasperFillManager`, lalu dialog preview / simpan ke file:

| File | Dipicu dari |
|---|---|
| `LaporanStatistikKambing.jrxml` | ViewListKambing → *Buat Laporan* |
| `LaporanKesehatan.jrxml` | RiwayatKesehatan → *Cetak Laporan* |
| `LaporanPenjualan.jrxml` | ViewListTransaksi → *Cetak Laporan Periode* |
| `InvoicePembelian.jrxml` | ViewListTransaksi → *Cetak Invoice* |

---

## 3. Skema Database

Seluruh tabel dibuat otomatis oleh `Database.initSchema()` saat aplikasi pertama dijalankan (`CREATE TABLE IF NOT EXISTS`), jadi tidak perlu import SQL manual.

```
pelanggan (id_pelanggan, nama_pelanggan, kontak, alamat)
user      (id_user, username, password, nama)
supplier_kambing (id_supplier, nama_supplier, kontak, alamat)
    │
    └─< kambing (id_kambing, kode_kambing UNIQUE, id_supplier→supplier,
                  jenis_kelamin ENUM, berat_awal, harga_modal,
                  status ENUM, tanggal_masuk)
            │                              │
            │                              └─< detail_transaksi (harga_jual_satuan)
            └─< riwayat_kesehatan (tanggal_pemeriksaan, keterangan, berat_saat_ini)

transaksi (id_transaksi, id_pelanggan→pelanggan, id_user→user,
           nomor_invoice UNIQUE, tanggal_transaksi,
           status_pembayaran ENUM, metode_pembayaran, uang_dibayar)
    │
    ├─< detail_transaksi (id_kambing→kambing)      [ON DELETE CASCADE / RESTRICT]
    └─< riwayat_pembayaran (nomor_invoice, tanggal_bayar,
                            nominal_masuk, keterangan)
```

### Dummy data otomatis
Bila tabel kosong, `Database.initDummyData()` mengisi:
- 3 supplier (Peternakan Berkah Jonggol, Saudagar Kambing Bekasi, Kambing Etawa Maju)
- 3 pelanggan (Bapak Budi/Aqiqah, Warung Sate Tegal Pak Kumis, Mas Yanto/Qurban)
- 6 kambing `KMB-2026-001` … `KMB-2026-006` (status.available, terjual, sakit)
- 1 user: `admin` / `admin123`

---

## 4. Library

| JAR | Versi | Kegunaan |
|---|---|---|
| mysql-connector-j | 9.6.0 | Driver MySQL |
| jasperreports | 6.21.3 | Generate PDF laporan & invoice |
| jcalendar | 0.8.1 | Popup pilihan tanggal |
| commons-logging / collections / beanutils / digester | 1.1.1 / 4.2 / 1.9.4 / 2.1 | Dependency JasperReports |

Semua JAR berada di `src/sistemmanajemenpenjualanhewanternak/lib/` dan sudah terdaftar di `nbproject/project.properties`.

---

## 5. Cara Menjalankan

**Prasyarat:** JDK 25, NetBeans IDE, MySQL/XAMPP aktif.

1. Jalankan MySQL, lalu **biarkan aplikasi membuat database & tabel sendiri**.
2. Buka project di NetBeans → **Run Project** (F6), atau lewat Ant:
   ```bash
   ant run
   ```
3. Login dengan `admin` / `admin123`.
4. Login gagal / popup error database → pastikan service MySQL menyala dan port `3306` sesuai.

Konfigurasi koneksi ada di `src/sistemmanajemenpenjualanhewanternak/Utils/Database.java`:

```java
private static final String URL  = "jdbc:mysql://localhost:3306/sistem_peternakan4";
private static final String USER = "root";
private static final String PASS = "";
```

---

## 6. Catatan Teknis /-gap yang Diketahui

Hal-hal yang perlu diperhatikan kalau mau melanjutkan pengembangan atau demo:

- **Kredensial hardcoded** — `root` tanpa password di dalam source. Tidak aman untuk produksi.
- **Password user disimpan plaintext** — tidak ada hashing (bcrypt/SHA) di tabel `user`.
- **Tidak ada validasi role/otorisasi** — semua user yang login otomatis punya akses penuh ke semua menu, padahal tabel sudah menyimpan `id_user`.
- **Logic bisnis tersebar di form** — SQL mentah di dalam event handler Swing, tidak ada DAO. Sulit diuji ulang dan dipakai kembali.
- **Tidak ada unit test** — folder `test/` kosong.
- **Tanpa migrasi** — skema dikelola manual lewat `CREATE TABLE IF NOT EXISTS`, jadi perubahan kolom pada tabel yang sudah ada tidak akan ter-apply ke instalasi lama.
- **Input transaksi kurang aman** — belum ada validasi tanggal, masih memakai `Statement` di beberapa tempat (bukan `PreparedStatement`).
- **`.DS_Store` dan `dist/` ikut ter-commit** di repository.

---

*Terakhir diupdate berdasarkan kondisi repository saat ini.*