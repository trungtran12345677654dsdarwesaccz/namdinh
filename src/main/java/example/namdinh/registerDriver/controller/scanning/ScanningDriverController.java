package example.namdinh.registerDriver.controller.scanning;

// ScanningDriverController.java

import example.namdinh.registerDriver.dto.request.DriverCompletionRequest;
import example.namdinh.registerDriver.dto.request.DriverInitRequest;
import example.namdinh.registerDriver.dto.response.DriverResponse;
import example.namdinh.registerDriver.service.ScanningDriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/scanning/drivers")
@RequiredArgsConstructor
public class ScanningDriverController {

    private final ScanningDriverService scanningService;

    // 1. Endpoint Dành cho thiết bị IoT
    @PostMapping("/init")
    public ResponseEntity<String> initializeDriver(@RequestBody DriverInitRequest request) {
        String driverId = scanningService.initializeDriver(request);
        return ResponseEntity.ok("Driver initialization record created with ID: " + driverId);
    }

    // 2. Endpoint Dành cho Web/User hoàn tất đăng ký
    @PostMapping("/complete")
    public ResponseEntity<DriverResponse> completeRegistration(@RequestBody DriverCompletionRequest request) {
        DriverResponse response = scanningService.completeRegistration(request);
        return ResponseEntity.ok(response);
    }
}