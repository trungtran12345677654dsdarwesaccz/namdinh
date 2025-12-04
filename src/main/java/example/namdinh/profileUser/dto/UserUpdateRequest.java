package example.namdinh.profileUser.dto;

import lombok.Data;

@Data
public class UserUpdateRequest {

    private String username;
    private String fullName;
    private String phone;
    private String email;

}
