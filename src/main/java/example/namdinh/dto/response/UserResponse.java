package example.namdinh.dto.response;
import example.namdinh.enumeration.UserRole;
import example.namdinh.enumeration.UserStatus;
import jakarta.persistence.Enumerated;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private String userName;
    private String fullName;
    private String password;
    private String email;
    @Enumerated
    private UserStatus status;
    @Enumerated
    private UserRole role;
}