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
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import sistemmanajemenpenjualanhewanternak.Utils.Database;

public class ViewListRiwayat extends JFrame {

    // 1. Class Wrapper untuk Combo Box
    class KambingItem {
        private int id;
        private String kode;

        public KambingItem(int id, String kode) {
            this.id = id;
            this.kode = kode;
        }

        public int getId() { return id; }

        @Override
        public String toString() {
            return kode; // Teks yang muncul di dropdown
        }
    }

    private JComboBox<KambingItem> cbCariKambing;
    private JTable tableRiwayat;
    private DefaultTableModel tableModel;
    private JButton btnTambah, btnHapus;

    public ViewListRiwayat() {
        setTitle("Data Riwayat Kesehatan Kambing");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // --- PANEL ATAS (FILTERING DENGAN DROPDOWN) ---
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Filter Kode Kambing:"));
        
        cbCariKambing = new JComboBox<>();
        loadDropdownKambing(); // Panggil fungsi untuk mengisi dropdown
        
       

        
        filterPanel.add(cbCariKambing);


        mainPanel.add(filterPanel, BorderLayout.NORTH);

        // --- PANEL TENGAH (TABEL) ---
        String[] columnNames = {"ID Riwayat", "Kode Kambing", "Tgl Pemeriksaan", "Berat (Kg)", "Keterangan Medis"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };

        tableRiwayat = new JTable(tableModel);
        tableRiwayat.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableRiwayat.setRowHeight(25);
        
        tableRiwayat.getColumnModel().getColumn(0).setPreferredWidth(80);  
        tableRiwayat.getColumnModel().getColumn(1).setPreferredWidth(120); 
        tableRiwayat.getColumnModel().getColumn(2).setPreferredWidth(120); 
        tableRiwayat.getColumnModel().getColumn(3).setPreferredWidth(80);  
        tableRiwayat.getColumnModel().getColumn(4).setPreferredWidth(350); 

        JScrollPane scrollPane = new JScrollPane(tableRiwayat);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // --- PANEL BAWAH (TOMBOL AKSI) ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnTambah = new JButton("Tambah Riwayat Kesehatan");
        btnHapus = new JButton("Hapus Data Terpilih");
        
        buttonPanel.add(btnTambah);
        buttonPanel.add(btnHapus);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);

        // --- EVENT LISTENERS ---


        // Opsional: Membuat dropdown otomatis memfilter saat di-klik tanpa perlu memencet tombol
        cbCariKambing.addActionListener(e -> {
            KambingItem selected = (KambingItem) cbCariKambing.getSelectedItem();
            if (selected != null) {
                loadData(selected.getId());
            }
        });

 
  

        // 3. Tombol Tambah
        btnTambah.addActionListener(e -> {
            FormInputRiwayat formInput = new FormInputRiwayat();
            formInput.setVisible(true);
            
            formInput.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    // Refresh tabel menggunakan filter yang sedang aktif
                    KambingItem selected = (KambingItem) cbCariKambing.getSelectedItem();
                    loadData(selected != null ? selected.getId() : 0); 
                }
            });
        });

        // 4. Tombol Hapus
        btnHapus.addActionListener(e -> actionHapus());

        // Load data pertama kali saat form dibuka (ID 0 berarti load semua)
        loadData(0);
    }

    // Method untuk memuat data master kambing ke dalam Dropdown Filter
    private void loadDropdownKambing() {
        cbCariKambing.addItem(new KambingItem(0, "-- Semua Kambing --"));
        try {
            Connection conn = Database.getKoneksi();
            if(conn == null) return;

            String sql = "SELECT id_kambing, kode_kambing FROM kambing ORDER BY kode_kambing ASC";
            try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    cbCariKambing.addItem(new KambingItem(rs.getInt("id_kambing"), rs.getString("kode_kambing")));
                }
            }
        } catch (SQLException ex) {
            System.err.println("Gagal memuat dropdown filter: " + ex.getMessage());
        }
    }

    // Method Load Data dirubah untuk menerima parameter INTEGER (id_kambing)
    private void loadData(int filterIdKambing) {
        tableModel.setRowCount(0); 

        try {
            Connection conn = Database.getKoneksi();
            if(conn == null) return;

            // Jika filterIdKambing == 0, tampilkan semua. Jika > 0, filter menggunakan WHERE.
            String sql = "SELECT r.id_riwayat, k.kode_kambing, r.tanggal_pemeriksaan, r.berat_saat_ini, r.keterangan "
                       + "FROM riwayat_kesehatan r "
                       + "JOIN kambing k ON r.id_kambing = k.id_kambing ";
            
            if (filterIdKambing > 0) {
                sql += "WHERE r.id_kambing = ? ";
            }
            sql += "ORDER BY r.tanggal_pemeriksaan DESC";

            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                
                if (filterIdKambing > 0) {
                    pst.setInt(1, filterIdKambing);
                }

                try (ResultSet rs = pst.executeQuery()) {
                    while (rs.next()) {
                        int idRiwayat = rs.getInt("id_riwayat");
                        String kodeKambing = rs.getString("kode_kambing");
                        java.sql.Date tgl = rs.getDate("tanggal_pemeriksaan");
                        double berat = rs.getDouble("berat_saat_ini");
                        String keterangan = rs.getString("keterangan");

                        Object[] rowData = {idRiwayat, kodeKambing, tgl, berat, keterangan};
                        tableModel.addRow(rowData);
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data riwayat:\n" + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actionHapus() {
        int selectedRow = tableRiwayat.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Silakan pilih data riwayat yang mau dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idRiwayat = (int) tableModel.getValueAt(selectedRow, 0);
        String kodeKambing = (String) tableModel.getValueAt(selectedRow, 1);

        int konfirmasi = JOptionPane.showConfirmDialog(this, 
                "Yakin ingin menghapus riwayat kesehatan untuk kambing " + kodeKambing + "?", 
                "Konfirmasi Hapus", 
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (konfirmasi == JOptionPane.YES_OPTION) {
            try {
                Connection conn = Database.getKoneksi();
                String sql = "DELETE FROM riwayat_kesehatan WHERE id_riwayat = ?";
                try (PreparedStatement pst = conn.prepareStatement(sql)) {
                    pst.setInt(1, idRiwayat);
                    pst.executeUpdate();
                    
                    JOptionPane.showMessageDialog(this, "Riwayat kesehatan berhasil dihapus!");
                    
                    // Refresh tabel sesuai filter yang sedang aktif
                    KambingItem selected = (KambingItem) cbCariKambing.getSelectedItem();
                    loadData(selected != null ? selected.getId() : 0);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Gagal menghapus data:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ex) { ex.printStackTrace(); }
        SwingUtilities.invokeLater(() -> new ViewListRiwayat().setVisible(true));
    }
}
