package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import model.NguoiDung;

public class MainApp extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JPanel menuPanel;
    private JButton btnBanHang, btnKho, btnBaoHanh, btnThongKe, btnThoat;

    private PanelBanHang pnlBanHang;
    private PanelKho pnlKho;
    private PanelBaoHanh pnlBaoHanh;
    private PanelThongKe pnlThongKe;

    public MainApp() {
        // 1. Đăng nhập
        LoginDialog login = new LoginDialog(this);
        login.setVisible(true);
        
        NguoiDung user = login.taiKhoanHienTai;

        if (user == null) {
            System.exit(0);
        }

        // 2. Setup giao diện
        setTitle("HỆ THỐNG QUẢN LÝ MÁY TÍNH - User: " + user.getFullName() + " (" + user.getRole() + ")");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- MENU BÊN TRÁI ---
        menuPanel = new JPanel(new BorderLayout());
        menuPanel.setBackground(new Color(20, 30, 48));
        menuPanel.setPreferredSize(new Dimension(220, 0));

        JPanel pnlTopMenu = new JPanel(new GridLayout(0, 1, 0, 10)); // 1 Cột, n dòng
        pnlTopMenu.setOpaque(false);
        pnlTopMenu.setBorder(new EmptyBorder(20, 10, 0, 10));

        btnBanHang = createMenuButton("BÁN HÀNG");
        btnKho = createMenuButton("KHO HÀNG");
        btnBaoHanh = createMenuButton("BẢO HÀNH");
        btnThongKe = createMenuButton("THỐNG KÊ");
        
        // Add hết vào trước, lát phân quyền sẽ ẩn sau
        pnlTopMenu.add(btnBanHang);
        pnlTopMenu.add(btnKho);
        pnlTopMenu.add(btnBaoHanh);
        pnlTopMenu.add(btnThongKe);

        JPanel pnlBottomMenu = new JPanel(new GridLayout(1, 1));
        pnlBottomMenu.setOpaque(false);
        pnlBottomMenu.setBorder(new EmptyBorder(0, 10, 20, 10));
        btnThoat = createMenuButton("ĐĂNG XUẤT");
        btnThoat.setBackground(new Color(192, 57, 43));
        pnlBottomMenu.add(btnThoat);

        menuPanel.add(pnlTopMenu, BorderLayout.NORTH);
        menuPanel.add(pnlBottomMenu, BorderLayout.SOUTH);
        add(menuPanel, BorderLayout.WEST);

        // --- NỘI DUNG CHÍNH ---
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        pnlBanHang = new PanelBanHang();
        pnlKho = new PanelKho();
        pnlBaoHanh = new PanelBaoHanh();
        pnlThongKe = new PanelThongKe();

        mainPanel.add(pnlBanHang, "BANHANG");
        mainPanel.add(pnlKho, "KHO");
        mainPanel.add(pnlBaoHanh, "BAOHANH");
        mainPanel.add(pnlThongKe, "THONGKE");
        
        add(mainPanel, BorderLayout.CENTER);

        // --- SỰ KIỆN ---
        btnBanHang.addActionListener(e -> {
            cardLayout.show(mainPanel, "BANHANG");
            updateButtonColor(btnBanHang);
            pnlBanHang.loadDataLenBang();
        });
        
        btnKho.addActionListener(e -> {
            cardLayout.show(mainPanel, "KHO");
            updateButtonColor(btnKho);
            pnlKho.loadData();
        });
        
        btnBaoHanh.addActionListener(e -> {
            cardLayout.show(mainPanel, "BAOHANH");
            updateButtonColor(btnBaoHanh);
        });

        btnThongKe.addActionListener(e -> {
            cardLayout.show(mainPanel, "THONGKE");
            updateButtonColor(btnThongKe);
            pnlThongKe.loadData();
        });
        
        btnThoat.addActionListener(e -> {
            dispose();
            new MainApp().setVisible(true);
        });
        
        // --- GỌI HÀM PHÂN QUYỀN Ở ĐÂY ---
        phanQuyen(user.getRole());
    }

    // === HÀM XỬ LÝ LOGIC PHÂN QUYỀN ===
 // === HÀM PHÂN QUYỀN MỚI (CHUYÊN BIỆT HÓA) ===
    private void phanQuyen(String role) {
        // 1. Mặc định: Ẩn tất cả các nút chức năng trước
        btnBanHang.setVisible(false);
        btnKho.setVisible(false);
        btnBaoHanh.setVisible(false);
        btnThongKe.setVisible(false);

        // 2. Bật lại nút dựa theo Quyền (Role)
        if ("SALE".equalsIgnoreCase(role)) {
            // SALE: Chỉ bán hàng
            btnBanHang.setVisible(true);
            cardLayout.show(mainPanel, "BANHANG");
            updateButtonColor(btnBanHang);
            
        } else if ("WAREHOUSE".equalsIgnoreCase(role)) {
            // KHO: Chỉ quản lý kho
            btnKho.setVisible(true);
            cardLayout.show(mainPanel, "KHO");
            updateButtonColor(btnKho);
            
        } else if ("WARRANTY".equalsIgnoreCase(role)) {
            // BẢO HÀNH: Chỉ làm bảo hành
            btnBaoHanh.setVisible(true);
            cardLayout.show(mainPanel, "BAOHANH");
            updateButtonColor(btnBaoHanh);
            
        } else if ("ACCOUNTANT".equalsIgnoreCase(role)) {
            // KẾ TOÁN: Chỉ xem thống kê tiền nong
            btnThongKe.setVisible(true);
            cardLayout.show(mainPanel, "THONGKE");
            updateButtonColor(btnThongKe);
            
        } else if ("ADMIN".equalsIgnoreCase(role)) {
            // ADMIN: Hiện tất cả
            btnBanHang.setVisible(true);
            btnKho.setVisible(true);
            btnBaoHanh.setVisible(true);
            btnThongKe.setVisible(true);
            
            // Mặc định Admin vào Bán hàng trước
            cardLayout.show(mainPanel, "BANHANG");
            updateButtonColor(btnBanHang);
        }
    }

    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(44, 62, 80));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void updateButtonColor(JButton activeBtn) {
        btnBanHang.setBackground(new Color(44, 62, 80));
        btnKho.setBackground(new Color(44, 62, 80));
        btnBaoHanh.setBackground(new Color(44, 62, 80));
        btnThongKe.setBackground(new Color(44, 62, 80));
        
        if(activeBtn.isVisible()) { // Chỉ đổi màu nếu nút đó đang hiện
             activeBtn.setBackground(new Color(41, 128, 185));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainApp().setVisible(true));
    }
}