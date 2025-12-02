package example.namdinh.entity;

import jakarta.persistence.*;
import lombok.*;

// Driver là một thực thể độc lập, đại diện cho hồ sơ lái xe.
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "DRIVERS")
public class Driver {

    @Id
    @Column(name = "driver_id", length = 50)
    private String driverId;

    @Column(name = "driver_name", length = 100, nullable = false)
    private String driverName;

    @Column(name = "age", length = 100, nullable = false)
    private int age;

    @Column(name = "license_number", length = 50)
    private String licenseNumber;

    @Column(name = "is_account_created", nullable = false)
    private boolean isAccountCreated = false;

    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    @Column(name = "face_model_id", length = 255)
    private String face;

    @Column(name = "license_image_url", length = 255)
    private String licenseImageUrl;

}
