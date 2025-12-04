package example.namdinh.listTrip.dto;


import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TripCheckoutRequest {

    // ID chuyến đi cần cập nhật (BẮT BUỘC cho thao tác này)
    private Long tripId;

    // Dữ liệu Check-out
    private LocalDateTime checkoutTimestamp;
    private BigDecimal checkoutLat;
    private BigDecimal checkoutLong;

    // Dữ liệu AI và Tính toán
    private BigDecimal aiConfidenceCheckout;
    private BigDecimal distanceKm;
    private Integer durationSeconds;

    // driverIdAiDetected có thể được gửi lại lúc checkout nếu cần
}