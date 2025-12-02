// DriverInitRequest.java (Từ Raspberry Pi/IoT)
package example.namdinh.registerDriver.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class DriverInitRequest {
    @NotBlank
    private String face;
}