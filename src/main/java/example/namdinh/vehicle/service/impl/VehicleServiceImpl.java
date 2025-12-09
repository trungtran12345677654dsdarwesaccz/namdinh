package example.namdinh.vehicle.service.impl;

import example.namdinh.entity.User;
import example.namdinh.entity.Vehicle;
import example.namdinh.vehicle.dto.VehicleCreateRequest;
import example.namdinh.vehicle.dto.VehicleResponse;
import example.namdinh.vehicle.repository.VehicleRepository;
import example.namdinh.vehicle.service.VehicleService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;

    @Override
    public VehicleResponse createVehicle(VehicleCreateRequest request, User currentUser) {

        // 1. KIỂM TRA RÀNG BUỘC 1-1
        if (currentUser.getVehicles() != null) {
            throw new IllegalStateException("User ID " + currentUser.getId() + " đã sở hữu một Vehicle.");
        }
        if (currentUser == null) {
            throw new IllegalStateException("Current user entity is null. Cannot proceed with vehicle creation.");
        }
        // END: THÊM KIỂM TRA BẢO VỆ


        // 2. Kiểm tra ràng buộc duy nhất (Biển số & Camera ID)
        if (vehicleRepository.findByLicensePlate(request.getLicensePlate()).isPresent()) {
            throw new IllegalArgumentException("Biển số xe đã tồn tại.");
        }
        if (vehicleRepository.findByCameraId(request.getCameraId()).isPresent()) {
            throw new IllegalArgumentException("Camera ID đã tồn tại.");
        }

        // 3. TẠO VÀ LƯU VEHICLE
        String generatedVehicleId = request.getLicensePlate().replace(" ", "") + "-" + currentUser.getId();

        Vehicle newVehicle = Vehicle.builder()
                .vehicleId(generatedVehicleId)
                .licensePlate(request.getLicensePlate())
                .cameraId(request.getCameraId())
                .owner(currentUser) // Liên kết với User hiện tại
                .build();

        newVehicle = vehicleRepository.save(newVehicle);

        // CẬP NHẬT LẠI ENTITY USER (Tùy chọn, nếu Entity User không tự cập nhật)
        // currentUser.setVehicles(newVehicle);

        return VehicleResponse.builder()
                .vehicleId(newVehicle.getVehicleId())
                .licensePlate(newVehicle.getLicensePlate())
                .ownerId(currentUser.getId())
                .message("Vehicle created and linked to user successfully.")
                .build();
    }
    @Override
    public VehicleResponse listVehicles(User currentUser) {
        Vehicle vehicle = currentUser.getVehicles();

        if (vehicle == null) {
            return VehicleResponse.builder()
                    .ownerId(currentUser.getId())
                    .message("User hiện tại chưa sở hữu phương tiện nào.")
                    .build();
        }

        return VehicleResponse.builder()
                .vehicleId(vehicle.getVehicleId())
                .licensePlate(vehicle.getLicensePlate())
                .ownerId(currentUser.getId())
                .message("Thông tin phương tiện hiện tại.")
                .build();
    }
}