package example.namdinh.registerDriver.controller.web;

// ManualDriverController.java

import example.namdinh.registerDriver.dto.request.DriverRegisterRequest;
import example.namdinh.registerDriver.dto.response.DriverResponse;
import example.namdinh.registerDriver.service.ManualDriverRegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/manual/drivers")
@RequiredArgsConstructor
public class ManualDriverController {

    private final ManualDriverRegistrationService registrationService;


    @PostMapping("/register")
    @PreAuthorize("hasRole('OWNER_LENDER')")
    public ResponseEntity<DriverResponse> register(@RequestBody DriverRegisterRequest request) {
        DriverResponse response = registrationService.registerNewDriver(request);
        return ResponseEntity.ok(response);
    }
}
