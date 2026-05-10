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
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import sistemmanajemenpenjualanhewanternak.Utils.Database;

public class ViewListPelanggan extends JFrame {

    private JTextField txtCari;
    private JButton btnCari, btnReset;
    private JTable tablePelanggan;
    private DefaultTableModel tableModel;
    private JButton btnTambah, btnEdit;

    public ViewListPelanggan() {
        setTitle("Master Data Pelanggan");
        setSize(750, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Jangan EXIT_ON_CLOSE agar tidak menutup aplikasi
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // --- PANEL ATAS (PENCARIAN) ---
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Cari Nama Pelanggan:"));
        
        txtCari = new JTextField(20);
        btnCari = new JButton("Cari");
        btnReset = new JButton("Tampilkan Semua");
        
        filterPanel.add(txtCari);
        filterPanel.add(btnCari);
        filterPanel.add(btnReset);

        mainPanel.add(filterPanel, BorderLayout.NORTH);

        // --- PANEL TENGAH (TABEL UTAMA) ---
        String[] columnNames = {"ID", "Nama Pelanggan", "Kontak (No. HP)", "Alamat"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Mencegah sel diedit langsung
            }
        };

        tablePelanggan = new JTable(tableModel);
        tablePelanggan.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablePelanggan.setRowHeight(25);
        
        // Mengatur lebar kolom agar proporsional
        tablePelanggan.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        tablePelanggan.getColumnModel().getColumn(1).setPreferredWidth(200); // Nama
        tablePelanggan.getColumnModel().getColumn(2).setPreferredWidth(120); // Kontak
        tablePelanggan.getColumnModel().getColumn(3).setPreferredWidth(300); // Alamat

        JScrollPane scrollPane = new JScrollPane(tablePelanggan);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // --- PANEL BAWAH (TOMBOL AKSI) ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnTambah = new JButton("Tambah Pelanggan");
        btnEdit = new JButton("Edit Terpilih");

        
        buttonPanel.add(btnTambah);
        buttonPanel.add(btnEdit);

        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);

        // --- EVENT LISTENERS ---
        
        btnCari.addActionListener(e -> loadData(txtCari.getText().trim()));
        txtCari.addActionListener(e -> loadData(txtCari.getText().trim())); // Support enter key
        
        btnReset.addActionListener(e -> {
            txtCari.setText("");
            loadData("");
        });

        btnTambah.addActionListener(e -> {
                    FormInputPelanggan formInput = new FormInputPelanggan();
                    formInput.setVisible(true);

                    // Auto-refresh tabel di view list saat form input ditutup
                    formInput.addWindowListener(new java.awt.event.WindowAdapter() {
                        @Override
                        public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                            loadData(txtCari.getText().trim()); // Memuat ulang data beserta filternya
                        }
                    });
                });
        btnEdit.addActionListener(e -> actionEdit());

        // Fitur double click pada baris tabel untuk edit
        tablePelanggan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    actionEdit();
                }
            }
        });

      
  
        loadData("");
    }

    // Method untuk menarik data dari Database (dengan fitur cari)
    private void loadData(String keyword) {
        tableModel.setRowCount(0); // Bersihkan tabel terlebih dahulu

        try {
            Connection conn = Database.getKoneksi();
            if (conn == null) return;

            String sql = "SELECT * FROM pelanggan ";
            if (!keyword.isEmpty()) {
                sql += "WHERE nama_pelanggan LIKE ? ";
            }
            sql += "ORDER BY id_pelanggan DESC"; // Urutkan dari yang terbaru

            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                if (!keyword.isEmpty()) {
                    pst.setString(1, "%" + keyword + "%");
                }

                try (ResultSet rs = pst.executeQuery()) {
                    while (rs.next()) {
                        int id = rs.getInt("id_pelanggan");
                        String nama = rs.getString("nama_pelanggan");
                        String kontak = rs.getString("kontak");
                        String alamat = rs.getString("alamat");

                        Object[] rowData = {id, nama, kontak, alamat};
                        tableModel.addRow(rowData);
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data pelanggan:\n" + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actionEdit() {
        int selectedRow = tablePelanggan.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Silakan pilih salah satu pelanggan di tabel terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idPelanggan = (int) tableModel.getValueAt(selectedRow, 0);
        String namaPelanggan = (String) tableModel.getValueAt(selectedRow, 1);
        
       FormEditPelanggan formEdit = new FormEditPelanggan(idPelanggan);
        formEdit.setVisible(true);

        // Event Listener: Otomatis refresh tabel saat Form Edit ditutup (selesai update)
        formEdit.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                loadData(txtCari.getText().trim()); // Refresh dengan mempertahankan kata kunci filter
            }
        });
    }

   
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ex) {}
        SwingUtilities.invokeLater(() -> new ViewListPelanggan().setVisible(true));
    }
}