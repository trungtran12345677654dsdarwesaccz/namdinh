package example.namdinh.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "VEHICLES")
public class Vehicle {

    @Id
    @Column(name = "vehicle_id", length = 50)
    private String vehicleId; // Mã định danh duy nhất của xe (Khóa Chính)

    @Column(name = "license_plate", length = 15, nullable = false, unique = true)
    private String licensePlate; // Biển số xe

    @Column(name = "vehicle_type", length = 50)
    private String vehicleType; // Loại xe

    @Column(name = "camera_id", length = 50, nullable = false, unique = true)
    private String cameraId; // Mã ID của camera gắn trên xe

    @Column(name = "installation_date")
    private LocalDate installationDate; // Ngày lắp đặt thiết bị

    // Mối quan hệ Many-to-One: Liên kết với Chủ sở hữu
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // Trường user_id trong bảng VEHICLES (Khóa ngoại)
    private User owner; // Chủ sở hữu của chiếc xe này

}
