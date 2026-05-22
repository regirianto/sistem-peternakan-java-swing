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
import java.sql.SQLException;
import sistemmanajemenpenjualanhewanternak.Utils.Database;

public class FormInputSupplier extends JFrame {

    private JTextField txtNama;
    private JTextField txtKontak;
    private JTextArea txtAlamat;
    private JButton btnSimpan, btnBatal;

    public FormInputSupplier() {
        setTitle("Form Input Data Supplier");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); 

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // 1. Nama Supplier
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0.0; // Label tidak perlu ditarik
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST; // Rata kiri
        mainPanel.add(new JLabel("Nama Supplier:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.weightx = 1.0; // MEMBERIKAN BOBOT 1.0 AGAR TEXTFIELD MEMANJANG
        gbc.fill = GridBagConstraints.HORIZONTAL; // Memanjang secara horizontal
        txtNama = new JTextField(); 
        mainPanel.add(txtNama, gbc);

        // 2. Kontak (No HP/Telp)
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(new JLabel("Kontak (No. HP):"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtKontak = new JTextField();
        mainPanel.add(txtKontak, gbc);

        // 3. Alamat (JTextArea ditarik memanjang secara Vertikal & Horizontal)
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST; // Label menempel di kiri atas
        mainPanel.add(new JLabel("Alamat:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0; // MEMBERIKAN BOBOT VERTIKAL
        gbc.fill = GridBagConstraints.BOTH; // Memanjang atas-bawah dan kiri-kanan
        txtAlamat = new JTextArea(); 
        txtAlamat.setLineWrap(true);
        txtAlamat.setWrapStyleWord(true);
        JScrollPane scrollAlamat = new JScrollPane(txtAlamat);
        mainPanel.add(scrollAlamat, gbc);
        
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

    private void simpanData() {
        String nama = txtNama.getText();
        String kontak = txtKontak.getText();
        String alamat = txtAlamat.getText();

        if (nama.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama Supplier wajib diisi!", "Validasi Error", JOptionPane.ERROR_MESSAGE);
            txtNama.requestFocus();
            return;
        }

        String sql = "INSERT INTO supplier_kambing (nama_supplier, kontak, alamat) VALUES (?, ?, ?)";
        
        Connection conn = Database.getKoneksi();
        if (conn == null) return;

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, nama);
            pst.setString(2, kontak);
            pst.setString(3, alamat);

            int affectedRows = pst.executeUpdate();
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "Data supplier berhasil disimpan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // <--- Menutup form secara otomatis
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan data supplier:\n" + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetForm() {
        txtNama.setText("");
        txtKontak.setText("");
        txtAlamat.setText("");
        txtNama.requestFocus();
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ex) { ex.printStackTrace(); }
        SwingUtilities.invokeLater(() -> new FormInputSupplier().setVisible(true));
    }
}