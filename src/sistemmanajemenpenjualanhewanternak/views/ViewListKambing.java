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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import sistemmanajemenpenjualanhewanternak.Utils.Database;

public class ViewListKambing extends JFrame {

    private JTable tableKambing;
    private DefaultTableModel tableModel;
    private JButton btnTambah, btnEdit, btnRefresh;

    public ViewListKambing() {
        setTitle("Data Master Kambing");
        setSize(800, 500);
        setLocationRelativeTo(null);

        // --- Panel Utama ---
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // --- Setup Table Model ---
        // Kolom ID sengaja kita letakkan di index 0 untuk memudahkan pengambilan data
        String[] columnNames = {"ID", "Kode Kambing", "Supplier", "Gender", "Berat (Kg)", "Harga Modal", "Status", "Tgl Masuk"};
        
        // Custom TableModel agar cell tidak bisa diedit langsung ketik (harus lewat form)
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };

        tableKambing = new JTable(tableModel);
        tableKambing.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Hanya bisa pilih 1 baris
        tableKambing.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(tableKambing);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // --- Panel Tombol ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnTambah = new JButton("Tambah Data Baru");
        btnEdit = new JButton("Edit Terpilih");
        btnRefresh = new JButton("Refresh Tabel");

        buttonPanel.add(btnTambah);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnRefresh);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);

        // --- Event Listeners ---
        
        // 1. Tombol Refresh
        btnRefresh.addActionListener(e -> loadData());

        // 2. Tombol Edit
        btnEdit.addActionListener(e -> actionEdit());

        // 3. Double Click pada baris tabel untuk Edit (UX Improvement)
        tableKambing.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // Jika double click
                    actionEdit();
                }
            }
        });

        // 4. Tombol Tambah
        btnTambah.addActionListener(e -> {
            FormInputKambing formInput = new FormInputKambing();
            formInput.setVisible(true);
            
            // Listener: Jika form input ditutup, otomatis refresh tabel di sini
            formInput.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadData();
                }
            });
        });


        // Load data pertama kali saat form dibuka
        loadData();
    }

    // Method untuk mengeksekusi aksi Edit
    private void actionEdit() {
        int selectedRow = tableKambing.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Silakan klik/pilih salah satu data di tabel terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Ambil ID dari kolom pertama (index 0) pada baris yang dipilih
        int idKambing = (int) tableModel.getValueAt(selectedRow, 0);

        // Buka form edit dan passing ID tersebut
        FormEditKambing formEdit = new FormEditKambing(idKambing);
        formEdit.setVisible(true);

        // Event Listener: Otomatis refresh tabel saat Form Edit ditutup (selesai update)
        formEdit.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                loadData();
            }
        });
    }

    // Method untuk menarik data dari database ke JTable
    private void loadData() {
        tableModel.setRowCount(0); // Kosongkan tabel sebelum diisi ulang

        try {
            Connection conn = Database.getKoneksi();
            if(conn == null) return;

            // Menggunakan LEFT JOIN agar kita bisa menampilkan 'Nama Supplier' bukan sekadar 'ID Supplier'
            String sql = "SELECT k.id_kambing, k.kode_kambing, s.nama_supplier, k.jenis_kelamin, "
                       + "k.berat_awal, k.harga_modal, k.status, k.tanggal_masuk "
                       + "FROM kambing k "
                       + "LEFT JOIN supplier_kambing s ON k.id_supplier = s.id_supplier "
                       + "ORDER BY k.id_kambing DESC";

            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    int id = rs.getInt("id_kambing");
                    String kode = rs.getString("kode_kambing");
                    
                    // Jika supplier null (ternak sendiri), tampilkan teks informatif
                    String supplier = rs.getString("nama_supplier");
                    if (supplier == null) supplier = "- Ternak Sendiri -";
                    
                    String gender = rs.getString("jenis_kelamin");
                    double berat = rs.getDouble("berat_awal");
                    double harga = rs.getDouble("harga_modal");
                    String status = rs.getString("status");
                    java.sql.Date tgl = rs.getDate("tanggal_masuk");

                    // Masukkan ke dalam Object array agar sesuai dengan kolom tabel
                    Object[] rowData = {id, kode, supplier, gender, berat, harga, status, tgl};
                    tableModel.addRow(rowData);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data:\n" + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method untuk eksekusi aksi Hapus (Bonus)
    private void actionHapus() {
        int selectedRow = tableKambing.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Silakan pilih data yang mau dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idKambing = (int) tableModel.getValueAt(selectedRow, 0);
        String kodeKambing = (String) tableModel.getValueAt(selectedRow, 1);

        int konfirmasi = JOptionPane.showConfirmDialog(this, 
                "Yakin ingin menghapus data kambing " + kodeKambing + "?\nData yang dihapus tidak bisa dikembalikan.", 
                "Konfirmasi Hapus", 
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (konfirmasi == JOptionPane.YES_OPTION) {
            try {
                Connection conn = Database.getKoneksi();
                String sql = "DELETE FROM kambing WHERE id_kambing = " + idKambing;
                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate(sql);
                    JOptionPane.showMessageDialog(this, "Data berhasil dihapus!");
                    loadData(); // Refresh tabel setelah hapus
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Gagal menghapus data (Mungkin sedang dipakai di transaksi):\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ex) { ex.printStackTrace(); }
        SwingUtilities.invokeLater(() -> new ViewListKambing().setVisible(true));
    }
}