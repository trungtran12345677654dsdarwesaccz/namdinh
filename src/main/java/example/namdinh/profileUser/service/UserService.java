package example.namdinh.profileUser.service;

import example.namdinh.entity.User;
import example.namdinh.profileUser.dto.UserUpdateRequest;

import java.util.Optional;

public interface UserService {
    Optional<User> findById(Long userId);
    User updateUser(Long userId, UserUpdateRequest request);
}
