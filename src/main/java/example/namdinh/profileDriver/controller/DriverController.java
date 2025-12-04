package example.namdinh.profileDriver.controller;

// src/main/java/example/namdinh/controller/DriverController.java


import example.namdinh.entity.Driver;
import example.namdinh.profileDriver.dto.DriverUpdateRequest;
import example.namdinh.profileDriver.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drivers/admin")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    // Lấy thông tin Driver
    @GetMapping("/{driverId}")
    @PreAuthorize("hasAnyRole('OWNER_LENDER')")
    public ResponseEntity<Driver> getDriver(@PathVariable String driverId) {
        Driver driver = driverService.getDriverById(driverId);
        return ResponseEntity.ok(driver);
    }

    // Cập nhật thông tin Driver (PUT: Cập nhật toàn bộ/PATCH: Cập nhật một phần)
    @PatchMapping("/{driverId}")
    @PreAuthorize("hasRole('OWNER_LENDER')")
    public ResponseEntity<Driver> updateDriver(
            @PathVariable String driverId,
            @RequestBody DriverUpdateRequest request) {

        Driver updatedDriver = driverService.updateDriver(driverId, request);
        return ResponseEntity.ok(updatedDriver);
    }

    // Xóa Driver
    @DeleteMapping("/{driverId}")
    @PreAuthorize("hasRole('OWNER_LENDER')")
    public ResponseEntity<Void> deleteDriver(@PathVariable String driverId) {
        driverService.deleteDriver(driverId);
        return ResponseEntity.noContent().build();
    }
    // 1. Lấy tất cả Driver
    // Đường dẫn: GET /api/drivers/admin
    @GetMapping
    @PreAuthorize("hasRole('OWNER_LENDER')")
    public ResponseEntity<List<Driver>> getAllDrivers() {
        List<Driver> drivers = driverService.getAllDrivers();
        return ResponseEntity.ok(drivers);
    }

    // 2. Tìm kiếm Driver theo tên, tuổi, SĐT
    // Đường dẫn: GET /api/drivers/admin/search?driverName=...&age=...&phoneNumber=...
    @GetMapping("/search")
    @PreAuthorize("hasRole('OWNER_LENDER')")
    public ResponseEntity<List<Driver>> searchDrivers(
            @RequestParam(required = false) String driverName,
            @RequestParam(required = false) Integer age,
            @RequestParam(required = false) String phoneNumber) {

        List<Driver> drivers = driverService.searchDrivers(driverName, age, phoneNumber);
        return ResponseEntity.ok(drivers);
    }
}
