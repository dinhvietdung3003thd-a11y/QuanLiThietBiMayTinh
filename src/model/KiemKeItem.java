package model;

public class KiemKeItem {
    private String maSP;
    private String tenSP;
    private int tonHeThong; // Số lượng trong phần mềm
    private int tonThucTe;  // Số lượng bạn đếm được
    private String lyDo;    // Lý do chênh lệch

    // Constructor
    public KiemKeItem(String maSP, String tenSP, int tonHeThong) {
        this.maSP = maSP;
        this.tenSP = tenSP;
        this.tonHeThong = tonHeThong;
        this.tonThucTe = tonHeThong; // Mặc định ban đầu: Thực tế = Hệ thống (Lệch = 0)
        this.lyDo = "";
    }

    // --- GETTER & SETTER ---
    public String getMaSP() { return maSP; }
    public void setMaSP(String maSP) { this.maSP = maSP; }

    public String getTenSP() { return tenSP; }
    public void setTenSP(String tenSP) { this.tenSP = tenSP; }

    public int getTonHeThong() { return tonHeThong; }
    public void setTonHeThong(int tonHeThong) { this.tonHeThong = tonHeThong; }

    public int getTonThucTe() { return tonThucTe; }
    public void setTonThucTe(int tonThucTe) { this.tonThucTe = tonThucTe; }

    public String getLyDo() { return lyDo; }
    public void setLyDo(String lyDo) { this.lyDo = lyDo; }

    // Hàm tính toán tiện lợi
    public int getChenhLech() {
        return tonThucTe - tonHeThong;
    }
}