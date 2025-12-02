package example.namdinh.registerDriver.service;

// ManualDriverRegistrationService.java (Interface)
import example.namdinh.registerDriver.dto.request.DriverRegisterRequest;
import example.namdinh.registerDriver.dto.response.DriverResponse;

public interface ManualDriverRegistrationService {
    DriverResponse registerNewDriver(DriverRegisterRequest request);
}