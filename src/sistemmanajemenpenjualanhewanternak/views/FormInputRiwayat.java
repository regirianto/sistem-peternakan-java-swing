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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import sistemmanajemenpenjualanhewanternak.Utils.Database;

public class FormInputRiwayat extends JFrame {

    // 1. Class Wrapper untuk Dropdown Kambing
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
            return kode; // Menampilkan Kode Kambing di dalam dropdown
        }
    }

    // Deklarasi Komponen UI
    private JComboBox<KambingItem> cbKambing;
    private JSpinner spinnerTanggal;
    private JTextField txtBeratSaatIni;
    private JTextArea txtKeterangan;
    private JButton btnSimpan, btnBatal;

    public FormInputRiwayat() {
        setTitle("Form Input Riwayat Kesehatan");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); 

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // 1. Pilih Kambing (Dropdown)
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(new JLabel("Pilih Kambing:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.weightx = 1.0; 
        gbc.fill = GridBagConstraints.HORIZONTAL; 
        cbKambing = new JComboBox<>();
        loadDataKambing(); // Panggil data dari database
        mainPanel.add(cbKambing, gbc);

        // 2. Tanggal Pemeriksaan
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(new JLabel("Tgl Pemeriksaan:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        spinnerTanggal = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spinnerTanggal, "yyyy-MM-dd");
        spinnerTanggal.setEditor(dateEditor);
        spinnerTanggal.setValue(new Date()); 
        mainPanel.add(spinnerTanggal, gbc);

        // 3. Berat Saat Ini (Kg)
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(new JLabel("Berat Saat Ini (Kg):"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtBeratSaatIni = new JTextField();
        mainPanel.add(txtBeratSaatIni, gbc);

        // 4. Keterangan / Hasil Pemeriksaan
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST; 
        mainPanel.add(new JLabel("Keterangan Medis:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0; // Bobot vertikal agar JTextArea memanjang
        gbc.fill = GridBagConstraints.BOTH; 
        txtKeterangan = new JTextArea(); 
        txtKeterangan.setLineWrap(true);
        txtKeterangan.setWrapStyleWord(true);
        JScrollPane scrollKeterangan = new JScrollPane(txtKeterangan);
        mainPanel.add(scrollKeterangan, gbc);
        
        // Panel Tombol
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSimpan = new JButton("Simpan");
        btnBatal = new JButton("Batal");
        buttonPanel.add(btnSimpan);
        buttonPanel.add(btnBatal);

        // Menambahkan ke Frame
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Event Listener
        btnSimpan.addActionListener(e -> simpanData());
        btnBatal.addActionListener(e -> dispose());
    }

    // Mengambil data kambing untuk dimasukkan ke ComboBox
    private void loadDataKambing() {
        try {
            Connection conn = Database.getKoneksi();
            if(conn == null) return;

            // Kita urutkan berdasarkan kode kambing agar rapi
            String sql = "SELECT id_kambing, kode_kambing FROM kambing ORDER BY kode_kambing ASC";
            try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    int id = rs.getInt("id_kambing");
                    String kode = rs.getString("kode_kambing");
                    cbKambing.addItem(new KambingItem(id, kode));
                }
            }
        } catch (SQLException ex) {
            System.err.println("Gagal memuat data kambing: " + ex.getMessage());
        }
    }

private void simpanData() {
        // Ambil objek kambing yang dipilih, lalu ekstrak ID-nya
        KambingItem selectedKambing = (KambingItem) cbKambing.getSelectedItem();
        
        if (selectedKambing == null) {
            JOptionPane.showMessageDialog(this, "Data kambing masih kosong! Pastikan master data kambing sudah terisi.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int idKambing = selectedKambing.getId();
        Date tanggalPeriksa = (Date) spinnerTanggal.getValue();
        String beratStr = txtBeratSaatIni.getText();
        String keterangan = txtKeterangan.getText();

        if (keterangan.trim().isEmpty() || beratStr.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Berat dan Keterangan Medis wajib diisi!", "Validasi Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double beratBaru;
        try {
            beratBaru = Double.parseDouble(beratStr.trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Input berat harus berupa angka!", "Format Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Query 1: Menyimpan riwayat kesehatan
        String sqlInsertRiwayat = "INSERT INTO riwayat_kesehatan (id_kambing, tanggal_pemeriksaan, keterangan, berat_saat_ini) VALUES (?, ?, ?, ?)";
        
        // Query 2: Mengupdate berat kambing di tabel master kambing
        String sqlUpdateBeratKambing = "UPDATE kambing SET berat_awal = ? WHERE id_kambing = ?";
        
        Connection conn = Database.getKoneksi();
        if (conn == null) return;

        try (PreparedStatement pstInsert = conn.prepareStatement(sqlInsertRiwayat)) {
            
            // --- 1. Eksekusi Insert Riwayat ---
            pstInsert.setInt(1, idKambing);
            java.sql.Date sqlDate = new java.sql.Date(tanggalPeriksa.getTime());
            pstInsert.setDate(2, sqlDate);
            pstInsert.setString(3, keterangan);
            pstInsert.setDouble(4, beratBaru);

            int affectedRows = pstInsert.executeUpdate();
            
            if (affectedRows > 0) {
                // --- 2. Eksekusi Update Berat Kambing JIKA Insert Sukses ---
                try (PreparedStatement pstUpdate = conn.prepareStatement(sqlUpdateBeratKambing)) {
                    pstUpdate.setDouble(1, beratBaru);
                    pstUpdate.setInt(2, idKambing);
                    pstUpdate.executeUpdate(); 
                    // Kita tidak perlu mengecek affectedRows di sini karena pasti update (ID valid)
                }

                JOptionPane.showMessageDialog(this, "Riwayat kesehatan dicatat & Berat master kambing berhasil diperbarui!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                dispose(); 
            }
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal memproses ke database:\n" + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ex) { ex.printStackTrace(); }
        SwingUtilities.invokeLater(() -> new FormInputRiwayat().setVisible(true));
    }
}