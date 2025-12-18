Bước 1: Cài đặt và Bật XAMPP
Tải XAMPP tại: https://www.apachefriends.org/download.html
Cài đặt như bình thường (Cứ nhấn Next).
Mở XAMPP Control Panel.
Bấm nút Start ở 2 dòng:
Apache (Web server)
MySQL (Database server) (Khi 2 dòng chuyển sang màu xanh lá là OK).

Bước 2: Tạo Cơ sở dữ liệu
Mở trình duyệt web, truy cập: http://localhost/phpmyadmin
Nhìn cột bên trái, bấm New.
Ô "Database name" nhập chính xác: quanlysinhvien
Bấm Create.

Bước 3: Chạy script SQL
Bấm vào tab SQL ở thanh menu trên cùng.
Copy toàn bộ đoạn code SQL dưới đây và dán vào ô trống:
-- 1. TẠO DATABASE MỚI
DROP DATABASE IF EXISTS quanlymaytinh;
CREATE DATABASE quanlymaytinh CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE quanlymaytinh;

-- =======================================================

-- NHÓM 1: CÁC BẢNG ĐỘC LẬP (Tạo trước vì không phụ thuộc ai)

-- =======================================================

-- 1. Bảng NHÂN VIÊN
CREATE TABLE nhanvien (
    maNV VARCHAR(20) PRIMARY KEY,
    hoTen NVARCHAR(100) NOT NULL,
    matKhau VARCHAR(100) NOT NULL,
    quyen VARCHAR(20) NOT NULL
);

-- 2. Bảng KHÁCH HÀNG (Theo diagram: dùng sdt làm khóa chính)
CREATE TABLE khachhang (
    sdt VARCHAR(20) PRIMARY KEY, -- Khóa chính là Số điện thoại
    tenKH NVARCHAR(100) NOT NULL,
    diaChi NVARCHAR(255)
);

-- 3. Bảng NHÀ CUNG CẤP
CREATE TABLE nhacungcap (
    maNCC INT AUTO_INCREMENT PRIMARY KEY,
    tenNCC NVARCHAR(100) NOT NULL,
    soDienThoai VARCHAR(20),
    diaChi NVARCHAR(255)
);

-- 4. Bảng SẢN PHẨM (Products)
CREATE TABLE products (
    productID VARCHAR(20) PRIMARY KEY,
    productName NVARCHAR(200) NOT NULL,
    price DECIMAL(15,0) DEFAULT 0,
    stock INT DEFAULT 0
);

-- 5. Bảng PHIẾU KIỂM KÊ (Header)
CREATE TABLE phieukiemke (
    maPhieu INT AUTO_INCREMENT PRIMARY KEY,
    ngayTao DATETIME DEFAULT CURRENT_TIMESTAMP,
    ghiChu NVARCHAR(255)
);

-- =======================================================
-- NHÓM 2: CÁC BẢNG CÓ LIÊN KẾT (Tạo sau để nối dây)
-- =======================================================

-- 6. Bảng SERIALS (Nối với Products)
CREATE TABLE productserials (
    serialNumber VARCHAR(50) PRIMARY KEY,
    productID VARCHAR(20) NOT NULL,
    status VARCHAR(50) DEFAULT 'AVAILABLE',
    
    -- TẠO LIÊN KẾT (Dây nối trong Diagram):
    CONSTRAINT FK_Serial_Product FOREIGN KEY (productID) 
    REFERENCES products(productID) ON DELETE CASCADE
);

-- 7. Bảng HÓA ĐƠN (Nối với NhanVien và KhachHang)
CREATE TABLE hoadon (
    maHD INT AUTO_INCREMENT PRIMARY KEY,
    ngayTao DATETIME DEFAULT CURRENT_TIMESTAMP,
    tongTien DECIMAL(15,0) DEFAULT 0,
    
    -- Khóa ngoại nối về Nhân Viên (Ai bán)
    maNV VARCHAR(20) NOT NULL,
    CONSTRAINT FK_HoaDon_NhanVien FOREIGN KEY (maNV) REFERENCES nhanvien(maNV),
    
    -- Khóa ngoại nối về Khách Hàng (Bán cho ai - Dựa trên SĐT)
    sdtKhachHang VARCHAR(20),
    CONSTRAINT FK_HoaDon_KhachHang FOREIGN KEY (sdtKhachHang) REFERENCES khachhang(sdt)
);

