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
        if (koneksi == null) {
            try {
                // Register driver (opsional di versi JDBC baru, tapi aman untuk boilerplate)
                Class.forName("com.mysql.cj.jdbc.Driver");
                koneksi = DriverManager.getConnection(URL, USER, PASS);
                initSchema(koneksi);
                System.out.println("Koneksi Database Berhasil!");
            } catch (ClassNotFoundException | SQLException e) {
                System.err.println("Koneksi Gagal: " + e.getMessage());
                // Munculkan popup agar pemula langsung tahu kalau XAMPP/MySQL mereka belum nyala
                JOptionPane.showMessageDialog(null, 
                    "Gagal koneksi ke database! Pastikan XAMPP/MySQL sudah menyala.\nError: " + e.getMessage(), 
                    "Database Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
        return koneksi;
    }
    private static void initSchema(Connection conn) {
        try {
            Statement stmt = conn.createStatement();
          
            
            String sqlTabelHewan = "CREATE TABLE IF NOT EXISTS data_hewan ("
                    + "id_hewan INT AUTO_INCREMENT PRIMARY KEY, "
                    + "jenis_hewan VARCHAR(50) NOT NULL, "
                    + "kode_ternak VARCHAR(20) UNIQUE NOT NULL, "
                    + "harga DOUBLE NOT NULL, "
                    + "status VARCHAR(20) DEFAULT 'Tersedia'"
                    + ")";
            
            stmt.execute(sqlTabelHewan);
            System.out.println("Pengecekan tabel data_hewan selesai (Aman).");
            
            
            
        } catch (SQLException e) {
            System.err.println("Gagal membuat setup tabel awal: " + e.getMessage());
        }
    }
}