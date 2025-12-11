// DriverRepository.java
package example.namdinh.registerDriver.repository;

import example.namdinh.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, Long> {
    boolean existsByface(String face);
    Optional<Driver> findByface(String face);
    // 1. Tìm kiếm theo Tên (không phân biệt chữ hoa/thường và chứa một phần)
    List<Driver> findByDriverNameContainingIgnoreCase(String driverName);

    // 2. Tìm kiếm theo Tuổi (chính xác)
    List<Driver> findByAge(int age);

    // 3. Tìm kiếm theo Số điện thoại (chính xác)
    List<Driver> findByPhoneNumber(String phoneNumber);

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
    List<Driver> findByIsAccountCreatedTrueAndFaceIsNotNull();
}