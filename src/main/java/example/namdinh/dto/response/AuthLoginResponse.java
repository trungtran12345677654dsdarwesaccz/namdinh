package example.namdinh.dto.response;

import example.namdinh.enumeration.UserRole;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthLoginResponse {
    private String accessToken;
    @Enumerated
    private UserRole role;
}

