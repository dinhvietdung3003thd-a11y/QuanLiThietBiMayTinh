package service;

import java.sql.*;
import javax.swing.table.DefaultTableModel;
import utils.DatabaseHelper;

public class BaoHanhService {

    // 1. Tra cứu thông tin máy (Giữ nguyên hoặc sửa nhẹ)
    public String[] traCuuSerial(String serial) {
        try (Connection conn = DatabaseHelper.getConnection()) {
            String sql = "SELECT s.serialNumber, p.productName, s.status " +
                         "FROM ProductSerials s " +
                         "JOIN Products p ON s.productID = p.productID " +
                         "WHERE s.serialNumber = ?";
            PreparedStatement pstm = conn.prepareStatement(sql);
            pstm.setString(1, serial);
            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                return new String[]{
                    rs.getString("serialNumber"),
                    rs.getString("productName"),
                    rs.getString("status")
                };
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null; // Không tìm thấy
    }

    // 2. Tạo Phiếu Bảo Hành Mới (Nhận máy từ khách)
    public boolean taoPhieuBaoHanh(String serial, String loi) {
        try (Connection conn = DatabaseHelper.getConnection()) {
            String sql = "INSERT INTO PhieuBaoHanh (serialNumber, loiKhachBao, trangThai, ngayNhan) VALUES (?, ?, 'ĐANG_XU_LY', CURRENT_DATE)";
            PreparedStatement pstm = conn.prepareStatement(sql);
            pstm.setString(1, serial);
            pstm.setString(2, loi);
            return pstm.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    // 3. Lấy danh sách máy đang bảo hành (để hiện lên bảng)
    public void loadDanhSachBaoHanh(DefaultTableModel model) {
        model.setRowCount(0);
        try (Connection conn = DatabaseHelper.getConnection()) {
            String sql = "SELECT pbh.maPhieu, pbh.serialNumber, p.productName, pbh.loiKhachBao, pbh.ngayNhan, pbh.trangThai " +
                         "FROM PhieuBaoHanh pbh " +
                         "JOIN ProductSerials s ON pbh.serialNumber = s.serialNumber " +
                         "JOIN Products p ON s.productID = p.productID " +
                         "ORDER BY pbh.maPhieu DESC";
            
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("maPhieu"),
                    rs.getString("serialNumber"),
                    rs.getString("productName"),
                    rs.getString("ngayNhan"),
                    rs.getString("loiKhachBao"),
                    rs.getString("trangThai")
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    // 4. Cập nhật trạng thái (Sửa xong / Trả khách)
    public boolean capNhatTrangThai(int maPhieu, String trangThaiMoi) {
        try (Connection conn = DatabaseHelper.getConnection()) {
            String sql = "UPDATE PhieuBaoHanh SET trangThai = ? WHERE maPhieu = ?";
            PreparedStatement pstm = conn.prepareStatement(sql);
            pstm.setString(1, trangThaiMoi);
            pstm.setInt(2, maPhieu);
            return pstm.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
}