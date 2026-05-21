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
    private JButton btnTambah, btnLihatDetail, btnLunasi, btnCetakInvoice;

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
        btnCetakInvoice = new JButton("Cetak Invoice");
        
        filterPanel.add(txtCari);
        filterPanel.add(btnCari);
        filterPanel.add(btnReset);
        filterPanel.add(btnCetakInvoice);

        mainPanel.add(filterPanel, BorderLayout.NORTH);

        // --- PANEL TENGAH (TABEL UTAMA) ---
        // Kolom Total Harga didapat dari hasil agregasi (SUM) detail transaksi
        String[] columnNames = {"ID", "No. Invoice", "Tanggal", "Pelanggan", "Kasir", "Status Bayar", "Metode", "Total Harga (Rp)", "Uang Di bayar"};
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
        tableTransaksi.getColumnModel().getColumn(0).setPreferredWidth(40);  // ID
        tableTransaksi.getColumnModel().getColumn(1).setPreferredWidth(140); // No. Invoice
        tableTransaksi.getColumnModel().getColumn(2).setPreferredWidth(130); // Tanggal
        tableTransaksi.getColumnModel().getColumn(3).setPreferredWidth(120); // Pelanggan
        tableTransaksi.getColumnModel().getColumn(4).setPreferredWidth(100); // Kasir
        tableTransaksi.getColumnModel().getColumn(5).setPreferredWidth(80);  // Status Bayar
        tableTransaksi.getColumnModel().getColumn(6).setPreferredWidth(70);  // Metode
        tableTransaksi.getColumnModel().getColumn(7).setPreferredWidth(110); // Total Harga
        tableTransaksi.getColumnModel().getColumn(8).setPreferredWidth(110); // Uang Dibayar

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
        btnCetakInvoice.addActionListener(e -> actionCetakInvoice());

        // Load data perdana
        loadData("");
    }

    // Method untuk menarik data transaksi dari Database
