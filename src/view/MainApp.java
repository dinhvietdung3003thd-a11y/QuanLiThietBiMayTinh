package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

// Import Model chuẩn (Không dùng NguoiDung nữa)
import model.NhanVien;

// Import các Panel chức năng
import view.PanelBanHang;
import view.PanelKho;
import view.PanelBaoHanh;
import view.PanelThongKe;
import view.PanelNhanVien;
import view.PanelNhaCungCap;

public class MainApp extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JPanel menuPanel;
    
    // Các nút Menu
    private JButton btnBanHang, btnKho, btnBaoHanh, btnThongKe;
    private JButton btnNhanVien, btnNhaCungCap, btnThoat;

    // Các màn hình chức năng
    private PanelBanHang pnlBanHang;
    private PanelKho pnlKho;
    private PanelBaoHanh pnlBaoHanh;
    private PanelThongKe pnlThongKe;
    private PanelNhanVien pnlNhanVien;
    private PanelNhaCungCap pnlNhaCungCap;

    public MainApp() {
        // 1. GỌI FORM ĐĂNG NHẬP
        LoginDialog login = new LoginDialog(this);
        login.setVisible(true);
        
        // Lấy thông tin NhanVien đã đăng nhập thành công
        NhanVien user = login.taiKhoanHienTai;

        // Nếu chưa đăng nhập (tắt form login) thì thoát chương trình
        if (user == null) {
            System.exit(0);
        }

        // 2. THIẾT LẬP GIAO DIỆN CHÍNH
        // Hiển thị tên và quyền trên thanh tiêu đề
        setTitle("HỆ THỐNG QUẢN LÝ - Xin chào: " + user.getHoTen() + " (" + user.getQuyen() + ")");
        setSize(1350, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- A. MENU BÊN TRÁI ---
        menuPanel = new JPanel(new BorderLayout());
        menuPanel.setBackground(new Color(20, 30, 48)); // Màu xanh đen sang trọng
        menuPanel.setPreferredSize(new Dimension(230, 0));

        JPanel pnlTopMenu = new JPanel(new GridLayout(0, 1, 0, 10)); 
        pnlTopMenu.setOpaque(false);
        pnlTopMenu.setBorder(new EmptyBorder(20, 10, 0, 10));

        // Tạo các nút
        btnBanHang = createMenuButton("BÁN HÀNG");
        btnKho = createMenuButton("KHO HÀNG");
        btnBaoHanh = createMenuButton("BẢO HÀNH");
        btnThongKe = createMenuButton("THỐNG KÊ");
        btnNhaCungCap = createMenuButton("NHÀ CUNG CẤP");
        btnNhanVien = createMenuButton("NHÂN SỰ (ADMIN)");
        
        // Add vào Menu
        pnlTopMenu.add(btnBanHang);
        pnlTopMenu.add(btnKho);
        pnlTopMenu.add(btnBaoHanh);
        pnlTopMenu.add(btnThongKe);
        pnlTopMenu.add(btnNhaCungCap);
        pnlTopMenu.add(btnNhanVien);

        // Nút Đăng xuất
        JPanel pnlBottomMenu = new JPanel(new GridLayout(1, 1));
        pnlBottomMenu.setOpaque(false);
        pnlBottomMenu.setBorder(new EmptyBorder(0, 10, 20, 10));
        btnThoat = createMenuButton("ĐĂNG XUẤT");
        btnThoat.setBackground(new Color(192, 57, 43)); // Đỏ
        pnlBottomMenu.add(btnThoat);

        menuPanel.add(pnlTopMenu, BorderLayout.NORTH);
        menuPanel.add(pnlBottomMenu, BorderLayout.SOUTH);
        add(menuPanel, BorderLayout.WEST);

        // --- B. NỘI DUNG CHÍNH (CARD LAYOUT) ---
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Khởi tạo các màn hình
        pnlBanHang = new PanelBanHang();
        pnlKho = new PanelKho();
        pnlBaoHanh = new PanelBaoHanh();
        pnlThongKe = new PanelThongKe();
        pnlNhanVien = new PanelNhanVien();
        pnlNhaCungCap = new PanelNhaCungCap();

        // Add vào mainPanel với các từ khóa (Key)
        mainPanel.add(pnlBanHang, "BANHANG");
        mainPanel.add(pnlKho, "KHO");
        mainPanel.add(pnlBaoHanh, "BAOHANH");
        mainPanel.add(pnlThongKe, "THONGKE");
        mainPanel.add(pnlNhanVien, "NHANVIEN");
        mainPanel.add(pnlNhaCungCap, "NHACUNGCAP");
        
        add(mainPanel, BorderLayout.CENTER);

        // --- C. SỰ KIỆN CHUYỂN TAB ---
        btnBanHang.addActionListener(e -> { 
            cardLayout.show(mainPanel, "BANHANG"); 
            updateButtonColor(btnBanHang);
            pnlBanHang.loadDataLenBang(); // Load dữ liệu mới nhất
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
        
        btnNhanVien.addActionListener(e -> { 
            cardLayout.show(mainPanel, "NHANVIEN");
            updateButtonColor(btnNhanVien);
            pnlNhanVien.loadDataLenBang();
        });
        
        btnNhaCungCap.addActionListener(e -> { 
            cardLayout.show(mainPanel, "NHACUNGCAP");
            updateButtonColor(btnNhaCungCap);
            pnlNhaCungCap.loadDataLenBang();
        });
        
        btnThoat.addActionListener(e -> {
            dispose();
            new MainApp().setVisible(true); // Đăng nhập lại
        });
        
        // 3. PHÂN QUYỀN (QUAN TRỌNG)
        phanQuyen(user.getQuyen());
    }

    // === HÀM PHÂN QUYỀN ===
    private void phanQuyen(String role) {
        // B1: Ẩn TẤT CẢ nút trước
        btnBanHang.setVisible(false);
        btnKho.setVisible(false);
        btnBaoHanh.setVisible(false);
        btnThongKe.setVisible(false);
        btnNhanVien.setVisible(false);
        btnNhaCungCap.setVisible(false);

        // B2: Hiện lại theo đúng chức vụ
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
            // BẢO HÀNH: Chỉ bảo hành
            btnBaoHanh.setVisible(true);
            cardLayout.show(mainPanel, "BAOHANH");
            updateButtonColor(btnBaoHanh);
            
        } else if ("ACCOUNTANT".equalsIgnoreCase(role)) {
            // KẾ TOÁN: Chỉ thống kê
            btnThongKe.setVisible(true);
            cardLayout.show(mainPanel, "THONGKE");
            updateButtonColor(btnThongKe);
            
        } else if ("ADMIN".equalsIgnoreCase(role)) {
            // ADMIN: Hiện 4 nút cơ bản
            btnBanHang.setVisible(true);
            btnKho.setVisible(true);
            btnBaoHanh.setVisible(true);
            btnThongKe.setVisible(true);
            
            // ADMIN: ĐƯỢC PHÉP THẤY 2 NÚT QUẢN TRỊ NÀY
            btnNhanVien.setVisible(true);
            btnNhaCungCap.setVisible(true);
            
            // Mặc định vào Bán hàng trước
            cardLayout.show(mainPanel, "BANHANG");
            updateButtonColor(btnBanHang);
        }
    }

    // Hàm tạo nút Menu nhanh
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

    // Hàm đổi màu nút active
    private void updateButtonColor(JButton activeBtn) {
        JButton[] allBtns = {btnBanHang, btnKho, btnBaoHanh, btnThongKe, btnNhanVien, btnNhaCungCap};
        for (JButton btn : allBtns) {
            btn.setBackground(new Color(44, 62, 80)); // Màu thường
        }
        if(activeBtn.isVisible()) {
             activeBtn.setBackground(new Color(41, 128, 185)); // Màu sáng (đang chọn)
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainApp().setVisible(true));
    }
}