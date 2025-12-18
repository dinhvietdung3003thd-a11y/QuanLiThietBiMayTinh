package service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.NhaCungCap;
import utils.DatabaseHelper;

public class NhaCungCapService {

    // 1. Lấy danh sách tất cả NCC
    public List<NhaCungCap> layDSNhaCungCap() {
        List<NhaCungCap> list = new ArrayList<>();
        try (Connection conn = DatabaseHelper.getConnection()) {
            String sql = "SELECT * FROM NhaCungCap";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                list.add(new NhaCungCap(
                    rs.getInt("maNCC"), 
                    rs.getString("tenNCC"), 
                    rs.getString("soDienThoai"), 
                    rs.getString("diaChi")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // 2. Thêm Nhà Cung Cấp
    public boolean themNhaCungCap(String ten, String sdt, String diaChi) {
        try (Connection conn = DatabaseHelper.getConnection()) {
            String sql = "INSERT INTO NhaCungCap (tenNCC, soDienThoai, diaChi) VALUES (?, ?, ?)";
            PreparedStatement pstm = conn.prepareStatement(sql);
            pstm.setString(1, ten);
            pstm.setString(2, sdt);
            pstm.setString(3, diaChi);
            return pstm.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    // 3. Sửa Nhà Cung Cấp
    public boolean suaNhaCungCap(NhaCungCap ncc) {
        try (Connection conn = DatabaseHelper.getConnection()) {
            String sql = "UPDATE NhaCungCap SET tenNCC=?, soDienThoai=?, diaChi=? WHERE maNCC=?";
            PreparedStatement pstm = conn.prepareStatement(sql);
            pstm.setString(1, ncc.getTenNCC());
            pstm.setString(2, ncc.getSdt());
            pstm.setString(3, ncc.getDiaChi());
            pstm.setInt(4, ncc.getMaNCC());
            return pstm.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    // 4. Xóa Nhà Cung Cấp
    public boolean xoaNhaCungCap(int maNCC) {
        try (Connection conn = DatabaseHelper.getConnection()) {
            String sql = "DELETE FROM NhaCungCap WHERE maNCC=?";
            PreparedStatement pstm = conn.prepareStatement(sql);
            pstm.setInt(1, maNCC);
            return pstm.executeUpdate() > 0;
        } catch (Exception e) { 
            // Có thể lỗi do NCC này đang cung cấp hàng hóa trong kho (Ràng buộc khóa ngoại)
            e.printStackTrace(); 
            return false; 
        }
    }

    // 5. Tìm kiếm NCC (Theo Tên hoặc SĐT)
    public List<NhaCungCap> timKiemNhaCungCap(String tuKhoa) {
        List<NhaCungCap> list = new ArrayList<>();
        try (Connection conn = DatabaseHelper.getConnection()) {
            String sql = "SELECT * FROM NhaCungCap WHERE tenNCC LIKE ? OR soDienThoai LIKE ?";
            PreparedStatement pstm = conn.prepareStatement(sql);
            String pattern = "%" + tuKhoa + "%";
            pstm.setString(1, pattern);
            pstm.setString(2, pattern);
            
            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {
                list.add(new NhaCungCap(
                    rs.getInt("maNCC"), 
                    rs.getString("tenNCC"), 
                    rs.getString("soDienThoai"), 
                    rs.getString("diaChi")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}