package sistemmanajemenpenjualanhewanternak.views;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.Date;
import sistemmanajemenpenjualanhewanternak.Utils.Database;

public class FormInputKambing extends  javax.swing.JFrame {

    // 1. Class Wrapper untuk menyimpan ID dan Nama secara bersamaan di dalam ComboBox
    class SupplierItem {
        private int id;
        private String nama;

        public SupplierItem(int id, String nama) {
            this.id = id;
            this.nama = nama;
        }

        public int getId() { return id; }

        // Method toString() ini yang akan menentukan teks apa yang muncul di layar (Dropdown)
        @Override
        public String toString() {
            return nama;
        }
    }

    // Deklarasi Komponen UI
    private JTextField txtKodeKambing;
    private JComboBox<SupplierItem> cbSupplier; // Mengubah JTextField menjadi JComboBox
    private JRadioButton rbJantan, rbBetina;
    private ButtonGroup bgJenisKelamin;
    private JTextField txtBeratAwal;
    private JTextField txtHargaModal;
    private JComboBox<String> cbStatus;
    private JSpinner spinnerTanggalMasuk;
    private JButton btnSimpan, btnBatal;

    public FormInputKambing() {
        setTitle("Form Input Data Kambing");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); 

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); 
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 1. Kode Kambing
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(new JLabel("Kode Kambing:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        txtKodeKambing = new JTextField(15);
        mainPanel.add(txtKodeKambing, gbc);

        // 2. Dropdown Supplier
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(new JLabel("Supplier:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        cbSupplier = new JComboBox<>();
        loadDataSupplier(); // Panggil fungsi untuk mengambil data dari tabel supplier_kambing
        mainPanel.add(cbSupplier, gbc);

        // 3. Jenis Kelamin
        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(new JLabel("Jenis Kelamin:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        JPanel panelJK = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        rbJantan = new JRadioButton("Jantan");
        rbBetina = new JRadioButton("Betina");
        bgJenisKelamin = new ButtonGroup();
        bgJenisKelamin.add(rbJantan);
        bgJenisKelamin.add(rbBetina);
        panelJK.add(rbJantan);
        panelJK.add(rbBetina);
        mainPanel.add(panelJK, gbc);

        // 4. Berat Awal
        gbc.gridx = 0; gbc.gridy = 3;
        mainPanel.add(new JLabel("Berat Awal (Kg):"), gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        txtBeratAwal = new JTextField(15);
        mainPanel.add(txtBeratAwal, gbc);

        // 5. Harga Modal
        gbc.gridx = 0; gbc.gridy = 4;
        mainPanel.add(new JLabel("Harga Modal (Rp):"), gbc);
        gbc.gridx = 1; gbc.gridy = 4;
        txtHargaModal = new JTextField(15);
        mainPanel.add(txtHargaModal, gbc);

        // 6. Status
        gbc.gridx = 0; gbc.gridy = 5;
        mainPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1; gbc.gridy = 5;
        String[] statusOptions = {"Tersedia", "Terjual", "Sakit", "Mati"};
        cbStatus = new JComboBox<>(statusOptions);
        mainPanel.add(cbStatus, gbc);

        // 7. Tanggal Masuk
        gbc.gridx = 0; gbc.gridy = 6;
        mainPanel.add(new JLabel("Tanggal Masuk:"), gbc);
        gbc.gridx = 1; gbc.gridy = 6;
        spinnerTanggalMasuk = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spinnerTanggalMasuk, "yyyy-MM-dd");
        spinnerTanggalMasuk.setEditor(dateEditor);
        spinnerTanggalMasuk.setValue(new Date()); 
        mainPanel.add(spinnerTanggalMasuk, gbc);

        // Panel Tombol
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSimpan = new JButton("Simpan");
        btnBatal = new JButton("Batal");
        buttonPanel.add(btnSimpan);
        buttonPanel.add(btnBatal);

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Event Listener
        btnSimpan.addActionListener(e -> simpanData());
        btnBatal.addActionListener(e -> dispose());
    }

    // Method untuk menarik data supplier dari database dan memasukkannya ke ComboBox
    private void loadDataSupplier() {
        // Opsi default jika kambing tersebut hasil ternak sendiri (tidak ada supplier)
        cbSupplier.addItem(new SupplierItem(0, "-- Pilih Supplier / Tanpa Supplier --"));

        try {
            Connection conn = Database.getKoneksi();
            if(conn == null) return;

            String sql = "SELECT id_supplier, nama_supplier FROM supplier_kambing ORDER BY nama_supplier ASC";
            try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    int id = rs.getInt("id_supplier");
                    String nama = rs.getString("nama_supplier");
                    cbSupplier.addItem(new SupplierItem(id, nama));
                }
            }
        } catch (SQLException ex) {
            System.err.println("Gagal memuat data supplier: " + ex.getMessage());
        }
    }

    private void simpanData() {
        String kode = txtKodeKambing.getText();
        String jk = rbJantan.isSelected() ? "Jantan" : (rbBetina.isSelected() ? "Betina" : "");
        String beratStr = txtBeratAwal.getText();
        String hargaStr = txtHargaModal.getText();
        String status = cbStatus.getSelectedItem().toString();
        java.util.Date tanggalMasuk = (java.util.Date) spinnerTanggalMasuk.getValue();

        // Mengambil ID Supplier dari item yang dipilih di ComboBox
        SupplierItem selectedSupplier = (SupplierItem) cbSupplier.getSelectedItem();
        int idSupplier = selectedSupplier.getId();

        if (kode.trim().isEmpty() || jk.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Kode dan Jenis Kelamin wajib diisi!", "Validasi Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "INSERT INTO kambing (kode_kambing, id_supplier, jenis_kelamin, berat_awal, harga_modal, status, tanggal_masuk) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = Database.getKoneksi();
        if (conn == null) return;

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, kode);

            // Jika milih "Tanpa Supplier" (ID 0), simpan sebagai NULL di database (sesuai ERD)
            if (idSupplier == 0) {
                pst.setNull(2, Types.INTEGER);
            } else {
                pst.setInt(2, idSupplier);
            }

            pst.setString(3, jk);
            pst.setDouble(4, beratStr.trim().isEmpty() ? 0.0 : Double.parseDouble(beratStr.trim()));
            pst.setDouble(5, hargaStr.trim().isEmpty() ? 0.0 : Double.parseDouble(hargaStr.trim()));
            pst.setString(6, status);

            java.sql.Date sqlDate = new java.sql.Date(tanggalMasuk.getTime());
            pst.setDate(7, sqlDate);

            int affectedRows = pst.executeUpdate();
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "Data kambing berhasil disimpan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                resetForm(); 
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Input angka tidak valid!", "Format Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan data ke database:\n" + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetForm() {
        txtKodeKambing.setText("");
        cbSupplier.setSelectedIndex(0); // Kembalikan ke pilihan default
        bgJenisKelamin.clearSelection();
        txtBeratAwal.setText("");
        txtHargaModal.setText("");
        cbStatus.setSelectedIndex(0);
        spinnerTanggalMasuk.setValue(new java.util.Date()); 
        txtKodeKambing.requestFocus();
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ex) { ex.printStackTrace(); }
        SwingUtilities.invokeLater(() -> new FormInputKambing().setVisible(true));
    }
}