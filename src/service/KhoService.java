package service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.*;
import utils.DatabaseHelper;

public class KhoService {

    // Lấy danh sách kho
    public List<SanPham> layDanhSachKho() {
        List<SanPham> list = new ArrayList<>();
        try (Connection conn = DatabaseHelper.getConnection()) {
            String sql = "SELECT * FROM Products";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                list.add(new SanPham(
                    rs.getString("productID"),
                    rs.getString("productName"),
                    rs.getDouble("price"),
                    rs.getInt("stock")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // Trả về List các mảng Object để đổ thẳng vào bảng
    public List<Object[]> layChiTietSerial(String maSP) {
        List<Object[]> list = new ArrayList<>();
        try (Connection conn = DatabaseHelper.getConnection()) {
            // Lấy tất cả Serial (cả đã bán và chưa bán) để theo dõi
            String sql = "SELECT serialNumber, status FROM ProductSerials WHERE productID = ? ORDER BY status";
            PreparedStatement pstm = conn.prepareStatement(sql);
            pstm.setString(1, maSP);
            
            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {
                String status = rs.getString("status");
                // Dịch sang tiếng Việt cho dễ nhìn
                String trangThaiViet = "AVAILABLE".equals(status) ? "Trong kho" : 
                                       ("SOLD".equals(status) ? "Đã bán" : status);
                
                list.add(new Object[]{ rs.getString("serialNumber"), trangThaiViet });
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }


    // Kiểm tra xem mã SP đã tồn tại chưa (Hỗ trợ nhập hàng)
    public boolean kiemTraTonTai(String maSP) {
        try (Connection conn = DatabaseHelper.getConnection()) {
            String sql = "SELECT count(*) FROM Products WHERE productID = ?";
            PreparedStatement pstm = conn.prepareStatement(sql);
            pstm.setString(1, maSP);
            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Trả về true nếu đã có
            }
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // Hàm nhập hàng THÔNG MINH (Vừa thêm mới, vừa cộng dồn)
    public boolean nhapHang(SanPham sp) {
        try (Connection conn = DatabaseHelper.getConnection()) {
            if (kiemTraTonTai(sp.getMaSP())) {
                // TRƯỜNG HỢP 1: Hàng đã có -> CẬP NHẬT (Cộng thêm số lượng, update giá mới nhất)
                String sql = "UPDATE Products SET stock = stock + ?, price = ?, productName = ? WHERE productID = ?";
                PreparedStatement pstm = conn.prepareStatement(sql);
                pstm.setInt(1, sp.getSoLuongTon()); // Số lượng nhập thêm
                pstm.setDouble(2, sp.getGia());     // Cập nhật giá mới
                pstm.setString(3, sp.getTenSP());   // Cập nhật tên (nếu muốn sửa)
                pstm.setString(4, sp.getMaSP());
                pstm.executeUpdate();
            } else {
                // TRƯỜNG HỢP 2: Hàng mới toanh -> THÊM MỚI (INSERT)
                String sql = "INSERT INTO Products (productID, productName, price, stock) VALUES (?, ?, ?, ?)";
                PreparedStatement pstm = conn.prepareStatement(sql);
                pstm.setString(1, sp.getMaSP());
                pstm.setString(2, sp.getTenSP());
                pstm.setDouble(3, sp.getGia());
                pstm.setInt(4, sp.getSoLuongTon()); // Số lượng khởi tạo
                pstm.executeUpdate();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public void nhapDanhSachSerial(List<ChiTietSerial> listSerial) {
        if (listSerial == null || listSerial.isEmpty()) return;

        try (Connection conn = DatabaseHelper.getConnection()) {
            // Dùng INSERT IGNORE để nếu lỡ trùng serial thì bỏ qua, ko lỗi
            String sql = "INSERT IGNORE INTO ProductSerials (serialNumber, productID, status) VALUES (?, ?, ?)";
            PreparedStatement pstm = conn.prepareStatement(sql);

            for (ChiTietSerial item : listSerial) {
                pstm.setString(1, item.getSerialNumber()); // Lấy từ Model
                pstm.setString(2, item.getMaSP());         // Lấy từ Model
                pstm.setString(3, item.getStatus());       // Lấy từ Model (AVAILABLE)
                pstm.addBatch();
            }
            pstm.executeBatch(); // Chạy 1 lần cho nhanh
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public boolean xoaSanPham(String maSP) {
        Connection conn = null;
        try {
            conn = DatabaseHelper.getConnection();
            
            // Lưu ý: Nếu sản phẩm đã có Serial hoặc đã nằm trong hóa đơn bán hàng,
            // MySQL sẽ chặn không cho xóa để bảo toàn dữ liệu (Foreign Key).
            // Muốn xóa triệt để thì phải xóa bảng con trước, nhưng ở đây mình làm đơn giản.
            
            String sql = "DELETE FROM Products WHERE productID = ?";
            PreparedStatement pstm = conn.prepareStatement(sql);
            pstm.setString(1, maSP);
            
            int rowsAffected = pstm.executeUpdate();
            return rowsAffected > 0; // Trả về true nếu xóa thành công

        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            // Lỗi này xảy ra khi SP đã bán hoặc có Serial
            System.out.println("Không thể xóa vì sản phẩm này đang có dữ liệu liên quan (Serial/Hóa đơn)");
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try { if (conn != null) conn.close(); } catch (Exception ex) {}
        }
    }
}