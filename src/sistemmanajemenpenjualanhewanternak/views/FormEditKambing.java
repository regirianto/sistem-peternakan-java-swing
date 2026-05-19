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
public class FormEditKambing extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FormEditKambing.class.getName());

    private int idKambing;
    
        public FormEditKambing() {

        initComponents();

        loadSupplier();

        cbStatus.removeAllItems();

        cbStatus.addItem("Tersedia");
        cbStatus.addItem("Terjual");
        cbStatus.addItem("Sakit");
        cbStatus.addItem("Mati");
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    }
        
    public FormEditKambing(
            int id,
            String kode,
            String supplier,
            String jenisKelamin,
            String berat,
            String modal,
            String status,
            java.util.Date tanggal) {

        initComponents();

        this.idKambing = id;

        loadSupplier();
        
        cbStatus.removeAllItems();
        
        cbStatus.addItem("Tersedia");
        cbStatus.addItem("Terjual");
        cbStatus.addItem("Sakit");
        cbStatus.addItem("Mati");

        txtKodeKambing.setText(kode);

        cbSupplier.setSelectedItem(supplier);

        if (jenisKelamin.equals("Jantan")) {
            rbJantan.setSelected(true);
        } else {
            rbBetina.setSelected(true);
        }

        txtBerat.setText(berat.replace(" Kg", ""));
        txtModal.setText(modal.replace(",", ""));

        cbStatus.setSelectedItem(status);

        dcTanggalMasuk.setDate(tanggal);
    }

    private void loadSupplier() {

    try {

        java.sql.Connection conn = Database.getKoneksi();

        String sql = "SELECT * FROM supplier_kambing";

        java.sql.Statement st = conn.createStatement();

        java.sql.ResultSet rs = st.executeQuery(sql);
    
        while (rs.next()) {
            cbSupplier.addItem(
                    rs.getString("nama_supplier"));
        }
        
    } catch (Exception e) {
        javax.swing.JOptionPane.showMessageDialog(null, e);
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
                    "SELECT * FROM kambing "
                    + "WHERE kode_kambing=? "
                    + "AND id_kambing!=?";
 
            java.sql.PreparedStatement pst = conn.prepareStatement(sql);

            pst.setString(1, txtKodeKambing.getText());
            
            pst.setInt(2, idKambing);

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

        cbStatus.setSelectedIndex(0);
        
        dcTanggalMasuk.setDate(new java.util.Date());
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
        cbStatus = new javax.swing.JComboBox<>();
        dcTanggalMasuk = new de.wannawork.jcalendar.JCalendarComboBox();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtKodeKambing = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        btnBatal = new javax.swing.JButton();
        btnReset = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(15, 107, 47));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("EDIT DATA KAMBING");

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel2.add(txtBerat, new org.netbeans.lib.awtextra.AbsoluteConstraints(168, 151, 235, -1));
        jPanel2.add(txtModal, new org.netbeans.lib.awtextra.AbsoluteConstraints(168, 185, 235, -1));

        cbSupplier.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-- Pilih Supplier/ Tanpa Supplier --" }));
        jPanel2.add(cbSupplier, new org.netbeans.lib.awtextra.AbsoluteConstraints(168, 83, 235, -1));

        rbJantan.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroup1.add(rbJantan);
        rbJantan.setText("Jantan");
        rbJantan.addActionListener(this::rbJantanActionPerformed);
        jPanel2.add(rbJantan, new org.netbeans.lib.awtextra.AbsoluteConstraints(168, 118, -1, -1));

        rbBetina.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroup1.add(rbBetina);
        rbBetina.setText("Betina");
        jPanel2.add(rbBetina, new org.netbeans.lib.awtextra.AbsoluteConstraints(242, 118, -1, -1));

        cbStatus.addActionListener(this::cbStatusActionPerformed);
        jPanel2.add(cbStatus, new org.netbeans.lib.awtextra.AbsoluteConstraints(168, 219, 140, -1));
        jPanel2.add(dcTanggalMasuk, new org.netbeans.lib.awtextra.AbsoluteConstraints(168, 253, 140, -1));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel2.setText("Kode Kambing:");
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(32, 49, -1, -1));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel3.setText("Supplier:");
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(32, 83, -1, -1));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel4.setText("Jenis Kelamin:");
        jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(32, 117, -1, -1));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel5.setText("Berat Awal (Kg):");
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(32, 151, -1, -1));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel6.setText("Harga Modal (Rp):");
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(32, 185, -1, -1));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel7.setText("Status Kambing:");
        jPanel2.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(32, 219, -1, -1));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel8.setText("Tanggal Masuk:");
        jPanel2.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(32, 253, -1, -1));
        jPanel2.add(txtKodeKambing, new org.netbeans.lib.awtextra.AbsoluteConstraints(168, 49, 235, -1));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(15, 107, 47));
        jLabel9.setText("INFORMASI KAMBING");
        jPanel2.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(32, 11, -1, -1));
        jPanel2.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 350, 420, 12));

        btnBatal.setBackground(new java.awt.Color(229, 57, 53));
        btnBatal.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnBatal.setText("Batal");
        btnBatal.addActionListener(this::btnBatalActionPerformed);
        jPanel2.add(btnBatal, new org.netbeans.lib.awtextra.AbsoluteConstraints(304, 368, 100, 35));

        btnReset.setBackground(new java.awt.Color(251, 140, 0));
        btnReset.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnReset.setText("Reset");
        btnReset.addActionListener(this::btnResetActionPerformed);
        jPanel2.add(btnReset, new org.netbeans.lib.awtextra.AbsoluteConstraints(166, 368, 100, 35));

        btnUpdate.setBackground(new java.awt.Color(30, 136, 229));
        btnUpdate.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnUpdate.setText("Update");
        btnUpdate.addActionListener(this::btnUpdateActionPerformed);
        jPanel2.add(btnUpdate, new org.netbeans.lib.awtextra.AbsoluteConstraints(27, 368, 100, 35));

        jPanel3.setBackground(new java.awt.Color(232, 245, 233));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(15, 107, 47));
        jLabel10.setText("Ubah data lalu klik UPDATE untuk menyimpan perubahan!");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel10)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel10))
        );

        jPanel2.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 329, 420, -1));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 436, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 421, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 458, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void rbJantanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbJantanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rbJantanActionPerformed

    private void cbStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbStatusActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbStatusActionPerformed

    private void btnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBatalActionPerformed
        dispose();
    }//GEN-LAST:event_btnBatalActionPerformed

    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetActionPerformed
        resetForm();
    }//GEN-LAST:event_btnResetActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed

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

            String namaSupplier = cbSupplier.getSelectedItem().toString();

            int idSupplier = 0;

            String sqlSupplier =
            "SELECT id_supplier "
            + "FROM supplier_kambing "
            + "WHERE nama_supplier=?";

            java.sql.PreparedStatement pstSupplier =
            conn.prepareStatement(sqlSupplier);

            pstSupplier.setString(1, namaSupplier);

            java.sql.ResultSet rsSupplier =
            pstSupplier.executeQuery();

            if (rsSupplier.next()) {
                idSupplier =
                rsSupplier.getInt("id_supplier");
            }

            String sql =
            "UPDATE kambing SET "
            + "kode_kambing=?, "
            + "id_supplier=?, "
            + "jenis_kelamin=?, "
            + "berat_awal=?, "
            + "harga_modal=?, "
            + "status=?, "
            + "tanggal_masuk=? "
            + "WHERE id_kambing=?";

            java.sql.PreparedStatement pst = conn.prepareStatement(sql);

            pst.setString(1, txtKodeKambing.getText());
            pst.setInt(2, idSupplier);
            pst.setString(3, jenisKelamin);

            pst.setDouble(4,
                Double.parseDouble(txtBerat.getText()));

            pst.setDouble(5,
                Double.parseDouble(txtModal.getText()));

            pst.setString(6,
                    cbStatus.getSelectedItem().toString());

            java.util.Date tanggal =
            dcTanggalMasuk.getDate();

            java.sql.Date sqlTanggal =
            new java.sql.Date(tanggal.getTime());

            pst.setDate(7, sqlTanggal);
            
            pst.setInt(8, idKambing);

            pst.executeUpdate();

            javax.swing.JOptionPane.showMessageDialog(
                this,
                "Data kambing berhasil diUpdate!"
            );
            
            new ViewListKambing().setVisible(true);

            dispose();

            resetForm();

        } catch (Exception e) {

            javax.swing.JOptionPane.showMessageDialog(this, e);
        }
    }//GEN-LAST:event_btnUpdateActionPerformed


    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new FormEditKambing().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBatal;
    private javax.swing.JButton btnReset;
    private javax.swing.JButton btnUpdate;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<String> cbStatus;
    private javax.swing.JComboBox<String> cbSupplier;
    private de.wannawork.jcalendar.JCalendarComboBox dcTanggalMasuk;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
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
