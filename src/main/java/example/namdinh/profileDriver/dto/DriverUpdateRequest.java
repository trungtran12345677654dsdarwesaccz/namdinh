package example.namdinh.profileDriver.dto;

// src/main/java/example/namdinh/dto/request/DriverUpdateRequest.java

import lombok.Data;

@Data
public class DriverUpdateRequest {
    private String driverName;
    private Integer age;
    private String licenseNumber;
    private String phoneNumber;
    private String face; // face_model_id mới
    private String licenseImageUrl;
}