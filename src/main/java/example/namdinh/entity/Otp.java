package example.namdinh.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import example.namdinh.enumeration.OtpStatus;
import jakarta.persistence.*;
import lombok.*;
// KHÔNG CẦN SỬ DỤNG @CreationTimestamp, @UpdateTimestamp KHI DÙNG @PrePersist/PreUpdate
// import org.hibernate.annotations.CreationTimestamp;
// import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity
// Bổ sung: Thêm ràng buộc UNIQUE cho user_id (nếu bạn muốn 1 user chỉ có 1 OTP hiện tại)
@Table(name = "otp", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id"})
})
public class Otp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 100, nullable = false)
    private String email;


    @Column(name = "otp_created_date", nullable = false, updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime createdDate;


    @Column(name = "updated_date", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime updatedDate;


    @Column(name = "expired_time", nullable = false)
    private LocalDateTime expiredTime;

    @Column(length = 20, nullable = false)
    private String otp;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OtpStatus status;


    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdDate == null) createdDate = now;
        if (updatedDate == null) updatedDate = now;
        if (expiredTime == null) expiredTime = now.plusMinutes(1);
        if (status == null) status = OtpStatus.PENDING;
    }

    @PreUpdate
    protected void onUpdate() {
        // Chỉ cập nhật updatedDate
        updatedDate = LocalDateTime.now();
    }
}