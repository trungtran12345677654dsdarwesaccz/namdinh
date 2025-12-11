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

@Service
@RequiredArgsConstructor
@Transactional
public class ScanningDriverServiceImpl implements ScanningDriverService {
    private final DriverRepository driverRepository;

    @Override
    public String initializeDriver(DriverInitRequest request) {
//        if (driverRepository.existsByface(request.getFace())) {
//            throw new RuntimeException("Face model already registered.");
//        }
        if (request.isAccountCreated() == true) {
            throw new RuntimeException("Face model already registered.");
        }
        if (driverRepository.existsByface(request.getFace())) {
                throw new RuntimeException("This face link already exists.");
             }
        Driver driver = Driver.builder()
                .face(request.getFace())
                .isAccountCreated(false)
                .driverName(null)
                .age(1)
                .build();

        Driver savedDriver = driverRepository.save(driver);

        return String.valueOf(savedDriver.getDriverId());
    }

    @Override
    public DriverResponse completeRegistration(DriverCompletionRequest request) {

        Driver driver = driverRepository
                .findByFaceAndIsAccountCreated(request.getFace(), false)
                .orElseThrow(() -> new RuntimeException("Driver initialization record not found or profile already completed."));

        // NOTE: Tại đây, driver.getFace() là giá trị Face Model ID/link ảnh đã được lưu trong DB.

        driver.setDriverName(request.getDriverName());
        driver.setAge(request.getAge());
        driver.setPhoneNumber(request.getPhoneNumber());
        driver.setLicenseImageUrl(request.getLicenseImageUrl());

        // 3. Đánh dấu trạng thái hoàn tất
        driver.setAccountCreated(true); // Chuyển trạng thái sang TRUE

        Driver savedDriver = driverRepository.save(driver);

        // 4. Trả về Response
        // (savedDriver.getFace() sẽ trả về giá trị ban đầu được lưu trong DB)
        return DriverResponse.builder()
                .driverId(savedDriver.getDriverId())
                .driverName(savedDriver.getDriverName())
                .isAccountCreated(savedDriver.isAccountCreated())
                .message("Driver profile registration completed successfully.")
                .build();
    }

}