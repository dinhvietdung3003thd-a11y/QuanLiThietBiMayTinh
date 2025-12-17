package model;

public class ChiTietSerial {
    private String serialNumber; // Mã định danh (VD: SN100)
    private String maSP;         // Thuộc về dòng máy nào (VD: SP01)
    private String status;       // Trạng thái: AVAILABLE (Trong kho), SOLD (Đã bán), WARRANTY (Đang sửa)
    
    // Bạn có thể thêm ngày nhập nếu muốn (cần sửa DB thêm cột Date)
    // private Date ngayNhap; 

    // 1. Constructor không tham số (Bắt buộc để một số thư viện hoạt động)
    public ChiTietSerial() {
    }

    // 2. Constructor đầy đủ
    public ChiTietSerial(String serialNumber, String maSP, String status) {
        this.serialNumber = serialNumber;
        this.maSP = maSP;
        this.status = status;
    }

    // 3. Getter & Setter (Để truy xuất dữ liệu)
    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getMaSP() {
        return maSP;
    }

    public void setMaSP(String maSP) {
        this.maSP = maSP;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // 4. Hàm toString (Optional - Giúp in ra log dễ nhìn hơn)
    @Override
    public String toString() {
        return serialNumber + " - " + status;
    }
}