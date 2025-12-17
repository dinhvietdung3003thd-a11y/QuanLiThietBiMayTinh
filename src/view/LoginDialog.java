package view;

import javax.swing.*;
import java.awt.*;
import model.NguoiDung;
import service.AuthService;

public class LoginDialog extends JDialog {
    private JTextField txtUser;
    private JPasswordField txtPass;
    private AuthService authService = new AuthService();
    public NguoiDung taiKhoanHienTai = null; // Lưu user đã đăng nhập

    public LoginDialog(Frame parent) {
        super(parent, "Đăng Nhập", true);
        setLayout(new GridLayout(3, 2, 10, 10));
        setSize(300, 180);
        setLocationRelativeTo(null);

        add(new JLabel("  Tài khoản:"));
        txtUser = new JTextField("admin"); // Mặc định cho nhanh test
        add(txtUser);

        add(new JLabel("  Mật khẩu:"));
        txtPass = new JPasswordField("123");
        add(txtPass);

        JButton btnLogin = new JButton("Đăng nhập");
        JButton btnExit = new JButton("Thoát");
        add(btnLogin);
        add(btnExit);

        btnLogin.addActionListener(e -> xuLyDangNhap());
        btnExit.addActionListener(e -> System.exit(0));
    }

    private void xuLyDangNhap() {
        String user = txtUser.getText();
        String pass = new String(txtPass.getPassword());
        
        taiKhoanHienTai = authService.dangNhap(user, pass);
        
        if (taiKhoanHienTai != null) {
            JOptionPane.showMessageDialog(this, "Xin chào " + taiKhoanHienTai.getFullName());
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Sai thông tin đăng nhập!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}