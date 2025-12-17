package service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.KiemKeItem;
import utils.DatabaseHelper;

public class KiemKeService {

    // 1. Lấy danh sách (Mapping vào Model độc lập)
    public List<KiemKeItem> layDanhSachSanPhamDeKiemKe() {
        List<KiemKeItem> list = new ArrayList<>();
        try (Connection conn = DatabaseHelper.getConnection()) {
            String sql = "SELECT productID, productName, stock FROM Products";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                // Tạo mới object KiemKeItem
                list.add(new KiemKeItem(
                    rs.getString("productID"),
                    rs.getString("productName"),
                    rs.getInt("stock")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // 2. Lưu phiếu (Giữ nguyên logic cũ, chỉ đổi kiểu dữ liệu đầu vào)
    public boolean luuKetQuaKiemKe(List<KiemKeItem> listKiemKe, String ghiChuChung) {
        Connection conn = null;
        try {
            conn = DatabaseHelper.getConnection();
            conn.setAutoCommit(false); 

            // B1: Tạo Header phiếu
            String sqlPhieu = "INSERT INTO PhieuKiemKe (ghiChu) VALUES (?)";
            PreparedStatement pstmPhieu = conn.prepareStatement(sqlPhieu, Statement.RETURN_GENERATED_KEYS);
            pstmPhieu.setString(1, ghiChuChung);
            pstmPhieu.executeUpdate();
            
            ResultSet rsKey = pstmPhieu.getGeneratedKeys();
            int maPhieu = 0;
            if (rsKey.next()) maPhieu = rsKey.getInt(1);

            // B2: Lưu chi tiết & Update kho
            String sqlChiTiet = "INSERT INTO ChiTietKiemKe (maPhieu, maSP, tonHeThong, tonThucTe, lyDo) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmChiTiet = conn.prepareStatement(sqlChiTiet);

            String sqlUpdateKho = "UPDATE Products SET stock = ? WHERE productID = ?";
            PreparedStatement pstmUpdateKho = conn.prepareStatement(sqlUpdateKho);

            for (KiemKeItem item : listKiemKe) {
                // Lưu chi tiết
                pstmChiTiet.setInt(1, maPhieu);
                pstmChiTiet.setString(2, item.getMaSP());
                pstmChiTiet.setInt(3, item.getTonHeThong());
                pstmChiTiet.setInt(4, item.getTonThucTe());
                pstmChiTiet.setString(5, item.getLyDo());
                pstmChiTiet.addBatch();

                // Cập nhật kho nếu có lệch
                if (item.getChenhLech() != 0) {
                    pstmUpdateKho.setInt(1, item.getTonThucTe());
                    pstmUpdateKho.setString(2, item.getMaSP());
                    pstmUpdateKho.addBatch();
                }
            }

            pstmChiTiet.executeBatch();
            pstmUpdateKho.executeBatch();

            conn.commit(); 
            return true;
        } catch (Exception e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) {}
            e.printStackTrace();
            return false;
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException ex) {}
        }
    }
    
 // ... (Giữ nguyên các hàm cũ)

    // HÀM MỚI 1: Lấy danh sách lịch sử các đợt kiểm kê
    public List<Object[]> layLichSuKiemKe() {
        List<Object[]> list = new ArrayList<>();
        try (Connection conn = DatabaseHelper.getConnection()) {
            String sql = "SELECT maPhieu, ngayTao, ghiChu FROM PhieuKiemKe ORDER BY ngayTao DESC"; // Mới nhất lên đầu
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getInt("maPhieu"),
                    rs.getTimestamp("ngayTao"), // Lấy cả ngày giờ
                    rs.getString("ghiChu")
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // HÀM MỚI 2: Lấy chi tiết sản phẩm của 1 phiếu cụ thể
    public List<Object[]> layChiTietPhieu(int maPhieu) {
        List<Object[]> list = new ArrayList<>();
        try (Connection conn = DatabaseHelper.getConnection()) {
            // Join bảng ChiTiet với Products để lấy tên SP
            String sql = "SELECT p.productName, ct.* " +
                         "FROM ChiTietKiemKe ct " +
                         "LEFT JOIN Products p ON ct.maSP = p.productID " +
                         "WHERE ct.maPhieu = ?";
            
            PreparedStatement pstm = conn.prepareStatement(sql);
            pstm.setInt(1, maPhieu);
            ResultSet rs = pstm.executeQuery();
            
            while (rs.next()) {
                int heThong = rs.getInt("tonHeThong");
                int thucTe = rs.getInt("tonThucTe");
                int chenhLech = thucTe - heThong;
                
                list.add(new Object[]{
                    rs.getString("maSP"),
                    rs.getString("productName"),
                    heThong,
                    thucTe,
                    chenhLech,
                    rs.getString("lyDo")
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}