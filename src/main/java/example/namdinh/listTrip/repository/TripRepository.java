package example.namdinh.listTrip.repository;

import example.namdinh.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {


    List<Trip> findByCheckoutTimestampIsNotNull();


    List<Trip> findByVehicleVehicleIdAndCheckoutTimestampIsNull(String vehicleId);
}