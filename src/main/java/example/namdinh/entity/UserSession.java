package example.namdinh.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_sessions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Transient
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "last_accessed_at")
    private LocalDateTime lastAccessedAt;

    private boolean active;
}
