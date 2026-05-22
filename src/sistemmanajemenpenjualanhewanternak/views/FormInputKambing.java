/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package sistemmanajemenpenjualanhewanternak.views;

import sistemmanajemenpenjualanhewanternak.Utils.Database;
/**
 *
 * @author Rizki Fadillah
 */
public class FormInputKambing extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FormInputKambing.class.getName());

    public FormInputKambing() {
        initComponents();

        loadSupplier();
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); 
        setResizable(false);
    }
    
    private void loadSupplier() {

        try {

            java.sql.Connection conn = Database.getKoneksi();

            String sql = "SELECT * FROM supplier_kambing";

            java.sql.Statement st = conn.createStatement();
            java.sql.ResultSet rs = st.executeQuery(sql);
    
            while (rs.next()) {
                                cbSupplier.addItem(rs.getString("nama_supplier"));
            }
            
        } catch (Exception e) {

            javax.swing.JOptionPane.showMessageDialog(null,e);
        }
    }
    
    private boolean validasiInput() {

        if (txtKodeKambing.getText().trim().isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "Kode kambing wajib diisi!"
            );
            return false;
        }

        if (txtBerat.getText().trim().isEmpty()) {

            javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "Berat awal wajib diisi!"
            );
            return false;
        }

        if (txtModal.getText().trim().isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "Harga modal wajib diisi!"
            );
            return false;
        }

        if (!rbJantan.isSelected() && !rbBetina.isSelected()) {

            javax.swing.JOptionPane.showMessageDialog(
                    this,
                   "Pilih jenis kelamin!"
            );
            return false;
        }

        try {

            double berat = Double.parseDouble(txtBerat.getText());
            double modal = Double.parseDouble(txtModal.getText());

            if (berat <= 0 || modal <= 0) {
                javax.swing.JOptionPane.showMessageDialog(
                        this,
                        "Berat dan modal harus lebih dari 0!"
                );
                return false;
            }

        } catch (NumberFormatException e) {
            javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "Berat dan modal harus angka!"
            );
            return false;
        }

        return true;
    }
    
    private boolean cekKodeKambing() {

        boolean ada = false;

        try {

            java.sql.Connection conn = Database.getKoneksi();

            String sql =
                    "SELECT * FROM kambing " + "WHERE kode_kambing=?";
 
            java.sql.PreparedStatement pst = conn.prepareStatement(sql);

            pst.setString(1, txtKodeKambing.getText());

            java.sql.ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                ada = true;
            }

        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(this, e);
        }

        return ada;
    }
    
    private void resetForm() {

        txtKodeKambing.setText("");
        txtBerat.setText("");
        txtModal.setText("");

        buttonGroup1.clearSelection();

        cbSupplier.setSelectedIndex(0);
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        txtBerat = new javax.swing.JTextField();
        txtModal = new javax.swing.JTextField();
        cbSupplier = new javax.swing.JComboBox<>();
        rbJantan = new javax.swing.JRadioButton();
        rbBetina = new javax.swing.JRadioButton();
        dcTanggalMasuk = new de.wannawork.jcalendar.JCalendarComboBox();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtKodeKambing = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        btnBatal = new javax.swing.JButton();
        btnReset = new javax.swing.JButton();
        btnSimpan = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(15, 107, 47));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("TAMBAH KAMBING BARU");

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        txtBerat.addActionListener(this::txtBeratActionPerformed);

        txtModal.addActionListener(this::txtModalActionPerformed);

        cbSupplier.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-- Pilih Supplier/ Tanpa Supplier --" }));

        rbJantan.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroup1.add(rbJantan);
        rbJantan.setText("Jantan");
        rbJantan.addActionListener(this::rbJantanActionPerformed);

        rbBetina.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroup1.add(rbBetina);
        rbBetina.setText("Betina");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel2.setText("Kode Kambing:");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel3.setText("Supplier:");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel4.setText("Jenis Kelamin:");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel5.setText("Berat Awal (Kg):");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel6.setText("Harga Modal (Rp):");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel8.setText("Tanggal Masuk:");

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(15, 107, 47));
        jLabel9.setText("INFORMASI KAMBING");

        btnBatal.setBackground(new java.awt.Color(229, 57, 53));
        btnBatal.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnBatal.setText("Batal");
        btnBatal.addActionListener(this::btnBatalActionPerformed);

        btnReset.setBackground(new java.awt.Color(251, 140, 0));
        btnReset.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnReset.setText("Reset");
        btnReset.addActionListener(this::btnResetActionPerformed);

        btnSimpan.setBackground(new java.awt.Color(67, 160, 71));
        btnSimpan.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnSimpan.setText("Simpan");
        btnSimpan.addActionListener(this::btnSimpanActionPerformed);

        jPanel3.setBackground(new java.awt.Color(232, 245, 233));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(15, 107, 47));
        jLabel10.setText("Pastikan semua data sudah benar sebelum disimpan!");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel10)
                .addContainerGap(144, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel10))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(jLabel9))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(jLabel2)
                .addGap(43, 43, 43)
                .addComponent(txtKodeKambing, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(jLabel3)
                .addGap(83, 83, 83)
                .addComponent(cbSupplier, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 420, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(btnSimpan, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(39, 39, 39)
                .addComponent(btnReset, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(38, 38, 38)
                .addComponent(btnBatal, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addComponent(jLabel6))
                        .addGap(23, 23, 23)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dcTanggalMasuk, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtModal, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5))
                        .addGap(39, 39, 39)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(rbJantan)
                                .addGap(18, 18, 18)
                                .addComponent(rbBetina))
                            .addComponent(txtBerat, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(jLabel9)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)
                        .addGap(2, 2, 2))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtKodeKambing, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(cbSupplier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(rbJantan)
                        .addComponent(jLabel4))
                    .addComponent(rbBetina))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtBerat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtModal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel8)
                    .addComponent(dcTanggalMasuk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(72, 72, 72)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnSimpan, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnReset, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBatal, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void rbJantanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbJantanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rbJantanActionPerformed

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
   
        if (!validasiInput()) {
            return;
        }

        if (cekKodeKambing()) {
            javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "Kode kambing sudah digunakan!"
            );
            return;
        }

        try {
            java.sql.Connection conn = Database.getKoneksi();

            String jenisKelamin = "";

            if (rbJantan.isSelected()) {
                jenisKelamin = "Jantan";
            } else {
                jenisKelamin = "Betina";

            }

            String namaSupplier =  cbSupplier.getSelectedItem().toString();

            int idSupplier = 0;

            String sqlSupplier =
                    "SELECT id_supplier "
                    + "FROM supplier_kambing "
                    + "WHERE nama_supplier=?";
    
            java.sql.PreparedStatement pstSupplier = conn.prepareStatement(sqlSupplier);

            pstSupplier.setString(1, namaSupplier);

            java.sql.ResultSet rsSupplier =
                    pstSupplier.executeQuery();

            if (rsSupplier.next()) {
                idSupplier =
                        rsSupplier.getInt("id_supplier");
            }

            String sql =
                    "INSERT INTO kambing "
                    + "(kode_kambing,id_supplier,"
                    + "jenis_kelamin,berat_awal,"
                    + "harga_modal,status,"
                    + "tanggal_masuk)"
                    + "VALUES (?,?,?,?,?,?,?)";

            java.sql.PreparedStatement pst =
                    conn.prepareStatement(sql);

            pst.setString(1, txtKodeKambing.getText());
            pst.setInt(2, idSupplier);
            pst.setString(3, jenisKelamin);
            
            pst.setDouble(4,
                    Double.parseDouble(txtBerat.getText())
            );

            pst.setDouble(5,
                    Double.parseDouble(txtModal.getText())
            );
            
            //Default Tersedia
            pst.setString(6,"Tersedia");

            java.util.Date tanggal = dcTanggalMasuk.getDate();

            java.sql.Date sqlTanggal =
                    new java.sql.Date(tanggal.getTime());

            pst.setDate(7, sqlTanggal);

            pst.executeUpdate();

            javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "Data kambing berhasil disimpan!"
            );
            
            dispose();

        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(this, e);
        }
    }//GEN-LAST:event_btnSimpanActionPerformed

    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetActionPerformed
        resetForm();
    }//GEN-LAST:event_btnResetActionPerformed

    private void btnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBatalActionPerformed
        dispose();
    }//GEN-LAST:event_btnBatalActionPerformed

    private void txtBeratActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBeratActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBeratActionPerformed

    private void txtModalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtModalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtModalActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> 
                new FormInputKambing().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBatal;
    private javax.swing.JButton btnReset;
    private javax.swing.JButton btnSimpan;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<String> cbSupplier;
    private de.wannawork.jcalendar.JCalendarComboBox dcTanggalMasuk;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JRadioButton rbBetina;
    private javax.swing.JRadioButton rbJantan;
    private javax.swing.JTextField txtBerat;
    private javax.swing.JTextField txtKodeKambing;
    private javax.swing.JTextField txtModal;
    // End of variables declaration//GEN-END:variables
}
