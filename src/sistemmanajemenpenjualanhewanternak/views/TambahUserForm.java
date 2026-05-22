package sistemmanajemenpenjualanhewanternak.views;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import sistemmanajemenpenjualanhewanternak.Utils.Database;

public class TambahUserForm extends JFrame {

    private JTextField txtNama, txtUsername;
    private JPasswordField txtPassword, txtKonfirmasi;
    private JButton btnSimpan;
    private JPanel mainPanel, cardPanel;

    public TambahUserForm() {
        initComponents();
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    }

    private void initComponents() {
        setTitle("Tambah Akun Staf");
        setSize(450, 650); // Diperpanjang sedikit agar tombol tidak terpotong
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        // --- PALET WARNA (Konsisten dengan Login Form) ---
        Color primaryGreen = new Color(39, 174, 96);
        Color darkGreen = new Color(30, 132, 73);
        Color bgLight = new Color(240, 248, 245);
        Color textDark = new Color(44, 62, 80);
        Color fieldBorder = new Color(220, 224, 230);

        // --- PANEL UTAMA ---
        mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(bgLight);

        cardPanel = new JPanel();
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(230, 235, 240), 1, true),
                new EmptyBorder(30, 40, 30, 40)
        ));

        // --- HEADER ---
        JLabel lblTitle = new JLabel("FORM TAMBAH AKUN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(primaryGreen);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // --- UKURAN STANDAR INPUT ---
        Dimension fieldSize = new Dimension(320, 40);

        // --- KOMPONEN INPUT ---
        txtNama = createTextField(fieldSize, fieldBorder);
        txtUsername = createTextField(fieldSize, fieldBorder);
        txtPassword = createPasswordField(fieldSize, fieldBorder);
        txtKonfirmasi = createPasswordField(fieldSize, fieldBorder);

        // --- TOMBOL SIMPAN ---
        btnSimpan = new JButton("SIMPAN AKUN");
        btnSimpan.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSimpan.setBackground(primaryGreen);
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.setPreferredSize(new Dimension(320, 45));
        btnSimpan.setMaximumSize(new Dimension(320, 45));
        btnSimpan.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSimpan.setFocusPainted(false);
        btnSimpan.setBorderPainted(false);
        btnSimpan.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnSimpan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnSimpan.setBackground(darkGreen);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnSimpan.setBackground(primaryGreen);
            }
        });

        // Event Listener Tombol
        btnSimpan.addActionListener((ActionEvent e) -> simpanUser());

        // --- SUSUN KOMPONEN KE DALAM CARD PANEL ---
        cardPanel.add(lblTitle);
        cardPanel.add(Box.createVerticalStrut(30));

        cardPanel.add(createLabel("Nama Lengkap", textDark));
        cardPanel.add(Box.createVerticalStrut(5));
        cardPanel.add(txtNama);
        cardPanel.add(Box.createVerticalStrut(15));

        cardPanel.add(createLabel("Username", textDark));
        cardPanel.add(Box.createVerticalStrut(5));
        cardPanel.add(txtUsername);
        cardPanel.add(Box.createVerticalStrut(15));

        cardPanel.add(createLabel("Password", textDark));
        cardPanel.add(Box.createVerticalStrut(5));
        cardPanel.add(txtPassword);
        cardPanel.add(Box.createVerticalStrut(15));

        cardPanel.add(createLabel("Konfirmasi Password", textDark));
        cardPanel.add(Box.createVerticalStrut(5));
        cardPanel.add(txtKonfirmasi);
        cardPanel.add(Box.createVerticalStrut(15));

        cardPanel.add(Box.createVerticalStrut(5));
        cardPanel.add(Box.createVerticalStrut(35));

        cardPanel.add(btnSimpan);

        mainPanel.add(cardPanel);
        add(mainPanel);
    }

    // --- METHOD BANTUAN UNTUK MEMBUAT KOMPONEN (Biar kode lebih bersih) ---
    private JLabel createLabel(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(color);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private JTextField createTextField(Dimension size, Color borderColor) {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(size);
        field.setMaximumSize(size);
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(borderColor, 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }

    private JPasswordField createPasswordField(Dimension size, Color borderColor) {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(size);
        field.setMaximumSize(size);
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(borderColor, 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }

    // --- LOGIKA DATABASE ---
    private void simpanUser() {
        String nama = txtNama.getText().trim();
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        String konfirmasi = new String(txtKonfirmasi.getPassword());

        // Validasi kosong
        if (nama.isEmpty() || username.isEmpty() || password.isEmpty() || konfirmasi.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field wajib diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validasi password cocok
        if (!password.equals(konfirmasi)) {
            JOptionPane.showMessageDialog(this, "Password dan Konfirmasi Password tidak cocok!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // PERBAIKAN: Membuka koneksi baru saat tombol diklik agar tidak kena error "Connection Closed"
        try (Connection conn = Database.getKoneksi()) {

            // Validasi username duplikat
            String checkQuery = "SELECT * FROM user WHERE username=?";
            try (PreparedStatement checkPst = conn.prepareStatement(checkQuery)) {
                checkPst.setString(1, username);
                try (ResultSet rs = checkPst.executeQuery()) {
                    if (rs.next()) {
                        JOptionPane.showMessageDialog(this, "Username sudah digunakan!", "Validasi", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
            }


            String query = "INSERT INTO user(nama, username, password) VALUES(?,?,?)";
            try (PreparedStatement pst = conn.prepareStatement(query)) {
                pst.setString(1, nama);
                pst.setString(2, username);
                pst.setString(3, password);

                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Akun berhasil dibuat!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                kosongkanForm();
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan data : " + e.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void kosongkanForm() {
        txtNama.setText("");
        txtUsername.setText("");
        txtPassword.setText("");
        txtKonfirmasi.setText("");
        txtNama.requestFocus(); // Kembalikan kursor ke kolom nama
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TambahUserForm().setVisible(true));
    }
}