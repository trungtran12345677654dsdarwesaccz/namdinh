package example.namdinh.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import example.namdinh.enumeration.UserRole;
import example.namdinh.enumeration.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "user")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class User  implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId; // ID duy nhất của người dùng/chủ xe (Khóa Chính, Tự tăng)

    @Column(name = "username", length = 50, nullable = false, unique = true)
    private String username; // Tên đăng nhập (Duy nhất)

    @Column(name = "password_hash", length = 255, nullable = false)
    private String password; // Mã hóa mật khẩu

    @Column(name = "full_name", length = 100)
    private String fullName; // Tên đầy đủ của chủ xe

    @Column(name = "email", length = 100)
    private String email; // Tên đầy đủ của chủ xe

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Column(name = "phone", length = 100)
    private String phone; // Tên đầy đủ của chủ xe

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 20)
    private UserRole role;

    @Column(name = "last_password_reset_date")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime lastPasswordResetDate;

    // 1. Mối quan hệ 1-n: Danh sách xe thuộc sở hữu
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private java.util.Set<Vehicle> vehicles; // Danh sách xe thuộc sở hữu

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + this.role.name()));
    }

    @Override
    public boolean isEnabled() {

        return this.status == UserStatus.ACTIVE;
    }

}
