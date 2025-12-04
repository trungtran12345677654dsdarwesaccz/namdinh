package example.namdinh.listTrip.dto;


import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@Builder
public class TripResponse {

    private Long tripId;
    private String vehicleId;
    private String finalDriverName;
    private LocalDateTime checkinTime;
    private LocalDateTime checkoutTime;
    private BigDecimal distanceKm;
    private Integer durationSeconds;
    private String status;
}