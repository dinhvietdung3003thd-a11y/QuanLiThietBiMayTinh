package service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.NhanVien;
import utils.DatabaseHelper;

public class NhanVienService {

    // 1. Lấy danh sách tất cả Nhân viên
    public List<NhanVien> layDSNhanVien() {
        List<NhanVien> list = new ArrayList<>();
        try (Connection conn = DatabaseHelper.getConnection()) {
            String sql = "SELECT * FROM NhanVien";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                list.add(new NhanVien(
                    rs.getString("maNV"), 
                    rs.getString("hoTen"), 
                    rs.getString("matKhau"), 
                    rs.getString("quyen")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // 2. Thêm Nhân Viên
    public boolean themNhanVien(NhanVien nv) {
        try (Connection conn = DatabaseHelper.getConnection()) {
            String sql = "INSERT INTO NhanVien (maNV, hoTen, matKhau, quyen) VALUES (?, ?, ?, ?)";
            PreparedStatement pstm = conn.prepareStatement(sql);
            pstm.setString(1, nv.getMaNV());
            pstm.setString(2, nv.getHoTen());
            pstm.setString(3, nv.getMatKhau());
            pstm.setString(4, nv.getQuyen());
            return pstm.executeUpdate() > 0;
        } catch (Exception e) { 
            // Lỗi thường gặp: Trùng mã nhân viên (Duplicate entry)
            e.printStackTrace(); 
            return false; 
        }
    }

    // 3. Sửa Nhân Viên (Không sửa Mã NV)
    public boolean suaNhanVien(NhanVien nv) {
        try (Connection conn = DatabaseHelper.getConnection()) {
            String sql = "UPDATE NhanVien SET hoTen=?, matKhau=?, quyen=? WHERE maNV=?";
            PreparedStatement pstm = conn.prepareStatement(sql);
            pstm.setString(1, nv.getHoTen());
            pstm.setString(2, nv.getMatKhau());
            pstm.setString(3, nv.getQuyen());
            pstm.setString(4, nv.getMaNV());
            return pstm.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    // 4. Xóa Nhân Viên
    public boolean xoaNhanVien(String maNV) {
        try (Connection conn = DatabaseHelper.getConnection()) {
            String sql = "DELETE FROM NhanVien WHERE maNV=?";
            PreparedStatement pstm = conn.prepareStatement(sql);
            pstm.setString(1, maNV);
            return pstm.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    // 5. Tìm kiếm Nhân Viên (Theo Mã hoặc Tên)
    public List<NhanVien> timKiemNhanVien(String tuKhoa) {
        List<NhanVien> list = new ArrayList<>();
        try (Connection conn = DatabaseHelper.getConnection()) {
            String sql = "SELECT * FROM NhanVien WHERE maNV LIKE ? OR hoTen LIKE ?";
            PreparedStatement pstm = conn.prepareStatement(sql);
            String pattern = "%" + tuKhoa + "%";
            pstm.setString(1, pattern);
            pstm.setString(2, pattern);
            
            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {
                list.add(new NhanVien(
                    rs.getString("maNV"), 
                    rs.getString("hoTen"), 
                    rs.getString("matKhau"), 
                    rs.getString("quyen")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}