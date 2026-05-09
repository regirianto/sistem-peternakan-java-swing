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

public class DashboardUtama extends JFrame {

    public DashboardUtama() {
        setTitle("Dashboard - Sistem Manajemen Peternakan Kambing");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Karena ini menu utama, wajib EXIT_ON_CLOSE
        setLocationRelativeTo(null); // Tampil di tengah layar

        // --- Panel Utama ---
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245));

        // --- HEADER ---
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(41, 128, 185)); // Warna Biru
        headerPanel.setPreferredSize(new Dimension(800, 80));
        headerPanel.setLayout(new GridBagLayout()); // Agar teks tepat di tengah
        
        JLabel lblTitle = new JLabel("SISTEM INFORMASI PETERNAKAN KAMBING");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle);

        // --- BODY (MENU GRID) ---
        // Margin/Padding untuk grid menu
        JPanel bodyPanel = new JPanel(new BorderLayout());
        bodyPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));
        bodyPanel.setOpaque(false);

        // Grid Layout: 2 Baris, 3 Kolom, Spasi Horizontal 20, Spasi Vertikal 20
        JPanel gridMenuPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        gridMenuPanel.setOpaque(false);

        // Membuat Tombol Menu
        JButton btnKambing = createMenuButton("Data Kambing", "Kelola master data kambing");
        JButton btnSupplier = createMenuButton("Data Supplier", "Kelola mitra pemasok");
        JButton btnPelanggan = createMenuButton("Data Pelanggan", "Kelola data pembeli");
        JButton btnTransaksi = createMenuButton("Transaksi", "Kasir & Pembayaran");
        JButton btnKesehatan = createMenuButton("Riwayat Kesehatan", "Pemeriksaan medis hewan");
        JButton btnUser = createMenuButton("Manajemen User", "Kelola admin & kasir");

        // Memasukkan tombol ke dalam Grid
        gridMenuPanel.add(btnKambing);
        gridMenuPanel.add(btnSupplier);
        gridMenuPanel.add(btnPelanggan);
        gridMenuPanel.add(btnTransaksi);
        gridMenuPanel.add(btnKesehatan);
        gridMenuPanel.add(btnUser);

        bodyPanel.add(gridMenuPanel, BorderLayout.CENTER);

        // --- FOOTER ---
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setBackground(new Color(220, 220, 220));
        JLabel lblFooter = new JLabel("© 2026 - Tugas Pemrograman Visual");
        footerPanel.add(lblFooter);

        // Menyatukan semua panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(bodyPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // --- EVENT LISTENER UNTUK TOMBOL ---

        // 1. Tombol Data Kambing (Membuka ViewListKambing yang sudah kita buat)
        btnKambing.addActionListener(e -> {
            ViewListKambing viewKambing = new ViewListKambing();
            viewKambing.setVisible(true);
        });

        // 2. Tombol Lainnya (Karena belum dibuat, kita kasih Popup Info saja dulu)
        btnSupplier.addActionListener(e -> {
            ViewListSupplier viewListSupplier= new ViewListSupplier();
            viewListSupplier.setVisible(true);
        });
        btnPelanggan.addActionListener(e -> showComingSoon("Data Pelanggan"));
        btnTransaksi.addActionListener(e -> {
            ViewListTransaksi viewTransaksi = new ViewListTransaksi();
            viewTransaksi.setVisible(true);
        });
       btnKesehatan.addActionListener(e -> {
       ViewListRiwayat viewRiwayat = new ViewListRiwayat();
         viewRiwayat.setVisible(true);
});
        btnUser.addActionListener(e -> showComingSoon("Manajemen User"));
    }

    // Method bantuan untuk membuat tombol yang desainnya seragam
    private JButton createMenuButton(String title, String tooltip) {
        JButton button = new JButton(title);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setBackground(Color.WHITE);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setToolTipText(tooltip);
        
        // Sedikit efek hover sederhana
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

    // Method bantuan untuk popup fitur yang belum dibuat
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
        
        SwingUtilities.invokeLater(() -> {
            new DashboardUtama().setVisible(true);
        });
    }
}
