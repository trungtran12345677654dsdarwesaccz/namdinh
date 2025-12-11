package example.namdinh.profileDriver.controller;


import example.namdinh.profileDriver.dto.FaceDataForPi;
import example.namdinh.profileDriver.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/scanning/drivers")
@RequiredArgsConstructor
public class PiController {

    private final DriverService driverService;

    @GetMapping("/face-data")
    public ResponseEntity<List<FaceDataForPi>> getDriverFaceDataForPi() {
        // Hàm này sẽ chỉ trả về Driver có isAccountCreated = true VÀ Face data tồn tại.
        List<FaceDataForPi> faceData = driverService.getActivatedDriverFaceData();
        return ResponseEntity.ok(faceData);
    }
}