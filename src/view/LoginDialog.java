package view;

import javax.swing.*;
import java.awt.*;
// 1. SỬA IMPORT: Dùng NhanVien thay vì NguoiDung
import model.NhanVien;
import service.AuthService;

public class LoginDialog extends JDialog {
    private JTextField txtUser;
    private JPasswordField txtPass;
    private AuthService authService = new AuthService();
    
    // 2. SỬA BIẾN: Lưu đối tượng NhanVien
    public NhanVien taiKhoanHienTai = null; 

    public LoginDialog(Frame parent) {
        super(parent, "Đăng Nhập Hệ Thống", true);
        setLayout(new GridLayout(3, 2, 10, 10));
        setSize(320, 180);
        setLocationRelativeTo(null);

        add(new JLabel("  Mã Nhân Viên:")); // Sửa nhãn cho rõ nghĩa
        txtUser = new JTextField("admin"); // Mặc định để test
        add(txtUser);

        add(new JLabel("  Mật khẩu:"));
        txtPass = new JPasswordField("123");
        add(txtPass);

        JButton btnLogin = new JButton("Đăng nhập");
        JButton btnExit = new JButton("Thoát");
        
        // Thêm màu sắc cho nút nhìn đẹp hơn xíu
        btnLogin.setBackground(new Color(0, 102, 204));
        btnLogin.setForeground(Color.WHITE);
        btnExit.setBackground(new Color(204, 0, 0));
        btnExit.setForeground(Color.WHITE);
        
        add(btnLogin);
        add(btnExit);

        btnLogin.addActionListener(e -> xuLyDangNhap());
        btnExit.addActionListener(e -> System.exit(0));
        
        // Cho phép ấn Enter để đăng nhập luôn
        getRootPane().setDefaultButton(btnLogin);
    }

    private void xuLyDangNhap() {
        String maNV = txtUser.getText();
        String pass = new String(txtPass.getPassword());
        
        // AuthService giờ trả về NhanVien nên code này khớp luôn
        taiKhoanHienTai = authService.dangNhap(maNV, pass);
        
        if (taiKhoanHienTai != null) {
            // 3. SỬA HÀM GET: getFullName() -> getHoTen()
            JOptionPane.showMessageDialog(this, "Xin chào: " + taiKhoanHienTai.getHoTen());
            dispose(); // Đóng form login để về MainApp
        } else {
            JOptionPane.showMessageDialog(this, "Sai mã nhân viên hoặc mật khẩu!", "Lỗi Đăng Nhập", JOptionPane.ERROR_MESSAGE);
        }
    }
}