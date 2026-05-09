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

public class ViewListTransaksi extends JFrame {

    private JTextField txtCari;
    private JButton btnCari, btnReset;
    private JTable tableTransaksi;
    private DefaultTableModel tableModel;
private JButton btnTambah, btnLihatDetail, btnLunasi;

    public ViewListTransaksi() {
        setTitle("Riwayat Transaksi Penjualan");
        setSize(900, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // --- PANEL ATAS (PENCARIAN) ---
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Cari (No. Invoice / Nama Pelanggan):"));
        
        txtCari = new JTextField(20);
        btnCari = new JButton("Cari");
        btnReset = new JButton("Tampilkan Semua");
        
        filterPanel.add(txtCari);
        filterPanel.add(btnCari);
        filterPanel.add(btnReset);

        mainPanel.add(filterPanel, BorderLayout.NORTH);

        // --- PANEL TENGAH (TABEL UTAMA) ---
        // Kolom Total Harga didapat dari hasil agregasi (SUM) detail transaksi
        String[] columnNames = {"ID", "No. Invoice", "Tanggal", "Pelanggan", "Kasir", "Status Bayar", "Metode", "Total Harga (Rp)"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };

        tableTransaksi = new JTable(tableModel);
        tableTransaksi.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableTransaksi.setRowHeight(25);
        
        // Atur lebar kolom
        tableTransaksi.getColumnModel().getColumn(0).setPreferredWidth(40);  
        tableTransaksi.getColumnModel().getColumn(1).setPreferredWidth(140); 
        tableTransaksi.getColumnModel().getColumn(2).setPreferredWidth(130); 
        tableTransaksi.getColumnModel().getColumn(3).setPreferredWidth(120); 
        tableTransaksi.getColumnModel().getColumn(4).setPreferredWidth(100); 
        tableTransaksi.getColumnModel().getColumn(5).setPreferredWidth(80);  
        tableTransaksi.getColumnModel().getColumn(6).setPreferredWidth(80);  
        tableTransaksi.getColumnModel().getColumn(7).setPreferredWidth(120); 

        JScrollPane scrollPane = new JScrollPane(tableTransaksi);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // --- PANEL BAWAH (TOMBOL AKSI) ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnTambah = new JButton("Buat Transaksi Baru");
        btnLihatDetail = new JButton("Lihat Detail Item Kambing");
        btnLunasi = new JButton("Lunasi Pembayaran");
        
        buttonPanel.add(btnLunasi); // Masukkan ke panel
        buttonPanel.add(btnTambah);
        buttonPanel.add(btnLihatDetail);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);

        // --- EVENT LISTENERS ---
        
        btnLunasi.addActionListener(e -> actionLunasi());
        
        btnCari.addActionListener(e -> loadData(txtCari.getText().trim()));
        txtCari.addActionListener(e -> loadData(txtCari.getText().trim())); // Support Enter key
        
        btnReset.addActionListener(e -> {
            txtCari.setText("");
            loadData("");
        });

        // Membuka Form Input Transaksi (Kasir)
        btnTambah.addActionListener(e -> {
            FormTransaksi formTransaksi = new FormTransaksi();
            formTransaksi.setVisible(true);
            
            // Auto refresh tabel jika jendela kasir ditutup
            formTransaksi.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadData(txtCari.getText().trim());
                }
            });
        });

        // Melihat kambing apa saja yang dibeli di dalam invoice tersebut
        btnLihatDetail.addActionListener(e -> actionLihatDetail());

        // Load data perdana
        loadData("");
    }

    // Method untuk menarik data transaksi dari Database
    private void loadData(String keyword) {
        tableModel.setRowCount(0);

        try {
            Connection conn = Database.getKoneksi();
            if(conn == null) return;

            // Query JOIN yang canggih:
            // Menggabungkan Transaksi, Pelanggan, User, dan melakukan SUM() pada Detail Transaksi
            String sql = "SELECT t.id_transaksi, t.nomor_invoice, t.tanggal_transaksi, "
                       + "p.nama_pelanggan, u.nama AS nama_kasir, t.status_pembayaran, t.metode_pembayaran, "
                       + "COALESCE(SUM(d.harga_jual_satuan), 0) AS total_harga "
                       + "FROM transaksi t "
                       + "LEFT JOIN pelanggan p ON t.id_pelanggan = p.id_pelanggan "
                       + "LEFT JOIN user u ON t.id_user = u.id_user "
                       + "LEFT JOIN detail_transaksi d ON t.id_transaksi = d.id_transaksi ";

            // Fitur Pencarian Dinamis
            if (!keyword.isEmpty()) {
                sql += "WHERE t.nomor_invoice LIKE ? OR p.nama_pelanggan LIKE ? ";
            }
            
            sql += "GROUP BY t.id_transaksi ORDER BY t.tanggal_transaksi DESC";

            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                if (!keyword.isEmpty()) {
                    String param = "%" + keyword + "%";
                    pst.setString(1, param);
                    pst.setString(2, param);
                }

                try (ResultSet rs = pst.executeQuery()) {
                    while (rs.next()) {
                        int id = rs.getInt("id_transaksi");
                        String invoice = rs.getString("nomor_invoice");
                        // Format Tanggal (membuang ".0" millisecond bawaan JDBC)
                        String tgl = rs.getTimestamp("tanggal_transaksi").toString().split("\\.")[0]; 
                        String pelanggan = rs.getString("nama_pelanggan");
                        String kasir = rs.getString("nama_kasir");
                        String status = rs.getString("status_pembayaran");
                        String metode = rs.getString("metode_pembayaran");
                        double total = rs.getDouble("total_harga");

                        // Format mata uang untuk tampilan
                        String totalFormat = String.format("%,.0f", total);

                        Object[] rowData = {id, invoice, tgl, pelanggan, kasir, status, metode, totalFormat};
                        tableModel.addRow(rowData);
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data transaksi:\n" + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Menampilkan detail kambing dari invoice yang dipilih menggunakan Popup JTable sederhana
    private void actionLihatDetail() {
        int selectedRow = tableTransaksi.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Silakan pilih salah satu transaksi di tabel terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idTransaksi = (int) tableModel.getValueAt(selectedRow, 0);
        String invoice = (String) tableModel.getValueAt(selectedRow, 1);

        try {
            Connection conn = Database.getKoneksi();
            String sql = "SELECT k.kode_kambing, k.jenis_kelamin, d.harga_jual_satuan "
                       + "FROM detail_transaksi d "
                       + "JOIN kambing k ON d.id_kambing = k.id_kambing "
                       + "WHERE d.id_transaksi = ?";

            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setInt(1, idTransaksi);
                try (ResultSet rs = pst.executeQuery()) {
                    
                    // Membuat String StringBuilder untuk mencetak Struk/Detail ke dalam Popup
                    StringBuilder detailMsg = new StringBuilder();
                    detailMsg.append("Detail Pembelian untuk Invoice: ").append(invoice).append("\n");
                    detailMsg.append("--------------------------------------------------\n");
                    
                    double total = 0;
                    int no = 1;
                    while (rs.next()) {
                        String kode = rs.getString("kode_kambing");
                        String jk = rs.getString("jenis_kelamin");
                        double harga = rs.getDouble("harga_jual_satuan");
                        
                        detailMsg.append(no).append(". ").append(kode).append(" (").append(jk).append(") - Rp ").append(String.format("%,.0f", harga)).append("\n");
                        total += harga;
                        no++;
                    }
                    detailMsg.append("--------------------------------------------------\n");
                    detailMsg.append("Total Keseluruhan: Rp ").append(String.format("%,.0f", total));

                    // Tampilkan di Text Area (read-only) agar mudah di-copy
                    JTextArea textArea = new JTextArea(detailMsg.toString());
                    textArea.setEditable(false);
                    textArea.setFont(new Font("Monospaced", Font.PLAIN, 14)); // Font monospaced agar rata
                    
                    JOptionPane.showMessageDialog(this, new JScrollPane(textArea), "Detail Transaksi", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal mengambil detail transaksi:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method untuk merubah status transaksi dari DP menjadi Lunas
    private void actionLunasi() {
        int selectedRow = tableTransaksi.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Silakan pilih salah satu transaksi di tabel terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idTransaksi = (int) tableModel.getValueAt(selectedRow, 0);
        String invoice = (String) tableModel.getValueAt(selectedRow, 1);
        String statusSaatIni = (String) tableModel.getValueAt(selectedRow, 5); // Index 5 adalah kolom Status Bayar

        // Validasi: Cek apakah transaksi sudah lunas atau batal
        if ("Lunas".equalsIgnoreCase(statusSaatIni)) {
            JOptionPane.showMessageDialog(this, "Transaksi ini sudah Lunas!", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        } else if ("Batal".equalsIgnoreCase(statusSaatIni)) {
            JOptionPane.showMessageDialog(this, "Transaksi ini sudah Dibatalkan dan tidak bisa dilunasi.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Konfirmasi ke user
        int konfirmasi = JOptionPane.showConfirmDialog(this, 
                "Pelanggan akan melunasi Invoice: " + invoice + ".\nApakah dana sudah diterima dan lanjutkan pelunasan?", 
                "Konfirmasi Pelunasan", 
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (konfirmasi == JOptionPane.YES_OPTION) {
            try {
                Connection conn = Database.getKoneksi();
                if (conn == null) return;

                String sql = "UPDATE transaksi SET status_pembayaran = 'Lunas' WHERE id_transaksi = ?";
                
                try (PreparedStatement pst = conn.prepareStatement(sql)) {
                    pst.setInt(1, idTransaksi);
                    int affectedRows = pst.executeUpdate();
                    
                    if (affectedRows > 0) {
                        JOptionPane.showMessageDialog(this, "Transaksi berhasil dilunasi!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                        // Refresh tabel agar status terupdate menjadi "Lunas" di layar
                        loadData(txtCari.getText().trim()); 
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Gagal mengupdate status transaksi:\n" + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ex) { ex.printStackTrace(); }
        SwingUtilities.invokeLater(() -> new ViewListTransaksi().setVisible(true));
    }
}