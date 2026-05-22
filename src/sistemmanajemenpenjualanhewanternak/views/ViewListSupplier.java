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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import sistemmanajemenpenjualanhewanternak.Utils.Database;

public class ViewListSupplier extends JFrame {

    private JTable tableSupplier;
    private DefaultTableModel tableModel;
    private JButton btnTambah, btnEdit,  btnRefresh;

    public ViewListSupplier() {
        setTitle("Data Master Supplier");
        setSize(700, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // DISPOSE agar tidak mematikan seluruh aplikasi saat ditutup
        setLocationRelativeTo(null);

        // --- Panel Utama ---
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // --- Setup Table Model ---
        String[] columnNames = {"ID", "Nama Supplier", "Kontak", "Alamat"};
        
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Mencegah edit langsung di sel tabel
            }
        };

        tableSupplier = new JTable(tableModel);
        tableSupplier.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableSupplier.setRowHeight(25);
        
        // Mengatur lebar kolom agar lebih proporsional
        tableSupplier.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        tableSupplier.getColumnModel().getColumn(1).setPreferredWidth(200); // Nama
        tableSupplier.getColumnModel().getColumn(2).setPreferredWidth(120); // Kontak
        tableSupplier.getColumnModel().getColumn(3).setPreferredWidth(300); // Alamat

        JScrollPane scrollPane = new JScrollPane(tableSupplier);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // --- Panel Tombol ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnTambah = new JButton("Tambah Supplier");
        btnEdit = new JButton("Edit Terpilih");
        btnRefresh = new JButton("Refresh Tabel");

        buttonPanel.add(btnTambah);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnRefresh);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);

        // --- Event Listeners ---
        btnRefresh.addActionListener(e -> loadData());

        btnTambah.addActionListener(e -> {
            // Nanti kita akan buat FormInputSupplier, sementara pakai Popup
  FormInputSupplier formInput = new FormInputSupplier();
    formInput.setVisible(true);
    
    // Auto-refresh tabel saat form input ditutup
    formInput.addWindowListener(new java.awt.event.WindowAdapter() {
        @Override
        public void windowClosed(java.awt.event.WindowEvent windowEvent) {
            loadData();
        }
    });
        });

        btnEdit.addActionListener(e -> actionEdit());

        tableSupplier.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    actionEdit();
                }
            }
        });


        // Load data pertama kali saat form dibuka
        loadData();
    }

    private void actionEdit() {
        int selectedRow = tableSupplier.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Silakan pilih salah satu supplier di tabel terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idSupplier = (int) tableModel.getValueAt(selectedRow, 0);
        String nama = (String) tableModel.getValueAt(selectedRow, 1);
        
        // Placeholder untuk memanggil Form Edit Supplier nantinya
       FormEditSupplier formEdit = new FormEditSupplier(idSupplier);
        formEdit.setVisible(true);

        // Event Listener: Otomatis refresh tabel saat Form Edit ditutup (selesai update)
        formEdit.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                loadData();
            }
        });
    }

    private void loadData() {
        tableModel.setRowCount(0);

        try {
            Connection conn = Database.getKoneksi();
            if(conn == null) return;

            String sql = "SELECT * FROM supplier_kambing ORDER BY id_supplier DESC";

            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    int id = rs.getInt("id_supplier");
                    String nama = rs.getString("nama_supplier");
                    String kontak = rs.getString("kontak");
                    String alamat = rs.getString("alamat");

                    Object[] rowData = {id, nama, kontak, alamat};
                    tableModel.addRow(rowData);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data supplier:\n" + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actionHapus() {
        int selectedRow = tableSupplier.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Silakan pilih supplier yang mau dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idSupplier = (int) tableModel.getValueAt(selectedRow, 0);
        String namaSupplier = (String) tableModel.getValueAt(selectedRow, 1);

        int konfirmasi = JOptionPane.showConfirmDialog(this, 
                "Yakin ingin menghapus supplier '" + namaSupplier + "'?\n" +
                "Catatan: Data kambing dari supplier ini TIDAK akan ikut terhapus (hanya status suppliernya menjadi kosong).", 
                "Konfirmasi Hapus", 
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (konfirmasi == JOptionPane.YES_OPTION) {
            try {
                Connection conn = Database.getKoneksi();
                // Query eksekusi hapus
                String sql = "DELETE FROM supplier_kambing WHERE id_supplier = " + idSupplier;
                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate(sql);
                    JOptionPane.showMessageDialog(this, "Data supplier berhasil dihapus!");
                    loadData(); // Refresh tabel
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Gagal menghapus data:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ex) { ex.printStackTrace(); }
        SwingUtilities.invokeLater(() -> new ViewListSupplier().setVisible(true));
    }
}