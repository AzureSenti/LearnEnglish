# Learn English App

Dự án ứng dụng học tiếng Anh trên Android (LearnEnglish).

## 🚀 Giới thiệu
Learn English là một ứng dụng di động hỗ trợ việc học tiếng Anh, cung cấp các tính năng như học từ vựng, đọc bài báo (articles), và quản lý tài khoản người dùng. 

## 🛠 Cách chạy dự án

1. **Yêu cầu hệ thống:**
   - Cài đặt [Android Studio](https://developer.android.com/studio) (phiên bản mới nhất được khuyến nghị).
   - JDK 11 hoặc mới hơn.

2. **Các bước mở và chạy dự án:**
   - Clone hoặc tải mã nguồn về máy.
   - Mở Android Studio, chọn **Open** và trỏ đến thư mục gốc của dự án (`LearnEnglish`).
   - Đợi Android Studio hoàn tất việc đồng bộ (Sync) Gradle.
   - Chọn thiết bị máy ảo (Emulator) hoặc thiết bị thật (đã bật USB Debugging).
   - Nhấn nút **Run** (biểu tượng ▶️) để biên dịch và chạy ứng dụng.

## 🔌 Kết nối với Server

Ứng dụng cần kết nối với [Backend server](https://github.com/AzureSenti/LearnEngServer) để hoạt động đầy đủ chức năng. 

1. **Thiết lập file cấu hình môi trường:**
   - Trong thư mục gốc của dự án Android, bạn sẽ thấy file mẫu tên là `application-local.properties.example`.
   - Tạo một file mới tên là `application-local.properties` ngang hàng với file mẫu trên.

2. **Cấu hình địa chỉ IP của Server:**
   - Mở file `application-local.properties` vừa tạo.
   - Cấu hình biến `API_BASE_URL` trỏ tới địa chỉ server của bạn.
   - **Ví dụ:** 
     - Nếu bạn chạy backend server ở máy tính cục bộ (localhost) cổng 8000 và đang test trên máy ảo Android (Emulator), hãy sử dụng địa chỉ IP đặc biệt của emulator là `10.0.2.2`:
       ```properties
       API_BASE_URL=http://10.0.2.2:8000/api/v1/
       ```
     - Nếu bạn dùng thiết bị thật (điện thoại) để chạy app, điện thoại và máy tính chạy server phải chung mạng Wi-Fi. Bạn thay `10.0.2.2` bằng địa chỉ IP LAN của máy tính (ví dụ: `192.168.1.X`):
       ```properties
       API_BASE_URL=http://192.168.1.X:8000/api/v1/
       ```


3. Khởi động lại ứng dụng (Rebuild & Run) để nhận cấu hình API mới.
