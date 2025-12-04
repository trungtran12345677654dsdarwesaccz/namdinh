// DriverResponse.java
package example.namdinh.registerDriver.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DriverResponse {
    private Long driverId;
    private String driverName;
    private String licenseNumber;
    private boolean isAccountCreated;
    private String message;
}