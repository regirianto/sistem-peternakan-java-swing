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

public class FormInputPelanggan extends JFrame {

    private JTextField txtNama;
    private JTextField txtKontak;
    private JTextArea txtAlamat;
    private JButton btnSimpan, btnBatal;

    public FormInputPelanggan() {
        setTitle("Form Tambah Data Pelanggan");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); 

        // Menggunakan GridBagLayout persis seperti form yang sudah kita optimalkan
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
        gbc.weightx = 1.0; // Memberikan bobot agar memanjang
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

        // 3. Alamat (JTextArea memanjang Vertikal & Horizontal)
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST; // Label nempel di atas
        mainPanel.add(new JLabel("Alamat:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0; // Bobot vertikal agar ditarik ke bawah
        gbc.fill = GridBagConstraints.BOTH; 
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

        // Validasi Sederhana
        if (nama.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama Pelanggan wajib diisi!", "Validasi Error", JOptionPane.ERROR_MESSAGE);
            txtNama.requestFocus();
            return;
        }

        // Query Insert ke tabel pelanggan
        String sql = "INSERT INTO pelanggan (nama_pelanggan, kontak, alamat) VALUES (?, ?, ?)";
        
        Connection conn = Database.getKoneksi();
        if (conn == null) return;

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, nama);
            pst.setString(2, kontak);
            pst.setString(3, alamat);

            int affectedRows = pst.executeUpdate();
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "Data pelanggan berhasil disimpan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // Langsung tutup form setelah sukses simpan
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan data pelanggan:\n" + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ex) { ex.printStackTrace(); }
        SwingUtilities.invokeLater(() -> new FormInputPelanggan().setVisible(true));
    }
}