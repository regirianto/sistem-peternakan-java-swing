/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package sistemmanajemenpenjualanhewanternak.views;

import sistemmanajemenpenjualanhewanternak.Utils.Database;
import javax.swing.table.DefaultTableModel;
import java.awt.Color;
import java.awt.Font;

/**
 *
 * @author Rizki Fadillah
 */

public class ViewListKambing extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ViewListKambing.class.getName());

    public ViewListKambing() {
        initComponents();
        
        setTableModel();
        styleTable();
        
        cbFilterStatus.removeAllItems();
        cbFilterStatus.addItem("Semua Status");
        cbFilterStatus.addItem("Tersedia");
        cbFilterStatus.addItem("Terjual");
        cbFilterStatus.addItem("Sakit");
        cbFilterStatus.addItem("Mati");
        
        tampilData();
        
        btnEdit.setEnabled(false);
        btnHapus.setEnabled(false);
    }
    
    private void setTableModel() {

        DefaultTableModel model = new DefaultTableModel() {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        model.addColumn("ID");
        model.addColumn("Kode");
        model.addColumn("Supplier");
        model.addColumn("Jenis Kelamin");
        model.addColumn("Berat Awal");
        model.addColumn("Harga Modal");
        model.addColumn("Status");
        model.addColumn("Tanggal Masuk");

        tblKambing.setModel(model);
    }
    
    private void styleTable() {

        // Tinggi baris
        tblKambing.setRowHeight(25);
        // Font isi tabel
        tblKambing.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        // Font header
        tblKambing.getTableHeader().setFont(
                new Font("Segoe UI", Font.PLAIN, 13));
        // Warna header
        tblKambing.getTableHeader().setDefaultRenderer(
            new javax.swing.table.DefaultTableCellRenderer() {

            @Override
            public java.awt.Component getTableCellRendererComponent(
                    javax.swing.JTable table,
                    Object value,
                    boolean isSelected,
                    boolean hasFocus,
                    int row,
                    int column) {

                java.awt.Component c =
                        super.getTableCellRendererComponent(
                                table,
                                value,
                                isSelected,
                                hasFocus,
                                row,
                                column);

                c.setBackground(new Color(153,255,153));
                c.setForeground(Color.BLACK);

                setHorizontalAlignment(LEFT);
                
                // GARIS HEADER
                setBorder(
                    javax.swing.BorderFactory.createMatteBorder(
                        0,0,1,1,
                        new Color(200,200,200)
                    )
                );
                
                return c;
            }
        });
        
        // Background tabel
        tblKambing.setBackground(Color.WHITE);
        // Warna garis tabel
        tblKambing.setGridColor(new Color(200,200,200));
        // Warna row saat dipilih
        tblKambing.setSelectionBackground(
                new Color(200,230,201));
        // Warna text saat dipilih
        tblKambing.setSelectionForeground(Color.BLACK);
        // Tinggi header
        tblKambing.getTableHeader().setPreferredSize(
                new java.awt.Dimension(0,35));
        // Tidak bisa edit tabel
        tblKambing.setDefaultEditor(Object.class, null);

        // Tidak bisa resize column
        tblKambing.getTableHeader().setResizingAllowed(false);
        // Tidak bisa reorder column
        tblKambing.getTableHeader().setReorderingAllowed(false);
        
        tblKambing.setShowGrid(true);
    }   
    

    private void tampilData() {

        DefaultTableModel model =
                (DefaultTableModel) tblKambing.getModel();

        model.setRowCount(0);

        try {

            java.sql.Connection conn = Database.getKoneksi();

            String sql =
                    "SELECT kambing.*, "
                    + "supplier_kambing.nama_supplier "
                    + "FROM kambing "
                    + "LEFT JOIN supplier_kambing "
                    + "ON kambing.id_supplier="
                    + "supplier_kambing.id_supplier";
    
            java.sql.Statement st = conn.createStatement();
            java.sql.ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {

                Object[] row = {
                    rs.getInt("id_kambing"),
                    rs.getString("kode_kambing"),
                    rs.getString("nama_supplier"),
                    rs.getString("jenis_kelamin"),
                    rs.getDouble("berat_awal"),
                    rs.getDouble("harga_modal"),
                    rs.getString("status"),
                    rs.getDate("tanggal_masuk")
                };

                model.addRow(row);
            }

        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(this,e);
        }
    }
    
    private void filterData() {

        DefaultTableModel model = (DefaultTableModel) tblKambing.getModel();

        model.setRowCount(0);

        try {
    
            java.sql.Connection conn = Database.getKoneksi();

            String keyword = txtCari.getText();
            if (keyword.equals(
                    "Cari kode kambing/Supplier....")) {

                keyword = "";
            }

            String status = cbFilterStatus.getSelectedItem().toString();

            String sql =
                "SELECT kambing.*, "
                + "supplier_kambing.nama_supplier "
                + "FROM kambing "
                + "LEFT JOIN supplier_kambing "
                + "ON kambing.id_supplier="
                + "supplier_kambing.id_supplier "
                + "WHERE "
                + "(kode_kambing LIKE ? "
                + "OR nama_supplier LIKE ?)";

            if (!status.equals("Semua Status")) {
    
                sql += " AND status=?";
            }

            java.sql.PreparedStatement pst = conn.prepareStatement(sql);
    
            pst.setString(1, "%" + keyword + "%");
            pst.setString(2, "%" + keyword + "%");
    
            if (!status.equals("Semua Status")) {
                pst.setString(3, status);
            }

            java.sql.ResultSet rs = pst.executeQuery();

            while (rs.next()) {

                Object[] row = {

                    rs.getInt("id_kambing"),
                    rs.getString("kode_kambing"),
                    rs.getString("nama_supplier"),
                    rs.getString("jenis_kelamin"),
                    rs.getDouble("berat_awal") + " Kg",
                    String.format("%,.0f",
                    rs.getDouble("harga_modal")),
                    rs.getString("status"),
                    rs.getDate("tanggal_masuk")
                };

                model.addRow(row);
            }

        } catch (Exception e) {

            javax.swing.JOptionPane.showMessageDialog(
                    this,
                    e
            );
        }
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        txtCari = new javax.swing.JTextField();
        cbFilterStatus = new javax.swing.JComboBox<>();
        btnRefresh = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblKambing = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lblTanggal = new javax.swing.JLabel();
        lblKode = new javax.swing.JLabel();
        lblSupplier = new javax.swing.JLabel();
        lblJenisKelamin = new javax.swing.JLabel();
        lblModal = new javax.swing.JLabel();
        Tanggal = new javax.swing.JLabel();
        lblBerat = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();
        btnEdit = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel1 = new javax.swing.JLabel();
        btnTambah = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(15, 107, 47));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        txtCari.setForeground(new java.awt.Color(204, 204, 204));
        txtCari.setText("Cari kode kambing/Supplier....");
        txtCari.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtCariFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtCariFocusLost(evt);
            }
        });
        txtCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCariKeyReleased(evt);
            }
        });

        cbFilterStatus.addActionListener(this::cbFilterStatusActionPerformed);

        btnRefresh.setBackground(new java.awt.Color(102, 204, 255));
        btnRefresh.setText("Refresh Data");
        btnRefresh.addActionListener(this::btnRefreshActionPerformed);

        jScrollPane2.setBackground(new java.awt.Color(230, 230, 230));

        tblKambing.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblKambing.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblKambingMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblKambing);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jPanel4.setBackground(new java.awt.Color(0, 153, 102));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("DETAIL KAMBING");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addContainerGap())
        );

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel3.setText("Kode Kambing:");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel4.setText("Dari Supplier:");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel5.setText("Jenis Kelamin:");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel6.setText("Berat (Kg):");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel7.setText("Modal:");

        lblTanggal.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTanggal.setText("-");

        lblKode.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblKode.setText("-");

        lblSupplier.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblSupplier.setText("-");

        lblJenisKelamin.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblJenisKelamin.setText("-");

        lblModal.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblModal.setText("-");

        Tanggal.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        Tanggal.setText("Tanggal Masuk:");

        lblBerat.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblBerat.setText("-");

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(51, 102, 255), new java.awt.Color(153, 255, 153)));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setText("Status Kambing:");

        lblStatus.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        lblStatus.setText("-");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(lblStatus))
                .addContainerGap(30, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblStatus)
                .addContainerGap(25, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel3)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(Tanggal))
                .addGap(30, 30, 30)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTanggal)
                    .addComponent(lblModal)
                    .addComponent(lblSupplier)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(lblBerat, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblJenisKelamin, javax.swing.GroupLayout.Alignment.LEADING))
                    .addComponent(lblKode))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(161, 161, 161))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(65, 65, 65)
                        .addComponent(lblJenisKelamin)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblBerat)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblModal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblTanggal))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(lblSupplier))
                        .addGap(7, 7, 7)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(lblKode))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Tanggal))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        btnEdit.setBackground(new java.awt.Color(30, 136, 229));
        btnEdit.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnEdit.setForeground(new java.awt.Color(255, 255, 255));
        btnEdit.setText("Edit Data Terpilih");
        btnEdit.addActionListener(this::btnEditActionPerformed);

        btnHapus.setBackground(new java.awt.Color(229, 57, 53));
        btnHapus.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnHapus.setForeground(new java.awt.Color(255, 255, 255));
        btnHapus.setText("Hapus Data Terpilih");
        btnHapus.addActionListener(this::btnHapusActionPerformed);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnEdit)
                .addGap(42, 42, 42)
                .addComponent(btnHapus)
                .addGap(25, 25, 25))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(txtCari, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(cbFilterStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 267, Short.MAX_VALUE)
                        .addComponent(btnRefresh)
                        .addGap(17, 17, 17))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane2))
                        .addContainerGap())
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jSeparator2)
                        .addContainerGap())))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCari, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbFilterStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRefresh))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 299, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnEdit)
                    .addComponent(btnHapus))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(55, Short.MAX_VALUE))
        );

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("DATA KAMBING");

        btnTambah.setBackground(new java.awt.Color(67, 160, 71));
        btnTambah.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnTambah.setForeground(new java.awt.Color(255, 255, 255));
        btnTambah.setText("+ Tambah Kambing");
        btnTambah.addActionListener(this::btnTambahActionPerformed);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(472, 472, 472)
                .addComponent(btnTambah, javax.swing.GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE)
                .addGap(16, 16, 16))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(btnTambah, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnTambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahActionPerformed
        FormInputKambing form = new FormInputKambing();
        form.setVisible(true);
    }//GEN-LAST:event_btnTambahActionPerformed

    private void txtCariKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCariKeyReleased
        filterData();
    }//GEN-LAST:event_txtCariKeyReleased

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
       
        txtCari.setText("");
        cbFilterStatus.setSelectedIndex(0);

        tampilData();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void cbFilterStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbFilterStatusActionPerformed
        filterData();
    }//GEN-LAST:event_cbFilterStatusActionPerformed

    private void tblKambingMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblKambingMouseClicked

        int row = tblKambing.getSelectedRow();
        
        btnEdit.setEnabled(true);
        btnHapus.setEnabled(true);
        
        lblKode.setText(tblKambing.getValueAt(row,1).toString());
        lblSupplier.setText(tblKambing.getValueAt(row,2).toString());
        lblJenisKelamin.setText(tblKambing.getValueAt(row,3).toString());
        lblBerat.setText(tblKambing.getValueAt(row,4).toString());
        lblModal.setText("Rp. " +tblKambing.getValueAt(row,5).toString());
        lblStatus.setText(tblKambing.getValueAt(row,6).toString());
        lblTanggal.setText(tblKambing.getValueAt(row,7).toString());
        
        String status =
        tblKambing.getValueAt(row,6).toString();
        
        lblStatus.setText(status);
        
            if (status.equals("Tersedia")) {

            lblStatus.setForeground(new java.awt.Color(67,160,71));

        } else if (status.equals("Sakit")) {

            lblStatus.setForeground(new java.awt.Color(251,140,0));

        } else if (status.equals("Mati")) {

            lblStatus.setForeground(new java.awt.Color(229,57,53));

        } else {

            lblStatus.setForeground(new java.awt.Color(30,136,229));
        }

        lblTanggal.setText(tblKambing.getValueAt(row,7).toString());
    }//GEN-LAST:event_tblKambingMouseClicked

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed

        int row = tblKambing.getSelectedRow();

        if (row == -1) {

            javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "Pilih data terlebih dahulu!"
            );
            
            return;
        }

        int konfirmasi =
                javax.swing.JOptionPane.showConfirmDialog(
                        this,
                        "Yakin ingin menghapus data ini?",
                        "Konfirmasi Hapus",
                        javax.swing.JOptionPane.YES_NO_OPTION
            );

        if (konfirmasi == javax.swing.JOptionPane.YES_OPTION) {

            try {

                java.sql.Connection conn = Database.getKoneksi();

                int id =
                        Integer.parseInt(
                        tblKambing.getValueAt(row,0).toString());

                String sql =
                        "DELETE FROM kambing "
                        + "WHERE id_kambing=?";

                java.sql.PreparedStatement pst = conn.prepareStatement(sql);

                pst.setInt(1, id);
                pst.executeUpdate();

                javax.swing.JOptionPane.showMessageDialog(
                        this,
                        "Data berhasil dihapus!"
                );

                tampilData();

                btnEdit.setEnabled(false);
                btnHapus.setEnabled(false);

            } catch (Exception e) {

                javax.swing.JOptionPane.showMessageDialog(
                        this,
                        e
                );
            }
        }
    }//GEN-LAST:event_btnHapusActionPerformed

    private void txtCariFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCariFocusGained
        // TODO add your handling code here:
        if (txtCari.getText().equals(
                    "Cari kode kambing/Supplier....")) {

            txtCari.setText("");
            txtCari.setForeground(Color.BLACK);
        }
    }//GEN-LAST:event_txtCariFocusGained

    private void txtCariFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCariFocusLost
        // TODO add your handling code here:
        if (txtCari.getText().trim().equals("")) {

            txtCari.setText(
                    "Cari kode kambing/Supplier....");

            txtCari.setForeground(
                    new Color(204,204,204));
        }
    }//GEN-LAST:event_txtCariFocusLost

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        // TODO add your handling code here:
        int row = tblKambing.getSelectedRow();

        if (row == -1) {

            javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "Pilih data terlebih dahulu!"
            );

            return;
        }

        int id = Integer.parseInt(
                tblKambing.getValueAt(row, 0).toString());

        String kode =
                tblKambing.getValueAt(row, 1).toString();

        String supplier =
                tblKambing.getValueAt(row, 2).toString();

        String jenisKelamin =
                tblKambing.getValueAt(row, 3).toString();

        String berat =
                tblKambing.getValueAt(row, 4).toString();

        String modal =
                tblKambing.getValueAt(row, 5).toString();

        String status =
                tblKambing.getValueAt(row, 6).toString();

        java.util.Date tanggal =
                java.sql.Date.valueOf(
                tblKambing.getValueAt(row, 7).toString());

        FormEditKambing form =
                new FormEditKambing(
                        id,
                        kode,
                        supplier,
                        jenisKelamin,
                        berat,
                        modal,
                        status,
                        tanggal);

        form.setVisible(true);
    }//GEN-LAST:event_btnEditActionPerformed


    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(() -> new ViewListKambing().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Tanggal;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnTambah;
    private javax.swing.JComboBox<String> cbFilterStatus;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lblBerat;
    private javax.swing.JLabel lblJenisKelamin;
    private javax.swing.JLabel lblKode;
    private javax.swing.JLabel lblModal;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblSupplier;
    private javax.swing.JLabel lblTanggal;
    private javax.swing.JTable tblKambing;
    private javax.swing.JTextField txtCari;
    // End of variables declaration//GEN-END:variables
}
