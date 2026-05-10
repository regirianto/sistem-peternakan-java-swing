/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistemmanajemenpenjualanhewanternak.views;

/**
 *
 * @author regi.suyono
 */
import javax.swing.*;
import java.awt.*;
import sistemmanajemenpenjualanhewanternak.Utils.UserSession;

public class DashboardUtama extends JFrame {

    public DashboardUtama() {
        // ========================================================
        // GEMBOK KEAMANAN: CEK APAKAH SUDAH LOGIN?
        // ========================================================
        if (UserSession.getLoginId() == 0 || UserSession.getLoginName() == null) {
  
            dispose(); // Tutup dashboard yang mencoba terbuka
            new FormLogin().setVisible(true); // Lempar ke form login
            return; // Hentikan eksekusi kode di bawahnya
        }
        // ========================================================

        setTitle("Dashboard - Sistem Manajemen Peternakan Kambing");
        setSize(850, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        setLocationRelativeTo(null); 

        // --- Panel Utama ---
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245));

        // --- HEADER ---
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(41, 128, 185)); // Warna Biru
        headerPanel.setPreferredSize(new Dimension(800, 80));
        headerPanel.setLayout(new GridBagLayout()); 
        
        JLabel lblTitle = new JLabel("SISTEM INFORMASI PETERNAKAN KAMBING");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle);

        // --- BODY (MENU GRID) ---
        JPanel bodyPanel = new JPanel(new BorderLayout());
        bodyPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));
        bodyPanel.setOpaque(false);

        JPanel gridMenuPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        gridMenuPanel.setOpaque(false);

        JButton btnKambing = createMenuButton("Data Kambing", "Kelola master data kambing");
        JButton btnSupplier = createMenuButton("Data Supplier", "Kelola mitra pemasok");
        JButton btnPelanggan = createMenuButton("Data Pelanggan", "Kelola data pembeli");
        JButton btnTransaksi = createMenuButton("Transaksi", "Kasir & Pembayaran");
        JButton btnKesehatan = createMenuButton("Riwayat Kesehatan", "Pemeriksaan medis hewan");
        JButton btnUser = createMenuButton("Manajemen User", "Kelola admin & kasir");

        gridMenuPanel.add(btnKambing);
        gridMenuPanel.add(btnSupplier);
        gridMenuPanel.add(btnPelanggan);
        gridMenuPanel.add(btnTransaksi);
        gridMenuPanel.add(btnKesehatan);
        gridMenuPanel.add(btnUser);

        bodyPanel.add(gridMenuPanel, BorderLayout.CENTER);

        // --- FOOTER (DENGAN TOMBOL LOGOUT) ---
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(new Color(220, 220, 220));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Memberi jarak margin

        // Menampilkan nama user yang sedang login
        JLabel lblFooter = new JLabel("© 2026 - Tugas Pemrograman Visual | Login sebagai: " + UserSession.getLoginName());
        lblFooter.setFont(new Font("Arial", Font.BOLD, 12));
        
        // Membuat Tombol Logout merah
        JButton btnLogout = new JButton("Logout");
        btnLogout.setForeground(Color.BLACK);
        btnLogout.setFocusPainted(false);
        btnLogout.setFont(new Font("Arial", Font.BOLD, 12));
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.setBackground(new Color(220, 220, 220));

        footerPanel.add(lblFooter, BorderLayout.WEST); // Teks di Kiri
        footerPanel.add(btnLogout, BorderLayout.EAST); // Tombol di Kanan

        // Menyatukan semua panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(bodyPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // --- EVENT LISTENER UNTUK TOMBOL MENU ---

        btnKambing.addActionListener(e -> {
            ViewListKambing viewKambing = new ViewListKambing();
            viewKambing.setVisible(true);
        });

        btnSupplier.addActionListener(e -> {
            ViewListSupplier viewSupplier = new ViewListSupplier();
            viewSupplier.setVisible(true);
        });

        btnPelanggan.addActionListener(e -> {
            ViewListPelanggan viewPelanggan = new ViewListPelanggan();
            viewPelanggan.setVisible(true);
        });
        
        btnTransaksi.addActionListener(e -> {
            ViewListTransaksi viewTransaksi = new ViewListTransaksi();
            viewTransaksi.setVisible(true);
        });
        
        btnKesehatan.addActionListener(e -> {
            ViewListRiwayat viewRiwayat = new ViewListRiwayat();
            viewRiwayat.setVisible(true);
        });

        btnUser.addActionListener(e -> showComingSoon("Manajemen User"));

        // --- EVENT LISTENER UNTUK TOMBOL LOGOUT ---
        btnLogout.addActionListener(e -> {
            int konfirmasi = JOptionPane.showConfirmDialog(this, 
                "Apakah Anda yakin ingin keluar dari aplikasi?", 
                "Konfirmasi Logout", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.QUESTION_MESSAGE);

            if (konfirmasi == JOptionPane.YES_OPTION) {
                // 1. Bersihkan Data Sesi
                UserSession.logout();
                
                // 2. Tutup Dashboard
                dispose();
                
                // 3. Buka Form Login Kembali
                new FormLogin().setVisible(true);
            }
        });
    }

    private JButton createMenuButton(String title, String tooltip) {
        JButton button = new JButton(title);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setBackground(Color.WHITE);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setToolTipText(tooltip);
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(236, 240, 241));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.WHITE);
            }
        });
        return button;
    }

    private void showComingSoon(String module) {
        JOptionPane.showMessageDialog(this, 
            "Modul " + module + " sedang dalam tahap pengembangan.", 
            "Coming Soon", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        try { 
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
        } catch (Exception ex) { 
            ex.printStackTrace(); 
        }
        
        // Peringatan: Saat Testing, jika dijalankan dari sini langsung akan mental ke Form Login
        SwingUtilities.invokeLater(() -> {
            new DashboardUtama().setVisible(true);
        });
    }
}
