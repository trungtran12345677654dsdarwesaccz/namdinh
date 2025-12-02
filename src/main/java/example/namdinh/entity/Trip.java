package example.namdinh.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "TRIPS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trip_id")
    private Long tripId; // ID duy nhất của chuyến đi (Khóa Chính, Tự tăng)

    // Khóa ngoại
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle; // Xe thực hiện chuyến đi

    @Column(name = "checkin_timestamp", nullable = false)
    private LocalDateTime checkinTimestamp; // Thời điểm chính xác lên xe

    @Column(name = "checkin_lat", precision = 10, scale = 8, nullable = false)
    private BigDecimal checkinLat; // Vĩ độ lúc lên xe

    @Column(name = "checkin_long", precision = 11, scale = 8, nullable = false)
    private BigDecimal checkinLong; // Kinh độ lúc lên xe

    @Column(name = "checkout_timestamp")
    private LocalDateTime checkoutTimestamp;

    @Column(name = "checkout_lat", precision = 10, scale = 8)
    private BigDecimal checkoutLat;

    @Column(name = "checkout_long", precision = 11, scale = 8)
    private BigDecimal checkoutLong;

    // Dữ liệu Nhận dạng Người lái (FK tới Driver)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id_ai_detected")
    private Driver driverAiDetected; // ID Người lái AI nhận dạng hợp nhất

    @Column(name = "ai_confidence_checkin", precision = 4, scale = 3)
    private BigDecimal aiConfidenceCheckin; // Độ tin cậy AI lúc lên xe

    @Column(name = "ai_confidence_checkout", precision = 4, scale = 3)
    private BigDecimal aiConfidenceCheckout; // Độ tin cậy AI lúc xuống xe

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id_final")
    private Driver driverFinal; // ID Người lái đã được xác nhận cuối cùng

    // Dữ liệu Tính toán
    @Column(name = "distance_km", precision = 10, scale = 2)
    private BigDecimal distanceKm; // Quãng đường di chuyển

    @Column(name = "duration_seconds")
    private Integer durationSeconds; // Thời gian di chuyển (giây)


}
