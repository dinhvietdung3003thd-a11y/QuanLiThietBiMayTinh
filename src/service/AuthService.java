package service;

import java.sql.*;
import model.NhanVien; // <--- Đổi từ NguoiDung sang NhanVien
import utils.DatabaseHelper;

public class AuthService {
    
    // Hàm trả về đối tượng NhanVien thay vì NguoiDung
    public NhanVien dangNhap(String maNV, String password) {
        try (Connection conn = DatabaseHelper.getConnection()) {
            // 1. Sửa câu lệnh SQL: Chọn từ bảng NhanVien
            // (Lưu ý tên cột phải khớp với file SQL mình gửi: maNV, matKhau)
            String sql = "SELECT * FROM NhanVien WHERE maNV = ? AND matKhau = ?";
            
            PreparedStatement pstm = conn.prepareStatement(sql);
            pstm.setString(1, maNV);
            pstm.setString(2, password);
            
            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                // 2. Tạo đối tượng NhanVien từ dữ liệu tìm được
                return new NhanVien(
                    rs.getString("maNV"),
                    rs.getString("hoTen"),
                    rs.getString("matKhau"),
                    rs.getString("quyen")
                );
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
        return null; // Trả về null nếu sai tài khoản hoặc mật khẩu
    }
}