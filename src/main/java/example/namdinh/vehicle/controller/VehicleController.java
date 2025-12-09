package example.namdinh.vehicle.controller;

import example.namdinh.entity.User;
import example.namdinh.repository.UserRepository;
import example.namdinh.vehicle.dto.VehicleCreateRequest;
import example.namdinh.vehicle.dto.VehicleResponse;
import example.namdinh.vehicle.service.VehicleService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_OWNER_LENDER')")
public class VehicleController {

    private final VehicleService vehicleService;
    private final UserRepository userRepository;
    @PostMapping
    public ResponseEntity<?> createNewVehicle(
            @RequestBody VehicleCreateRequest request,
            @AuthenticationPrincipal UserDetails principal) {

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Xác thực người dùng không hợp lệ.");
        }

        try {

            String email = principal.getUsername();
            User currentUser = userRepository.findByEmail(email).orElse(null);
            // Giả sử userRepository được tiêm (inject) vào Controller

            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy thông tin người dùng trong cơ sở dữ liệu.");
            }

            VehicleResponse response = vehicleService.createVehicle(request, currentUser);
            return ResponseEntity.ok(response);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi hệ thống không mong muốn: " + e.getMessage());
        }
    }

    // Trong VehicleController.java (Giả sử bạn đã tiêm UserRepository)
    @GetMapping
    public ResponseEntity<?> listVehicles(
            @AuthenticationPrincipal UserDetails principal) { // Sử dụng UserDetails

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Xác thực người dùng không hợp lệ.");
        }

        try {
            // Lấy email từ principal
            String email = principal.getUsername();

            // Tải thực thể User đầy đủ từ Database
            // Giả sử userRepository được tiêm (inject)
            User currentUser = userRepository.findByEmail(email).orElse(null);

            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy thông tin người dùng trong cơ sở dữ liệu.");
            }
            VehicleResponse response = vehicleService.listVehicles(currentUser);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi hệ thống không mong muốn: " + e.getMessage());
        }
    }
}
