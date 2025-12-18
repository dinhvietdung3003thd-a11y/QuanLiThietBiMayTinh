package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import model.NhanVien;
import service.NhanVienService; // <--- Đã sửa import

public class PanelNhanVien extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    
    // Đã đổi sang service riêng
    private NhanVienService service = new NhanVienService();

    // Các ô nhập liệu
    private JTextField txtMa, txtTen, txtPass, txtTimKiem;
    private JComboBox<String> cboQuyen;
    private JButton btnThem, btnSua, btnXoa, btnLamMoi, btnTim;

    public PanelNhanVien() {
        setLayout(new BorderLayout());

        // --- PHẦN 1: GIAO DIỆN NHẬP LIỆU ---
        JPanel pnlTop = new JPanel(new BorderLayout());
        
        JPanel pnlFields = new JPanel(new GridLayout(4, 2, 10, 10));
        pnlFields.setBorder(BorderFactory.createTitledBorder("Thông tin Nhân Viên"));

        txtMa = new JTextField();
        txtTen = new JTextField();
        txtPass = new JTextField(); 
        
        String[] roles = {"SALE", "WAREHOUSE", "WARRANTY", "ACCOUNTANT", "ADMIN"};
        cboQuyen = new JComboBox<>(roles);

        pnlFields.add(new JLabel("Mã Nhân Viên:")); pnlFields.add(txtMa);
        pnlFields.add(new JLabel("Họ Tên:")); pnlFields.add(txtTen);
        pnlFields.add(new JLabel("Mật Khẩu:")); pnlFields.add(txtPass);
        pnlFields.add(new JLabel("Chức Vụ (Role):")); pnlFields.add(cboQuyen);

        // Thanh công cụ
        JPanel pnlTools = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlTools.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        btnThem = createButton("Thêm NV", new Color(0, 153, 76));
        btnSua = createButton("Cập Nhật", new Color(255, 153, 51));
        btnXoa = createButton("Xóa NV", new Color(204, 0, 0));
        btnLamMoi = createButton("Làm Mới", new Color(51, 153, 255));
        
        txtTimKiem = new JTextField(15);
        btnTim = new JButton("Tìm Kiếm");

        pnlTools.add(btnThem);
        pnlTools.add(btnSua);
        pnlTools.add(btnXoa);
        pnlTools.add(btnLamMoi);
        pnlTools.add(Box.createHorizontalStrut(30));
        pnlTools.add(new JLabel("Tìm Mã/Tên:"));
        pnlTools.add(txtTimKiem);
        pnlTools.add(btnTim);

        pnlTop.add(pnlFields, BorderLayout.CENTER);
        pnlTop.add(pnlTools, BorderLayout.SOUTH);
        add(pnlTop, BorderLayout.NORTH);

        // --- PHẦN 2: BẢNG DỮ LIỆU ---
        String[] cols = {"Mã NV", "Họ Tên", "Mật Khẩu", "Chức Vụ"};
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

        // Sự kiện Click bảng -> Đổ dữ liệu
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    txtMa.setText(table.getValueAt(row, 0).toString());
                    // KHÓA ô Mã NV lại khi sửa
                    txtMa.setEditable(false);
                    txtMa.setBackground(new Color(230, 230, 230));
                    
                    txtTen.setText(table.getValueAt(row, 1).toString());
                    txtPass.setText(table.getValueAt(row, 2).toString());
                    
                    String role = table.getValueAt(row, 3).toString();
                    cboQuyen.setSelectedItem(role);
                }
            }
        });

        // Nút Thêm
        btnThem.addActionListener(e -> {
            if (txtMa.getText().isEmpty() || txtTen.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã và Tên!");
                return;
            }
            NhanVien nv = new NhanVien(
                txtMa.getText(), 
                txtTen.getText(), 
                txtPass.getText(), 
                cboQuyen.getSelectedItem().toString()
            );
            
            if (service.themNhanVien(nv)) {
                JOptionPane.showMessageDialog(this, "Thêm thành công!");
                loadDataLenBang();
                resetForm();
            } else {
                JOptionPane.showMessageDialog(this, "Thêm thất bại (Mã trùng)!");
            }
        });

        // Nút Sửa
        btnSua.addActionListener(e -> {
            if (txtMa.getText().isEmpty() || txtMa.isEditable()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên để sửa!");
                return;
            }
            
            NhanVien nv = new NhanVien(
                txtMa.getText(), 
                txtTen.getText(), 
                txtPass.getText(), 
                cboQuyen.getSelectedItem().toString()
            );

            if (service.suaNhanVien(nv)) {
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                loadDataLenBang();
                resetForm();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi cập nhật!");
            }
        });

        // Nút Xóa
        btnXoa.addActionListener(e -> {
            if (txtMa.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần xóa!");
                return;
            }
            if (txtMa.getText().equalsIgnoreCase("admin")) {
                JOptionPane.showMessageDialog(this, "Không thể xóa Super Admin!");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, "Xóa nhân viên " + txtTen.getText() + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (service.xoaNhanVien(txtMa.getText())) {
                    JOptionPane.showMessageDialog(this, "Đã xóa!");
                    loadDataLenBang();
                    resetForm();
                } else {
                    JOptionPane.showMessageDialog(this, "Xóa thất bại!");
                }
            }
        });

        // Nút Tìm kiếm
        btnTim.addActionListener(e -> {
            String tuKhoa = txtTimKiem.getText();
            List<NhanVien> list = service.timKiemNhanVien(tuKhoa);
            model.setRowCount(0);
            for (NhanVien nv : list) {
                model.addRow(new Object[]{nv.getMaNV(), nv.getHoTen(), nv.getMatKhau(), nv.getQuyen()});
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
        List<NhanVien> list = service.layDSNhanVien();
        for (NhanVien nv : list) {
            model.addRow(new Object[]{nv.getMaNV(), nv.getHoTen(), nv.getMatKhau(), nv.getQuyen()});
        }
    }

    private void resetForm() {
        txtMa.setText("");
        txtMa.setEditable(true); // Mở lại ô Mã
        txtMa.setBackground(Color.WHITE);
        
        txtTen.setText("");
        txtPass.setText("");
        cboQuyen.setSelectedIndex(0);
        txtTimKiem.setText("");
    }
    
    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setForeground(color);
        return btn;
    }
}