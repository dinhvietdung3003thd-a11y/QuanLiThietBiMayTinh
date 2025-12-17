package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import service.ThongKeService;

public class PanelThongKe extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JLabel lblTongDoanhThu;
    private JTextField txtNgayTimKiem; // Ô nhập ngày
    private ThongKeService sv = new ThongKeService();

    public PanelThongKe() {
        setLayout(new BorderLayout());

        // --- PHẦN TIÊU ĐỀ & TÌM KIẾM ---
        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.setBackground(new Color(41, 128, 185));
        
        // 1. Tiêu đề
        JLabel lblTitle = new JLabel("THỐNG KÊ DOANH THU", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        // 2. Thanh tìm kiếm
        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlSearch.setOpaque(false); // Làm trong suốt để thấy nền xanh
        
        JLabel lblNhap = new JLabel("Nhập ngày (yyyy-mm-dd): ");
        lblNhap.setForeground(Color.WHITE);
        
        txtNgayTimKiem = new JTextField(15);
        JButton btnTim = new JButton("Tìm Kiếm");
        JButton btnTaiLai = new JButton("Tải Lại Tất Cả");

        pnlSearch.add(lblNhap);
        pnlSearch.add(txtNgayTimKiem);
        pnlSearch.add(btnTim);
        pnlSearch.add(btnTaiLai);

        // 3. Tổng tiền
        lblTongDoanhThu = new JLabel("Tổng doanh thu: 0 VNĐ", SwingConstants.CENTER);
        lblTongDoanhThu.setFont(new Font("Arial", Font.BOLD, 24));
        lblTongDoanhThu.setForeground(Color.YELLOW);
        lblTongDoanhThu.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0));

        // Gộp lại
        JPanel pnlCenterHeader = new JPanel(new GridLayout(2, 1));
        pnlCenterHeader.setOpaque(false);
        pnlCenterHeader.add(pnlSearch);
        pnlCenterHeader.add(lblTongDoanhThu);

        pnlTop.add(lblTitle, BorderLayout.NORTH);
        pnlTop.add(pnlCenterHeader, BorderLayout.CENTER);
        
        add(pnlTop, BorderLayout.NORTH);

        // --- BẢNG THỐNG KÊ ---
        model = new DefaultTableModel(new String[]{"Ngày", "Số Hóa Đơn", "Doanh Thu Ngày"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        
        add(new JScrollPane(table), BorderLayout.CENTER);
        
        JLabel lblHint = new JLabel("  (*Mẹo: Click đúp vào dòng Ngày để xem chi tiết hóa đơn)");
        lblHint.setFont(new Font("Arial", Font.ITALIC, 11));
        lblHint.setForeground(Color.BLUE);
        add(lblHint, BorderLayout.SOUTH);

        // --- SỰ KIỆN ---
        
        // 1. Nút Tìm
        btnTim.addActionListener(e -> xuLyTimKiem());
        
        // 2. Nút Tải lại (Reset)
        btnTaiLai.addActionListener(e -> {
            txtNgayTimKiem.setText("");
            loadData();
        });

        // 3. Click đúp xem chi tiết
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        java.sql.Date ngayChon = (java.sql.Date) model.getValueAt(row, 0);
                        hienThiChiTiet(ngayChon);
                    }
                }
            }
        });

        loadData();
    }

    public void loadData() {
        hienThiLenBang(sv.layDoanhThuTheoNgay()); // Load tất cả
    }

    private void xuLyTimKiem() {
        String ngay = txtNgayTimKiem.getText().trim();
        if (ngay.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập ngày (Ví dụ: 2025-12-16)");
            return;
        }
        // Gọi hàm tìm kiếm
        List<Object[]> ketQua = sv.timKiemDoanhThuTheoNgay(ngay);
        if (ketQua.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy doanh thu ngày: " + ngay);
        } else {
            hienThiLenBang(ketQua);
        }
    }

    // Hàm phụ để đỡ viết lặp code đổ dữ liệu
    private void hienThiLenBang(List<Object[]> list) {
        model.setRowCount(0);
        long tongTienHienTai = 0;
        
        for (Object[] row : list) {
            double tien = (Double) row[2];
            String tienStr = String.format("%,.0f VNĐ", tien);
            model.addRow(new Object[]{row[0], row[1], tienStr});
            
            // Cộng dồn tiền của danh sách đang hiển thị
            tongTienHienTai += (long) tien;
        }
        
        // Cập nhật nhãn tổng tiền theo danh sách đang xem
        lblTongDoanhThu.setText("Tổng doanh thu: " + String.format("%,d VNĐ", tongTienHienTai));
    }

    // --- DIALOG CHI TIẾT (Giữ nguyên như cũ) ---
    private void hienThiChiTiet(java.sql.Date ngay) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Chi Tiết Hóa Đơn Ngày: " + ngay, true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        
        List<Object[]> listHD = sv.layChiTietHoaDon(ngay);
        
        String[] cols = {"Mã HD", "Khách Hàng", "SĐT", "Tổng Tiền"};
        DefaultTableModel modelHD = new DefaultTableModel(cols, 0);
        
        for (Object[] row : listHD) {
            double tien = (Double) row[3];
            modelHD.addRow(new Object[]{
                row[0], row[1], row[2], String.format("%,.0f", tien)
            });
        }
        
        JTable tblHD = new JTable(modelHD);
        tblHD.setRowHeight(25);
        dialog.add(new JScrollPane(tblHD));
        dialog.setVisible(true);
    }
}