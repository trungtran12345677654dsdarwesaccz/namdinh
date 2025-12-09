package example.namdinh.vehicle.service;
import example.namdinh.entity.User;
import example.namdinh.vehicle.dto.VehicleCreateRequest;
import example.namdinh.vehicle.dto.VehicleResponse;

public interface VehicleService {
    VehicleResponse listVehicles(User currentUser);
    VehicleResponse createVehicle(VehicleCreateRequest request, User currentUser);
}