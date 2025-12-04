package example.namdinh.vehicle.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VehicleResponse {
    private String vehicleId;
    private String licensePlate;
    private Long ownerId;
    private String message;
}