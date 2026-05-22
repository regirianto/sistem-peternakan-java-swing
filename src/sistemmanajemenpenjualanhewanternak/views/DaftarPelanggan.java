package sistemmanajemenpenjualanhewanternak.views;

import sistemmanajemenpenjualanhewanternak.Utils.Database;
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;


public class DaftarPelanggan extends JFrame {

    JTable tabel;
    DefaultTableModel model;

    JTextField txtCari;

    JButton btnCari;
    JButton btnTambah;
    JButton btnUpdate;

    Color biru = new Color(52, 152, 219);

    public DaftarPelanggan() {

        setTitle("Daftar Pelanggan");
        setSize(900, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(245, 247, 250));

        add(panel);

        JLabel title = new JLabel("DAFTAR PELANGGAN");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(biru);
        title.setBounds(30, 20, 400, 30);

        panel.add(title);

        txtCari = new JTextField();
        txtCari.setBounds(30, 80, 250, 35);
        txtCari.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        panel.add(txtCari);

        btnCari = new JButton("Search");
        btnCari.setBounds(290, 80, 100, 35);

        styleButton(btnCari);

        panel.add(btnCari);

        btnTambah = new JButton("Tambah Pelanggan");
        btnTambah.setBounds(520, 80, 170, 35);

        styleButton(btnTambah);

        panel.add(btnTambah);

        btnUpdate = new JButton("Update Data");
        btnUpdate.setBounds(700, 80, 150, 35);

        styleButton(btnUpdate);

        panel.add(btnUpdate);

        model = new DefaultTableModel();

        model.addColumn("ID");
        model.addColumn("Nama");
        model.addColumn("Kontak");
        model.addColumn("Alamat");

        tabel = new JTable(model);

        styleTable();

        JScrollPane scroll = new JScrollPane(tabel);
        scroll.setBounds(30, 140, 820, 320);

        panel.add(scroll);

        tampilData();

        // SEARCH
        btnCari.addActionListener(e -> cariData());

        // TAMBAH
        btnTambah.addActionListener(e -> {

            new TambahPelanggan(this)
                    .setVisible(true);
        });

        // UPDATE
        btnUpdate.addActionListener(e -> {

            int baris = tabel.getSelectedRow();

            if (baris >= 0) {

                int id = Integer.parseInt(
                        tabel.getValueAt(baris, 0).toString());

                String nama =
                        tabel.getValueAt(baris, 1).toString();

                String kontak =
                        tabel.getValueAt(baris, 2).toString();

                String alamat =
                        tabel.getValueAt(baris, 3).toString();

                new UpdatePelanggan(
                        this,
                        id,
                        nama,
                        kontak,
                        alamat
                ).setVisible(true);

            } else {

                JOptionPane.showMessageDialog(null,
                        "Pilih data terlebih dahulu!");
            }
        });
    }

    // STYLE BUTTON
    private void styleButton(JButton button) {

        button.setBackground(biru);
        button.setForeground(Color.WHITE);

        button.setFocusPainted(false);

        button.setFont(new Font("Segoe UI",
                Font.BOLD, 13));

        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // STYLE TABLE
    private void styleTable() {

        tabel.setRowHeight(30);

        tabel.setFont(new Font("Segoe UI",
                Font.PLAIN, 13));

        JTableHeader header = tabel.getTableHeader();

        header.setFont(new Font("Segoe UI",
                Font.BOLD, 14));

        header.setBackground(biru);

        header.setForeground(Color.WHITE);

        tabel.setSelectionBackground(
                new Color(184, 207, 229));
    }

    // TAMPIL DATA
    public void tampilData() {

        model.setRowCount(0);

        try {

            Connection c = Database.getKoneksi();

            String sql = "SELECT * FROM pelanggan";

            Statement s = c.createStatement();

            ResultSet r = s.executeQuery(sql);

            while (r.next()) {

                model.addRow(new Object[]{
                    r.getInt("id"),
                    r.getString("nama"),
                    r.getString("kontak"),
                    r.getString("alamat")
                });
            }

            model.fireTableDataChanged();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(null, e);
        }
    }

    // SEARCH
    public void cariData() {

        model.setRowCount(0);

        try {

            Connection c = Database.getKoneksi();

            String sql =
                    "SELECT * FROM pelanggan "
                    + "WHERE nama LIKE ? "
                    + "OR kontak LIKE ?";

            PreparedStatement p =
                    c.prepareStatement(sql);

            p.setString(1,
                    "%" + txtCari.getText() + "%");

            p.setString(2,
                    "%" + txtCari.getText() + "%");

            ResultSet r = p.executeQuery();

            while (r.next()) {

                model.addRow(new Object[]{
                    r.getInt("id"),
                    r.getString("nama"),
                    r.getString("kontak"),
                    r.getString("alamat")
                });
            }

            model.fireTableDataChanged();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(null, e);
        }
    }

    public static void main(String[] args) {

        new DaftarPelanggan()
                .setVisible(true);
    }
}