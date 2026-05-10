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
import sistemmanajemenpenjualanhewanternak.Utils.UserSession;

public class FormLogin extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin, btnBatal;

    public FormLogin() {
        setTitle("Login Sistem Peternakan");
        setSize(350, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Menutup aplikasi jika disilang
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Header Login
        JLabel lblTitle = new JLabel("SILAKAN LOGIN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        mainPanel.add(lblTitle, gbc);

        // Reset gridwidth
        gbc.gridwidth = 1;

        // Username
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0;
        mainPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        txtUsername = new JTextField(15);
        mainPanel.add(txtUsername, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.0;
        mainPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1.0;
        txtPassword = new JPasswordField(15);
        mainPanel.add(txtPassword, gbc);

        // Tombol
        JPanel panelTombol = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnLogin = new JButton("Login");
        btnBatal = new JButton("Batal");
        panelTombol.add(btnLogin);
        panelTombol.add(btnBatal);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        mainPanel.add(panelTombol, gbc);

        add(mainPanel);

        // --- Event Listener ---
        btnLogin.addActionListener(e -> prosesLogin());
        
        // Memungkinkan user menekan tombol Enter di kotak password
        txtPassword.addActionListener(e -> prosesLogin());
        
        btnBatal.addActionListener(e -> System.exit(0));
    }

    private void prosesLogin() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword()); // Mengambil string dari JPasswordField

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username dan Password tidak boleh kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Connection conn = Database.getKoneksi();
        if (conn == null) return;

        try {
            // Query mencocokkan username dan password
            String sql = "SELECT id_user, nama FROM user WHERE username = ? AND password = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, username);
            pst.setString(2, password);
            
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                // LOGIN BERHASIL!
                int id = rs.getInt("id_user");
                String nama = rs.getString("nama");

                // 1. Simpan ke Global Session
                UserSession.setLogin(id, nama);

                JOptionPane.showMessageDialog(this, "Selamat datang, " + nama + "!");

                // 2. Buka Dashboard
                DashboardUtama dashboard = new DashboardUtama();
                dashboard.setVisible(true);

                // 3. Tutup Form Login
                this.dispose();
            } else {
                // LOGIN GAGAL
                JOptionPane.showMessageDialog(this, "Username atau Password salah!", "Login Gagal", JOptionPane.ERROR_MESSAGE);
                txtPassword.setText("");
                txtPassword.requestFocus();
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error Database: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ex) {}
        // Mulai sekarang, jalankan (Run File) aplikasi dari FormLogin.java, bukan dari Dashboard lagi!
        SwingUtilities.invokeLater(() -> new FormLogin().setVisible(true));
    }
}