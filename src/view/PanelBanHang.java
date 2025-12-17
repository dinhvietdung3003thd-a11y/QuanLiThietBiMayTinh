package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import model.SanPham;
import service.BanHangService;

public class PanelBanHang extends JPanel {
    private JTable tblSanPham, tblGioHang;
    private DefaultTableModel modelSanPham, modelGioHang;
    private JLabel lblTongTien;
    
    // Khai báo 3 ô nhập liệu
    private JTextField txtSDT, txtTenKH, txtDiaChi; 
    
    private BanHangService service = new BanHangService();

    public PanelBanHang() {
        setLayout(new BorderLayout());

        JSplitPane splitPane = new JSplitPane();
        splitPane.setResizeWeight(0.5);

        // --- BÊN TRÁI: KHO HÀNG ---
        JPanel pnlTrai = new JPanel(new BorderLayout());
        pnlTrai.setBorder(BorderFactory.createTitledBorder("Kho Hàng"));
        
        String[] colsSP = {"Mã SP", "Tên Sản Phẩm", "Đơn Giá", "Tồn Kho"};
        modelSanPham = new DefaultTableModel(colsSP, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblSanPham = new JTable(modelSanPham);
        
        JButton btnThem = new JButton("Thêm vào giỏ >>");
        btnThem.setBackground(new Color(0, 153, 76));
        btnThem.setForeground(Color.WHITE);
        
        pnlTrai.add(new JScrollPane(tblSanPham), BorderLayout.CENTER);
        pnlTrai.add(btnThem, BorderLayout.SOUTH);

        // --- BÊN PHẢI: GIỎ HÀNG & THÔNG TIN KHÁCH ---
        JPanel pnlPhai = new JPanel(new BorderLayout());
        pnlPhai.setBorder(BorderFactory.createTitledBorder("Thông Tin Thanh Toán"));
        
        // 1. Panel nhập khách hàng (3 dòng)
        JPanel pnlKhach = new JPanel(new GridLayout(3, 2, 5, 5));
        pnlKhach.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));
        
        txtSDT = new JTextField();
        txtTenKH = new JTextField();
        txtDiaChi = new JTextField(); // Khởi tạo

        pnlKhach.add(new JLabel("Số ĐT Khách (*):"));
        pnlKhach.add(txtSDT);
        
        pnlKhach.add(new JLabel("Tên Khách Hàng:"));
        pnlKhach.add(txtTenKH);
        
        pnlKhach.add(new JLabel("Địa Chỉ:")); // Label địa chỉ
        pnlKhach.add(txtDiaChi);              // Ô nhập địa chỉ
        
        pnlPhai.add(pnlKhach, BorderLayout.NORTH);

        // 2. Bảng giỏ hàng
        String[] colsGH = {"Mã SP", "Tên SP", "Số Lượng", "Thành Tiền"};
        modelGioHang = new DefaultTableModel(colsGH, 0);
        tblGioHang = new JTable(modelGioHang);
        pnlPhai.add(new JScrollPane(tblGioHang), BorderLayout.CENTER);
        
        // 3. Khu vực thanh toán
        JPanel pnlThanhToan = new JPanel(new GridLayout(2, 1));
        lblTongTien = new JLabel("Tổng tiền: 0 VNĐ", SwingConstants.RIGHT);
        lblTongTien.setFont(new Font("Arial", Font.BOLD, 16));
        lblTongTien.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton btnThanhToan = new JButton("THANH TOÁN & LƯU HÓA ĐƠN");
        btnThanhToan.setBackground(new Color(204, 0, 0));
        btnThanhToan.setForeground(Color.WHITE);
        btnThanhToan.setFont(new Font("Arial", Font.BOLD, 14));
        
        pnlThanhToan.add(lblTongTien);
        pnlThanhToan.add(btnThanhToan);
        pnlPhai.add(pnlThanhToan, BorderLayout.SOUTH);

        splitPane.setLeftComponent(pnlTrai);
        splitPane.setRightComponent(pnlPhai);
        add(splitPane, BorderLayout.CENTER);

        // --- EVENTS ---
        loadDataLenBang();

        btnThem.addActionListener(e -> xuLyThemVaoGio());
        btnThanhToan.addActionListener(e -> xuLyThanhToan());
    }

    public void loadDataLenBang() {
        modelSanPham.setRowCount(0);
        List<SanPham> danhSach = service.layDanhSachSanPham();
        for (SanPham sp : danhSach) {
            modelSanPham.addRow(new Object[]{
                sp.getMaSP(), sp.getTenSP(),
                String.format("%,.0f", sp.getGia()), sp.getSoLuongTon()
            });
        }
    }

    private void xuLyThemVaoGio() {
        int row = tblSanPham.getSelectedRow();
        if (row == -1) return;

        String ma = modelSanPham.getValueAt(row, 0).toString();
        String ten = modelSanPham.getValueAt(row, 1).toString();
        String giaStr = modelSanPham.getValueAt(row, 2).toString().replace(",", "").replace(".", "");
        long donGia = Long.parseLong(giaStr);
        int tonKho = Integer.parseInt(modelSanPham.getValueAt(row, 3).toString());

        if (tonKho <= 0) {
            JOptionPane.showMessageDialog(this, "Hết hàng!");
            return;
        }

        long thanhTien = donGia * 1;
        modelGioHang.addRow(new Object[]{ma, ten, 1, String.format("%,d", thanhTien)});
        
        long tong = service.tinhTongTien(modelGioHang);
        lblTongTien.setText("Tổng tiền: " + String.format("%,d VNĐ", tong));
    }

    private void xuLyThanhToan() {
        if (modelGioHang.getRowCount() == 0) return;

        // Lấy dữ liệu từ 3 ô nhập
        String sdt = txtSDT.getText().trim();
        String ten = txtTenKH.getText().trim();
        String diaChi = txtDiaChi.getText().trim(); // Lấy địa chỉ
        
        if (sdt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Số điện thoại khách hàng!");
            txtSDT.requestFocus();
            return;
        }
        
        if (ten.isEmpty()) ten = "Khách vãng lai";

        long tongTien = service.tinhTongTien(modelGioHang);

        // Confirm
        int choice = JOptionPane.showConfirmDialog(this, 
            "Khách: " + ten + "\nSĐT: " + sdt + "\nĐ/c: " + diaChi + 
            "\n\nTổng tiền: " + lblTongTien.getText() + "\nXác nhận thanh toán?", 
            "Xác nhận", JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            // Gọi Service với đầy đủ tham số
            boolean kq = service.thanhToan(modelGioHang, tongTien, sdt, ten, diaChi);

            if (kq) {
                JOptionPane.showMessageDialog(this, "Thanh toán thành công!");
                modelGioHang.setRowCount(0);
                lblTongTien.setText("Tổng tiền: 0 VNĐ");
                
                // Reset form nhập
                txtSDT.setText(""); 
                txtTenKH.setText(""); 
                txtDiaChi.setText("");
                
                loadDataLenBang();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi thanh toán!");
            }
        }
    }
}