package sistemmanajemenpenjualanhewanternak.views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import sistemmanajemenpenjualanhewanternak.Utils.Database;

public class ViewListTransaksi extends JFrame {

    private JTextField txtCari;
    private JButton btnCari, btnReset;
    private JTable tableTransaksi;
    private DefaultTableModel tableModel;
    private JButton btnTambah, btnLihatDetail, btnLunasi, btnCetakInvoice,btnCetakLaporanPeriode;

    public ViewListTransaksi() {
        setTitle("Riwayat Transaksi Penjualan");
        setSize(1050, 550); // Diperlebar agar 10 kolom muat sempurna
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
        btnCetakLaporanPeriode = new JButton("Cetak Laporan Periode"); // TAMBAHAN
        
        filterPanel.add(txtCari);
        filterPanel.add(btnCari);
        filterPanel.add(btnReset);
        filterPanel.add(btnCetakInvoice);
        filterPanel.add(btnCetakLaporanPeriode);

        mainPanel.add(filterPanel, BorderLayout.NORTH);

        // --- PANEL TENGAH (TABEL UTAMA) ---
        String[] columnNames = {"ID", "No. Invoice", "Tanggal", "Pelanggan", "Kasir", "Status Bayar", "Metode", "Total Harga (Rp)", "Uang Di bayar", "Sisa Tagihan"};
        
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };

        tableTransaksi = new JTable(tableModel);
        tableTransaksi.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableTransaksi.setRowHeight(25);
        
        // Atur lebar untuk 10 Kolom
        tableTransaksi.getColumnModel().getColumn(0).setPreferredWidth(40);  // ID
        tableTransaksi.getColumnModel().getColumn(1).setPreferredWidth(140); // No. Invoice
        tableTransaksi.getColumnModel().getColumn(2).setPreferredWidth(130); // Tanggal
        tableTransaksi.getColumnModel().getColumn(3).setPreferredWidth(120); // Pelanggan
        tableTransaksi.getColumnModel().getColumn(4).setPreferredWidth(100); // Kasir
        tableTransaksi.getColumnModel().getColumn(5).setPreferredWidth(80);  // Status Bayar
        tableTransaksi.getColumnModel().getColumn(6).setPreferredWidth(70);  // Metode
        tableTransaksi.getColumnModel().getColumn(7).setPreferredWidth(110); // Total Harga
        tableTransaksi.getColumnModel().getColumn(8).setPreferredWidth(110); // Uang Dibayar
        tableTransaksi.getColumnModel().getColumn(9).setPreferredWidth(110); // Sisa Tagihan

        JScrollPane scrollPane = new JScrollPane(tableTransaksi);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // --- PANEL BAWAH (TOMBOL AKSI) ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnTambah = new JButton("Buat Transaksi Baru");
        btnLihatDetail = new JButton("Lihat Detail Item Kambing");
        btnLunasi = new JButton("Lunasi / Cicil Pembayaran");
        