-- 8. Bảng CHI TIẾT HÓA ĐƠN (Bảng trung gian để biết Hóa đơn đó bán Serial nào)
-- (Trong diagram bạn có thể ẩn, nhưng bắt buộc phải có để lưu dữ liệu bán hàng)
CREATE TABLE chitiethoadon (
    id INT AUTO_INCREMENT PRIMARY KEY,
    maHD INT NOT NULL,
    serialNumber VARCHAR(50) NOT NULL,
    giaBan DECIMAL(15,0) DEFAULT 0,
    
    CONSTRAINT FK_CTHD_HoaDon FOREIGN KEY (maHD) REFERENCES hoadon(maHD) ON DELETE CASCADE,
    CONSTRAINT FK_CTHD_Serial FOREIGN KEY (serialNumber) REFERENCES productserials(serialNumber)
);

-- 9. Bảng PHIẾU BẢO HÀNH (Nối với Serial)
CREATE TABLE phieubaohanh (
    maPhieu INT AUTO_INCREMENT PRIMARY KEY,
    serialNumber VARCHAR(50) NOT NULL, -- Bảo hành cho máy nào
    ngayNhan DATETIME DEFAULT CURRENT_TIMESTAMP,
    loiKhachBao NVARCHAR(255),
    trangThai NVARCHAR(50) DEFAULT N'Đang xử lý',
    
    -- Tạo liên kết
    CONSTRAINT FK_BaoHanh_Serial FOREIGN KEY (serialNumber) REFERENCES productserials(serialNumber)
);

-- 10. Bảng CHI TIẾT KIỂM KÊ (Nối với PhieuKiemKe và Products)
CREATE TABLE chitietkiemke (
    id INT AUTO_INCREMENT PRIMARY KEY,
    maPhieu INT NOT NULL,
    maSP VARCHAR(20) NOT NULL,
    tonHeThong INT DEFAULT 0,
    tonThucTe INT DEFAULT 0,
    lyDo NVARCHAR(255),
    
    -- Tạo 2 đường dây nối
    CONSTRAINT FK_CTKK_Phieu FOREIGN KEY (maPhieu) REFERENCES phieukiemke(maPhieu) ON DELETE CASCADE,
    CONSTRAINT FK_CTKK_Product FOREIGN KEY (maSP) REFERENCES products(productID)
);

-- =======================================================
-- DATA MẪU (Để test sơ đồ chạy đúng không)
-- =======================================================

INSERT INTO nhanvien (maNV, hoTen, matKhau, quyen) VALUES 
('admin', N'Quản Trị Viên', '123', 'ADMIN'),
('sale01', N'Nhân Viên Bán', '123', 'SALE');

INSERT INTO khachhang (sdt, tenKH, diaChi) VALUES 
('0912345678', N'Nguyễn Văn A', N'Hà Nội');

INSERT INTO products (productID, productName, price, stock) VALUES 
('SP01', N'Laptop Dell', 15000000, 2);

INSERT INTO productserials (serialNumber, productID, status) VALUES 
('SN001', 'SP01', 'AVAILABLE'),
('SN002', 'SP01', 'SOLD');

-- Tạo thử 1 hóa đơn cho khách 0912345678 mua máy SN002
INSERT INTO hoadon (maNV, sdtKhachHang, tongTien) VALUES ('sale01', '0912345678', 15000000);
INSERT INTO chitiethoadon (maHD, serialNumber, giaBan) VALUES (1, 'SN002', 15000000);

-- Tạo thử phiếu bảo hành cho máy SN002
INSERT INTO phieubaohanh (serialNumber, loiKhachBao) VALUES ('SN002', N'Lỗi màn hình');
--lưu ý : khi run vào file databasehelper đổi phần mật khẩu thành "".
