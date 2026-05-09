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
import java.sql.*;
import java.util.Date;
import sistemmanajemenpenjualanhewanternak.Utils.Database;

public class FormEditKambing extends JFrame {

    // Class Wrapper untuk Dropdown Supplier
    class SupplierItem {
        private int id;
        private String nama;

        public SupplierItem(int id, String nama) {
            this.id = id;
            this.nama = nama;
        }

        public int getId() { return id; }

        @Override
        public String toString() {
            return nama;
        }
    }

    // Variabel untuk menyimpan ID kambing yang sedang diedit
    private int currentKambingId;

    // Deklarasi Komponen UI
    private JTextField txtKodeKambing;
    private JComboBox<SupplierItem> cbSupplier;
    private JRadioButton rbJantan, rbBetina;
    private ButtonGroup bgJenisKelamin;
    private JTextField txtBeratAwal;
    private JTextField txtHargaModal;
    private JComboBox<String> cbStatus;
    private JSpinner spinnerTanggalMasuk;
    private JButton btnUpdate, btnBatal;

    // Konstruktor sekarang menerima parameter idKambing
    public FormEditKambing(int idKambing) {
        this.currentKambingId = idKambing;

        setTitle("Form Edit Data Kambing");
        setSize(400, 450);
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
        loadDataSupplier(); // Load pilihan supplier DULU sebelum data kambingnya
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
        mainPanel.add(spinnerTanggalMasuk, gbc);

        // Panel Tombol
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnUpdate = new JButton("Update Data"); // Nama tombol disesuaikan
        btnBatal = new JButton("Batal");
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnBatal);

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Event Listener
        btnUpdate.addActionListener(e -> updateData());
        btnBatal.addActionListener(e -> dispose());

        // TARIK DATA LAMA DARI DATABASE (Wajib dipanggil di akhir setelah UI jadi)
        loadDataKambingAsli(); 
    }

    private void loadDataSupplier() {
        cbSupplier.addItem(new SupplierItem(0, "-- Pilih Supplier / Tanpa Supplier --"));
        try {
            Connection conn = Database.getKoneksi();
            if(conn == null) return;

            String sql = "SELECT id_supplier, nama_supplier FROM supplier_kambing ORDER BY nama_supplier ASC";
            try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    cbSupplier.addItem(new SupplierItem(rs.getInt("id_supplier"), rs.getString("nama_supplier")));
                }
            }
        } catch (SQLException ex) {
            System.err.println("Gagal memuat supplier: " + ex.getMessage());
        }
    }

    // METHOD BARU: Untuk mengisi komponen form dengan data lama dari database
    private void loadDataKambingAsli() {
        try {
            Connection conn = Database.getKoneksi();
            if(conn == null) return;

            String sql = "SELECT * FROM kambing WHERE id_kambing = ?";
            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setInt(1, currentKambingId);
                
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        txtKodeKambing.setText(rs.getString("kode_kambing"));
                        
                        // Set Radio Button Jenis Kelamin
                        String jk = rs.getString("jenis_kelamin");
                        if ("Jantan".equalsIgnoreCase(jk)) {
                            rbJantan.setSelected(true);
                        } else {
                            rbBetina.setSelected(true);
                        }

                        // Set Berat dan Harga
                        txtBeratAwal.setText(String.valueOf(rs.getDouble("berat_awal")));
                        
                        // Format harga hilangkan desimal nol jika diperlukan, atau set murni
                        txtHargaModal.setText(String.valueOf(rs.getDouble("harga_modal")));

                        // Set Status di ComboBox
                        cbStatus.setSelectedItem(rs.getString("status"));

                        // Set Tanggal
                        Date tanggalMasuk = rs.getDate("tanggal_masuk");
                        if (tanggalMasuk != null) {
                            spinnerTanggalMasuk.setValue(tanggalMasuk);
                        }

                        // Set Supplier (Ini butuh logika looping untuk mencocokkan ID)
                        int idSupDb = rs.getInt("id_supplier"); // Jika NULL, JDBC mereturn 0
                        for (int i = 0; i < cbSupplier.getItemCount(); i++) {
                            SupplierItem item = cbSupplier.getItemAt(i);
                            if (item.getId() == idSupDb) {
                                cbSupplier.setSelectedIndex(i);
                                break;
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Data kambing tidak ditemukan!", "Error", JOptionPane.ERROR_MESSAGE);
                        dispose(); // Tutup form jika data tidak ada
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal meload data kambing:\n" + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // METHOD UPDATE: Merubah INSERT menjadi UPDATE
    private void updateData() {
        String kode = txtKodeKambing.getText();
        String jk = rbJantan.isSelected() ? "Jantan" : (rbBetina.isSelected() ? "Betina" : "");
        String beratStr = txtBeratAwal.getText();
        String hargaStr = txtHargaModal.getText();
        String status = cbStatus.getSelectedItem().toString();
        Date tanggalMasuk = (Date) spinnerTanggalMasuk.getValue();

        SupplierItem selectedSupplier = (SupplierItem) cbSupplier.getSelectedItem();
        int idSupplier = selectedSupplier.getId();

        if (kode.trim().isEmpty() || jk.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Kode dan Jenis Kelamin wajib diisi!", "Validasi Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // QUERY UPDATE menggunakan WHERE id_kambing = ?
        String sql = "UPDATE kambing SET kode_kambing=?, id_supplier=?, jenis_kelamin=?, berat_awal=?, harga_modal=?, status=?, tanggal_masuk=? WHERE id_kambing=?";
        
        Connection conn = Database.getKoneksi();
        if (conn == null) return;

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, kode);

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

            // Parameter ke-8 adalah id_kambing yang sedang kita edit
            pst.setInt(8, currentKambingId);

            int affectedRows = pst.executeUpdate();
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "Data kambing berhasil diupdate!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // Tutup form setelah berhasil update
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Input angka tidak valid!", "Format Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal mengupdate data ke database:\n" + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Untuk keperluan Testing secara langsung (Standalone)
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ex) { ex.printStackTrace(); }
        
        SwingUtilities.invokeLater(() -> {
            // Misal: Kita paksa buka form untuk mengedit Kambing dengan ID = 1 
            // (Pastikan kambing dengan ID 1 sudah ada di database dari dummy data sebelumnya)
            new FormEditKambing(1).setVisible(true);
        });
    }
}
