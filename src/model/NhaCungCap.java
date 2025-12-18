package model;

public class NhaCungCap {
    private int maNCC;
    private String tenNCC;
    private String sdt;
    private String diaChi;

    public NhaCungCap(int maNCC, String tenNCC, String sdt, String diaChi) {
        this.maNCC = maNCC;
        this.tenNCC = tenNCC;
        this.sdt = sdt;
        this.diaChi = diaChi;
    }
    
    // Getter & Setter
    public int getMaNCC() { return maNCC; }
    public String getTenNCC() { return tenNCC; }
    public String getSdt() { return sdt; }
    public String getDiaChi() { return diaChi; }
    
    // Hàm này để hiển thị trong ComboBox sau này
    @Override
    public String toString() { return tenNCC; } 
}