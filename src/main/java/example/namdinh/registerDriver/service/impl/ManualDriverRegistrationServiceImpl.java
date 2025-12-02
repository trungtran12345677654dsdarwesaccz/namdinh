package example.namdinh.registerDriver.service.impl;

// ManualDriverRegistrationServiceImpl.java (Implementation)

import example.namdinh.registerDriver.dto.request.DriverRegisterRequest;
import example.namdinh.registerDriver.dto.response.DriverResponse;
import example.namdinh.entity.Driver;
import example.namdinh.registerDriver.repository.DriverRepository;
import example.namdinh.registerDriver.service.ManualDriverRegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ManualDriverRegistrationServiceImpl implements ManualDriverRegistrationService {
    private final DriverRepository driverRepository;
    // private final UserService userService; // Cần thiết nếu liên kết User

    @Override
    public DriverResponse registerNewDriver(DriverRegisterRequest request) {
        // 1. Kiểm tra driverId và licenseNumber đã tồn tại chưa (logic kiểm tra bổ sung)
        // 2. Tạo Driver Entity
        Driver driver = Driver.builder()
                .driverId(request.getDriverId())
                .driverName(request.getDriverName())
                .age(request.getAge())
                .licenseNumber(request.getLicenseNumber())
                .licenseImageUrl(request.getLicenseImageUrl())
                .phoneNumber(request.getPhoneNumber())
                .face(request.getFace() != null ? request.getFace() : UUID.randomUUID().toString()) // Gán tạm nếu không có
                .isAccountCreated(true) // Đăng ký thủ công -> tài khoản được tạo
                .build();

        Driver savedDriver = driverRepository.save(driver);

        return DriverResponse.builder()
                .driverId(savedDriver.getDriverId())
                .driverName(savedDriver.getDriverName())
                .isAccountCreated(savedDriver.isAccountCreated())
                .message("Driver profile created successfully via manual registration.")
                .build();
    }
}