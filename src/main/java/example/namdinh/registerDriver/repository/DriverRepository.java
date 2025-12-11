// DriverRepository.java
package example.namdinh.registerDriver.repository;

import example.namdinh.entity.Driver;
import example.namdinh.registerDriver.dto.response.FaceMapResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, Long> {
    boolean existsByface(String face);

    // 4. Tìm kiếm tổng hợp theo Tên, Tuổi, hoặc SĐT
    @Query("SELECT d FROM Driver d WHERE " +
            "(:driverName IS NULL OR LOWER(d.driverName) LIKE LOWER(CONCAT('%', :driverName, '%'))) AND " +
            "(:age IS NULL OR d.age = :age) AND " +
            "(:phoneNumber IS NULL OR d.phoneNumber = :phoneNumber)")
    List<Driver> searchDrivers(
            @Param("driverName") String driverName,
            @Param("age") Integer age,
            @Param("phoneNumber") String phoneNumber);
    Optional<Driver> findByFaceAndIsAccountCreated(String face, boolean isAccountCreated);
    @Query("SELECT d.face FROM Driver d")
    List<String> findAllFaces();
    // Phương thức 1: Lấy danh sách Driver ID, Face Model và Status đã được kích hoạt
    @Query("SELECT new example.namdinh.registerDriver.dto.response.FaceMapResponse(d.driverId, d.face, d.isAccountCreated) " +
            "FROM Driver d WHERE d.face IS NOT NULL AND d.isAccountCreated = true")
    List<FaceMapResponse> findAllActivatedFaceMaps();

    // Phương thức 2: Tìm theo Face Model
    Optional<Driver> findByFace(String face);

}