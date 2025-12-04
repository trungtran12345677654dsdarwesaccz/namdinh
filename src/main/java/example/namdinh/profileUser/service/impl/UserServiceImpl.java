package example.namdinh.profileUser.service.impl;

import example.namdinh.entity.User;
import example.namdinh.profileUser.dto.UserUpdateRequest;
import example.namdinh.profileUser.service.UserService;
 // Thay thế bằng package thực tế
import example.namdinh.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);
    }

    @Override
    public User updateUser(Long userId, UserUpdateRequest request) {
        // 1. Tìm User hoặc ném ngoại lệ nếu không tìm thấy
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // 2. Áp dụng các thay đổi từ DTO

        // a. Username
        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            user.setUsername(request.getUsername());
        }

        // b. Full Name
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }

        // c. Phone
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }

        // d. Email
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        return userRepository.save(user);
    }
}