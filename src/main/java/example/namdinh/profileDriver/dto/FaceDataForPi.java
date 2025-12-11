package example.namdinh.profileDriver.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FaceDataForPi {
    private Long driverId;
    private String driverName;
    private String face;
}