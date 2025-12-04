// DriverRegisterRequest.java (Đăng ký Thủ công - Web)
package example.namdinh.registerDriver.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

@Data
public class DriverRegisterRequest {
    @NotBlank
    private Long driverId;
    @NotBlank
    private String driverName;
    @NotNull @Min(18)
    private Integer age;
    private String phoneNumber;
    private String face;
    private String licenseImageUrl;
}