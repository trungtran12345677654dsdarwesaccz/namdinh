// DriverRepository.java
package example.namdinh.registerDriver.repository;

import example.namdinh.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, String> {
    boolean existsByface(String face);
    Optional<Driver> findByface(String face);
}