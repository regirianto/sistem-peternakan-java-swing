/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package sistemmanajemenpenjualanhewanternak;

import sistemmanajemenpenjualanhewanternak.Utils.Database;
import sistemmanajemenpenjualanhewanternak.views.DashboardUtama;
import sistemmanajemenpenjualanhewanternak.views.LoginForm;
import sistemmanajemenpenjualanhewanternak.views.TambahDataForm;


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
                new DashboardUtama().setVisible(true);

            }
        });
    }
    
}
