package example.namdinh.vehicle.controller;

import example.namdinh.entity.User;
import example.namdinh.vehicle.dto.VehicleCreateRequest;
import example.namdinh.vehicle.dto.VehicleResponse;
import example.namdinh.vehicle.service.VehicleService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
@PreAuthorize("hasRole('OWNER_LENDER')")
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping
    public ResponseEntity<?> createNewVehicle(
            @RequestBody VehicleCreateRequest request,
            @AuthenticationPrincipal User currentUser) {
        try {
            VehicleResponse response = vehicleService.createVehicle(request, currentUser);
            return ResponseEntity.ok(response);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}