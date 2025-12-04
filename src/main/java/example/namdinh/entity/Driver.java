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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "driver_id")
    private Long driverId;

    @Column(name = "driver_name", length = 100)
    private String driverName;

    @Column(name = "age", length = 100)
    private int age;


    @Column(name = "is_account_created", nullable = false)
    private boolean isAccountCreated = false;

    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    @Column(name = "face_model_id", length = 255)
    private String face;

    @Column(name = "license_image_url", length = 255)
    private String licenseImageUrl;

}
