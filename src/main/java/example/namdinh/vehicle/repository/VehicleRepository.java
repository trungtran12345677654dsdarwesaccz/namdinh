package example.namdinh.vehicle.repository;


import example.namdinh.entity.Vehicle; // Giả định Vehicle entity nằm ở đây
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
// Key type là String (vehicleId)
public interface VehicleRepository extends JpaRepository<Vehicle, String> {

    // Tìm Vehicle theo ID chủ sở hữu (user_id)
    Optional<Vehicle> findByOwnerId(Long ownerId);

    // Tìm Vehicle theo Camera ID (unique)
    Optional<Vehicle> findByCameraId(String cameraId);

    // Tìm Vehicle theo Biển số xe (unique)
    Optional<Vehicle> findByLicensePlate(String licensePlate);
}