/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Main;

/**
 *
 * @author teguh
 */
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.NumberFormat;
import java.util.Locale;

public class MainApp extends JFrame {
    // Deklarasi Komponen GUI
    private JLabel lblTotalPemasukan, lblTotalPengeluaran, lblSisaSaldo;
    private JRadioButton rbPemasukan, rbPengeluaran;
    private JComboBox<String> cbKategori;
    private JTextField txtNominal, txtKeterangan;
    private JButton btnSimpan;
    private JTable tblTransaksi;
    private DefaultTableModel tableModel;
    
    // Format Rupiah
    private NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

    public MainApp() {
        // Setup JFrame Dasar
        setTitle("Aplikasi Rekap Keuangan Pribadi");
        setSize(950, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Posisi di tengah layar
        setLayout(new BorderLayout(15, 15));

        initUI();
        loadKategori("Pemasukan"); // Default load combo box
        loadDataTabel(); // Load data dan hitung saldo awal
    }

    private void initUI() {
        // ================= PANEL ATAS (DASHBOARD SALDO) =================
        JPanel panelAtas = new JPanel(new GridLayout(1, 3, 10, 0));
        panelAtas.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        lblTotalPemasukan = createCardLabel("Total Pemasukan", new Color(46, 204, 113));
        lblTotalPengeluaran = createCardLabel("Total Pengeluaran", new Color(231, 76, 60));
        lblSisaSaldo = createCardLabel("Saldo", new Color(52, 152, 219));

        panelAtas.add(lblTotalPemasukan);
        panelAtas.add(lblTotalPengeluaran);
        panelAtas.add(lblSisaSaldo);
        add(panelAtas, BorderLayout.NORTH);

        // ================= PANEL TENGAH (BAGI DUA KIRI & KANAN) =================
        JPanel panelTengah = new JPanel(new GridLayout(1, 2, 15, 0));
        panelTengah.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- A. PANEL KIRI (FORM INPUT) ---
        JPanel panelKiri = new JPanel(null); // Absolute layout untuk form custom
        panelKiri.setBorder(BorderFactory.createTitledBorder("FORM TRANSAKSI"));

        JLabel lblJenis = new JLabel("Jenis Transaksi:");
        lblJenis.setBounds(20, 30, 120, 25);
        panelKiri.add(lblJenis);

        rbPemasukan = new JRadioButton("Pemasukan", true);
        rbPemasukan.setBounds(120, 30, 100, 25);
        rbPengeluaran = new JRadioButton("Pengeluaran");
        rbPengeluaran.setBounds(220, 30, 100, 25);
        
        ButtonGroup bgJenis = new ButtonGroup();
        bgJenis.add(rbPemasukan);
        bgJenis.add(rbPengeluaran);
        panelKiri.add(rbPemasukan);
        panelKiri.add(rbPengeluaran);

        JLabel lblKategori = new JLabel("Pilih Kategori:");
        lblKategori.setBounds(20, 70, 120, 25);
        panelKiri.add(lblKategori);
        
        cbKategori = new JComboBox<>();
        cbKategori.setBounds(120, 70, 250, 25);
        panelKiri.add(cbKategori);

        JLabel lblNominal = new JLabel("Nominal (Rp):");
        lblNominal.setBounds(20, 110, 120, 25);
        panelKiri.add(lblNominal);
        
        txtNominal = new JTextField();
        txtNominal.setBounds(120, 110, 250, 25);
        panelKiri.add(txtNominal);

        JLabel lblKeterangan = new JLabel("Keterangan:");
        lblKeterangan.setBounds(20, 150, 120, 25);
        panelKiri.add(lblKeterangan);
        
        txtKeterangan = new JTextField();
        txtKeterangan.setBounds(120, 150, 250, 25);
        panelKiri.add(txtKeterangan);

        btnSimpan = new JButton("SIMPAN");
        btnSimpan.setBounds(120, 200, 120, 35);
        btnSimpan.setBackground(new Color(52, 152, 219));
        btnSimpan.setForeground(Color.BLUE);
        panelKiri.add(btnSimpan);

        panelTengah.add(panelKiri);

        // ---PANEL KANAN (TABEL RIWAYAT) ---
        JPanel panelKanan = new JPanel(new GridLayout(1, 1));
        panelKanan.setBorder(BorderFactory.createTitledBorder("REKAP"));

        String[] kolomTabel = {"Tanggal", "Kategori", "Jenis", "Nominal", "Keterangan"};
        tableModel = new DefaultTableModel(kolomTabel, 0);
        tblTransaksi = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tblTransaksi);
        panelKanan.add(scrollPane, BorderLayout.CENTER);

        panelTengah.add(panelKanan);
        add(panelTengah, BorderLayout.CENTER);

        // ================= 3. EVENT LISTENERS (LOGIKA) =================

        // Logika Dynamic ComboBox
        rbPemasukan.addActionListener(e -> loadKategori("Pemasukan"));
        rbPengeluaran.addActionListener(e -> loadKategori("Pengeluaran"));

        // Logika Tombol Simpan
        btnSimpan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                simpanData();
            }
        });
    }

    // Fungsi Bantuan untuk mempercantik Label Dasboard
    private JLabel createCardLabel(String title, Color bgColor) {
        JLabel label = new JLabel("<html><div style='text-align: center; padding: 10px;'>"
                + "<p style='margin: 0; font-size: 12px; color: #555;'>" + title + "</p>"
                + "<h2 style='margin: 0; color: " + String.format("#%06x", bgColor.getRGB() & 0x00FFFFFF) + ";'>Rp 0</h2></div></html>");
        label.setOpaque(true);
        label.setBackground(new Color(245, 245, 245));
        label.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }

    // Fungsi mengambil daftar kategori dari Database ke ComboBox
    private void loadKategori(String jenis) {
        cbKategori.removeAllItems();
        try {
            Connection conn = Koneksi.getKoneksi();
            String sql = "SELECT nama_kategori FROM kategori WHERE jenis = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, jenis);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                cbKategori.addItem(rs.getString("nama_kategori"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal load kategori: " + e.getMessage());
        }
    }

    // Fungsi menyimpan transaksi ke Database
    private void simpanData() {
        if (txtNominal.getText().isEmpty() || cbKategori.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Isi nominal dan kategori dengan benar!");
            return;
        }

        try {
            Connection conn = Koneksi.getKoneksi();
            
            // 1. Cari id_kategori berdasarkan nama yang dipilih di ComboBox
            String namaKategori = cbKategori.getSelectedItem().toString();
            String sqlCariKategori = "SELECT id_kategori FROM kategori WHERE nama_kategori = ?";
            PreparedStatement pstKategori = conn.prepareStatement(sqlCariKategori);
            pstKategori.setString(1, namaKategori);
            ResultSet rsKategori = pstKategori.executeQuery();
            
            int idKat = 0;
            if(rsKategori.next()) {
                idKat = rsKategori.getInt("id_kategori");
            }

            // 2. Insert ke tabel transaksi
            String sqlInsert = "INSERT INTO transaksi (id_kategori, nominal, keterangan) VALUES (?, ?, ?)";
            PreparedStatement pstInsert = conn.prepareStatement(sqlInsert);
            pstInsert.setInt(1, idKat);
            pstInsert.setLong(2, Long.parseLong(txtNominal.getText()));
            pstInsert.setString(3, txtKeterangan.getText());
            pstInsert.executeUpdate();

            JOptionPane.showMessageDialog(this, "Data Berhasil Disimpan!");
            
            // 3. Bersihkan form & segarkan tabel
            txtNominal.setText("");
            txtKeterangan.setText("");
            loadDataTabel(); 

        } catch (SQLException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error Simpan: Pastikan Nominal hanya Angka! \n" + e.getMessage());
        }
    }

    // Fungsi memuat data ke JTable dan menghitung Auto-Dashboard
    private void loadDataTabel() {
        tableModel.setRowCount(0); // Kosongkan tabel GUI
        long totalMasuk = 0;
        long totalKeluar = 0;

        try {
            Connection conn = Koneksi.getKoneksi();
            // Melakukan JOIN (Relasi) untuk mendapatkan Nama Kategori dan Jenisnya
            String sql = "SELECT t.tanggal, k.nama_kategori, k.jenis, t.nominal, t.keterangan "
                       + "FROM transaksi t JOIN kategori k ON t.id_kategori = k.id_kategori "
                       + "ORDER BY t.tanggal DESC";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                String tgl = rs.getString("tanggal");
                String kat = rs.getString("nama_kategori");
                String jenis = rs.getString("jenis");
                long nom = rs.getLong("nominal");
                String ket = rs.getString("keterangan");

                // Konversi nominal ke format Rupiah
                String strNominal = formatRupiah.format(nom).replace(",00", "");

                tableModel.addRow(new Object[]{tgl, kat, jenis, strNominal, ket});

                // Kalkulasi Saldo
                if (jenis.equals("Pemasukan")) {
                    totalMasuk += nom;
                } else {
                    totalKeluar += nom;
                }
            }

            // Update Label Dashboard di atas
            long sisaSaldo = totalMasuk - totalKeluar;
            updateLabelCard(lblTotalPemasukan, "Total Pemasukan", totalMasuk, "#2ecc71");
            updateLabelCard(lblTotalPengeluaran, "Total Pengeluaran", totalKeluar, "#e74c3c");
            updateLabelCard(lblSisaSaldo, "Sisa Saldo Bersih", sisaSaldo, sisaSaldo >= 0 ? "#3498db" : "#c0392b");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal meload data: " + e.getMessage());
        }
    }

    // Fungsi bantuan update warna & angka label
    private void updateLabelCard(JLabel label, String title, long nominal, String hexColor) {
        String strNominal = formatRupiah.format(nominal).replace(",00", "");
        label.setText("<html><div style='text-align: center; padding: 10px;'>"
                + "<p style='margin: 0; font-size: 12px; color: #555;'>" + title + "</p>"
                + "<h2 style='margin: 0; color: " + hexColor + ";'>" + strNominal + "</h2></div></html>");
    }

    // ================= ENTRY POINT APLIKASI =================
    public static void main(String[] args) {
        // Terapkan tema LookAndFeel Windows/Native agar GUI terlihat modern
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        // Jalankan Aplikasi
        SwingUtilities.invokeLater(() -> {
            new MainApp().setVisible(true);
        });
    }
}