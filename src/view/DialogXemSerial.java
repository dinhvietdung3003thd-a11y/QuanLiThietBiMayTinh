package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import service.KhoService;

public class DialogXemSerial extends JDialog {
    private JTable table;
    private DefaultTableModel model;
    private KhoService khoService = new KhoService();

    // Constructor nhận vào Mã SP và Tên SP để hiển thị
    public DialogXemSerial(Frame parent, String maSP, String tenSP) {
        super(parent, "Chi Tiết Serial: " + tenSP, true); // true = Modal (chặn cửa sổ cha)
        setSize(450, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // --- TIÊU ĐỀ ---
        JLabel lblTitle = new JLabel("DANH SÁCH SERIAL / IMEI", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setForeground(new Color(0, 102, 204));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(lblTitle, BorderLayout.NORTH);

        // --- BẢNG DỮ LIỆU ---
        String[] cols = {"Số Serial / IMEI", "Trạng Thái"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(25);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Tô màu dòng trạng thái cho đẹp
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (column == 1) { // Cột Trạng Thái
                    String status = value.toString();
                    if (status.equals("Đã bán")) c.setForeground(Color.RED);
                    else if (status.equals("Trong kho")) c.setForeground(new Color(0, 153, 76)); // Xanh lá
                    else c.setForeground(Color.BLACK);
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                } else {
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);

        // --- NÚT ĐÓNG ---
        JPanel pnlBot = new JPanel();
        JButton btnDong = new JButton("Đóng");
        btnDong.addActionListener(e -> dispose());
        pnlBot.add(btnDong);
        add(pnlBot, BorderLayout.SOUTH);

        // --- LOAD DỮ LIỆU ---
        loadData(maSP);
    }

    private void loadData(String maSP) {
        model.setRowCount(0);
        List<Object[]> list = khoService.layChiTietSerial(maSP);
        for (Object[] row : list) {
            model.addRow(row);
        }
    }
}