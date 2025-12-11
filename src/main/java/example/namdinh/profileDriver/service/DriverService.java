package example.namdinh.profileDriver.service;

import example.namdinh.entity.Driver;
import example.namdinh.profileDriver.dto.DriverUpdateRequest;

import java.util.List;

public interface DriverService {
    // Cập nhật thông tin Driver
    Driver updateDriver(Long driverId, DriverUpdateRequest request);

    // Xóa Driver
    void deleteDriver(Long driverId);

    // Tìm kiếm Driver (Hữu ích cho Controller)
    Driver getDriverById(Long driverId);
    // 1. Liệt kê tất cả
    List<Driver> getAllDrivers();
    List<Driver> searchDrivers(String driverName, Integer age, String phoneNumber);
}