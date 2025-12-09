package example.namdinh.profileUser.controller;

import example.namdinh.entity.User;
import example.namdinh.profileUser.dto.UserUpdateRequest;
import example.namdinh.profileUser.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/profile")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PatchMapping("/{userId}")
    @PreAuthorize("hasRole('OWNER_LENDER')")
    public ResponseEntity<User> updateProfile(
            @PathVariable Long userId,
            @RequestBody UserUpdateRequest request) {
        User updatedUser = userService.updateUser(userId, request);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('OWNER_LENDER')")
    public ResponseEntity<User> getUserProfile(@PathVariable Long userId) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        return ResponseEntity.ok(user);
    }
}