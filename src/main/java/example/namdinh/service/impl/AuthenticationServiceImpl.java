package example.namdinh.service.impl;

import example.namdinh.config.JwtUtil;
import example.namdinh.config.SecurityConfig;
import example.namdinh.dto.request.LoginRequest;
import example.namdinh.dto.request.RegisterRequest;
import example.namdinh.dto.request.StatusChangeRequest;
import example.namdinh.dto.response.UserResponse;
import example.namdinh.dto.response.UserSessionResponse;
import example.namdinh.entity.User;
import example.namdinh.entity.UserSession;
import example.namdinh.enumeration.UserRole;
import example.namdinh.enumeration.UserStatus;
import example.namdinh.repository.RoleRepository;
import example.namdinh.repository.UserRepository;
import example.namdinh.repository.UserSessionRepository;
import example.namdinh.service.AuthenticationService;
import example.namdinh.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final SecurityConfig securityConfig;
    private final RoleRepository roleRepository;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;
    private final UserSessionRepository userSessionRepository;


    @Override
    public List<UserSessionResponse> getActiveSessions(HttpServletRequest request) {
        String token = jwtUtil.resolveToken(request);
        String email = jwtUtil.extractUsername(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với email: " + email));

        List<UserSession> sessions = userSessionRepository.findByUserAndActiveTrue(user);

        return sessions.stream().map(session -> {
            UserSessionResponse dto = new UserSessionResponse();
            dto.setId(session.getId());
            dto.setCreatedAt(session.getCreatedAt());
            dto.setLastAccessedAt(session.getLastAccessedAt());
            dto.setEmail(user.getEmail());
            dto.setRole(user.getRole().name());
            return dto;
        }).collect(Collectors.toList());
    }



    @Override
// 2. Thêm throws MessagingException vào chữ ký của phương thức login
    public String login(LoginRequest request) throws MessagingException {
        // Xác thực thông tin đăng nhập
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // Lấy thông tin user sau khi xác thực thành công
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found after authentication."));

        // Kiểm tra trạng thái tài khoản. Nếu INACTIVE thì không cho tiếp tục

        emailService.sendOTP(user.getEmail());

        // 4. Trả về một thông báo cho frontend biết OTP đã được gửi thành công.
        // Frontend sẽ chuyển sang màn hình nhập OTP.
        return "OTP has been sent to your email. Please enter it to complete login.";

    }
    //securityConfig.passwordEncoder().encode(register.getPassword())
    @Override
    @Transactional
    public UserResponse register(RegisterRequest register) {
        if (userRepository.existsByUsername(register.getUsername())) {
            throw new RuntimeException("Username already exists.");
        }

        if (userRepository.existsByEmail(register.getEmail())) {
            throw new RuntimeException("   Email already exists.");
        }
        if (register.getPassword().length() > 72) {
            throw new IllegalArgumentException("Password cannot be more than 72 characters.");
        }

        User user = new User();
        user.setUsername(register.getUsername());
        user.setEmail(register.getEmail());
        user.setPassword(securityConfig.passwordEncoder().encode(register.getPassword()));
        user.setPhone(register.getPhone());
        user.setFullName(register.getFullName());
        user.setRole(UserRole.OWNER_LENDER);
        userRepository.save(user);

        // --- Bắt đầu phần thay đổi: Tự động gửi yêu cầu thay đổi trạng thái sau khi đăng ký ---
        // Tạo một StatusChangeRequest giả định cho mục đích này
        StatusChangeRequest statusChangeRequest = StatusChangeRequest.builder()
                .email(user.getEmail()) // Sử dụng email của user vừa tạo
                .build();
        String statusChangeMessage = this.requestStatusChange(statusChangeRequest); // Sử dụng 'this' để gọi phương thức của cùng class
        System.out.println("DEBUG: Auto status change request initiated for new user: " + statusChangeMessage);
        // --- Kết thúc phần thay đổi ---

        UserResponse response = new UserResponse();
        response.setUserName(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFullName(user.getFullName());
        return response;
    }

    @Override
    @Transactional
    public String requestStatusChange(StatusChangeRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + request.getEmail()));

        if (user.getStatus() == UserStatus.INACTIVE) {
            return "Your account is already pending approval. Please wait for a manager to approve it.";
        }
        return  "Your account is already approval";

    }



    @Override
    @Transactional
    public void requestPasswordReset(String email) throws MessagingException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        String resetToken = jwtUtil.generatePasswordResetToken(user);
        emailService.sendPasswordResetEmail(user.getEmail(), resetToken);
        System.out.println("Password reset link sent to: " + email);
    }
    // ---  Đặt lại mật khẩu ---
    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        // Trích xuất email từ token
        String email = jwtUtil.extractUsername(token);
        if (email == null) {
            throw new BadCredentialsException("Invalid or malformed reset password token.");
        }

        // Tìm người dùng theo email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found for password reset."));

        // Xác minh token
        if (!jwtUtil.validateResetPasswordToken(token, user)) {
            throw new BadCredentialsException("Invalid or expired password reset token.");
        }

             // Kiểm tra độ dài mật khẩu mới
        if (newPassword == null || newPassword.length() < 6 || newPassword.length() > 72) {
            throw new IllegalArgumentException("New password must be between 6 and 72 characters.");
        }

        // Mã hóa và cập nhật mật khẩu mới
        user.setPassword(securityConfig.passwordEncoder().encode(newPassword));
        // Cập nhật lastPasswordResetDate để vô hiệu hóa tất cả các token reset cũ hơn
        user.setLastPasswordResetDate(LocalDateTime.now());
        userRepository.save(user);

        System.out.println("Password for user " + email + " has been reset successfully.");
    }


}