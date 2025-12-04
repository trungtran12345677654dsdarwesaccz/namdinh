package example.namdinh.profileDriver.service.impl;

import example.namdinh.entity.Driver;

import example.namdinh.profileDriver.dto.DriverUpdateRequest;
import example.namdinh.profileDriver.service.DriverService;
import example.namdinh.registerDriver.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;

    @Override
    public Driver getDriverById(Long driverId) {
        return driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found with ID: " + driverId));
    }

    @Override
    public Driver updateDriver(Long driverId, DriverUpdateRequest request) {
        Driver driver = getDriverById(driverId);

        // Áp dụng các thay đổi từ DTO
        if (request.getDriverName() != null) {
            driver.setDriverName(request.getDriverName());
        }
        if (request.getAge() != null && request.getAge() > 0) {
            driver.setAge(request.getAge());
        }
        if (request.getPhoneNumber() != null) {
            driver.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getFace() != null) {
            // Cảnh báo: Cần logic AI để xác nhận face model ID mới
            driver.setFace(request.getFace());
        }
        if (request.getLicenseImageUrl() != null) {
            driver.setLicenseImageUrl(request.getLicenseImageUrl());
        }

        // Lưu và trả về bản ghi đã cập nhật
        return driverRepository.save(driver);
    }

    @Override
    public void deleteDriver(Long driverId) {
        if (!driverRepository.existsById(driverId)) {
            throw new RuntimeException("Driver not found with ID: " + driverId);
        }
        driverRepository.deleteById(driverId);
    }
    @Override
    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    @Override
    public List<Driver> searchDrivers(String driverName, Integer age, String phoneNumber) {
        return driverRepository.searchDrivers(driverName, age, phoneNumber);
    }
}