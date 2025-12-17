package model;

public class KhachHang {
    private String sdt;
    private String tenKH;
    private String diaChi;

    public KhachHang() {}
    public KhachHang(String sdt, String tenKH, String diaChi) {
        this.sdt = sdt;
        this.tenKH = tenKH;
        this.diaChi = diaChi;
    }

    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }
    public String getTenKH() { return tenKH; }
    public void setTenKH(String tenKH) { this.tenKH = tenKH; }
    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
}