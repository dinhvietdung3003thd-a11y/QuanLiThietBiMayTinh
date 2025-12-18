package model;

public class NhanVien {
    private String maNV;
    private String hoTen;
    private String matKhau;
    private String quyen;

    public NhanVien(String maNV, String hoTen, String matKhau, String quyen) {
        this.maNV = maNV;
        this.hoTen = hoTen;
        this.matKhau = matKhau;
        this.quyen = quyen;
    }

    // Getter & Setter
    public String getMaNV() { return maNV; }
    public String getHoTen() { return hoTen; }
    public String getMatKhau() { return matKhau; }
    public String getQuyen() { return quyen; }
}