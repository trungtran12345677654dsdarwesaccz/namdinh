// DriverCompletionRequest.java (Hoàn tất Hồ sơ Quét mặt - Web)
package example.namdinh.registerDriver.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

@Data
public class DriverCompletionRequest {
    @NotBlank
    private String face; // Dùng để tìm bản ghi đã khởi tạo
    @NotBlank
    private String driverId;
    @NotBlank
    private String driverName;
    @NotNull @Min(18)
    private Integer age;
    @NotBlank
    private String licenseNumber;
    private String phoneNumber;
    private String licenseImageUrl;
}