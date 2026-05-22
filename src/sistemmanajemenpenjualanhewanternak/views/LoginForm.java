package sistemmanajemenpenjualanhewanternak.views;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import sistemmanajemenpenjualanhewanternak.Utils.Database; 

public class LoginForm extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JLabel lblTitle, lblSubTitle, lblUsername, lblPassword;
    private JPanel mainPanel, cardPanel;

    public LoginForm() {
        setTitle("Berkah Aamiin Farm - Login");
        setSize(450, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 
        setResizable(false);

        // Uncomment baris di bawah jika ingin membuat tabel otomatis saat run

        // --- PALET WARNA ---
        Color primaryGreen = new Color(39, 174, 96);
        Color darkGreen = new Color(30, 132, 73);
        Color bgLight = new Color(240, 248, 245);
        Color textDark = new Color(44, 62, 80);
        Color textMuted = new Color(127, 140, 141);
        Color fieldBorder = new Color(220, 224, 230);

        // --- PANEL UTAMA ---
        mainPanel = new JPanel(new GridBagLayout()); 
        mainPanel.setBackground(bgLight);
        
        cardPanel = new JPanel();
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(230, 235, 240), 1, true),
                new EmptyBorder(40, 40, 40, 40)
        ));

        // --- HEADER ---
        lblTitle = new JLabel("BERKAH AAMIIN FARM");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(primaryGreen);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblSubTitle = new JLabel("Kelola Penjualan Hewan Ternak Anda");
        lblSubTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubTitle.setForeground(textMuted);
        lblSubTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- INPUT USERNAME ---
        lblUsername = new JLabel("Username");
        lblUsername.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUsername.setForeground(textDark);
        lblUsername.setAlignmentX(Component.CENTER_ALIGNMENT); // Diubah ke CENTER agar konsisten dengan BoxLayout

        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        Dimension fieldSize = new Dimension(300, 40);
        txtUsername.setPreferredSize(fieldSize);
        txtUsername.setMaximumSize(fieldSize); // Mengunci ukuran agar tidak melar
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(fieldBorder, 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));

        // --- INPUT PASSWORD ---
        lblPassword = new JLabel("Password");
        lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPassword.setForeground(textDark);
        lblPassword.setAlignmentX(Component.CENTER_ALIGNMENT); // Diubah ke CENTER

        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setPreferredSize(fieldSize);
        txtPassword.setMaximumSize(fieldSize); // Mengunci ukuran agar tidak melar
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(fieldBorder, 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));
        
        // Enter untuk login
        txtPassword.addActionListener(e -> btnLogin.doClick());

        // --- TOMBOL LOGIN ---
        btnLogin = new JButton("MASUK KE SISTEM");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setBackground(primaryGreen);
        btnLogin.setForeground(Color.WHITE);
        Dimension btnSize = new Dimension(300, 45);
        btnLogin.setPreferredSize(btnSize);
        btnLogin.setMaximumSize(btnSize); // Mengunci ukuran
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnLogin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnLogin.setBackground(darkGreen);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnLogin.setBackground(primaryGreen);
            }
        });

        // --- SUSUN KOMPONEN ---
        cardPanel.add(lblTitle);
        cardPanel.add(Box.createVerticalStrut(5));
        cardPanel.add(lblSubTitle);
        cardPanel.add(Box.createVerticalStrut(30));

        cardPanel.add(lblUsername);
        cardPanel.add(Box.createVerticalStrut(5));
        cardPanel.add(txtUsername);
        
        cardPanel.add(Box.createVerticalStrut(15));

        cardPanel.add(lblPassword);
        cardPanel.add(Box.createVerticalStrut(5));
        cardPanel.add(txtPassword);
        
        cardPanel.add(Box.createVerticalStrut(35));
        cardPanel.add(btnLogin);

        mainPanel.add(cardPanel);
        add(mainPanel);

        // --- ACTION LISTENER DENGAN DB ---
        btnLogin.addActionListener(this::handleLogin);
    }
    
    
    private void handleLogin(ActionEvent e) {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Username dan Password tidak boleh kosong!",
                    "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // PERBAIKAN: query menggunakan 'users' (dengan s), karena saat create table menggunakan nama 'users'
        String sql = "SELECT * FROM user WHERE username = ? AND password = ?";
        Connection conn = null;
        try {
            conn = Database.getKoneksi();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setString(1, username);
                stmt.setString(2, password);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        // Login berhasil
                        javax.swing.JFrame frame = new javax.swing.JFrame("Dashboard Berkah Aamiin Farm");
                        Dashboard_AaminFarm panel = new Dashboard_AaminFarm();
                    
                        frame.add(panel);
                        frame.pack(); 
                        frame.setLocationRelativeTo(null);
                        frame.setVisible(true);
                        this.dispose();
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Kombinasi Username atau Password salah!",
                                "Login Gagal", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (Throwable t) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            JTextArea textArea = new JTextArea(sw.toString(), 12, 50);
            textArea.setEditable(false);
            JOptionPane.showMessageDialog(this, new JScrollPane(textArea),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        
    }
}