        buttonPanel.add(btnLunasi); 
        buttonPanel.add(btnTambah);
        buttonPanel.add(btnLihatDetail);
        buttonPanel.add(btnCetakLaporanPeriode);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);

        // --- EVENT LISTENERS ---
        btnLunasi.addActionListener(e -> actionTambahPembayaran());
        
        btnCari.addActionListener(e -> loadData(txtCari.getText().trim()));
        txtCari.addActionListener(e -> loadData(txtCari.getText().trim())); 
        
        btnReset.addActionListener(e -> {
            txtCari.setText("");
            loadData("");
        });

        btnTambah.addActionListener(e -> {
            FormTransaksi formTransaksi = new FormTransaksi();
            formTransaksi.setVisible(true);
            
            formTransaksi.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadData(txtCari.getText().trim());
                }
            });
        });

        btnLihatDetail.addActionListener(e -> actionLihatDetail());
        btnCetakInvoice.addActionListener(e -> actionCetakInvoice());
        btnCetakLaporanPeriode.addActionListener(e -> actionCetakLaporanPeriode());

        // Load data perdana
        loadData("");
    }

    private void actionCetakLaporanPeriode() {
        // Membuat desain panel form untuk memilih tanggal
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        
        panel.add(new JLabel("Pilih Tanggal Awal:"));
        JSpinner spinnerTglAwal = new JSpinner(new SpinnerDateModel());
        spinnerTglAwal.setEditor(new JSpinner.DateEditor(spinnerTglAwal, "yyyy-MM-dd"));
        panel.add(spinnerTglAwal);

        panel.add(new JLabel("Pilih Tanggal Akhir:"));
        JSpinner spinnerTglAkhir = new JSpinner(new SpinnerDateModel());
        spinnerTglAkhir.setEditor(new JSpinner.DateEditor(spinnerTglAkhir, "yyyy-MM-dd"));
        panel.add(spinnerTglAkhir);

        // Munculkan popup dialog ke kasir/admin
        int result = JOptionPane.showConfirmDialog(this, panel, "Cetak Laporan Penjualan", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            // Ambil tanggal dari spinner
            java.util.Date tglAwal = (java.util.Date) spinnerTglAwal.getValue();
            java.util.Date tglAkhir = (java.util.Date) spinnerTglAkhir.getValue();

            // Validasi: Tgl Akhir tidak boleh lebih kecil dari Tgl Awal
            if (tglAkhir.before(tglAwal)) {
                JOptionPane.showMessageDialog(this, "Tanggal akhir tidak boleh lebih awal dari tanggal awal!", "Error Validasi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // ==========================================================
            // PROSES CETAK JASPER REPORT
            // ==========================================================
            JDialog loadingDialog = new JDialog(this, "Memproses Laporan", true);
            loadingDialog.setLayout(new BorderLayout(10, 10));
            loadingDialog.setSize(300, 110);
            loadingDialog.setLocationRelativeTo(this);
            loadingDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

            JProgressBar progressBar = new JProgressBar();
            progressBar.setIndeterminate(true); 
            progressBar.setString("Menarik data periode...");
            progressBar.setStringPainted(true); 

            loadingDialog.add(new JLabel("Mohon tunggu sebentar...", SwingConstants.CENTER), BorderLayout.NORTH);
            loadingDialog.add(progressBar, BorderLayout.CENTER);

            SwingWorker<net.sf.jasperreports.engine.JasperPrint, Void> worker = new SwingWorker<>() {
                @Override
                protected net.sf.jasperreports.engine.JasperPrint doInBackground() throws Exception {
                    Connection conn = Database.getKoneksi();
                    if (conn == null) throw new Exception("Koneksi database gagal!");

                    java.io.InputStream reportStream = getClass().getResourceAsStream("/report/LaporanPenjualan.jrxml");
                    if (reportStream == null) {
                        java.io.File fileReport = new java.io.File("src/report/LaporanPenjualan.jrxml");
                        if (fileReport.exists()) reportStream = new java.io.FileInputStream(fileReport);
                    }

                    if (reportStream == null) {
                        throw new Exception("File template 'LaporanPenjualan.jrxml' tidak ditemukan!");
                    }

                    try {
                        net.sf.jasperreports.engine.JasperReport jasperReport = net.sf.jasperreports.engine.JasperCompileManager.compileReport(reportStream);
                        
                        // Memasukkan Parameter Tanggal
                        java.util.HashMap<String, Object> parameters = new java.util.HashMap<>();
                        parameters.put("param_tgl_awal", tglAwal); 
                        parameters.put("param_tgl_akhir", tglAkhir); 

                        return net.sf.jasperreports.engine.JasperFillManager.fillReport(jasperReport, parameters, conn);
                    } finally {
                        if (reportStream != null) reportStream.close(); 
                    }
                }

                @Override
                protected void done() {
                    loadingDialog.dispose(); 
                    try {
                        net.sf.jasperreports.engine.JasperPrint print = get();

                        if (print.getPages().isEmpty()) {
                            JOptionPane.showMessageDialog(ViewListTransaksi.this, "Tidak ada transaksi penjualan pada periode tanggal tersebut.", "Data Kosong", JOptionPane.INFORMATION_MESSAGE);
                            return;
                        }

                        net.sf.jasperreports.view.JasperViewer.viewReport(print, false); 

                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(ViewListTransaksi.this, "Gagal mencetak laporan:\n" + ex.getMessage(), "Error Laporan", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };

            worker.execute(); 
            loadingDialog.setVisible(true); 
        }
    }
    
    
    // Method untuk menarik data dari Database (Dengan 10 Kolom & Subquery)
    private void loadData(String keyword) {
        tableModel.setRowCount(0); 

        try {
            Connection conn = Database.getKoneksi();
            if (conn == null) return;

            String sql = "SELECT t.id_transaksi, t.nomor_invoice, t.tanggal_transaksi, "
                       + "p.nama_pelanggan, u.nama AS nama_kasir, t.status_pembayaran, "
                       + "t.metode_pembayaran, "
                       + "(SELECT COALESCE(SUM(harga_jual_satuan), 0) FROM detail_transaksi WHERE id_transaksi = t.id_transaksi) AS total_harga, "
                       + "(SELECT COALESCE(SUM(nominal_masuk), 0) FROM riwayat_pembayaran WHERE id_transaksi = t.id_transaksi) AS uang_dibayar "
                       + "FROM transaksi t "
                       + "JOIN pelanggan p ON t.id_pelanggan = p.id_pelanggan "
                       + "JOIN user u ON t.id_user = u.id_user ";
            
            if (!keyword.isEmpty()) {
                sql += "WHERE t.nomor_invoice LIKE ? OR p.nama_pelanggan LIKE ? ";
            }
            
            sql += "ORDER BY t.id_transaksi DESC";

            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                if (!keyword.isEmpty()) {
                    pst.setString(1, "%" + keyword + "%");
                    pst.setString(2, "%" + keyword + "%");
                }

                try (ResultSet rs = pst.executeQuery()) {
                    while (rs.next()) {
                        int id = rs.getInt("id_transaksi");
                        String invoice = rs.getString("nomor_invoice");
                        String tanggal = rs.getString("tanggal_transaksi");
                        String pelanggan = rs.getString("nama_pelanggan");
                        String kasir = rs.getString("nama_kasir");
                        String status = rs.getString("status_pembayaran");
                        String metode = rs.getString("metode_pembayaran");
                        
                        double totalHarga = rs.getDouble("total_harga");
                        double uangDibayar = rs.getDouble("uang_dibayar");
                        
                        // --- HITUNG SISA TAGIHAN ---
                        double sisaTagihan = totalHarga - uangDibayar;
                        if (sisaTagihan < 0) sisaTagihan = 0; // Menghindari sisa minus jika ada pembulatan

                        // Format Semua Variabel Uang ke Rupiah
                        String strTotal = String.format("Rp %,.0f", totalHarga);
                        String strUang = String.format("Rp %,.0f", uangDibayar);
                        String strSisa = String.format("Rp %,.0f", sisaTagihan);

                        Object[] rowData = {id, invoice, tanggal, pelanggan, kasir, status, metode, strTotal, strUang, strSisa};
                        tableModel.addRow(rowData);
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data transaksi:\n" + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

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

                    JTextArea textArea = new JTextArea(detailMsg.toString());
                    textArea.setEditable(false);
                    textArea.setFont(new Font("Monospaced", Font.PLAIN, 14)); 
                    
                    JOptionPane.showMessageDialog(this, new JScrollPane(textArea), "Detail Transaksi", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal mengambil detail transaksi:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actionTambahPembayaran() {
        int selectedRow = tableTransaksi.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Silakan pilih salah satu transaksi di tabel terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idTransaksi = (int) tableModel.getValueAt(selectedRow, 0);
        String invoice = (String) tableModel.getValueAt(selectedRow, 1);
        String statusBayar = (String) tableModel.getValueAt(selectedRow, 5); 

        if ("Lunas".equalsIgnoreCase(statusBayar)) {
            JOptionPane.showMessageDialog(this, "Transaksi ini sudah berstatus Lunas!", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            Connection conn = Database.getKoneksi();
            if (conn == null) return;

            // Hitung Total Belanja dari detail
            double totalBelanja = 0;
            String sqlTotal = "SELECT COALESCE(SUM(harga_jual_satuan), 0) AS total FROM detail_transaksi WHERE id_transaksi = ?";
            try (PreparedStatement pstTotal = conn.prepareStatement(sqlTotal)) {
                pstTotal.setInt(1, idTransaksi);
                try (ResultSet rs = pstTotal.executeQuery()) {
                    if (rs.next()) totalBelanja = rs.getDouble("total");
                }
            }

            // Hitung akumulasi uang masuk dari riwayat
            double totalUangMasuk = 0;
            int jumlahTahapBayar = 0;
            String sqlRiwayatSum = "SELECT COALESCE(SUM(nominal_masuk), 0) AS total_masuk, COUNT(*) AS jml_baris FROM riwayat_pembayaran WHERE id_transaksi = ?";
            try (PreparedStatement pstSum = conn.prepareStatement(sqlRiwayatSum)) {
                pstSum.setInt(1, idTransaksi);
                try (ResultSet rs = pstSum.executeQuery()) {
                    if (rs.next()) {
                        totalUangMasuk = rs.getDouble("total_masuk");
                        jumlahTahapBayar = rs.getInt("jml_baris");
                    }
                }
            }

            double sisaTagihan = totalBelanja - totalUangMasuk;

            if (sisaTagihan <= 0) {
                try (PreparedStatement pstFix = conn.prepareStatement("UPDATE transaksi SET status_pembayaran = 'Lunas' WHERE id_transaksi = ?")) {
                    pstFix.setInt(1, idTransaksi);
                    pstFix.executeUpdate();
                }
                JOptionPane.showMessageDialog(this, "Tagihan transaksi ini sebenarnya sudah lunas. Status diperbarui.", "Info", JOptionPane.INFORMATION_MESSAGE);
                loadData(txtCari.getText().trim());
                return;
            }

            String pesanDialog = "Invoice: " + invoice + "\n"
                    + "Total Belanja: Rp " + String.format("%,.0f", totalBelanja) + "\n"
                    + "Sudah Dibayar: Rp " + String.format("%,.0f", totalUangMasuk) + "\n"
                    + "Sisa Tagihan: Rp " + String.format("%,.0f", sisaTagihan) + "\n\n"
                    + "Masukkan Nominal Pembayaran Baru (Rp):";

            String inputUang = JOptionPane.showInputDialog(this, pesanDialog, "Input Pembayaran Cicilan / Pelunasan", JOptionPane.QUESTION_MESSAGE);
            
            if (inputUang == null) return; 

            double uangMasukBaru = 0;
            try {
                uangMasukBaru = Double.parseDouble(inputUang.trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Input harus berupa angka nominal yang valid!", "Error Input", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (uangMasukBaru <= 0) {
                JOptionPane.showMessageDialog(this, "Nominal pembayaran baru harus lebih besar dari Rp 0!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (uangMasukBaru > sisaTagihan) {
                JOptionPane.showMessageDialog(this, "Uang yang dimasukkan melebihi sisa tagihan!", "Kelebihan Bayar", JOptionPane.ERROR_MESSAGE);
                return;
            }

            conn.setAutoCommit(false);
            try {
                String sqlInsertBayar = "INSERT INTO riwayat_pembayaran (id_transaksi, nomor_invoice, nominal_masuk, keterangan) VALUES (?, ?, ?, ?)";
                int urutanDP = jumlahTahapBayar + 1;
                String penandaInvoice = (uangMasukBaru == sisaTagihan) ? "-LUNAS" : "-DP" + urutanDP;
                String ketBayar = (uangMasukBaru == sisaTagihan) ? "Pelunasan Akhir" : "Pembayaran DP Ke-" + urutanDP;

                try (PreparedStatement pstIns = conn.prepareStatement(sqlInsertBayar)) {
                    pstIns.setInt(1, idTransaksi);
                    pstIns.setString(2, invoice + penandaInvoice);
                    pstIns.setDouble(3, uangMasukBaru);
                    pstIns.setString(4, ketBayar);
                    pstIns.executeUpdate();
                }

                double akumulasiUangMasuk = totalUangMasuk + uangMasukBaru;
                String statusAkhir = (akumulasiUangMasuk >= totalBelanja) ? "Lunas" : "DP";
                
                String sqlUpdateMaster = "UPDATE transaksi SET status_pembayaran = ?, uang_dibayar = ? WHERE id_transaksi = ?";
                try (PreparedStatement pstUpMaster = conn.prepareStatement(sqlUpdateMaster)) {
                    pstUpMaster.setString(1, statusAkhir);
                    pstUpMaster.setDouble(2, akumulasiUangMasuk);
                    pstUpMaster.setInt(3, idTransaksi);
                    pstUpMaster.executeUpdate();
                }

                conn.commit();
                JOptionPane.showMessageDialog(this, "Pembayaran berhasil dicatat!\nStatus Transaksi saat ini: " + statusAkhir, "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadData(txtCari.getText().trim());

            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal memproses pembayaran:\n" + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
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
        // FITUR POPUP PILIHAN RIWAYAT PEMBAYARAN
        // ==========================================================
        int idPembayaranDipilih = -1;
        
        try {
            Connection conn = Database.getKoneksi();
            if (conn == null) return;

            String sqlRiwayat = "SELECT id_pembayaran, nomor_invoice, keterangan, nominal_masuk FROM riwayat_pembayaran WHERE id_transaksi = ? ORDER BY id_pembayaran ASC";
            java.util.List<String> listPilihan = new java.util.ArrayList<>();
            java.util.List<Integer> listId = new java.util.ArrayList<>();
            
            try (PreparedStatement pst = conn.prepareStatement(sqlRiwayat)) {
                pst.setInt(1, idTransaksi);
                try (ResultSet rs = pst.executeQuery()) {
                    while (rs.next()) {
                        listId.add(rs.getInt("id_pembayaran"));
                        String teksInfo = rs.getString("nomor_invoice") + " | " + rs.getString("keterangan") + " (Rp " + String.format("%,.0f", rs.getDouble("nominal_masuk")) + ")";
                        listPilihan.add(teksInfo);
                    }
                }
            }

            if (listPilihan.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Data riwayat pembayaran tidak ditemukan untuk transaksi ini!", "Error Laporan", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String pilihanTerpilih = (String) JOptionPane.showInputDialog(this, 
                    "Pilih riwayat pembayaran yang ingin dicetak:", 
                    "Pilih Nota Invoice", 
                    JOptionPane.QUESTION_MESSAGE, 
                    null, 
                    listPilihan.toArray(), 
                    listPilihan.get(listPilihan.size() - 1));

            if (pilihanTerpilih == null) return; 

            int selectedIndex = listPilihan.indexOf(pilihanTerpilih);
            idPembayaranDipilih = listId.get(selectedIndex);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal meload riwayat: " + ex.getMessage());
            return;
        }

        // ==========================================================
        // PROSES CETAK JASPER
        // ==========================================================
        JDialog loadingDialog = new JDialog(this, "Memproses Laporan", true);
        loadingDialog.setLayout(new BorderLayout(10, 10));
        loadingDialog.setSize(300, 110);
        loadingDialog.setLocationRelativeTo(this);
        loadingDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true); 
        progressBar.setString("Menghasilkan invoice...");
        progressBar.setStringPainted(true); 

        loadingDialog.add(new JLabel("Mohon tunggu sebentar...", SwingConstants.CENTER), BorderLayout.NORTH);
        loadingDialog.add(progressBar, BorderLayout.CENTER);

        final int finalIdPembayaran = idPembayaranDipilih;

        SwingWorker<net.sf.jasperreports.engine.JasperPrint, Void> worker = new SwingWorker<>() {
            
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
                    net.sf.jasperreports.engine.JasperReport jasperReport = net.sf.jasperreports.engine.JasperCompileManager.compileReport(reportStream);
                    java.util.HashMap<String, Object> parameters = new java.util.HashMap<>();
                    
                    parameters.put("param_id_pembayaran", finalIdPembayaran); 

                    return net.sf.jasperreports.engine.JasperFillManager.fillReport(jasperReport, parameters, conn);
                } finally {
                    if (reportStream != null) reportStream.close(); 
                }
            }

            @Override
            protected void done() {
                loadingDialog.dispose(); 

                try {
                    net.sf.jasperreports.engine.JasperPrint print = get();

                    if (print.getPages().isEmpty()) {
                        JOptionPane.showMessageDialog(ViewListTransaksi.this, "Laporan berhasil dimuat tapi isinya KOSONG!\nPastikan param_id_pembayaran sudah sesuai di Jaspersoft Studio.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    net.sf.jasperreports.view.JasperViewer.viewReport(print, false); 

                } catch (Exception ex) {
                    String msg = ex.getMessage();
                    if (ex.getCause() != null) msg = ex.getCause().getMessage();
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(ViewListTransaksi.this, "Gagal mencetak invoice:\n" + msg, "Error Laporan", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.execute(); 
        loadingDialog.setVisible(true); 
    }
    
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ex) { ex.printStackTrace(); }
        SwingUtilities.invokeLater(() -> new ViewListTransaksi().setVisible(true));
    }
}