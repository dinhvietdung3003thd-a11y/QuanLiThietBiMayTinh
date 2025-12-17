package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import service.KiemKeService;

public class DialogLichSuKiemKe extends JDialog {
    private JTable tblPhieu;   // Bảng danh sách phiếu (Bên trái)
    private JTable tblChiTiet; // Bảng chi tiết sản phẩm (Bên phải)
    private DefaultTableModel modelPhieu, modelChiTiet;
    private KiemKeService service = new KiemKeService();

    public DialogLichSuKiemKe(Frame parent) {
        super(parent, "Lịch Sử Kiểm Kê Kho", true);
        setSize(900, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // --- TIÊU ĐỀ ---
        JLabel lblTitle = new JLabel("NHẬT KÝ KIỂM KÊ KHO HÀNG", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setForeground(new Color(41, 128, 185));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(lblTitle, BorderLayout.NORTH);

        // --- PHẦN GIỮA: SPLIT PANE (Chia đôi màn hình) ---
        JSplitPane splitPane = new JSplitPane();
        splitPane.setResizeWeight(0.35); // Bên trái chiếm 35%, phải 65%

        // 1. PANEL TRÁI: DANH SÁCH CÁC ĐỢT KIỂM
        JPanel pnlLeft = new JPanel(new BorderLayout());
        pnlLeft.setBorder(BorderFactory.createTitledBorder("Chọn đợt kiểm kê:"));
        
        modelPhieu = new DefaultTableModel(new String[]{"Mã Phiếu", "Ngày Giờ", "Ghi Chú"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblPhieu = new JTable(modelPhieu);
        tblPhieu.setRowHeight(25);
        // Chỉnh độ rộng cột cho đẹp
        tblPhieu.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblPhieu.getColumnModel().getColumn(1).setPreferredWidth(130);
        
        pnlLeft.add(new JScrollPane(tblPhieu), BorderLayout.CENTER);
        
        // Gợi ý nhỏ
        JLabel lblHint = new JLabel("<html><i>(Click vào dòng để xem chi tiết)</i></html>");
        lblHint.setHorizontalAlignment(SwingConstants.CENTER);
        pnlLeft.add(lblHint, BorderLayout.SOUTH);

        // 2. PANEL PHẢI: CHI TIẾT SẢN PHẨM
        JPanel pnlRight = new JPanel(new BorderLayout());
        pnlRight.setBorder(BorderFactory.createTitledBorder("Chi tiết phiếu đã chọn:"));

        modelChiTiet = new DefaultTableModel(new String[]{"Mã SP", "Tên SP", "Hệ Thống", "Thực Tế", "Lệch", "Lý Do"}, 0) {
             public boolean isCellEditable(int r, int c) { return false; }
        };
        tblChiTiet = new JTable(modelChiTiet);
        tblChiTiet.setRowHeight(25);
        
        // Tô màu dòng chênh lệch (Renderer)
        tblChiTiet.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                // Lấy giá trị cột Lệch (index 4)
                try {
                    int lech = Integer.parseInt(table.getValueAt(row, 4).toString());
                    if (lech != 0) {
                        c.setForeground(Color.RED);
                        c.setFont(c.getFont().deriveFont(Font.BOLD));
                    } else {
                        c.setForeground(Color.BLACK);
                    }
                } catch (Exception e) { }
                return c;
            }
        });

        pnlRight.add(new JScrollPane(tblChiTiet), BorderLayout.CENTER);

        // Thêm vào SplitPane
        splitPane.setLeftComponent(pnlLeft);
        splitPane.setRightComponent(pnlRight);
        add(splitPane, BorderLayout.CENTER);

        // --- NÚT ĐÓNG ---
        JPanel pnlBot = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnDong = new JButton("Đóng");
        btnDong.addActionListener(e -> dispose());
        pnlBot.add(btnDong);
        add(pnlBot, BorderLayout.SOUTH);

        // --- LOGIC ---
        loadDanhSachPhieu();

        // Sự kiện: Khi click vào bảng bên trái -> Load dữ liệu bảng bên phải
        tblPhieu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblPhieu.getSelectedRow();
                if (row != -1) {
                    int maPhieu = (int) tblPhieu.getValueAt(row, 0);
                    loadChiTiet(maPhieu);
                }
            }
        });
    }

    private void loadDanhSachPhieu() {
        modelPhieu.setRowCount(0);
        List<Object[]> list = service.layLichSuKiemKe();
        for (Object[] row : list) {
            modelPhieu.addRow(row);
        }
    }

    private void loadChiTiet(int maPhieu) {
        modelChiTiet.setRowCount(0);
        List<Object[]> list = service.layChiTietPhieu(maPhieu);
        for (Object[] row : list) {
            modelChiTiet.addRow(row);
        }
    }
}