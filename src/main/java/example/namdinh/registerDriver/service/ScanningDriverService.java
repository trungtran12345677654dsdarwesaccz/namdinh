package example.namdinh.registerDriver.service;

// ScanningDriverService.java (Interface)

import example.namdinh.registerDriver.dto.request.DriverCompletionRequest;
import example.namdinh.registerDriver.dto.request.DriverInitRequest;
import example.namdinh.registerDriver.dto.response.DriverResponse;

import java.util.List;

public interface ScanningDriverService {
    // 1. Phương thức khởi tạo hồ sơ (gọi từ Raspberry Pi/IoT)
    String initializeDriver(DriverInitRequest request);
    List<String> getAllDriverFaces();
    // 2. Phương thức hoàn tất hồ sơ (gọi từ Web)
    DriverResponse completeRegistration(DriverCompletionRequest request);
}