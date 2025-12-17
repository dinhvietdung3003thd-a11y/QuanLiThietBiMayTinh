package model;

public class SanPham {
    private String maSP;
    private String tenSP;
    private double gia;
    private int soLuongTon;

    public SanPham() {}

    public SanPham(String maSP, String tenSP, double gia, int soLuongTon) {
        this.maSP = maSP;
        this.tenSP = tenSP;
        this.gia = gia;
        this.soLuongTon = soLuongTon;
    }

    // Getter & Setter (Bắt buộc để truy xuất dữ liệu)
    public String getMaSP() { return maSP; }
    public void setMaSP(String maSP) { this.maSP = maSP; }

    public String getTenSP() { return tenSP; }
    public void setTenSP(String tenSP) { this.tenSP = tenSP; }

    public double getGia() { return gia; }
    public void setGia(double gia) { this.gia = gia; }

    public int getSoLuongTon() { return soLuongTon; }
    public void setSoLuongTon(int soLuongTon) { this.soLuongTon = soLuongTon; }
}