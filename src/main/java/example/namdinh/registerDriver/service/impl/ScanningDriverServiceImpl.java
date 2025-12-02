package example.namdinh.registerDriver.service.impl;

// ScanningDriverServiceImpl.java (Implementation)

import example.namdinh.registerDriver.dto.request.DriverCompletionRequest;
import example.namdinh.registerDriver.dto.request.DriverInitRequest;
import example.namdinh.registerDriver.dto.response.DriverResponse;
import example.namdinh.entity.Driver;
import example.namdinh.registerDriver.repository.DriverRepository;
import example.namdinh.registerDriver.service.ScanningDriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ScanningDriverServiceImpl implements ScanningDriverService {
    private final DriverRepository driverRepository;

    @Override
    public String initializeDriver(DriverInitRequest request) {
        if (driverRepository.existsByface(request.getFace())) {
            // Có thể trả về ID Driver đã tồn tại nếu muốn cho phép cập nhật
            throw new RuntimeException("Face model already registered.");
        }

        Driver driver = Driver.builder()
                .driverId(UUID.randomUUID().toString()) // ID tạm thời
                .face(request.getFace())
                .isAccountCreated(false) // Trạng thái: Chờ hoàn tất
                .driverName("PENDING")
                .age(0)
                .licenseNumber("PENDING")
                .build();

        driverRepository.save(driver);
        return driver.getDriverId();
    }

    @Override
    public DriverResponse completeRegistration(DriverCompletionRequest request) {
        Driver driver = driverRepository.findByface(request.getFace())
                .orElseThrow(() -> new RuntimeException("Driver initialization record not found."));

        if (driver.isAccountCreated()) {
            throw new RuntimeException("Driver profile already completed.");
        }

        // Cập nhật các trường còn thiếu
        driver.setDriverId(request.getDriverId());
        driver.setDriverName(request.getDriverName());
        driver.setAge(request.getAge());
        driver.setLicenseNumber(request.getLicenseNumber());
        driver.setLicenseImageUrl(request.getLicenseImageUrl());
        driver.setAccountCreated(true); // Đã hoàn tất đăng ký/liên kết tài khoản

        Driver savedDriver = driverRepository.save(driver);

        return DriverResponse.builder()
                .driverId(savedDriver.getDriverId())
                .driverName(savedDriver.getDriverName())
                .isAccountCreated(savedDriver.isAccountCreated())
                .message("Driver profile registration completed successfully.")
                .build();
    }
}