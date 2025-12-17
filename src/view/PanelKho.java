package view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import model.SanPham;
import service.KhoService;

// Đảm bảo bạn đã có các file Dialog này trong package view:
import view.DialogNhapHang;
import view.DialogKiemKe;
import view.DialogLichSuKiemKe;
import view.DialogXemSerial; 

public class PanelKho extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private KhoService khoService = new KhoService();

    public PanelKho() {
        setLayout(new BorderLayout());

        // =================================================================================
        // 1. THANH CÔNG CỤ (TOOLBAR)
        // =================================================================================
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBackground(new Color(245, 245, 245));

        // --- Nút Tải Lại ---
        JButton btnLoad = new JButton("Tải lại");
        btnLoad.setToolTipText("Làm mới danh sách kho");

        // --- Nút Nhập Hàng ---
        JButton btnThem = new JButton("Nhập Hàng");
        btnThem.setForeground(new Color(0, 100, 0)); // Xanh lá đậm
        btnThem.setFont(new Font("Arial", Font.BOLD, 12));

        // --- Nút Kiểm Kê (Chức năng mới) ---
        JButton btnKiemKe = new JButton("Kiểm Kê Kho");
        btnKiemKe.setForeground(new Color(102, 0, 153)); // Màu tím
        btnKiemKe.setFont(new Font("Arial", Font.BOLD, 12));

        // --- Nút Lịch Sử (Chức năng mới) ---
        JButton btnLichSu = new JButton("Lịch Sử KK");
        btnLichSu.setForeground(new Color(0, 51, 153)); // Xanh dương đậm
        btnLichSu.setFont(new Font("Arial", Font.BOLD, 12));

        // --- Nút Xóa ---
        JButton btnXoa = new JButton("Xóa Hàng");
        btnXoa.setForeground(Color.RED);
        btnXoa.setFont(new Font("Arial", Font.BOLD, 12));

        // Thêm vào Toolbar (Có ngăn cách cho đẹp)
        toolBar.add(btnLoad);
        toolBar.addSeparator(new Dimension(20, 0)); 
        toolBar.add(btnThem);
        toolBar.addSeparator();
        toolBar.add(btnKiemKe); 
        toolBar.add(btnLichSu); 
        toolBar.addSeparator(new Dimension(20, 0));
        toolBar.add(btnXoa);

        add(toolBar, BorderLayout.NORTH);

        // =================================================================================
        // 2. BẢNG DỮ LIỆU (TABLE)
        // =================================================================================
        String[] columns = {"Mã SP", "Tên Sản Phẩm", "Giá Bán (VNĐ)", "Tồn Kho"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho sửa trực tiếp trên bảng
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(28);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Căn giữa cột Tồn kho (Cột index 3) cho dễ nhìn
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        add(new JScrollPane(table), BorderLayout.CENTER);

        // Dòng gợi ý dưới chân trang
        JLabel lblHint = new JLabel("  (*Mẹo: Click đúp chuột vào dòng sản phẩm để xem chi tiết danh sách Serial/IMEI)");
        lblHint.setFont(new Font("Arial", Font.ITALIC, 11));
        lblHint.setForeground(Color.BLUE);
        lblHint.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(lblHint, BorderLayout.SOUTH);

        // =================================================================================
        // 3. XỬ LÝ SỰ KIỆN (EVENTS)
        // =================================================================================
        
        // A. Nút Tải lại
        btnLoad.addActionListener(e -> loadData());

        // B. Nút Nhập Hàng
        btnThem.addActionListener(e -> {
            JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
            DialogNhapHang dialog = new DialogNhapHang(parent);
            dialog.setVisible(true);
            
            // Nếu nhập thành công thì load lại bảng
            if (dialog.isSuccess()) loadData();
        });

        // C. Nút Kiểm Kê
        btnKiemKe.addActionListener(e -> {
            JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
            DialogKiemKe dialog = new DialogKiemKe(parent);
            dialog.setVisible(true);
            
            // Nếu có Lưu phiếu -> Kho thay đổi -> Load lại
            if (dialog.isSaved()) loadData();
        });

        // D. Nút Lịch Sử
        btnLichSu.addActionListener(e -> {
            JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
            new DialogLichSuKiemKe(parent).setVisible(true);
        });

        // E. Nút Xóa Hàng
        btnXoa.addActionListener(e -> xuLyXoa());

        // F. Sự kiện Double Click -> Xem Serial (Sử dụng file DialogXemSerial riêng)
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // Double click
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        String maSP = tableModel.getValueAt(row, 0).toString();
                        String tenSP = tableModel.getValueAt(row, 1).toString();
                        
                        // Mở Dialog xem chi tiết Serial (Code gọn gàng hơn hẳn)
                        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(PanelKho.this);
                        new DialogXemSerial(parent, maSP, tenSP).setVisible(true);
                    }
                }
            }
        });

        // Load dữ liệu ngay khi mở
        loadData();
    }

    // --- CÁC HÀM HỖ TRỢ ---

    // 1. Hàm tải dữ liệu lên bảng
    public void loadData() {
        tableModel.setRowCount(0);
        List<SanPham> list = khoService.layDanhSachKho();
        for (SanPham sp : list) {
            tableModel.addRow(new Object[]{
                sp.getMaSP(), 
                sp.getTenSP(), 
                String.format("%,.0f", sp.getGia()), // Format tiền tệ
                sp.getSoLuongTon()
            });
        }
    }

    // 2. Hàm xử lý logic xóa (UI Handler)
    private void xuLyXoa() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
             JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm cần xóa!"); 
             return;
        }
        
        String maSP = tableModel.getValueAt(selectedRow, 0).toString();
        String tenSP = tableModel.getValueAt(selectedRow, 1).toString();
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc chắn muốn xóa: " + tenSP + " (" + maSP + ")?\n" +
            "Hệ thống sẽ xóa cả Serial tồn kho và Lịch sử kiểm kê liên quan.\n" +
            "(Lưu ý: Không thể xóa nếu sản phẩm đã từng được bán)", 
            "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
             // Gọi xuống Service để xử lý xóa an toàn
             boolean kq = khoService.xoaSanPham(maSP);
             
             if (kq) {
                 JOptionPane.showMessageDialog(this, "Đã xóa thành công!");
                 loadData();
             } else {
                 JOptionPane.showMessageDialog(this, 
                     "KHÔNG THỂ XÓA!\n" +
                     "Lý do: Sản phẩm này đã có lịch sử bán hàng (Serial trạng thái SOLD).\n" +
                     "Hệ thống giữ lại để bảo toàn dữ liệu hóa đơn cũ.");
             }
        }
    }
}