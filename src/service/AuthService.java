package service;

import java.sql.*;
import model.NguoiDung;
import utils.DatabaseHelper;

public class AuthService {
    
    public NguoiDung dangNhap(String user, String pass) {
        try (Connection conn = DatabaseHelper.getConnection()) {
            String sql = "SELECT * FROM Users WHERE username = ? AND password = ?";
            PreparedStatement pstm = conn.prepareStatement(sql);
            pstm.setString(1, user);
            pstm.setString(2, pass);
            
            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                return new NguoiDung(
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("fullName"),
                    rs.getString("role")
                );
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null; // Đăng nhập thất bại
    }
}