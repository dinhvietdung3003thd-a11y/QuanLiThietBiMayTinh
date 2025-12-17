package service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import model.SanPham;
import utils.DatabaseHelper;

public class BanHangService {

    // 1. Lấy danh sách sản phẩm (Giữ nguyên)
    public List<SanPham> layDanhSachSanPham() {
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

    // 2. Tính tổng tiền (Giữ nguyên)
    public long tinhTongTien(DefaultTableModel modelGioHang) {
        long tong = 0;
        for (int i = 0; i < modelGioHang.getRowCount(); i++) {
            String tienStr = modelGioHang.getValueAt(i, 3).toString()
                             .replace(",", "").replace(".", "").replace(" VNĐ", "").trim();
            tong += Long.parseLong(tienStr);
        }
        return tong;
    }

    // 3. THANH TOÁN (LOGIC MỚI: TỰ ĐỘNG CẬP NHẬT TRẠNG THÁI SERIAL)
    public boolean thanhToan(DefaultTableModel modelGioHang, long tongTien, String sdt, String tenKH, String diaChi) {
        Connection conn = null;
        try {
            conn = DatabaseHelper.getConnection();
            conn.setAutoCommit(false); // Bắt đầu Transaction

            // --- BƯỚC 1: XỬ LÝ KHÁCH HÀNG ---
            PreparedStatement pstmCheck = conn.prepareStatement("SELECT * FROM KhachHang WHERE sdt = ?");
            pstmCheck.setString(1, sdt);
            ResultSet rs = pstmCheck.executeQuery();
            
            if (!rs.next()) {
                String sqlInsertKH = "INSERT INTO KhachHang (sdt, tenKH, diaChi) VALUES (?, ?, ?)";
                PreparedStatement pstmInsert = conn.prepareStatement(sqlInsertKH);
                pstmInsert.setString(1, sdt);
                pstmInsert.setString(2, tenKH);
                pstmInsert.setString(3, diaChi);
                pstmInsert.executeUpdate();
            } else {
                String sqlUpdateKH = "UPDATE KhachHang SET tenKH = ?, diaChi = ? WHERE sdt = ?";
                PreparedStatement pstmUpdate = conn.prepareStatement(sqlUpdateKH);
                pstmUpdate.setString(1, tenKH);
                pstmUpdate.setString(2, diaChi);
                pstmUpdate.setString(3, sdt);
                pstmUpdate.executeUpdate();
            }

            // --- BƯỚC 2: TẠO HÓA ĐƠN ---
            String sqlHD = "INSERT INTO HoaDon (ngayTao, tongTien, sdtKhachHang) VALUES (CURRENT_DATE, ?, ?)";
            PreparedStatement pstmHD = conn.prepareStatement(sqlHD);
            pstmHD.setDouble(1, tongTien);
            pstmHD.setString(2, sdt);
            pstmHD.executeUpdate();

            // --- BƯỚC 3: TRỪ KHO & CẬP NHẬT SERIAL (QUAN TRỌNG) ---
            String sqlKho = "UPDATE Products SET stock = stock - ? WHERE productID = ?";
            PreparedStatement pstmKho = conn.prepareStatement(sqlKho);

            // Câu lệnh để tìm các Serial đang rảnh
            String sqlGetSerial = "SELECT serialNumber FROM ProductSerials WHERE productID = ? AND status = 'AVAILABLE' LIMIT ?";
            // Câu lệnh để đổi trạng thái thành SOLD
            String sqlUpdateSerial = "UPDATE ProductSerials SET status = 'SOLD' WHERE serialNumber = ?";
            PreparedStatement pstmUpdateSerial = conn.prepareStatement(sqlUpdateSerial);

            for (int i = 0; i < modelGioHang.getRowCount(); i++) {
                String maSP = modelGioHang.getValueAt(i, 0).toString();
                int soLuongMua = Integer.parseInt(modelGioHang.getValueAt(i, 2).toString());

                // 3a. TỰ ĐỘNG TÌM SERIAL ĐỂ BÁN (FIFO)
                PreparedStatement pstmGet = conn.prepareStatement(sqlGetSerial);
                pstmGet.setString(1, maSP);
                pstmGet.setInt(2, soLuongMua); // Lấy đúng số lượng khách mua
                ResultSet rsSerial = pstmGet.executeQuery();

                while(rsSerial.next()) {
                    String serialTimDuoc = rsSerial.getString("serialNumber");
                    // Đánh dấu serial này là ĐÃ BÁN
                    pstmUpdateSerial.setString(1, serialTimDuoc);
                    pstmUpdateSerial.addBatch(); 
                }

                // 3b. Trừ số lượng tổng
                pstmKho.setInt(1, soLuongMua);
                pstmKho.setString(2, maSP);
                pstmKho.addBatch();
            }

            // Chạy lệnh cập nhật hàng loạt
            pstmUpdateSerial.executeBatch(); 
            pstmKho.executeBatch();

            conn.commit(); // Chốt đơn
            return true;
        } catch (Exception e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) {}
            e.printStackTrace();
            return false;
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException ex) {}
        }
    }
}