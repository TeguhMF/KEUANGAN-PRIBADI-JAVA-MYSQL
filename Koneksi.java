/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author teguh
 */
package Main;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Koneksi {
    private static Connection konek;

    public static Connection getKoneksi() {
        if (konek == null) {
            try {
                String url = "jdbc:mysql://localhost:3306/dbkeuanganpribadi";
                String user = "root";
                String pass = "";
                
                // Mendaftarkan Driver MySQL
                DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
                konek = DriverManager.getConnection(url, user, pass);
                System.out.println("Koneksi Database Berhasil!");
            } catch (SQLException e) {
                System.out.println("Koneksi Gagal: " + e.getMessage());
            }
        }
        return konek;
    }
}
