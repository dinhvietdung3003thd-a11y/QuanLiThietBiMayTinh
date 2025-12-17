package service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import utils.DatabaseHelper;

public class ThongKeService {

    // Trả về List các mảng Object: [Ngày, Số đơn, Doanh thu]
    public List<Object[]> layDoanhThuTheoNgay() {
        List<Object[]> list = new ArrayList<>();
        try (Connection conn = DatabaseHelper.getConnection()) {
            // Câu lệnh SQL "thần thánh": Gom nhóm theo ngày và tính tổng tiền
            String sql = "SELECT ngayTao, COUNT(*) as soDon, SUM(tongTien) as doanhThu " +
                         "FROM HoaDon " +
                         "GROUP BY ngayTao " +
                         "ORDER BY ngayTao DESC"; // Ngày mới nhất lên đầu
            
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Object[] row = new Object[3];
                row[0] = rs.getDate("ngayTao");
                row[1] = rs.getInt("soDon");
                row[2] = rs.getDouble("doanhThu");
                list.add(row);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
    
    // Tính tổng doanh thu toàn bộ
    public long tinhTongDoanhThu() {
        long tong = 0;
        try (Connection conn = DatabaseHelper.getConnection()) {
            ResultSet rs = conn.createStatement().executeQuery("SELECT SUM(tongTien) FROM HoaDon");
            if (rs.next()) tong = rs.getLong(1);
        } catch (Exception e) { e.printStackTrace(); }
        return tong;
    }

    // HÀM MỚI: Lấy danh sách hóa đơn chi tiết của 1 ngày
    public List<Object[]> layChiTietHoaDon(java.sql.Date ngayCanXem) {
        List<Object[]> list = new ArrayList<>();
        try (Connection conn = DatabaseHelper.getConnection()) {
            // Lấy Mã HD, Tên Khách, SĐT và Tổng tiền
            // Dùng LEFT JOIN để nếu khách vãng lai (không có tên) vẫn hiện ra hóa đơn
            String sql = "SELECT hd.maHD, kh.tenKH, hd.sdtKhachHang, hd.tongTien " +
                         "FROM HoaDon hd " +
                         "LEFT JOIN KhachHang kh ON hd.sdtKhachHang = kh.sdt " +
                         "WHERE hd.ngayTao = ?";
            
            PreparedStatement pstm = conn.prepareStatement(sql);
            pstm.setDate(1, ngayCanXem);
            
            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {
                String tenKhach = rs.getString("tenKH");
                if (tenKhach == null) tenKhach = "Khách vãng lai"; // Xử lý nếu không có tên
                
                list.add(new Object[]{
                    rs.getInt("maHD"),
                    tenKhach,
                    rs.getString("sdtKhachHang"),
                    rs.getDouble("tongTien")
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
    
    public List<Object[]> timKiemDoanhThuTheoNgay(String ngayInput) {
        List<Object[]> list = new ArrayList<>();
        try (Connection conn = DatabaseHelper.getConnection()) {
            // Thêm điều kiện WHERE ngayTao = ?
            String sql = "SELECT ngayTao, COUNT(*) as soDon, SUM(tongTien) as doanhThu " +
                         "FROM HoaDon " +
                         "WHERE ngayTao = ? " + 
                         "GROUP BY ngayTao";
            
            PreparedStatement pstm = conn.prepareStatement(sql);
            pstm.setString(1, ngayInput); // Định dạng chuỗi yyyy-MM-dd
            
            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {
                Object[] row = new Object[3];
                row[0] = rs.getDate("ngayTao");
                row[1] = rs.getInt("soDon");
                row[2] = rs.getDouble("doanhThu");
                list.add(row);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
