package example.namdinh.listTrip.dto;


import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TripCheckinRequest {
    private String cameraId;
    private String vehicleId;
    private Long driverIdAiDetected;

    // Dữ liệu BẮT BUỘC (nullable = false trong Entity)
    private LocalDateTime checkinTimestamp;
    private BigDecimal checkinLat;
    private BigDecimal checkinLong;

    // Dữ liệu AI
    private BigDecimal aiConfidenceCheckin;
}