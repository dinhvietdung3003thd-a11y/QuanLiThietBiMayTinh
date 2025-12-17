package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import service.BaoHanhService;

public class PanelBaoHanh extends JPanel {
    private JTextField txtSerial, txtTenSP, txtLoi;
    private JTable tblDanhSach;
    private DefaultTableModel modelDanhSach;
    private BaoHanhService service = new BaoHanhService();

    public PanelBaoHanh() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- PHẦN 1: FORM TIẾP NHẬN (Ở TRÊN) ---
        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.setBorder(BorderFactory.createTitledBorder("Tiếp Nhận Bảo Hành"));
        pnlTop.setPreferredSize(new Dimension(0, 180));

        // Layout nhập liệu
        JPanel pnlInput = new JPanel(new GridLayout(3, 2, 10, 10));
        pnlInput.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        txtSerial = new JTextField();
        JButton btnTraCuu = new JButton("Kiểm Tra Serial");
        
        txtTenSP = new JTextField(); 
        txtTenSP.setEditable(false); // Chỉ hiện tên, không cho sửa
        
        txtLoi = new JTextField();

        pnlInput.add(new JLabel("Serial / IMEI (*):"));
        JPanel pnlSearch = new JPanel(new BorderLayout());
        pnlSearch.add(txtSerial, BorderLayout.CENTER);
        pnlSearch.add(btnTraCuu, BorderLayout.EAST);
        pnlInput.add(pnlSearch);

        pnlInput.add(new JLabel("Tên Sản Phẩm:"));
        pnlInput.add(txtTenSP);

        pnlInput.add(new JLabel("Mô Tả Lỗi Của Khách:"));
        pnlInput.add(txtLoi);

        // Nút Tạo Phiếu
        JPanel pnlBtn = new JPanel();
        JButton btnTaoPhieu = new JButton("TẠO PHIẾU TIẾP NHẬN");
        btnTaoPhieu.setBackground(new Color(0, 153, 76));
        btnTaoPhieu.setForeground(Color.WHITE);
        btnTaoPhieu.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Nút Hoàn Thành (Sửa xong)
        JButton btnHoanThanh = new JButton("Đánh dấu ĐÃ XONG");
        btnHoanThanh.setBackground(new Color(41, 128, 185));
        btnHoanThanh.setForeground(Color.WHITE);

        pnlBtn.add(btnTaoPhieu);
        pnlBtn.add(btnHoanThanh);

        pnlTop.add(pnlInput, BorderLayout.CENTER);
        pnlTop.add(pnlBtn, BorderLayout.SOUTH);
        add(pnlTop, BorderLayout.NORTH);


        // --- PHẦN 2: DANH SÁCH ĐANG BẢO HÀNH (Ở DƯỚI) ---
        JPanel pnlBot = new JPanel(new BorderLayout());
        pnlBot.setBorder(BorderFactory.createTitledBorder("Danh Sách Máy Đang Bảo Hành"));
        
        String[] cols = {"Mã Phiếu", "Serial", "Tên SP", "Ngày Nhận", "Lỗi", "Trạng Thái"};
        modelDanhSach = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tblDanhSach = new JTable(modelDanhSach);
        pnlBot.add(new JScrollPane(tblDanhSach), BorderLayout.CENTER);
        
        add(pnlBot, BorderLayout.CENTER);


        // --- SỰ KIỆN ---
        
        // 1. Tra cứu Serial
        btnTraCuu.addActionListener(e -> {
            String sr = txtSerial.getText().trim();
            if (sr.isEmpty()) return;
            
            String[] info = service.traCuuSerial(sr);
            if (info != null) {
                txtTenSP.setText(info[1]); // Điền tên SP vào ô
                if ("SOLD".equals(info[2])) {
                    JOptionPane.showMessageDialog(this, "Hợp lệ! Sản phẩm đã bán, có thể bảo hành.");
                } else {
                    JOptionPane.showMessageDialog(this, "Cảnh báo: Serial này đang có trạng thái: " + info[2]);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy Serial này trong hệ thống!");
                txtTenSP.setText("");
            }
        });

        // 2. Tạo phiếu mới
        btnTaoPhieu.addActionListener(e -> {
            String sr = txtSerial.getText().trim();
            String loi = txtLoi.getText().trim();
            
            if (sr.isEmpty() || loi.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập Serial và Mô tả lỗi!");
                return;
            }
            
            // Gọi service tạo phiếu
            if (service.taoPhieuBaoHanh(sr, loi)) {
                JOptionPane.showMessageDialog(this, "Đã tiếp nhận bảo hành thành công!");
                loadData(); // Tải lại bảng
                txtLoi.setText(""); 
                txtSerial.setText(""); 
                txtTenSP.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi! Kiểm tra lại xem Serial có đúng không.");
            }
        });

        // 3. Đánh dấu Đã xong
        btnHoanThanh.addActionListener(e -> {
            int row = tblDanhSach.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Chọn một dòng để cập nhật trạng thái!");
                return;
            }
            
            int maPhieu = (int) modelDanhSach.getValueAt(row, 0);
            String trangThaiCu = modelDanhSach.getValueAt(row, 5).toString();
            
            if (trangThaiCu.equals("DA_XONG")) {
                JOptionPane.showMessageDialog(this, "Phiếu này đã xong rồi!");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận đã sửa xong máy này?", "Cập nhật", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (service.capNhatTrangThai(maPhieu, "DA_XONG")) {
                    loadData();
                }
            }
        });

        // Load dữ liệu khi mở
        loadData();
    }
    
    // Hàm public để MainApp gọi refresh
    public void loadData() {
        service.loadDanhSachBaoHanh(modelDanhSach);
    }
}