private void loadData(String keyword) {
        tableModel.setRowCount(0); 

        try {
            Connection conn = Database.getKoneksi();
            if (conn == null) return;

            // Query menggunakan JOIN ke tabel user, pelanggan, dan detail_transaksi
            // Menggunakan SUM() untuk menghitung total belanja per nota
            String sql = "SELECT t.id_transaksi, t.nomor_invoice, t.tanggal_transaksi, "
                       + "p.nama_pelanggan, u.nama AS nama_kasir, t.status_pembayaran, "
                       + "t.metode_pembayaran, COALESCE(SUM(d.harga_jual_satuan), 0) AS total_harga, "
                       + "t.uang_dibayar "
                       + "FROM transaksi t "
                       + "JOIN pelanggan p ON t.id_pelanggan = p.id_pelanggan "
                       + "JOIN user u ON t.id_user = u.id_user "
                       + "LEFT JOIN detail_transaksi d ON t.id_transaksi = d.id_transaksi ";
            
            if (!keyword.isEmpty()) {
                sql += "WHERE t.nomor_invoice LIKE ? OR p.nama_pelanggan LIKE ? ";
            }
            
            // Wajib pakai GROUP BY karena kita menggunakan fungsi SUM()
            sql += "GROUP BY t.id_transaksi "
                 + "ORDER BY t.id_transaksi DESC";

            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                if (!keyword.isEmpty()) {
                    pst.setString(1, "%" + keyword + "%");
                    pst.setString(2, "%" + keyword + "%");
                }

                try (ResultSet rs = pst.executeQuery()) {
                    while (rs.next()) {
                        // Tarik semua 9 data dari ResultSet
                        int id = rs.getInt("id_transaksi");
                        String invoice = rs.getString("nomor_invoice");
                        String tanggal = rs.getString("tanggal_transaksi");
                        String pelanggan = rs.getString("nama_pelanggan");
                        String kasir = rs.getString("nama_kasir");
                        String status = rs.getString("status_pembayaran");
                        String metode = rs.getString("metode_pembayaran");
                        
                        double totalHarga = rs.getDouble("total_harga");
                        double uangDibayar = rs.getDouble("uang_dibayar");
                        
                        // Format Uang ke Rupiah
                        String strTotal = String.format("Rp %,.0f", totalHarga);
                        String strUang = String.format("Rp %,.0f", uangDibayar);

                        // Masukkan ke array sesuai urutan kolom yang baru (9 buah)
                        Object[] rowData = {id, invoice, tanggal, pelanggan, kasir, status, metode, strTotal, strUang};
                        tableModel.addRow(rowData);
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data transaksi:\n" + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
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
                "Pelanggan akan melunasi Invoice: " + invoice + ".\nSistem akan otomatis menyesuaikan nominal 'Uang Dibayar' menjadi penuh.\nLanjutkan?", 
                "Konfirmasi Pelunasan", 
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (konfirmasi == JOptionPane.YES_OPTION) {
            try {
                Connection conn = Database.getKoneksi();
                if (conn == null) return;

                // --- LANGKAH 1: Cari total harga sebenarnya dari transaksi ini ---
                double totalBelanja = 0;
                String sqlTotal = "SELECT SUM(harga_jual_satuan) AS total FROM detail_transaksi WHERE id_transaksi = ?";
                try (PreparedStatement pstTotal = conn.prepareStatement(sqlTotal)) {
                    pstTotal.setInt(1, idTransaksi);
                    try (ResultSet rs = pstTotal.executeQuery()) {
                        if (rs.next()) {
                            totalBelanja = rs.getDouble("total");
                        }
                    }
                }

                // --- LANGKAH 2: Update status Lunas DAN update uang_dibayar sekaligus ---
                String sqlUpdate = "UPDATE transaksi SET status_pembayaran = 'Lunas', uang_dibayar = ? WHERE id_transaksi = ?";
                
                try (PreparedStatement pstUpdate = conn.prepareStatement(sqlUpdate)) {
                    pstUpdate.setDouble(1, totalBelanja); // Mengubah uang dibayar jadi lunas 100%
                    pstUpdate.setInt(2, idTransaksi);
                    
                    int affectedRows = pstUpdate.executeUpdate();
                    
                    if (affectedRows > 0) {
                        JOptionPane.showMessageDialog(this, "Transaksi berhasil dilunasi!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                        // Refresh tabel agar status terupdate menjadi "Lunas" dan nominal uang berubah di layar
                        loadData(txtCari.getText().trim()); 
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Gagal mengupdate status transaksi:\n" + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void actionCetakInvoice() {
        int selectedRow = tableTransaksi.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Silakan pilih salah satu transaksi di tabel terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idTransaksi = (int) tableModel.getValueAt(selectedRow, 0);

        // ==========================================================
        // 1. DESAIN JENDELA LOADING (POP-UP)
        // ==========================================================
        JDialog loadingDialog = new JDialog(this, "Memproses Laporan", true); // 'true' membuatnya menjadi Modal (fokus terkunci)
        loadingDialog.setLayout(new BorderLayout(10, 10));
        loadingDialog.setSize(300, 110);
        loadingDialog.setLocationRelativeTo(this);
        loadingDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); // Cegah user menyilang/menutup paksa saat loading

        // Membuat Progress Bar yang jalan terus (Indeterminate)
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true); 
        progressBar.setString("Menghasilkan invoice...");
        progressBar.setStringPainted(true); // Menampilkan teks di dalam loading bar

        loadingDialog.add(new JLabel("Mohon tunggu sebentar...", SwingConstants.CENTER), BorderLayout.NORTH);
        loadingDialog.add(progressBar, BorderLayout.CENTER);


        // ==========================================================
        // 2. PROSES BACKGROUND (AGAR APLIKASI TIDAK MACET)
        // ==========================================================
        SwingWorker<net.sf.jasperreports.engine.JasperPrint, Void> worker = new SwingWorker<>() {
            
            // a. Kode di dalam ini berjalan di Belakang Layar (Tugas Berat)
            @Override
            protected net.sf.jasperreports.engine.JasperPrint doInBackground() throws Exception {
                Connection conn = Database.getKoneksi();
                if (conn == null) throw new Exception("Koneksi database gagal!");

                java.io.InputStream reportStream = getClass().getResourceAsStream("/report/InvoicePembelian.jrxml");
                if (reportStream == null) {
                    java.io.File fileReport = new java.io.File("src/report/InvoicePembelian.jrxml");
                    if (fileReport.exists()) reportStream = new java.io.FileInputStream(fileReport);
                }

                if (reportStream == null) {
                    throw new Exception("File template 'InvoicePembelian.jrxml' tidak ditemukan!");
                }

                try {
                    // Compile & Fill (Ini yang bikin lama)
                    net.sf.jasperreports.engine.JasperReport jasperReport = net.sf.jasperreports.engine.JasperCompileManager.compileReport(reportStream);
                    java.util.HashMap<String, Object> parameters = new java.util.HashMap<>();
                    parameters.put("param_id_transaksi", idTransaksi); 

                    return net.sf.jasperreports.engine.JasperFillManager.fillReport(jasperReport, parameters, conn);
                } finally {
                    if (reportStream != null) reportStream.close(); // Tutup stream agar memory aman
                }
            }

            // b. Kode di dalam ini berjalan setelah tugas berat SELESAI
            @Override
            protected void done() {
                loadingDialog.dispose(); // Wajib! Hilangkan pop-up loading dari layar

                try {
                    // Ambil hasil cetakan dari doInBackground
                    net.sf.jasperreports.engine.JasperPrint print = get();

                    // Pengecekan jika datanya kosong
                    if (print.getPages().isEmpty()) {
                        JOptionPane.showMessageDialog(ViewListTransaksi.this, "Laporan berhasil dimuat tapi isinya KOSONG!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    // Tampilkan Laporan ke layar
                    net.sf.jasperreports.view.JasperViewer.viewReport(print, false); 

                } catch (Exception ex) {
                    // Ambil pesan error aslinya
                    String msg = ex.getMessage();
                    if (ex.getCause() != null) msg = ex.getCause().getMessage();
                    
                    JOptionPane.showMessageDialog(ViewListTransaksi.this, "Gagal mencetak invoice:\n" + msg, "Error Laporan", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        // ==========================================================
        // 3. EKSEKUSI
        // ==========================================================
        worker.execute(); // Jalankan pekerja background
        loadingDialog.setVisible(true); // Kunci layar dengan pop-up loading
    }
    
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ex) { ex.printStackTrace(); }
        SwingUtilities.invokeLater(() -> new ViewListTransaksi().setVisible(true));
    }
}