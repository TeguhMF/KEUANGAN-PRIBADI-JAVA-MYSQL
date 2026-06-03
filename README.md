# Aplikasi Rekap Keuangan Pribadi
Aplikasi desktop berbasis Java Swing (Hardcoded GUI) yang terintegrasi dengan database MySQL untuk mencatat, mengelola, dan merekapitulasi keuangan pribadi secara taktis dan efisien. Aplikasi ini dirancang khusus untuk kebutuhan mahasiswa dalam memantau pos pemasukan dan pengeluaran secara real-time.

# Fitur Utama

- **Dashboard Saldo Otomatis:** Menampilkan *Total Pemasukan*, *Total Pengeluaran*, dan *Sisa Saldo Bersih* yang otomatis terhitung ulang (*auto-calculate*) setiap ada transaksi baru.
- **Dynamic ComboBox (Validasi Cerdas):** Komponen kategori secara dinamis menyesuaikan pilihan berdasarkan *Radio Button* jenis transaksi (Pemasukan/Pengeluaran) untuk mencegah kesalahan input data (*user blunder*).
- **Log Riwayat Interaktif:** Menggunakan `JTable` yang bersih untuk menampilkan seluruh kronologi keuangan lengkap dengan konversi format mata uang Rupiah (IDR).
- **Hardcoded GUI Layout:** Antarmuka dibangun murni melalui barisan kode Java tanpa menggunakan fitur *drag-and-drop* NetBeans, menghasilkan performa yang ringan dan struktur kode yang clean.

---

# Arsitektur Sistem & Database

Aplikasi ini menggunakan pendekatan berorientasi objek sederhana dengan memisahkan fungsi konektivitas database (`Koneksi.java`) dan komponen antarmuka beserta logika bisnis (`MainApp.java`).

# Struktur Tabel MySQL (`db_keuangan_pribadi`)

1. **Tabel `kategori` (Master Data)**
   - `id_kategori` (INT, Primary Key, Auto Increment)
   - `nama_kategori` (VARCHAR) -> *Gaji, Dana dari orang tua, Kebutuhan Primer, Transportasi, Bayar uang kuliah*
   - `jenis` (ENUM) -> *Pemasukan, Pengeluaran*

2. **Tabel `transaksi` (Data Utama)**
   - `id_transaksi` (INT, Primary Key, Auto Increment)
   - `id_kategori` (INT, Foreign Key)
   - `nominal` (BIGINT)
   - `keterangan` (TEXT)
   - `tanggal` (TIMESTAMP, Default Current)

---

## Persyaratan Sistem

Sebelum menjalankan project, pastikan perangkat mu sudah menginstal:
- **Java Development Kit (JDK):** Versi 8 atau yang lebih baru.
- **IDE:** Apache NetBeans (atau IDE Java lainnya).
- **Database Server:** XAMPP atau Laragon (MySQL Server).
- **Driver:** MySQL JDBC Connector (file `.jar`).

---

## Tutorial Instalasi & Penggunaan

Ikuti langkah-langkah berikut untuk menjalankan project ini di komputer lokal Mu:

### Langkah 1: Setup Database (MySQL)
1. Jalankan module **Apache** dan **MySQL** pada XAMPP Control Panel.
2. Buka browser dan akses `http://localhost/phpmyadmin/`.
3. Buat database baru bernama `db_keuangan_pribadi`.
4. Masuk ke tab **SQL**, *copy-paste* query berikut, lalu klik **Go**:

```sql
CREATE TABLE kategori (
    id_kategori INT AUTO_INCREMENT PRIMARY KEY,
    nama_kategori VARCHAR(50) NOT NULL,
    jenis ENUM('Pemasukan', 'Pengeluaran') NOT NULL
);

INSERT INTO kategori (nama_kategori, jenis) VALUES
('Gaji', 'Pemasukan'),
('Dana dari orang tua', 'Pemasukan'),
('Kebutuhan Primer', 'Pengeluaran'),
('Transportasi', 'Pengeluaran'),
('Bayar uang kuliah', 'Pengeluaran');

CREATE TABLE transaksi (
    id_transaksi INT AUTO_INCREMENT PRIMARY KEY,
    id_kategori INT NOT NULL,
    nominal BIGINT NOT NULL,
    keterangan TEXT,
    tanggal TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_kategori) REFERENCES kategori(id_kategori)
);
