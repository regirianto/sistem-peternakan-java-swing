package sistemmanajemenpenjualanhewanternak.views;

/**
 *
 * @author Lenovo Ip3
 */
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.List;
import java.util.ArrayList;
import javax.swing.RowFilter;
import sistemmanajemenpenjualanhewanternak.Utils.Database;

public class RiwayatKesehatan extends javax.swing.JFrame {
    
    // Variabel untuk fitur Search/Filter
    TableRowSorter<javax.swing.table.DefaultTableModel> rowSorter;
    
    public RiwayatKesehatan() {
        initComponents();
        setupTableSorter(); // Panggil method setup saat form dibuka
        loadFilterKambing(); // Panggil data dari database saat form dibuka
        loadDataTabel(); // PANGGIL DI SINI BIAR PAS DIBUKA LANGSUNG MUNCUL DATANYA
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    }
    
    // Method untuk menarik data riwayat kesehatan dari database ke JTable
    public void loadDataTabel() {
        javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel) tabelKambing.getModel();
        model.setRowCount(0); // Bersihkan isi tabel sebelum diisi ulang

        try {
            java.sql.Connection conn = sistemmanajemenpenjualanhewanternak.Utils.Database.getKoneksi();
            java.sql.Statement stmt = conn.createStatement();
            
            // --- INI QUERY JOIN-NYA ---
            String sql = "SELECT r.id_riwayat, k.kode_kambing, r.tanggal_pemeriksaan, r.keterangan, r.berat_saat_ini " +
                         "FROM riwayat_kesehatan r " +
                         "JOIN kambing k ON r.id_kambing = k.id_kambing";
            
            java.sql.ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                // Perhatikan: index ke-1 sekarang kita isi pakai KODE KAMBING, bukan ID KAMBING
                model.addRow(new Object[]{
                    rs.getString("id_riwayat"),
                    rs.getString("kode_kambing"), 
                    rs.getString("tanggal_pemeriksaan"),
                    rs.getString("keterangan"),
                    rs.getString("berat_saat_ini")
                });
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Gagal memuat data riwayat: " + e.getMessage());
        }
    }
    
    // --- METHOD UNTUK SETUP TABEL & WORD WRAP ---
    private void setupTable() {
        javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel) tabelKambing.getModel();
        rowSorter = new TableRowSorter<>(model);
        tabelKambing.setRowSorter(rowSorter);
        
        tabelKambing.getColumnModel().getColumn(3).setCellRenderer(new CustomTextAreaRenderer());
        tabelKambing.setRowHeight(60); 

        // --- TAMBAHKAN KODE STYLING INI ---
        
        // 1. Styling untuk Header Tabel (Judul Kolom)
        javax.swing.table.JTableHeader header = tabelKambing.getTableHeader();
        // Bikin font header lebih tebal dari isi tabel
        header.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14)); 
        // Samakan warna background header dengan warna panelmu
        header.setBackground(new java.awt.Color(153, 153, 0)); 
        header.setForeground(java.awt.Color.WHITE); // Teks header warna putih
        
        // 2. Lebar kolom spesifik (Biar kolom ID nggak terlalu lebar, Keterangan dibikin lebar)
        tabelKambing.getColumnModel().getColumn(0).setPreferredWidth(100); // id_riwayat
        tabelKambing.getColumnModel().getColumn(1).setPreferredWidth(50);  // id_kambing
        tabelKambing.getColumnModel().getColumn(2).setPreferredWidth(150); // tanggal
        tabelKambing.getColumnModel().getColumn(3).setPreferredWidth(350); // keterangan (Paling lebar)
        tabelKambing.getColumnModel().getColumn(4).setPreferredWidth(100); // berat
        
        // Memasang class Custom Renderer (yang kita buat di bawah) ke kolom Keterangan (Index ke-3)
        tabelKambing.getColumnModel().getColumn(3).setCellRenderer(new CustomTextAreaRenderer());
        
        // Lebarkan tinggi semua baris di tabel biar teksnya muat ke bawah
        tabelKambing.setRowHeight(60); 
        // Mengganti teks header index ke-1 menjadi Kode Kambing
        tabelKambing.getColumnModel().getColumn(1).setHeaderValue("Kode Kambing");
        tabelKambing.getTableHeader().repaint(); // Refresh tampilan header
    }
    
    // --- METHOD UNTUK NARIK DATABASE KE COMBOBOX FILTER ---
    private void loadFilterKambing() {
        KodeKambing.removeAllItems();
        KodeKambing.addItem("- Semua Kambing -"); 

        try {
            Connection conn = Database.getKoneksi();
            Statement stmt = conn.createStatement();
            String sql = "SELECT id_kambing, kode_kambing FROM kambing";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String id = rs.getString("id_kambing");
                String kode = rs.getString("kode_kambing");
                KodeKambing.addItem(id + " - " + kode); // Format sama seperti form input
            }
            rs.close(); stmt.close();
        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Gagal muat filter: " + e.getMessage());
        }
    }
    
    // --- METHOD UNTUK MENGGABUNGKAN FITUR CARI DAN FILTER ---
    private void saringTabel() {
        if (rowSorter == null) return;

        List<RowFilter<Object,Object>> filters = new ArrayList<>();

        // 1. Baca isi dari Text Field Pencarian
        String keywordTeks = jCariKambing.getText().trim();
        if (!keywordTeks.isEmpty() && !keywordTeks.equals("Cari Kambing")) {
            filters.add(RowFilter.regexFilter("(?i)" + keywordTeks)); // Cari di semua kolom
        }

        // 2. Baca pilihan dari Combobox Filter
        if (KodeKambing.getSelectedIndex() > 0) { // Jika yang dipilih BUKAN "- Semua Kambing -"
            String comboTerpilih = KodeKambing.getSelectedItem().toString();
            String idKambing = comboTerpilih.split(" - ")[0]; // Ambil ID-nya saja (angka di depan)
            
            // Filter KHUSUS untuk kolom index ke-1 (id_kambing) dengan ID tersebut
            filters.add(RowFilter.regexFilter("^" + idKambing + "$", 1)); 
        }

        // 3. Terapkan filternya
        if (filters.isEmpty()) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(RowFilter.andFilter(filters));
        }
    }
    
    // --- CLASS CUSTOM UNTUK BIKIN TEXTAREA DI DALAM TABEL (WORD WRAP) ---
    class CustomTextAreaRenderer extends javax.swing.JTextArea implements javax.swing.table.TableCellRenderer {
        public CustomTextAreaRenderer() {
            setLineWrap(true);       // Teks turun ke bawah kalau kepanjangan
            setWrapStyleWord(true);  // Turunnya per kata, bukan kepotong per huruf
            setOpaque(true);
            setMargin(new java.awt.Insets(5, 5, 5, 5)); // Kasih jarak biar rapi
        }

        @Override
        public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            setText(value != null ? value.toString() : "");
            
            // --- TAMBAHKAN BARIS INI BIA FONT KETERANGAN NGURUTIN FONT TABEL ---
            setFont(table.getFont()); 
            
            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            } else {
                setBackground(table.getBackground());
                setForeground(table.getForeground());
            }
            return this;
        }
    }
    
    // Method untuk setup filter
    private void setupTableSorter() {
        DefaultTableModel model = (DefaultTableModel) tabelKambing.getModel();
        rowSorter = new TableRowSorter<>(model);
        tabelKambing.setRowSorter(rowSorter);
    }
    
    // Method publik agar form input bisa kirim data ke tabel ini
    public void tambahDataKeTabel(Object[] dataRow) {
        DefaultTableModel model = (DefaultTableModel) tabelKambing.getModel();
        model.addRow(dataRow);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabelKambing = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        btnTambah = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        KodeKambing = new javax.swing.JComboBox<>();
        jCariKambing = new javax.swing.JTextField();
        btnCari = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        tabelKambing.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        tabelKambing.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        tabelKambing.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "id riwayat", "kode kambing", "tanggal pemeriksaan", "keterangan", "berat saat ini"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabelKambing.setGridColor(new java.awt.Color(204, 204, 204));
        tabelKambing.setSelectionBackground(new java.awt.Color(0, 204, 204));
        tabelKambing.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabelKambingMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tabelKambing);

        jPanel3.setBackground(new java.awt.Color(153, 153, 0));
        jPanel3.setForeground(new java.awt.Color(204, 204, 0));

        jLabel2.setBackground(new java.awt.Color(0, 204, 204));
        jLabel2.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Riwayat Kesehatan Kambing");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(11, 11, 11))
        );

        btnTambah.setBackground(new java.awt.Color(204, 204, 0));
        btnTambah.setFont(new java.awt.Font("Agency FB", 1, 18)); // NOI18N
        btnTambah.setText("Tambah Kambing");
        btnTambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambahActionPerformed(evt);
            }
        });

        btnHapus.setBackground(new java.awt.Color(153, 153, 0));
        btnHapus.setFont(new java.awt.Font("Agency FB", 1, 18)); // NOI18N
        btnHapus.setText("Hapus Data");
        btnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 911, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnHapus)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnTambah))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 564, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnHapus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnTambah, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        jPanel2.setBackground(new java.awt.Color(204, 204, 0));

        jLabel1.setBackground(new java.awt.Color(0, 204, 204));
        jLabel1.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Filter");

        KodeKambing.setBackground(new java.awt.Color(102, 102, 0));
        KodeKambing.setFont(new java.awt.Font("Agency FB", 1, 18)); // NOI18N
        KodeKambing.setForeground(new java.awt.Color(153, 153, 0));
        KodeKambing.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "- Kode Kambing -" }));
        KodeKambing.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                KodeKambingActionPerformed(evt);
            }
        });

        jCariKambing.setFont(new java.awt.Font("Agency FB", 1, 18)); // NOI18N
        jCariKambing.setForeground(new java.awt.Color(153, 153, 0));
        jCariKambing.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCariKambingActionPerformed(evt);
            }
        });

        btnCari.setText("Cari");
        btnCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(KodeKambing, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jCariKambing, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCari)
                .addGap(32, 32, 32))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnCari, javax.swing.GroupLayout.DEFAULT_SIZE, 39, Short.MAX_VALUE)
                            .addComponent(jCariKambing)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(KodeKambing)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnTambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahActionPerformed
     FormInputRiwayat formInput = new FormInputRiwayat();
     formInput.setFormUtama(this); // Mengirim referensi form ini ke form input
     formInput.setVisible(true);
    }//GEN-LAST:event_btnTambahActionPerformed

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        int selectedRow = tabelKambing.getSelectedRow();
        
        // Cek apakah ada baris yang dipilih
        if (selectedRow != -1) {
            // Kasih pop-up konfirmasi biar aman (UX yang baik)
            int konfirmasi = javax.swing.JOptionPane.showConfirmDialog(this, 
                "Yakin ingin menghapus riwayat kesehatan ini secara permanen?", 
                "Konfirmasi Hapus", 
                javax.swing.JOptionPane.YES_NO_OPTION);
            
            if (konfirmasi == javax.swing.JOptionPane.YES_OPTION) {
                try {
                    // Konversi index baris (aman walau tabel sedang di-filter/search)
                    int modelRow = tabelKambing.convertRowIndexToModel(selectedRow);
                    
                    // Ambil 'id_riwayat' dari tabel (ada di kolom paling kiri / index 0)
                    String idRiwayat = tabelKambing.getModel().getValueAt(modelRow, 0).toString();
                    
                    // --- EKSEKUSI QUERY DELETE KE MYSQL ---
                    java.sql.Connection conn = sistemmanajemenpenjualanhewanternak.Utils.Database.getKoneksi();
                    String sql = "DELETE FROM riwayat_kesehatan WHERE id_riwayat = ?";
                    java.sql.PreparedStatement pst = conn.prepareStatement(sql);
                    
                    pst.setString(1, idRiwayat);
                    pst.executeUpdate(); // Hapus dari database
                    pst.close();
                    
                    // Panggil method ini biar tabel otomatis narik data terbaru (yang udah bersih)
                    loadDataTabel();
                    
                    javax.swing.JOptionPane.showMessageDialog(this, "Data berhasil dihapus dari database!");
                    
                } catch (Exception e) {
                    javax.swing.JOptionPane.showMessageDialog(this, "Gagal menghapus data: " + e.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            // Kalau user klik tombol hapus tapi belum milih baris di tabel
            javax.swing.JOptionPane.showMessageDialog(this, "Pilih baris yang ingin dihapus terlebih dahulu!", "Peringatan", javax.swing.JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnHapusActionPerformed

    private void btnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariActionPerformed
        saringTabel();
    }//GEN-LAST:event_btnCariActionPerformed

    private void KodeKambingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_KodeKambingActionPerformed
        saringTabel();
    }//GEN-LAST:event_KodeKambingActionPerformed

    private void jCariKambingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCariKambingActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCariKambingActionPerformed

    private void tabelKambingMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabelKambingMouseClicked
        // Deteksi jika user melakukan DOUBLE CLICK (Klik 2x)
        if (evt.getClickCount() == 2) {
            int selectedRow = tabelKambing.getSelectedRow();
            
            if (selectedRow != -1) {
                // Konversi indeks baris (aman walau tabel sedang di-filter/search)
                int modelRow = tabelKambing.convertRowIndexToModel(selectedRow);
                
                // Ambil semua data string dari baris tabel yang di-double click
                String idRiwayat = tabelKambing.getModel().getValueAt(modelRow, 0).toString();
                String idKambing = tabelKambing.getModel().getValueAt(modelRow, 1).toString();
                String tanggal = tabelKambing.getModel().getValueAt(modelRow, 2).toString();
                String keterangan = tabelKambing.getModel().getValueAt(modelRow, 3).toString();
                String berat = tabelKambing.getModel().getValueAt(modelRow, 4).toString();
                
                // Instansiasi form input pop-up
                FormInputRiwayat formInput = new FormInputRiwayat();
                formInput.setFormUtama(this); // Berikan akses refresh tabel
                
                // Picu Mode Edit dengan mengirimkan data lama
                formInput.setEditMode(idRiwayat, idKambing, tanggal, keterangan, berat);
                
                // Tampilkan form pop-upnya
                formInput.setVisible(true);
            }
        }
    }//GEN-LAST:event_tabelKambingMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(RiwayatKesehatan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RiwayatKesehatan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RiwayatKesehatan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RiwayatKesehatan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new RiwayatKesehatan().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> KodeKambing;
    private javax.swing.JButton btnCari;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnTambah;
    private javax.swing.JTextField jCariKambing;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tabelKambing;
    // End of variables declaration//GEN-END:variables

}
