/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package sistemmanajemenpenjualanhewanternak;

import sistemmanajemenpenjualanhewanternak.Utils.Database;


/**
 *
 * @author regi.suyono
 */
public class SistemManajemenPenjualanHewanTernak {

    /**
     * @param args the command line arguments
     */
public static void main(String[] args) {
        // Cek koneksi saat aplikasi pertama kali jalan
        Database.getKoneksi();
       
        // Membuka UI menggunakan thread yang aman untuk Swing
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                
            javax.swing.JFrame frame = new javax.swing.JFrame("Dashboard Berkah Aamiin Farm");
            Dashboard_AaminFarm panel = new Dashboard_AaminFarm();
            
            // 3. Masukkan panel ke dalam frame
            frame.add(panel);
            
            // 4. Sesuaikan ukuran frame (bisa pakai pack() atau setSize manual)
            frame.pack(); 
            // frame.setSize(1280, 720); // Gunakan ini jika pack() dirasa kurang pas
            
            // 5. Posisikan window di tengah layar
            frame.setLocationRelativeTo(null);
            
            // 6. Tampilkan ke layar
            frame.setVisible(true);

            }
        });
    }
    
}
