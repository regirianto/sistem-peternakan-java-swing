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
import sistemmanajemenpenjualanhewanternak.Utils.Database;

public class FormEditSupplier extends JFrame {

    // Menyimpan ID Supplier yang sedang diedit
    private int currentIdSupplier;

    private JTextField txtNama;
    private JTextField txtKontak;
    private JTextArea txtAlamat;
    private JButton btnUpdate, btnBatal;

    // Konstruktor menerima parameter ID Supplier
    public FormEditSupplier(int idSupplier) {
        this.currentIdSupplier = idSupplier;

        setTitle("Form Edit Data Supplier");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); 

        // Menggunakan GridBagLayout persis seperti form input yang sudah diperbaiki
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // 1. Nama Supplier
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0.0; 
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST; 
        mainPanel.add(new JLabel("Nama Supplier:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.weightx = 1.0; 
        gbc.fill = GridBagConstraints.HORIZONTAL; 
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

        // 3. Alamat 
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST; 
        mainPanel.add(new JLabel("Alamat:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0; 
        gbc.fill = GridBagConstraints.BOTH; 
        txtAlamat = new JTextArea(); 
        txtAlamat.setLineWrap(true);
        txtAlamat.setWrapStyleWord(true);
        JScrollPane scrollAlamat = new JScrollPane(txtAlamat);
        mainPanel.add(scrollAlamat, gbc);
        
        // Panel Tombol
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnUpdate = new JButton("Update Data");
        btnBatal = new JButton("Batal");
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnBatal);

        // Menambahkan ke Frame
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Event Listener
        btnUpdate.addActionListener(e -> updateData());
        btnBatal.addActionListener(e -> dispose());

        // LOAD DATA LAMA SETELAH UI SELESAI DIBANGUN
        loadDataSupplierAsli();
    }

    // Method untuk menarik data lama dari database dan memunculkannya di form
    private void loadDataSupplierAsli() {
        try {
            Connection conn = Database.getKoneksi();
            if(conn == null) return;

            String sql = "SELECT * FROM supplier_kambing WHERE id_supplier = ?";
            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setInt(1, currentIdSupplier);
                
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        txtNama.setText(rs.getString("nama_supplier"));
                        txtKontak.setText(rs.getString("kontak"));
                        txtAlamat.setText(rs.getString("alamat"));
                    } else {
                        JOptionPane.showMessageDialog(this, "Data supplier tidak ditemukan!", "Error", JOptionPane.ERROR_MESSAGE);
                        dispose(); 
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal meload data supplier:\n" + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method untuk mengeksekusi query UPDATE
    private void updateData() {
        String nama = txtNama.getText();
        String kontak = txtKontak.getText();
        String alamat = txtAlamat.getText();

        if (nama.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama Supplier wajib diisi!", "Validasi Error", JOptionPane.ERROR_MESSAGE);
            txtNama.requestFocus();
            return;
        }

        String sql = "UPDATE supplier_kambing SET nama_supplier=?, kontak=?, alamat=? WHERE id_supplier=?";
        
        Connection conn = Database.getKoneksi();
        if (conn == null) return;

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, nama);
            pst.setString(2, kontak);
            pst.setString(3, alamat);
            pst.setInt(4, currentIdSupplier); // Parameter WHERE id_supplier = ?

            int affectedRows = pst.executeUpdate();
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "Data supplier berhasil diupdate!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // Langsung tutup form setelah sukses update
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal mengupdate data supplier:\n" + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ex) { ex.printStackTrace(); }
        // Untuk testing, panggil supplier dengan ID 1 (pastikan data dummy sudah ada)
        SwingUtilities.invokeLater(() -> new FormEditSupplier(1).setVisible(true));
    }
}