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

public class FormEditPelanggan extends JFrame {

    private int currentIdPelanggan; // Menyimpan ID Pelanggan yang sedang diedit

    private JTextField txtNama;
    private JTextField txtKontak;
    private JTextArea txtAlamat;
    private JButton btnUpdate, btnBatal;

    // Konstruktor ini WAJIB menerima ID Pelanggan dari List View
    public FormEditPelanggan(int idPelanggan) {
        this.currentIdPelanggan = idPelanggan;

        setTitle("Form Edit Data Pelanggan");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); 

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // 1. Nama Pelanggan
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0.0; 
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST; 
        mainPanel.add(new JLabel("Nama Pelanggan:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.weightx = 1.0; 
        gbc.fill = GridBagConstraints.HORIZONTAL; 
        txtNama = new JTextField(); 
        mainPanel.add(txtNama, gbc);

        // 2. Kontak (No HP)
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

        // SETELAH UI SELESAI, TARIK DATA LAMA DARI DATABASE
        loadDataPelangganLama();
    }

    // Method untuk menarik data dari database dan mengisi kotak teks
    private void loadDataPelangganLama() {
        try {
            Connection conn = Database.getKoneksi();
            if(conn == null) return;

            String sql = "SELECT * FROM pelanggan WHERE id_pelanggan = ?";
            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setInt(1, currentIdPelanggan);
                
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        txtNama.setText(rs.getString("nama_pelanggan"));
                        txtKontak.setText(rs.getString("kontak"));
                        txtAlamat.setText(rs.getString("alamat"));
                    } else {
                        JOptionPane.showMessageDialog(this, "Data pelanggan tidak ditemukan!", "Error", JOptionPane.ERROR_MESSAGE);
                        dispose(); 
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal meload data pelanggan:\n" + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method untuk mengeksekusi query UPDATE
    private void updateData() {
        String nama = txtNama.getText();
        String kontak = txtKontak.getText();
        String alamat = txtAlamat.getText();

        if (nama.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama Pelanggan wajib diisi!", "Validasi Error", JOptionPane.ERROR_MESSAGE);
            txtNama.requestFocus();
            return;
        }

        String sql = "UPDATE pelanggan SET nama_pelanggan=?, kontak=?, alamat=? WHERE id_pelanggan=?";
        
        Connection conn = Database.getKoneksi();
        if (conn == null) return;

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, nama);
            pst.setString(2, kontak);
            pst.setString(3, alamat);
            pst.setInt(4, currentIdPelanggan); // Kunci WHERE

            int affectedRows = pst.executeUpdate();
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "Data pelanggan berhasil diupdate!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // Tutup form setelah sukses
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal mengupdate data pelanggan:\n" + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ex) { ex.printStackTrace(); }
        // Test run dengan ID dummy (misalnya 1)
        SwingUtilities.invokeLater(() -> new FormEditPelanggan(1).setVisible(true));
    }
}