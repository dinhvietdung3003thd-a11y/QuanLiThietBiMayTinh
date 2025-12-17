package view;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import model.SanPham;
import model.ChiTietSerial; // <--- Import Model
import service.KhoService;

public class DialogNhapHang extends JDialog {
    private JTextField txtMaSP, txtTenSP, txtGia, txtSoLuong;
    private JTextArea txtListSerial;
    private KhoService khoService = new KhoService();
    private boolean isSuccess = false;

    public DialogNhapHang(Frame parent) {
        super(parent, "Nhập Hàng Hóa Chuẩn Chỉ", true);
        setSize(500, 480);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // --- GIAO DIỆN (Giữ nguyên cho đẹp) ---
        JPanel pnlCenter = new JPanel(new GridBagLayout());
        pnlCenter.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        txtMaSP = new JTextField(15);
        txtTenSP = new JTextField(15);
        txtGia = new JTextField(15);
        txtSoLuong = new JTextField(15);
        txtListSerial = new JTextArea(5, 15);
        txtListSerial.setBorder(BorderFactory.createEtchedBorder());
        JScrollPane scrollSerial = new JScrollPane(txtListSerial);

        // Dòng 1: Mã
        gbc.gridx = 0; gbc.gridy = 0; pnlCenter.add(new JLabel("Mã Sản Phẩm (*):"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; pnlCenter.add(txtMaSP, gbc);

        // Dòng 2: Tên
        gbc.gridx = 0; gbc.gridy = 1; pnlCenter.add(new JLabel("Tên Sản Phẩm:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; pnlCenter.add(txtTenSP, gbc);

        // Dòng 3: Giá
        gbc.gridx = 0; gbc.gridy = 2; pnlCenter.add(new JLabel("Giá Bán:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; pnlCenter.add(txtGia, gbc);

        // Dòng 4: Số lượng
        gbc.gridx = 0; gbc.gridy = 3; pnlCenter.add(new JLabel("Số Lượng Nhập:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; pnlCenter.add(txtSoLuong, gbc);

        // Dòng 5: Serial
        gbc.gridx = 0; gbc.gridy = 4; gbc.anchor = GridBagConstraints.NORTHWEST;
        pnlCenter.add(new JLabel("DS Serial/IMEI:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH;
        pnlCenter.add(scrollSerial, gbc);

        // Hint
        gbc.gridx = 1; gbc.gridy = 5; gbc.weighty = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel lblHint = new JLabel("<html><i>(Mỗi dòng 1 mã Serial)</i></html>");
        lblHint.setForeground(Color.GRAY);
        pnlCenter.add(lblHint, gbc);

        add(pnlCenter, BorderLayout.CENTER);

        // Nút bấm
        JPanel pnlBot = new JPanel();
        JButton btnLuu = new JButton("Lưu Kho");
        JButton btnHuy = new JButton("Hủy Bỏ");
        btnLuu.setBackground(new Color(0, 153, 76));
        btnLuu.setForeground(Color.WHITE);
        
        pnlBot.add(btnLuu);
        pnlBot.add(btnHuy);
        add(pnlBot, BorderLayout.SOUTH);

        btnLuu.addActionListener(e -> xuLyLuu());
        btnHuy.addActionListener(e -> dispose());
    }

    private void xuLyLuu() {
        if (txtMaSP.getText().isEmpty() || txtTenSP.getText().isEmpty() || 
            txtGia.getText().isEmpty() || txtSoLuong.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập thông tin cơ bản!");
            return;
        }

        try {
            // 1. Lấy dữ liệu cơ bản
            String ma = txtMaSP.getText().trim();
            String ten = txtTenSP.getText().trim();
            double gia = Double.parseDouble(txtGia.getText().replace(",", ""));
            int sl = Integer.parseInt(txtSoLuong.getText());

            // 2. Xử lý danh sách Serial -> Chuyển thành List Model
            List<ChiTietSerial> listSerialModel = new ArrayList<>();
            String textSerial = txtListSerial.getText();
            
            if (!textSerial.trim().isEmpty()) {
                String[] arr = textSerial.split("[\\n,]+"); // Cắt theo xuống dòng hoặc dấu phẩy
                for (String s : arr) {
                    if (!s.trim().isEmpty()) {
                        // Tạo đối tượng Model chuẩn chỉ
                        ChiTietSerial item = new ChiTietSerial(s.trim(), ma, "AVAILABLE");
                        listSerialModel.add(item);
                    }
                }
            }
            
            // Logic kiểm tra: Số lượng serial nhập vào có khớp với số lượng hàng không?
            // (Tùy bạn, ở đây mình chỉ cảnh báo nhẹ nếu nhập thiếu)
            if (listSerialModel.size() > 0 && listSerialModel.size() != sl) {
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "Cảnh báo: Bạn nhập số lượng là " + sl + " nhưng chỉ có " + listSerialModel.size() + " mã Serial.\nBạn có muốn tiếp tục không?",
                    "Cảnh báo lệch kho", JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) return;
            }

            // 3. GỌI SERVICE
            // Bước 3a: Lưu sản phẩm (bảng Products)
            SanPham sp = new SanPham(ma, ten, gia, sl);
            boolean kq1 = khoService.nhapHang(sp);

            // Bước 3b: Lưu danh sách Serial (bảng ProductSerials) dùng List Model
            if (kq1) {
                khoService.nhapDanhSachSerial(listSerialModel); // <--- Truyền List Model
                JOptionPane.showMessageDialog(this, "Nhập hàng thành công!");
                isSuccess = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi lưu sản phẩm!");
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Giá và Số lượng phải là số!");
        }
    }
    
    public boolean isSuccess() { return isSuccess; }
}