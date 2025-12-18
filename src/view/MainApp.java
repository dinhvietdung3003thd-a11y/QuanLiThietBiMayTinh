package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import model.NguoiDung;

// Import đầy đủ các Panel chức năng
import view.PanelBanHang;
import view.PanelKho;
import view.PanelBaoHanh;
import view.PanelThongKe;
import view.PanelNhanVien;   // Panel mới
import view.PanelNhaCungCap; // Panel mới

public class MainApp extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JPanel menuPanel;
    
    // Khai báo các nút Menu
    private JButton btnBanHang, btnKho, btnBaoHanh, btnThongKe;
    private JButton btnNhanVien, btnNhaCungCap, btnThoat;

    // Khai báo các màn hình (Panel)
    private PanelBanHang pnlBanHang;
    private PanelKho pnlKho;
    private PanelBaoHanh pnlBaoHanh;
    private PanelThongKe pnlThongKe;
    private PanelNhanVien pnlNhanVien;     // Mới
    private PanelNhaCungCap pnlNhaCungCap; // Mới

    public MainApp() {
        // 1. GỌI FORM ĐĂNG NHẬP
        LoginDialog login = new LoginDialog(this);
        login.setVisible(true);
        
        NguoiDung user = login.taiKhoanHienTai;

        // Nếu người dùng tắt form login hoặc chưa đăng nhập thì thoát app
        if (user == null) {
            System.exit(0);
        }

        // 2. THIẾT LẬP GIAO DIỆN CHÍNH
        setTitle("HỆ THỐNG QUẢN LÝ MÁY TÍNH - User: " + user.getFullName() + " (" + user.getRole() + ")");
        setSize(1350, 750); // Kích thước cửa sổ
        setLocationRelativeTo(null); // Ra giữa màn hình
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- A. MENU BÊN TRÁI ---
        menuPanel = new JPanel(new BorderLayout());
        menuPanel.setBackground(new Color(20, 30, 48)); // Màu nền tối
        menuPanel.setPreferredSize(new Dimension(230, 0));

        JPanel pnlTopMenu = new JPanel(new GridLayout(0, 1, 0, 10)); // Xếp dọc, cách nhau 10px
        pnlTopMenu.setOpaque(false);
        pnlTopMenu.setBorder(new EmptyBorder(20, 10, 0, 10));

        // Tạo các nút menu
        btnBanHang = createMenuButton("BÁN HÀNG");
        btnKho = createMenuButton("KHO HÀNG");
        btnBaoHanh = createMenuButton("BẢO HÀNH");
        btnThongKe = createMenuButton("THỐNG KÊ");
        btnNhaCungCap = createMenuButton("NHÀ CUNG CẤP"); // Mới
        btnNhanVien = createMenuButton("QUẢN LÝ NHÂN SỰ"); // Mới
        
        // Thêm các nút vào Menu (Thứ tự từ trên xuống)
        pnlTopMenu.add(btnBanHang);
        pnlTopMenu.add(btnKho);
        pnlTopMenu.add(btnBaoHanh);
        pnlTopMenu.add(btnThongKe);
        pnlTopMenu.add(btnNhaCungCap);
        pnlTopMenu.add(btnNhanVien);

        // Nút Đăng xuất ở dưới cùng
        JPanel pnlBottomMenu = new JPanel(new GridLayout(1, 1));
        pnlBottomMenu.setOpaque(false);
        pnlBottomMenu.setBorder(new EmptyBorder(0, 10, 20, 10));
        btnThoat = createMenuButton("ĐĂNG XUẤT");
        btnThoat.setBackground(new Color(192, 57, 43)); // Màu đỏ
        pnlBottomMenu.add(btnThoat);

        menuPanel.add(pnlTopMenu, BorderLayout.NORTH);
        menuPanel.add(pnlBottomMenu, BorderLayout.SOUTH);
        add(menuPanel, BorderLayout.WEST);

        // --- B. NỘI DUNG CHÍNH (PANEL BÊN PHẢI) ---
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Khởi tạo các màn hình con
        pnlBanHang = new PanelBanHang();
        pnlKho = new PanelKho();
        pnlBaoHanh = new PanelBaoHanh();
        pnlThongKe = new PanelThongKe();
        pnlNhanVien = new PanelNhanVien();
        pnlNhaCungCap = new PanelNhaCungCap();

        // Đưa các màn hình vào CardLayout kèm theo "Tên Thẻ" (Key)
        mainPanel.add(pnlBanHang, "BANHANG");
        mainPanel.add(pnlKho, "KHO");
        mainPanel.add(pnlBaoHanh, "BAOHANH");
        mainPanel.add(pnlThongKe, "THONGKE");
        mainPanel.add(pnlNhanVien, "NHANVIEN");
        mainPanel.add(pnlNhaCungCap, "NHACUNGCAP");
        
        add(mainPanel, BorderLayout.CENTER);

        // --- C. XỬ LÝ SỰ KIỆN CLICK NÚT ---
        
        btnBanHang.addActionListener(e -> {
            cardLayout.show(mainPanel, "BANHANG");
            updateButtonColor(btnBanHang);
            pnlBanHang.loadDataLenBang(); // Load lại dữ liệu cho mới
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
        
        // Sự kiện cho 2 nút mới
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
            dispose(); // Đóng cửa sổ hiện tại
            new MainApp().setVisible(true); // Mở lại từ đầu (hiện Login)
        });
        
        // --- D. GỌI HÀM PHÂN QUYỀN ---
        phanQuyen(user.getRole());
    }

    // === HÀM PHÂN QUYỀN (Logic If-Else bạn yêu cầu) ===
    private void phanQuyen(String role) {
        // 1. Mặc định: Ẩn TẤT CẢ các nút chức năng trước
        btnBanHang.setVisible(false);
        btnKho.setVisible(false);
        btnBaoHanh.setVisible(false);
        btnThongKe.setVisible(false);
        
        // Ẩn luôn 2 nút quản lý cao cấp
        btnNhanVien.setVisible(false);
        btnNhaCungCap.setVisible(false);

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
            // ADMIN: Hiện tất cả các nút cũ
            btnBanHang.setVisible(true);
            btnKho.setVisible(true);
            btnBaoHanh.setVisible(true);
            btnThongKe.setVisible(true);
            
            // ĐẶC BIỆT: Admin mới được thấy 2 nút này
            btnNhanVien.setVisible(true);
            btnNhaCungCap.setVisible(true);
            
            // Mặc định Admin vào Bán hàng trước
            cardLayout.show(mainPanel, "BANHANG");
            updateButtonColor(btnBanHang);
        }
    }

    // Hàm tạo giao diện nút bấm cho đẹp
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

    // Hàm đổi màu nút đang chọn (Active)
    private void updateButtonColor(JButton activeBtn) {
        // Reset màu tất cả
        JButton[] allBtns = {btnBanHang, btnKho, btnBaoHanh, btnThongKe, btnNhanVien, btnNhaCungCap};
        for (JButton btn : allBtns) {
            btn.setBackground(new Color(44, 62, 80));
        }
        
        // Set màu xanh sáng cho nút đang hiện
        if(activeBtn.isVisible()) {
             activeBtn.setBackground(new Color(41, 128, 185));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainApp().setVisible(true));
    }
}