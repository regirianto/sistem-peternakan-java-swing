package sistemmanajemenpenjualanhewanternak.views;

import sistemmanajemenpenjualanhewanternak.Utils.Database;
import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class TambahPelanggan extends JFrame {

    DaftarPelanggan dashboard;

    JTextField txtNama, txtKontak;
    JTextArea txtAlamat;

    JButton btnSimpan;

    Color biru = new Color(52, 152, 219);

    public TambahPelanggan(
            DaftarPelanggan dashboard) {

        this.dashboard = dashboard;

        setTitle("Tambah Pelanggan");
        setSize(420, 400);
        setLocationRelativeTo(null);
        setLayout(null);

        getContentPane().setBackground(Color.WHITE);

        JLabel title = new JLabel("Tambah Pelanggan");
        title.setBounds(100, 20, 250, 30);

        title.setFont(new Font("Segoe UI",
                Font.BOLD, 22));

        title.setForeground(biru);

        add(title);

        JLabel l1 = new JLabel("Nama");
        l1.setBounds(30, 80, 100, 25);

        add(l1);

        txtNama = new JTextField();
        txtNama.setBounds(130, 80, 220, 30);

        add(txtNama);

        JLabel l2 = new JLabel("Kontak");
        l2.setBounds(30, 130, 100, 25);

        add(l2);

        txtKontak = new JTextField();
        txtKontak.setBounds(130, 130, 220, 30);

        add(txtKontak);

        JLabel l3 = new JLabel("Alamat");
        l3.setBounds(30, 180, 100, 25);

        add(l3);

        txtAlamat = new JTextArea();

        JScrollPane scroll =
                new JScrollPane(txtAlamat);

        scroll.setBounds(130, 180, 220, 80);

        add(scroll);

        btnSimpan = new JButton("Simpan");
        btnSimpan.setBounds(130, 290, 120, 35);

        btnSimpan.setBackground(biru);
        btnSimpan.setForeground(Color.WHITE);

        add(btnSimpan);
        
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); 
        setResizable(false);

        btnSimpan.addActionListener(e -> {
            simpanData();
        });
    }

    private void simpanData() {

        try {

            Connection c = Database.getKoneksi();

            String sql =
                    "INSERT INTO pelanggan"
                    + "(nama_pelanggan,kontak,alamat)"
                    + "VALUES(?,?,?)";

            PreparedStatement p =
                    c.prepareStatement(sql);

            p.setString(1, txtNama.getText());
            p.setString(2, txtKontak.getText());
            p.setString(3, txtAlamat.getText());

            p.executeUpdate();

            JOptionPane.showMessageDialog(null,
                    "Data berhasil ditambahkan");

            // AUTO REFRESH
            dashboard.tampilData();

            dispose();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(null, e);
        }
    }
}