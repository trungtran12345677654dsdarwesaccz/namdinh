package example.namdinh.listTrip.controller;


import example.namdinh.listTrip.dto.TripCheckinRequest;
import example.namdinh.listTrip.dto.TripCheckoutRequest;
import example.namdinh.listTrip.dto.TripResponse;
import example.namdinh.listTrip.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService; // Inject Interface

    // --------------------------------------------------------
    // A. NHẬN DATA TỪ PI (IOT)
    // --------------------------------------------------------

    @PostMapping("/checkin")
    public ResponseEntity<String> checkIn(@RequestBody TripCheckinRequest request) {
        try {
            Long tripId = tripService.initializeTrip(request);
            return ResponseEntity.ok("Trip started successfully. ID: " + tripId);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/checkout")
    public ResponseEntity<String> checkOut(@RequestBody TripCheckoutRequest request) {
        try {
            tripService.completeTrip(request);
            return ResponseEntity.ok("Trip " + request.getTripId() + " completed successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // --------------------------------------------------------
    // B. LIST DANH SÁCH TRIP
    // --------------------------------------------------------

    @GetMapping("/completed")
    public ResponseEntity<List<TripResponse>> getCompletedTrips() {
        List<TripResponse> trips = tripService.getCompletedTrips();
        return ResponseEntity.ok(trips);
    }
}