package example.namdinh.vehicle.dto;


import lombok.Data;

@Data
public class VehicleCreateRequest {
    private String licensePlate;
    private String cameraId;
}