/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistemmanajemenpenjualanhewanternak.Utils;

import java.sql.*;
import javax.swing.JOptionPane;


/**
 *
 * @author regi.suyono
 */
public class Database {
    private static Connection koneksi;
    
    // Ganti sesuai dengan database kampus kalian
    private static final String URL = "jdbc:mysql://localhost:3306/sistem_peternakan";
    private static final String USER = "root";
    private static final String PASS = "P@ssw0rd"; 

    public static Connection getKoneksi() {
        try {
            // PERUBAHAN DISINI: Tambahkan pengecekan apakah koneksi sudah tertutup (isClosed)
            // Menggunakan || (OR), jadi jika null ATAU sudah ditutup, dia akan buat koneksi baru
            if (koneksi == null || koneksi.isClosed()) {
                
                // Register driver (opsional di versi JDBC baru, tapi aman untuk boilerplate)
                Class.forName("com.mysql.cj.jdbc.Driver");
                koneksi = DriverManager.getConnection(URL, USER, PASS);
                
                initSchema(koneksi);
                
                initDummyData(koneksi);
                
                System.out.println("Koneksi Database Berhasil!");
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Koneksi Gagal: " + e.getMessage());
            // Munculkan popup agar pemula langsung tahu kalau XAMPP/MySQL mereka belum nyala
            JOptionPane.showMessageDialog(null, 
                "Gagal koneksi ke database! Pastikan XAMPP/MySQL sudah menyala.\nError: " + e.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
        return koneksi;
    }
    private static void initSchema(Connection conn) {
    // Menggunakan try-with-resources untuk Statement agar otomatis tertutup setelah selesai
    try (Statement stmt = conn.createStatement()) {
        
        // 1. Tabel Master: pelanggan
        String sqlPelanggan = "CREATE TABLE IF NOT EXISTS pelanggan ("
                + "id_pelanggan INT AUTO_INCREMENT PRIMARY KEY, "
                + "nama_pelanggan VARCHAR(100) NOT NULL, "
                + "kontak VARCHAR(20), "
                + "alamat TEXT"
                + ")";
        stmt.execute(sqlPelanggan);

        // 2. Tabel Master: user
        String sqlUser = "CREATE TABLE IF NOT EXISTS user ("
                + "id_user INT AUTO_INCREMENT PRIMARY KEY, "
                + "username VARCHAR(50) NOT NULL UNIQUE, "
                + "password VARCHAR(255) NOT NULL, "
                + "nama VARCHAR(100) NOT NULL"
                + ")";
        stmt.execute(sqlUser);

        // 3. Tabel Master: supplier_kambing
        String sqlSupplier = "CREATE TABLE IF NOT EXISTS supplier_kambing ("
                + "id_supplier INT AUTO_INCREMENT PRIMARY KEY, "
                + "nama_supplier VARCHAR(100) NOT NULL, "
                + "kontak VARCHAR(20), "
                + "alamat TEXT"
                + ")";
        stmt.execute(sqlSupplier);

        // 4. Tabel Relasi 1: kambing (Bergantung pada supplier_kambing)
        String sqlKambing = "CREATE TABLE IF NOT EXISTS kambing ("
                + "id_kambing INT AUTO_INCREMENT PRIMARY KEY, "
                + "kode_kambing VARCHAR(50) NOT NULL UNIQUE, "
                + "id_supplier INT, "
                + "jenis_kelamin ENUM('Jantan', 'Betina') NOT NULL, "
                + "berat_awal DECIMAL(5,2), "
                + "harga_modal DECIMAL(15,2), "
                + "status ENUM('Tersedia', 'Terjual', 'Sakit', 'Mati') DEFAULT 'Tersedia', "
                + "tanggal_masuk DATE, "
                + "FOREIGN KEY (id_supplier) REFERENCES supplier_kambing(id_supplier) ON DELETE SET NULL"
                + ")";
        stmt.execute(sqlKambing);

        // 5. Tabel Relasi 2: transaksi (Bergantung pada pelanggan & user)
                    String sqlTransaksi = "CREATE TABLE IF NOT EXISTS transaksi ("
                 + "id_transaksi INT AUTO_INCREMENT PRIMARY KEY, "
                 + "id_pelanggan INT, "
                 + "id_user INT, "
                 + "nomor_invoice VARCHAR(50) UNIQUE, "
                 + "tanggal_transaksi DATETIME, "
                 + "status_pembayaran ENUM('Lunas', 'DP', 'Batal'), "
                 + "metode_pembayaran VARCHAR(50), "
                 + "uang_dibayar DOUBLE DEFAULT 0, " // <--- TAMBAHKAN INI UNTUK KOMPUTER BARU
                 + "FOREIGN KEY (id_pelanggan) REFERENCES pelanggan(id_pelanggan), "
                 + "FOREIGN KEY (id_user) REFERENCES user(id_user)"
                 + ")";
             stmt.executeUpdate(sqlTransaksi);

        // 6. Tabel Relasi 3: detail_transaksi (Bergantung pada transaksi & kambing)
        String sqlDetailTransaksi = "CREATE TABLE IF NOT EXISTS detail_transaksi ("
                + "id_detail INT AUTO_INCREMENT PRIMARY KEY, "
                + "id_transaksi INT, "
                + "id_kambing INT, "
                + "harga_jual_satuan DECIMAL(15,2) NOT NULL, "
                + "FOREIGN KEY (id_transaksi) REFERENCES transaksi(id_transaksi) ON DELETE CASCADE, "
                + "FOREIGN KEY (id_kambing) REFERENCES kambing(id_kambing) ON DELETE RESTRICT"
                + ")";
        stmt.execute(sqlDetailTransaksi);

        // 7. Tabel Relasi 4: riwayat_kesehatan (Bergantung pada kambing)
        String sqlRiwayatKesehatan = "CREATE TABLE IF NOT EXISTS riwayat_kesehatan ("
                + "id_riwayat INT AUTO_INCREMENT PRIMARY KEY, "
                + "id_kambing INT, "
                + "tanggal_pemeriksaan DATE, "
                + "keterangan TEXT, "
                + "berat_saat_ini DECIMAL(5,2), "
                + "FOREIGN KEY (id_kambing) REFERENCES kambing(id_kambing) ON DELETE CASCADE"
                + ")";
        stmt.execute(sqlRiwayatKesehatan);

        System.out.println("Pengecekan dan setup tabel ERD Sistem Peternakan selesai (Aman).");

    } catch (SQLException e) {
        System.err.println("Gagal membuat setup tabel awal: " + e.getMessage());
        // Memunculkan popup error jika tabel gagal dibuat (opsional tapi membantu saat testing)
        javax.swing.JOptionPane.showMessageDialog(null, 
            "Gagal mengeksekusi skema database!\nError: " + e.getMessage(), 
            "Schema Error", 
            javax.swing.JOptionPane.ERROR_MESSAGE);
    }
}
    
    private static void initDummyData(Connection conn) {
    try (Statement stmt = conn.createStatement()) {
        
        // 1. Cek dan Insert Dummy Data Supplier
        ResultSet rsSupplier = stmt.executeQuery("SELECT COUNT(*) AS total FROM supplier_kambing");
        rsSupplier.next();
        if (rsSupplier.getInt("total") == 0) {
            String sqlSupplier = "INSERT INTO supplier_kambing (nama_supplier, kontak, alamat) VALUES "
                    + "('Peternakan Berkah Jonggol', '081234567890', 'Jl. Raya Jonggol No. 12, Bogor'),"
                    + "('Saudagar Kambing Bekasi', '085678901234', 'Jl. Bintara Jaya, Bekasi Barat'),"
                    + "('Kambing Etawa Maju', '081122334455', 'Desa Sukamakmur, Purwakarta')";
            stmt.execute(sqlSupplier);
            System.out.println("Dummy data supplier_kambing berhasil ditambahkan.");
        }

        // 2. Cek dan Insert Dummy Data Pelanggan
        ResultSet rsPelanggan = stmt.executeQuery("SELECT COUNT(*) AS total FROM pelanggan");
        rsPelanggan.next();
        if (rsPelanggan.getInt("total") == 0) {
            String sqlPelanggan = "INSERT INTO pelanggan (nama_pelanggan, kontak, alamat) VALUES "
                    + "('Bapak Budi (Aqiqah)', '081987654321', 'Perumahan Harapan Indah, Kota Bekasi'),"
                    + "('Warung Sate Tegal Pak Kumis', '087766554433', 'Jl. Kalimalang Raya, Jakarta Timur'),"
                    + "('Mas Yanto (Panitia Qurban)', '081344556677', 'Kec. Tambun Selatan, Kabupaten Bekasi')";
            stmt.execute(sqlPelanggan);
            System.out.println("Dummy data pelanggan berhasil ditambahkan.");
        }

        // 3. Cek dan Insert Dummy Data Kambing
        ResultSet rsKambing = stmt.executeQuery("SELECT COUNT(*) AS total FROM kambing");
        rsKambing.next();
        if (rsKambing.getInt("total") == 0) {
            String sqlKambing = "INSERT INTO kambing (kode_kambing, id_supplier, jenis_kelamin, berat_awal, harga_modal, status, tanggal_masuk) VALUES "
                    + "('KMB-2026-001', 1, 'Jantan', 25.50, 2200000.00, 'Tersedia', '2026-04-10'),"
                    + "('KMB-2026-002', 1, 'Betina', 22.00, 1850000.00, 'Tersedia', '2026-04-10'),"
                    + "('KMB-2026-003', 2, 'Jantan', 35.00, 3200000.00, 'Terjual', '2026-03-15'),"
                    + "('KMB-2026-004', NULL, 'Jantan', 28.50, 0.00, 'Tersedia', '2026-05-01'),"
                    + "('KMB-2026-005', 3, 'Betina', 40.00, 3500000.00, 'Sakit', '2026-05-05'),"
                    + "('KMB-2026-006', 2, 'Jantan', 31.00, 2750000.00, 'Tersedia', '2026-05-08')";
            stmt.execute(sqlKambing);
            System.out.println("Dummy data kambing berhasil ditambahkan.");
        }

        System.out.println("Pengecekan dummy data selesai (Aman).");

    } catch (SQLException e) {
        System.err.println("Gagal memasukkan dummy data: " + e.getMessage());
    }
}
}