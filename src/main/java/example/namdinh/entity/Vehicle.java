package example.namdinh.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
@Entity
@Table(name = "vehicles")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {

    @Id
    @Column(name = "vehicle_id", length = 50, nullable = false)
    private String vehicleId; // Ví dụ: Biển kiểm soát + hậu tố

    @Column(name = "license_plate", length = 15, nullable = false, unique = true)
    private String licensePlate; // Biển số xe (Unique)

    @Column(name = "camera_id", length = 50, nullable = false, unique = true)
    private String cameraId;

    @OneToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn(name = "user_id", unique = true) // Cột FK trong bảng vehicles
    private User owner;
}
