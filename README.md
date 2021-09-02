# Taxi-Server
## Tổng quan về hệ thống
![alt text](https://github.com/phantruongbovi/Taxi-Server/blob/main/Diagram.png)
- Để test mình đang lưu trên redis 1m xe, chia đều cho 3 loại xe (1, 2, 3).
## Setup
1. Clone repo về máy
```
git clone https://github.com/phantruongbovi/Taxi-Server.git
```
2. Mở folder bằng IntelliJ IDEA (Hoặc một công cụ khác tương đương)
3. Cài đặt môi trường
- Vào file **build.gradle** chuột phải -> **link with gradle** -> **Gradle** (Thanh bên phải) -> **Task** -> **orther** -> **generateProto**
- Chọn vào setting IDE -> **Build, Excution, Deployment** -> **Build Tools** -> **Gradle** 
-> Đổi 2 cái trong **Build and run** từ Gradle thành IntelliJIDEA
## Run
1. Mở src > main > java > taxi > client > Client.java (Đọc và điều chỉnh thông số theo ý muốn) -> RUN file
2. Địa chỉ ip và port:
- taxi-server: 20.43.157.85:50001
- osrm-server: 20.197.105.250:5000
3. Cấu hình VM: 
- vCPUs: 2
- RAM: 4 GiB
- Data disks: 4
- Max IOPS: 1280
- Temp storage: 8 GiB
