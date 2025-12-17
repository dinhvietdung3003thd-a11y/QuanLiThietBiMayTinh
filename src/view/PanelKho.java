package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import model.SanPham;
import service.KhoService;

public class PanelKho extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private KhoService khoService = new KhoService();

    public PanelKho() {
        setLayout(new BorderLayout());

        // --- TOOLBAR ---
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        
        JButton btnLoad = new JButton("Tải lại");
        JButton btnThem = new JButton("Nhập hàng");
        JButton btnXoa = new JButton("Xóa hàng");
        
        btnXoa.setForeground(Color.RED);
        btnXoa.setFont(new Font("Arial", Font.BOLD, 12));

        toolBar.add(btnLoad);
        toolBar.addSeparator();
        toolBar.add(btnThem);
        toolBar.addSeparator();
        toolBar.add(btnXoa);
        
        add(toolBar, BorderLayout.NORTH);

        // --- BẢNG DỮ LIỆU ---
        tableModel = new DefaultTableModel(new String[]{"Mã SP", "Tên Sản Phẩm", "Giá Bán", "Tồn Kho"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);
        
        // Gợi ý nhỏ ở dưới
        JLabel lblHint = new JLabel("  (*Mẹo: Click đúp vào dòng để xem danh sách Serial chi tiết)");
        lblHint.setFont(new Font("Arial", Font.ITALIC, 11));
        lblHint.setForeground(Color.BLUE);
        add(lblHint, BorderLayout.SOUTH);

        // --- SỰ KIỆN (EVENTS) ---
        
        btnLoad.addActionListener(e -> loadData());

        btnThem.addActionListener(e -> {
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            DialogNhapHang dialog = new DialogNhapHang(parentFrame);
            dialog.setVisible(true);
            if (dialog.isSuccess()) loadData();
        });

        btnXoa.addActionListener(e -> xuLyXoa());

        // SỰ KIỆN MỚI: CLICK ĐÚP CHUỘT (Double Click) ĐỂ XEM SERIAL
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // Nếu click 2 lần
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        String maSP = table.getValueAt(row, 0).toString();
                        String tenSP = table.getValueAt(row, 1).toString();
                        hienThiDialogSerial(maSP, tenSP);
                    }
                }
            }
        });

        loadData(); 
    }

    public void loadData() {
        tableModel.setRowCount(0);
        List<SanPham> list = khoService.layDanhSachKho();
        for (SanPham sp : list) {
            tableModel.addRow(new Object[]{
                sp.getMaSP(), sp.getTenSP(), 
                String.format("%,.0f", sp.getGia()), sp.getSoLuongTon()
            });
        }
    }

    private void xuLyXoa() {
        // ... (Giữ nguyên code xóa cũ của bạn) ...
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
             JOptionPane.showMessageDialog(this, "Chọn sản phẩm cần xóa!"); return;
        }
        String maSP = tableModel.getValueAt(selectedRow, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this, "Xóa sản phẩm " + maSP + "?", "Xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
             if(khoService.xoaSanPham(maSP)) loadData();
             else JOptionPane.showMessageDialog(this, "Không thể xóa (Đã có dữ liệu)!");
        }
    }

    // --- HÀM MỚI: HIỂN THỊ DIALOG DANH SÁCH SERIAL ---
    private void hienThiDialogSerial(String maSP, String tenSP) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Chi Tiết Serial: " + tenSP, true);
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(this);
        
        // Lấy dữ liệu từ Service
        List<Object[]> listSerial = khoService.layChiTietSerial(maSP);
        
        // Tạo bảng con
        String[] cols = {"Số Serial / IMEI", "Trạng Thái"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        for (Object[] row : listSerial) {
            model.addRow(row);
        }
        
        JTable tblSerial = new JTable(model);
        dialog.add(new JScrollPane(tblSerial));
        
        dialog.setVisible(true);
    }
}