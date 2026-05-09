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
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import sistemmanajemenpenjualanhewanternak.Utils.Database;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FormTransaksi extends JFrame {

    // --- Wrapper Classes untuk Dropdown ---
    class PelangganItem {
        int id; String nama;
        public PelangganItem(int id, String nama) { this.id = id; this.nama = nama; }
        public int getId() { return id; }
        @Override public String toString() { return nama; }
    }

    class UserItem {
        int id; String nama;
        public UserItem(int id, String nama) { this.id = id; this.nama = nama; }
        public int getId() { return id; }
        @Override public String toString() { return nama; }
    }

    class KambingItem {
        int id; String kode; double hargaModal;
        public KambingItem(int id, String kode, double hargaModal) { this.id = id; this.kode = kode; this.hargaModal = hargaModal;}
        public int getId() { return id; }
        public double getHargaModal() { return hargaModal; }
        @Override public String toString() { return kode; }
    }

    // --- Komponen Header (Transaksi) ---
    private JTextField txtInvoice;
    private JComboBox<PelangganItem> cbPelanggan;
    private JComboBox<UserItem> cbUser;
    private JComboBox<String> cbMetodeBayar;
    private JComboBox<String> cbStatusBayar;
    private JSpinner spinnerTanggal;
    private JTextField txtUangDibayar; // KOMPONEN BARU

    // --- Komponen Detail (Keranjang) ---
    private JComboBox<KambingItem> cbPilihKambing;
    private JTextField txtHargaJual;
    private JButton btnTambahKambing, btnHapusKambing;
    private JTable tableKeranjang;
    private DefaultTableModel tableModel;
    private JLabel lblTotalHarga;

    private JButton btnSimpanTransaksi, btnBatal;

    public FormTransaksi() {
        setTitle("Form Transaksi Penjualan Kambing");
        setSize(850, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // ==================== PANEL HEADER (INFORMASI TRANSAKSI) ====================
        JPanel headerPanel = new JPanel(new GridBagLayout());
        headerPanel.setBorder(BorderFactory.createTitledBorder("Informasi Transaksi"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; headerPanel.add(new JLabel("No. Invoice:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0; 
        txtInvoice = new JTextField("INV-" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        txtInvoice.setEditable(false); 
        headerPanel.add(txtInvoice, gbc);

        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0.0; headerPanel.add(new JLabel("Tanggal:"), gbc);
        gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 1.0;
        spinnerTanggal = new JSpinner(new SpinnerDateModel());
        spinnerTanggal.setEditor(new JSpinner.DateEditor(spinnerTanggal, "yyyy-MM-dd HH:mm:ss"));
        headerPanel.add(spinnerTanggal, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0; headerPanel.add(new JLabel("Pelanggan:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        cbPelanggan = new JComboBox<>();
        headerPanel.add(cbPelanggan, gbc);

        gbc.gridx = 2; gbc.gridy = 1; gbc.weightx = 0.0; headerPanel.add(new JLabel("Kasir/User:"), gbc);
        gbc.gridx = 3; gbc.gridy = 1; gbc.weightx = 1.0;
        cbUser = new JComboBox<>();
        headerPanel.add(cbUser, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.0; headerPanel.add(new JLabel("Metode:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1.0;
        cbMetodeBayar = new JComboBox<>(new String[]{"Cash", "Transfer"});
        headerPanel.add(cbMetodeBayar, gbc);

        // --- TAMBAHAN BARU: UANG DIBAYAR ---
        gbc.gridx = 2; gbc.gridy = 2; gbc.weightx = 0.0; headerPanel.add(new JLabel("Uang Dibayar (Rp):"), gbc);
        gbc.gridx = 3; gbc.gridy = 2; gbc.weightx = 1.0;
        txtUangDibayar = new JTextField("0");
        headerPanel.add(txtUangDibayar, gbc);

        // --- STATUS BAYAR DIBUAT READ-ONLY (OTOMATIS) ---
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.0; headerPanel.add(new JLabel("Status Bayar:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 1.0;
        cbStatusBayar = new JComboBox<>(new String[]{"DP", "Lunas"});
        cbStatusBayar.setEnabled(false); // Dimatikan karena diatur oleh sistem
        headerPanel.add(cbStatusBayar, gbc);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // ==================== PANEL TENGAH (KERANJANG BELANJA) ====================
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.setBorder(BorderFactory.createTitledBorder("Detail Kambing yang Dibeli"));

        JPanel inputDetailPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputDetailPanel.add(new JLabel("Pilih Kambing:"));
        cbPilihKambing = new JComboBox<>();
        cbPilihKambing.setPreferredSize(new Dimension(150, 25));
        inputDetailPanel.add(cbPilihKambing);
        
        inputDetailPanel.add(new JLabel("Harga Jual (Rp):"));
        txtHargaJual = new JTextField(10);
        inputDetailPanel.add(txtHargaJual);

        btnTambahKambing = new JButton("Tambah ke Keranjang");
        inputDetailPanel.add(btnTambahKambing);

        btnHapusKambing = new JButton("Hapus Pilihan");
        inputDetailPanel.add(btnHapusKambing);

        centerPanel.add(inputDetailPanel, BorderLayout.NORTH);

        String[] cols = {"ID Kambing", "Kode Kambing", "Harga Jual"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tableKeranjang = new JTable(tableModel);
        centerPanel.add(new JScrollPane(tableKeranjang), BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // ==================== PANEL BAWAH (TOTAL & SIMPAN) ====================
        JPanel bottomPanel = new JPanel(new BorderLayout());
        
        lblTotalHarga = new JLabel("Total: Rp 0");
        lblTotalHarga.setFont(new Font("Arial", Font.BOLD, 20));
        lblTotalHarga.setForeground(Color.RED);
        bottomPanel.add(lblTotalHarga, BorderLayout.WEST);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSimpanTransaksi = new JButton("Simpan Transaksi");
        btnSimpanTransaksi.setFont(new Font("Arial", Font.BOLD, 14));
        btnBatal = new JButton("Batal");
        actionPanel.add(btnSimpanTransaksi);
        actionPanel.add(btnBatal);
        bottomPanel.add(actionPanel, BorderLayout.EAST);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        add(mainPanel);

        // ==================== EVENT LISTENERS ====================

        cbPilihKambing.addActionListener(e -> {
            KambingItem k = (KambingItem) cbPilihKambing.getSelectedItem();
            if (k != null) {
                double saranHarga = k.getHargaModal() + 500000;
                txtHargaJual.setText(String.format("%.0f", saranHarga));
            }
        });

        // Event listener untuk Real-time status DP/Lunas saat mengetik Uang Dibayar
        txtUangDibayar.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { cekStatusPembayaranOtomatis(); }
            public void removeUpdate(DocumentEvent e) { cekStatusPembayaranOtomatis(); }
            public void insertUpdate(DocumentEvent e) { cekStatusPembayaranOtomatis(); }
        });

        btnTambahKambing.addActionListener(e -> tambahKeKeranjang());
        btnHapusKambing.addActionListener(e -> hapusDariKeranjang());
        btnSimpanTransaksi.addActionListener(e -> prosesSimpanTransaksi());
        btnBatal.addActionListener(e -> dispose());

        loadMasterData();
    }

    // --- LOGIKA PERHITUNGAN DAN VALIDASI ---

    private double getTotalBelanja() {
        double total = 0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            total += (double) tableModel.getValueAt(i, 2);
        }
        return total;
    }

    private void hitungTotal() {
        double total = getTotalBelanja();
        lblTotalHarga.setText(String.format("Total: Rp %,.0f", total));
        cekStatusPembayaranOtomatis(); // Panggil pengecekan status tiap keranjang berubah
    }

    // Fungsi otomatis mengubah Status berdasar Uang Dibayar vs Total Belanja
    private void cekStatusPembayaranOtomatis() {
        double total = getTotalBelanja();
        if (total == 0) {
            cbStatusBayar.setSelectedItem("DP");
            return;
        }

        try {
            double uangDibayar = Double.parseDouble(txtUangDibayar.getText().trim());
            if (uangDibayar >= total) {
                cbStatusBayar.setSelectedItem("Lunas");
            } else {
                cbStatusBayar.setSelectedItem("DP");
            }
        } catch (NumberFormatException ex) {
            cbStatusBayar.setSelectedItem("DP");
        }
    }

    private void loadMasterData() {
        Connection conn = Database.getKoneksi();
        if (conn == null) return;
        try (Statement st = conn.createStatement()) {
            ResultSet rsP = st.executeQuery("SELECT id_pelanggan, nama_pelanggan FROM pelanggan");
            while (rsP.next()) cbPelanggan.addItem(new PelangganItem(rsP.getInt(1), rsP.getString(2)));
            rsP.close();

            ResultSet rsU = st.executeQuery("SELECT id_user, nama FROM user");
            boolean hasUser = false;
            while (rsU.next()) {
                cbUser.addItem(new UserItem(rsU.getInt(1), rsU.getString(2)));
                hasUser = true;
            }
            rsU.close();
            
            if (!hasUser) {
                st.executeUpdate("INSERT INTO user (username, password, nama) VALUES ('admin', 'admin', 'Super Admin')");
                cbUser.addItem(new UserItem(1, "Super Admin"));
            }

            loadKambingTersedia();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal meload master data: " + ex.getMessage());
        }
    }

    private void loadKambingTersedia() {
        cbPilihKambing.removeAllItems();
        try {
            Connection conn = Database.getKoneksi();
            Statement st = conn.createStatement();
            ResultSet rsK = st.executeQuery("SELECT id_kambing, kode_kambing, harga_modal FROM kambing WHERE status = 'Tersedia'");
            while (rsK.next()) {
                cbPilihKambing.addItem(new KambingItem(rsK.getInt(1), rsK.getString(2), rsK.getDouble(3)));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void tambahKeKeranjang() {
        KambingItem k = (KambingItem) cbPilihKambing.getSelectedItem();
        String hargaStr = txtHargaJual.getText();

        if (k == null) { JOptionPane.showMessageDialog(this, "Tidak ada kambing tersedia!"); return; }
        if (hargaStr.isEmpty()) { JOptionPane.showMessageDialog(this, "Harga jual harus diisi!"); return; }

        try {
            double hargaJual = Double.parseDouble(hargaStr);
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if ((int) tableModel.getValueAt(i, 0) == k.getId()) {
                    JOptionPane.showMessageDialog(this, "Kambing ini sudah ada di keranjang!");
                    return;
                }
            }
            tableModel.addRow(new Object[]{k.getId(), k.kode, hargaJual});
            hitungTotal();
            cbPilihKambing.removeItem(k);
            txtHargaJual.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Format harga tidak valid!");
        }
    }

    private void hapusDariKeranjang() {
        int row = tableKeranjang.getSelectedRow();
        if (row != -1) {
            tableModel.removeRow(row);
            hitungTotal();
            loadKambingTersedia();
            for(int i=0; i<tableModel.getRowCount(); i++){
                int idDiKeranjang = (int) tableModel.getValueAt(i, 0);
                for(int j=0; j<cbPilihKambing.getItemCount(); j++){
                    if(cbPilihKambing.getItemAt(j).getId() == idDiKeranjang){
                        cbPilihKambing.removeItemAt(j);
                        break;
                    }
                }
            }
        }
    }

    private void prosesSimpanTransaksi() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Keranjang belanja kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double totalBelanja = getTotalBelanja();
        double uangDibayar = 0;

        try {
            uangDibayar = Double.parseDouble(txtUangDibayar.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Format uang dibayar tidak valid angka!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // --- VALIDASI UANG ---
        if (uangDibayar > totalBelanja) {
            JOptionPane.showMessageDialog(this, 
                "Uang dibayarkan (Rp " + String.format("%,.0f", uangDibayar) + ") TIDAK BOLEH LEBIH dari total belanja (Rp " + String.format("%,.0f", totalBelanja) + ")!\nHarap perbaiki input uang dibayar.", 
                "Validasi Pembayaran", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (uangDibayar <= 0) {
            JOptionPane.showMessageDialog(this, "Uang dibayarkan minimal harus lebih dari 0 untuk DP!", "Validasi Pembayaran", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Tentukan status akhir yang masuk ke database untuk keamanan ekstra
        String finalStatusBayar = (uangDibayar == totalBelanja) ? "Lunas" : "DP";

        PelangganItem pelanggan = (PelangganItem) cbPelanggan.getSelectedItem();
        UserItem user = (UserItem) cbUser.getSelectedItem();
        if (pelanggan == null || user == null) return;

        String invoice = txtInvoice.getText();
        java.util.Date tgl = (java.util.Date) spinnerTanggal.getValue();
        String metodeBayar = cbMetodeBayar.getSelectedItem().toString();

        Connection conn = Database.getKoneksi();
        if (conn == null) return;

        try {
            conn.setAutoCommit(false);

            // Perubahan Query: Menambahkan uang_dibayar
            String sqlTransaksi = "INSERT INTO transaksi (id_pelanggan, id_user, nomor_invoice, tanggal_transaksi, status_pembayaran, metode_pembayaran, uang_dibayar) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstMaster = conn.prepareStatement(sqlTransaksi, Statement.RETURN_GENERATED_KEYS);
            pstMaster.setInt(1, pelanggan.getId());
            pstMaster.setInt(2, user.getId());
            pstMaster.setString(3, invoice);
            pstMaster.setTimestamp(4, new java.sql.Timestamp(tgl.getTime()));
            pstMaster.setString(5, finalStatusBayar); // Memakai status hasil validasi akhir
            pstMaster.setString(6, metodeBayar);
            pstMaster.setDouble(7, uangDibayar); // Data baru
            pstMaster.executeUpdate();

            ResultSet rsKeys = pstMaster.getGeneratedKeys();
            int idTransaksiBaru = rsKeys.next() ? rsKeys.getInt(1) : 0;

            String sqlDetail = "INSERT INTO detail_transaksi (id_transaksi, id_kambing, harga_jual_satuan) VALUES (?, ?, ?)";
            PreparedStatement pstDetail = conn.prepareStatement(sqlDetail);

            String sqlUpdateKambing = "UPDATE kambing SET status = 'Terjual' WHERE id_kambing = ?";
            PreparedStatement pstUpdate = conn.prepareStatement(sqlUpdateKambing);

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                int idKambing = (int) tableModel.getValueAt(i, 0);
                double hargaJual = (double) tableModel.getValueAt(i, 2);

                pstDetail.setInt(1, idTransaksiBaru);
                pstDetail.setInt(2, idKambing);
                pstDetail.setDouble(3, hargaJual);
                pstDetail.addBatch();

                pstUpdate.setInt(1, idKambing);
                pstUpdate.addBatch();
            }

            pstDetail.executeBatch();
            pstUpdate.executeBatch();

            conn.commit();
            JOptionPane.showMessageDialog(this, "Transaksi berhasil disimpan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (SQLException ex) {
            try { conn.rollback(); } catch (SQLException rEx) { rEx.printStackTrace(); }
            JOptionPane.showMessageDialog(this, "Error Database: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ex) {}
        SwingUtilities.invokeLater(() -> new FormTransaksi().setVisible(true));
    }
}