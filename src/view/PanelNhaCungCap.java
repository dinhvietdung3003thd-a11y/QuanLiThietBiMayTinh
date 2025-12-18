package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import model.NhaCungCap;
import service.NhaCungCapService; // <--- Đã sửa import

public class PanelNhaCungCap extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    
    // Đã đổi sang service riêng
    private NhaCungCapService service = new NhaCungCapService(); 
    
    // Các ô nhập liệu
    private JTextField txtMa, txtTen, txtSDT, txtDiaChi, txtTimKiem;
    private JButton btnThem, btnSua, btnXoa, btnLamMoi, btnTim;

    public PanelNhaCungCap() {
        setLayout(new BorderLayout());
        
        // --- PHẦN 1: GIAO DIỆN NHẬP LIỆU ---
        JPanel pnlTop = new JPanel(new BorderLayout());
        
        // Form nhập thông tin
        JPanel pnlFields = new JPanel(new GridLayout(4, 2, 10, 10));
        pnlFields.setBorder(BorderFactory.createTitledBorder("Thông tin Nhà Cung Cấp"));
        
        txtMa = new JTextField(); 
        txtMa.setEditable(false); // Mã tự tăng -> Cấm sửa
        txtMa.setBackground(new Color(230, 230, 230));
        
        txtTen = new JTextField();
        txtSDT = new JTextField();
        txtDiaChi = new JTextField();
        
        pnlFields.add(new JLabel("Mã NCC (Auto):")); pnlFields.add(txtMa);
        pnlFields.add(new JLabel("Tên Nhà Cung Cấp:")); pnlFields.add(txtTen);
        pnlFields.add(new JLabel("Số Điện Thoại:")); pnlFields.add(txtSDT);
        pnlFields.add(new JLabel("Địa Chỉ:")); pnlFields.add(txtDiaChi);

        // Thanh công cụ nút bấm
        JPanel pnlTools = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlTools.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        btnThem = createButton("Thêm", new Color(0, 153, 76));
        btnSua = createButton("Sửa", new Color(255, 153, 51));
        btnXoa = createButton("Xóa", new Color(204, 0, 0));
        btnLamMoi = createButton("Làm Mới", new Color(51, 153, 255));
        
        txtTimKiem = new JTextField(15);
        btnTim = new JButton("Tìm Kiếm");
        
        pnlTools.add(btnThem);
        pnlTools.add(btnSua);
        pnlTools.add(btnXoa);
        pnlTools.add(btnLamMoi);
        pnlTools.add(Box.createHorizontalStrut(30)); // Khoảng cách
        pnlTools.add(new JLabel("Tìm theo Tên/SĐT:"));
        pnlTools.add(txtTimKiem);
        pnlTools.add(btnTim);

        pnlTop.add(pnlFields, BorderLayout.CENTER);
        pnlTop.add(pnlTools, BorderLayout.SOUTH);
        add(pnlTop, BorderLayout.NORTH);

        // --- PHẦN 2: BẢNG DỮ LIỆU ---
        String[] cols = {"Mã NCC", "Tên Nhà Cung Cấp", "SĐT", "Địa Chỉ"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(25);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        add(new JScrollPane(table), BorderLayout.CENTER);

        // --- PHẦN 3: SỰ KIỆN ---
        loadDataLenBang(); 

        // Sự kiện: Click vào bảng -> Đổ dữ liệu lên ô nhập
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    txtMa.setText(table.getValueAt(row, 0).toString());
                    txtTen.setText(table.getValueAt(row, 1).toString());
                    txtSDT.setText(table.getValueAt(row, 2).toString());
                    txtDiaChi.setText(table.getValueAt(row, 3).toString());
                }
            }
        });

        // Nút Thêm
        btnThem.addActionListener(e -> {
            if (txtTen.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập tên NCC!");
                return;
            }
            if (service.themNhaCungCap(txtTen.getText(), txtSDT.getText(), txtDiaChi.getText())) {
                JOptionPane.showMessageDialog(this, "Thêm thành công!");
                loadDataLenBang();
                resetForm();
            } else {
                JOptionPane.showMessageDialog(this, "Thêm thất bại!");
            }
        });

        // Nút Sửa
        btnSua.addActionListener(e -> {
            if (txtMa.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn NCC cần sửa!");
                return;
            }
            try {
                int maNCC = Integer.parseInt(txtMa.getText());
                NhaCungCap ncc = new NhaCungCap(maNCC, txtTen.getText(), txtSDT.getText(), txtDiaChi.getText());
                
                if (service.suaNhaCungCap(ncc)) {
                    JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                    loadDataLenBang();
                    resetForm();
                } else {
                    JOptionPane.showMessageDialog(this, "Lỗi cập nhật!");
                }
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        // Nút Xóa
        btnXoa.addActionListener(e -> {
            if (txtMa.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn NCC cần xóa!");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "Xóa NCC này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                int maNCC = Integer.parseInt(txtMa.getText());
                if (service.xoaNhaCungCap(maNCC)) {
                    JOptionPane.showMessageDialog(this, "Đã xóa!");
                    loadDataLenBang();
                    resetForm();
                } else {
                    JOptionPane.showMessageDialog(this, "Xóa thất bại (NCC đang được sử dụng)!");
                }
            }
        });

        // Nút Tìm kiếm
        btnTim.addActionListener(e -> {
            String tuKhoa = txtTimKiem.getText();
            List<NhaCungCap> list = service.timKiemNhaCungCap(tuKhoa);
            model.setRowCount(0);
            for (NhaCungCap ncc : list) {
                model.addRow(new Object[]{ncc.getMaNCC(), ncc.getTenNCC(), ncc.getSdt(), ncc.getDiaChi()});
            }
        });

        // Nút Làm Mới
        btnLamMoi.addActionListener(e -> {
            resetForm();
            loadDataLenBang();
        });
    }

    public void loadDataLenBang() {
        model.setRowCount(0);
        List<NhaCungCap> list = service.layDSNhaCungCap();
        for (NhaCungCap ncc : list) {
            model.addRow(new Object[]{ncc.getMaNCC(), ncc.getTenNCC(), ncc.getSdt(), ncc.getDiaChi()});
        }
    }

    private void resetForm() {
        txtMa.setText(""); txtTen.setText(""); txtSDT.setText(""); txtDiaChi.setText(""); txtTimKiem.setText("");
    }
    
    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setForeground(color);
        return btn;
    }
}