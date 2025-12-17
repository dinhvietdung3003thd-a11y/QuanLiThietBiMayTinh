package utils;

import java.sql.Connection;
import java.sql.DriverManager; 

public class DatabaseHelper {
    // Thay đổi thông tin phù hợp với máy của bạn
    private static final String URL = "jdbc:mysql://localhost:3306/quanlymaytinh"; 
    private static final String USER = "root"; 
    private static final String PASS = "Dinncairo1@"; // Mật khẩu MySQL của bạn

    public static Connection getConnection() {
        try {
            // Đăng ký driver (đối với các bản Java cũ, bản mới có thể bỏ qua dòng này)
            Class.forName("com.mysql.cj.jdbc.Driver"); 
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (Exception e) {
            System.out.println("❌ Kết nối thất bại!");
            e.printStackTrace();
            return null;
        }
    }
}