package sistemmanajemenpenjualanhewanternak.views;

import sistemmanajemenpenjualanhewanternak.Utils.Database;
import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class UpdatePelanggan extends JFrame {

    DaftarPelanggan dashboard;

    int id;

    JTextField txtNama, txtKontak;
    JTextArea txtAlamat;

    JButton btnUpdate;

    Color biru = new Color(52, 152, 219);

    public UpdatePelanggan(
            DaftarPelanggan dashboard,
            int id,
            String nama,
            String kontak,
            String alamat) {

        this.dashboard = dashboard;
        this.id = id;

        setTitle("Update Pelanggan");
        setSize(420, 400);

        setLocationRelativeTo(null);

        setLayout(null);

        getContentPane().setBackground(Color.WHITE);

        JLabel title =
                new JLabel("Update Pelanggan");

        title.setBounds(100, 20, 250, 30);

        title.setFont(new Font("Segoe UI",
                Font.BOLD, 22));

        title.setForeground(biru);

        add(title);

        JLabel l1 = new JLabel("Nama");
        l1.setBounds(30, 80, 100, 25);

        add(l1);

        txtNama = new JTextField(nama);
        txtNama.setBounds(130, 80, 220, 30);

        add(txtNama);

        JLabel l2 = new JLabel("Kontak");
        l2.setBounds(30, 130, 100, 25);

        add(l2);

        txtKontak = new JTextField(kontak);
        txtKontak.setBounds(130, 130, 220, 30);

        add(txtKontak);

        JLabel l3 = new JLabel("Alamat");
        l3.setBounds(30, 180, 100, 25);

        add(l3);

        txtAlamat = new JTextArea(alamat);

        JScrollPane scroll =
                new JScrollPane(txtAlamat);

        scroll.setBounds(130, 180, 220, 80);

        add(scroll);

        btnUpdate = new JButton("Update");
        btnUpdate.setBounds(130, 290, 120, 35);

        btnUpdate.setBackground(biru);
        btnUpdate.setForeground(Color.WHITE);

        add(btnUpdate);

        btnUpdate.addActionListener(e -> {
            updateData();
        });
    }

    private void updateData() {

        try {

            Connection c = Database.getKoneksi();

            String sql =
                    "UPDATE pelanggan "
                    + "SET nama=?, kontak=?, alamat=? "
                    + "WHERE id=?";

            PreparedStatement p =
                    c.prepareStatement(sql);

            p.setString(1, txtNama.getText());
            p.setString(2, txtKontak.getText());
            p.setString(3, txtAlamat.getText());
            p.setInt(4, id);

            p.executeUpdate();

            JOptionPane.showMessageDialog(null,
                    "Data berhasil diupdate");

            // AUTO REFRESH
            dashboard.tampilData();

            dispose();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(null, e);
        }
    }
}