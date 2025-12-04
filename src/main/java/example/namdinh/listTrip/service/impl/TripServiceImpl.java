package example.namdinh.listTrip.service.impl;

import example.namdinh.entity.Trip;


import example.namdinh.entity.Vehicle;
import example.namdinh.listTrip.dto.TripCheckinRequest;
import example.namdinh.listTrip.dto.TripCheckoutRequest;
import example.namdinh.listTrip.dto.TripResponse;
import example.namdinh.listTrip.repository.TripRepository;
import example.namdinh.listTrip.service.TripService;
import example.namdinh.registerDriver.repository.DriverRepository;
import example.namdinh.vehicle.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TripServiceImpl implements TripService {

    private final TripRepository tripRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;

    // --- Mapper Utility ---
    private TripResponse mapToResponse(Trip trip) {
        // ... (Logic map đơn giản)
        String driverName = trip.getDriverFinal() != null ? trip.getDriverFinal().getDriverName() : "Chưa xác nhận";
        String status = trip.getCheckoutTimestamp() != null ? "COMPLETED" : "ACTIVE";

        return TripResponse.builder()
                .tripId(trip.getTripId())
                .vehicleId(trip.getVehicle().getVehicleId())
                .checkinTime(trip.getCheckinTimestamp())
                .checkoutTime(trip.getCheckoutTimestamp())
                .distanceKm(trip.getDistanceKm())
                .durationSeconds(trip.getDurationSeconds())
                .finalDriverName(driverName)
                .status(status)
                .build();
    }

    // =========================================================
    // TRIỂN KHAI NHẬN DATA TỪ PI (CHECK-IN)
    // =========================================================

    @Override
    public Long initializeTrip(TripCheckinRequest request) {

        // 1. TÌM VEHICLE BẰNG Camera ID (Dữ liệu từ Pi)
        // Dùng findByCameraId() vì Pi gửi Camera ID
        Vehicle vehicle = vehicleRepository.findByCameraId(request.getCameraId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Vehicle với Camera ID: " + request.getCameraId()));

        // 2. KIỂM TRA RÀNG BUỘC 1-TRIP/XE
        // Tìm các chuyến đi đang mở (chưa Check-out) dựa trên vehicleId đã tìm được
        List<Trip> activeTrips = tripRepository.findByVehicleVehicleIdAndCheckoutTimestampIsNull(vehicle.getVehicleId());

        if (!activeTrips.isEmpty()) {
            // Ràng buộc 1 xe chỉ có 1 trip tại 1 thời điểm
            throw new IllegalStateException("Xe " + vehicle.getLicensePlate() + " đang có chuyến đi mở. Hãy Checkout chuyến ID: " + activeTrips.get(0).getTripId() + " trước.");
        }

        // 3. TÌM DRIVER AI (Nếu có)
        var driverAi = driverRepository.findById(request.getDriverIdAiDetected()).orElse(null);

        // 4. TẠO TRIP MỚI (Check-in)
        var trip = Trip.builder()
                .vehicle(vehicle) // Gắn Vehicle đã tìm thấy
                .checkinTimestamp(request.getCheckinTimestamp())
                .checkinLat(request.getCheckinLat())
                .checkinLong(request.getCheckinLong())
                .driverAiDetected(driverAi)
                .aiConfidenceCheckin(request.getAiConfidenceCheckin())
                .build();

        trip = tripRepository.save(trip);
        return trip.getTripId();
    }

    // =========================================================
    // TRIỂN KHAI NHẬN DATA TỪ PI (CHECK-OUT) VÀ TÍNH TOÁN
    // =========================================================

    @Override
    public void completeTrip(TripCheckoutRequest request) {
        var trip = tripRepository.findById(request.getTripId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Trip ID: " + request.getTripId()));

        if (trip.getCheckoutTimestamp() != null) {
            throw new IllegalStateException("Trip " + request.getTripId() + " đã hoàn tất rồi.");
        }

        // 1. Cập nhật các trường Pi gửi về
        trip.setCheckoutTimestamp(request.getCheckoutTimestamp());
        trip.setCheckoutLat(request.getCheckoutLat());
        trip.setCheckoutLong(request.getCheckoutLong());
        trip.setAiConfidenceCheckout(request.getAiConfidenceCheckout());

        // 2. TÍNH TOÁN 1: THỜI GIAN DI CHUYỂN (durationSeconds)
        long seconds = Duration.between(trip.getCheckinTimestamp(), trip.getCheckoutTimestamp()).getSeconds();
        trip.setDurationSeconds((int) seconds);

        // 3. TÍNH TOÁN 2: KHOẢNG CÁCH (distanceKm)
        BigDecimal distance = calculateHaversineDistance(
                trip.getCheckinLat().doubleValue(),
                trip.getCheckinLong().doubleValue(),
                trip.getCheckoutLat().doubleValue(),
                trip.getCheckoutLong().doubleValue()
        );

        trip.setDistanceKm(distance);

        tripRepository.save(trip);
    }

    // =========================================================
    // TRIỂN KHAI LIST DANH SÁCH TRIP
    // =========================================================

    @Override
    public List<TripResponse> getCompletedTrips() {
        List<Trip> completedTrips = tripRepository.findByCheckoutTimestampIsNotNull();

        return completedTrips.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // =========================================================
    // HÀM TÍNH TOÁN KHOẢNG CÁCH HAVERSINE
    // =========================================================
    private BigDecimal calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distanceKm = R * c;

        return BigDecimal.valueOf(distanceKm)
                .setScale(2, RoundingMode.HALF_UP);
    }
}