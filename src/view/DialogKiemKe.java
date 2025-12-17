package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import model.KiemKeItem; // Import model độc lập
import service.KiemKeService;

public class DialogKiemKe extends JDialog {
    private JTable table;
    private DefaultTableModel model;
    private KiemKeService service = new KiemKeService();
    private List<KiemKeItem> listKiemKe;
    private boolean isSaved = false; 

    public DialogKiemKe(Frame parent) {
        super(parent, "Phiếu Kiểm Kê Kho Hàng", true);
        setSize(850, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JLabel lblTitle = new JLabel("KIỂM KÊ SỐ LƯỢNG THỰC TẾ", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setForeground(new Color(41, 128, 185));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(lblTitle, BorderLayout.NORTH);

        // --- BẢNG (Logic chỉnh sửa trực tiếp) ---
        String[] cols = {"Mã SP", "Tên Sản Phẩm", "Hệ Thống", "Thực Tế (Sửa)", "Chênh Lệch", "Lý Do (Sửa)"};
        
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 3 || col == 5; // Chỉ sửa cột 3 và 5
            }
            
            @Override
            public void setValueAt(Object aValue, int row, int column) {
                super.setValueAt(aValue, row, column);
                
                // Logic cập nhật ngược lại vào List Model
                KiemKeItem item = listKiemKe.get(row);

                if (column == 3) { // Sửa số lượng
                    try {
                        int thucTe = Integer.parseInt(aValue.toString());
                        item.setTonThucTe(thucTe); // Cập nhật vào Model
                        
                        // Tính lại chênh lệch hiển thị
                        int chenhLech = item.getChenhLech();
                        super.setValueAt(chenhLech, row, 4);
                        
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "Phải nhập số!");
                    }
                } else if (column == 5) { // Sửa lý do
                    item.setLyDo(aValue.toString());
                }
            }
        };

        table = new JTable(model);
        table.setRowHeight(30);
        
        // Tô màu cột nhập liệu
        table.getColumnModel().getColumn(3).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(new Color(255, 255, 224)); // Vàng nhạt
                return c;
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);

        // --- NÚT BẤM ---
        JPanel pnlBot = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnLuu = new JButton("HOÀN TẤT & CẬP NHẬT KHO");
        btnLuu.setBackground(new Color(0, 153, 76));
        btnLuu.setForeground(Color.WHITE);
        btnLuu.setPreferredSize(new Dimension(250, 40));

        pnlBot.add(btnLuu);
        add(pnlBot, BorderLayout.SOUTH);

        // Load data
        listKiemKe = service.layDanhSachSanPhamDeKiemKe();
        for (KiemKeItem item : listKiemKe) {
            model.addRow(new Object[]{
                item.getMaSP(),
                item.getTenSP(),
                item.getTonHeThong(),
                item.getTonThucTe(),
                item.getChenhLech(),
                item.getLyDo()
            });
        }

        // Sự kiện Lưu
        btnLuu.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận cập nhật kho?", "Lưu", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (service.luuKetQuaKiemKe(listKiemKe, "Kiểm kê qua Dialog")) {
                    JOptionPane.showMessageDialog(this, "Thành công!");
                    isSaved = true;
                    dispose();
                }
            }
        });
    }
    
    
    
    public boolean isSaved() { return isSaved; }
}