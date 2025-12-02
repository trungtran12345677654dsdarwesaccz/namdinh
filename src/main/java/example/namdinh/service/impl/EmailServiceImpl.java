package example.namdinh.service.impl;

import example.namdinh.config.JwtUtil;
import example.namdinh.dto.request.VerifyOTPRequest;
import example.namdinh.dto.response.AuthLoginResponse;
import example.namdinh.entity.Otp;
import example.namdinh.entity.User;
import example.namdinh.entity.UserSession;
import example.namdinh.enumeration.OtpStatus;
import example.namdinh.enumeration.UserRole;
import example.namdinh.enumeration.UserStatus;
import example.namdinh.repository.OTPVerificationRepository;
import example.namdinh.repository.UserRepository;
import example.namdinh.repository.UserSessionRepository;
import example.namdinh.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender javaMailSender;
    private final OTPVerificationRepository otpVerificationRepository;
    private final UserRepository userRepository;
    @Value("${spring.mail.username}")
    private String email;
    @Value("${app.frontend.url}")
    private String frontendUrl;
    private final JwtUtil jwtUtil;
    private final EmailAsyncSender emailAsyncSender;
    private final UserSessionRepository userSessionRepository;
    @Override
    @Transactional
    public void sendOTP(String recipient) {
        String cleanRecipient = recipient.trim();

        User user = userRepository.findByEmail(cleanRecipient)
                .orElseThrow(() -> new UsernameNotFoundException("User not found for OTP generation: " + cleanRecipient));

        otpVerificationRepository.findByEmail(cleanRecipient)
                .ifPresent(otpVerificationRepository::delete);

        String otpCode = generateOTPEmail();
        System.out.println("DEBUG: sendOTP - Generated OTP code: " + otpCode + " for " + cleanRecipient);

        Otp verification = new Otp();
        verification.setEmail(cleanRecipient);
        verification.setOtp(otpCode);
        verification.setCreatedDate(LocalDateTime.now());
        verification.setExpiredTime(LocalDateTime.now().plusMinutes(5));
        verification.setStatus(OtpStatus.PENDING);
        verification.setUser(user);
        otpVerificationRepository.save(verification);

        emailAsyncSender.sendOTPAsync(cleanRecipient, otpCode);
    }


    @Override
    @Transactional
    public AuthLoginResponse verifyOtp(VerifyOTPRequest request) {
        String userEmail = request.getEmail();
        String otpCode = request.getOtp();

        Otp otpRecord = otpVerificationRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BadCredentialsException("OTP not found for this email."));

        if (otpRecord.getStatus() != OtpStatus.PENDING) {
            throw new BadCredentialsException("OTP is not active or has been used/expired.");
        }

        if (otpRecord.getExpiredTime().isBefore(LocalDateTime.now())) {
            otpRecord.setStatus(OtpStatus.EXPIRED);
            otpVerificationRepository.save(otpRecord);
            throw new BadCredentialsException("OTP has expired.");
        }

        if (!otpRecord.getOtp().equals(otpCode)) {
            otpRecord.setStatus(OtpStatus.USED);
            otpVerificationRepository.save(otpRecord);
            throw new BadCredentialsException("Invalid OTP.");
        }


        otpRecord.setStatus(OtpStatus.VERIFIED);
        otpVerificationRepository.save(otpRecord);

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found after OTP verification."));

        if (user.getStatus() != UserStatus.ACTIVE) {
            user.setStatus(UserStatus.ACTIVE);
            userRepository.save(user);
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BadCredentialsException("Account is not active. Please ensure your email is verified.");
        }

        String token;
        UserRole assignedRole = null;

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        if (authorities != null && !authorities.isEmpty()) {
            String roleStringFromAuthority = authorities.iterator().next().getAuthority();
            if (roleStringFromAuthority.startsWith("ROLE_")) {
                roleStringFromAuthority = roleStringFromAuthority.substring(5);
            }

            try {
                UserRole potentialRole = UserRole.valueOf(roleStringFromAuthority.toUpperCase());

                if (potentialRole == UserRole.OWNER_LENDER) {
                    assignedRole = potentialRole;
                } else {
                    System.err.println("Attempted login with disallowed role: '" + potentialRole + "' for user " + user.getUsername());
                    throw new InsufficientAuthenticationException("Access denied: Only MANAGER and STAFF roles are allowed.");
                }
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid role string found from user authorities: " + roleStringFromAuthority + ". For user: " + user.getUsername());
                throw new InsufficientAuthenticationException("Access denied: Invalid user role.");
            }
        } else {
            System.err.println("User " + user.getUsername() + " has no assigned authorities.");
            throw new InsufficientAuthenticationException("Access denied: User has no assigned roles.");
        }

        if (assignedRole == null) {
            System.err.println("Unexpected state: assignedRole is null after processing for user " + user.getUsername());
            throw new InsufficientAuthenticationException("Access denied: Role could not be determined.");
        }
        List<UserSession> oldSessions = userSessionRepository.findByUserAndActiveTrue(user);
        for (UserSession old : oldSessions) {
            old.setActive(false);
        }
        userSessionRepository.saveAll(oldSessions);

        token = jwtUtil.generateToken(user);
        saveUserSession(user, token, request);
        AuthLoginResponse authLoginResponse = new AuthLoginResponse();
        authLoginResponse.setAccessToken(token);
        authLoginResponse.setRole(assignedRole);
        return authLoginResponse;
    }

    private void saveUserSession(User user, String token, VerifyOTPRequest request) {
        UserSession session = UserSession.builder()
                .token(token)
                .user(user)
                .createdAt(LocalDateTime.now())
                .lastAccessedAt(LocalDateTime.now())
                .active(true)
                .build();
        userSessionRepository.save(session);
    }


    private String generateOTPEmail() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    @Async
    @Override
    public void sendStatusChangeNotification(String recipientEmail, UserStatus newStatus) throws MessagingException {
        System.out.println("DEBUG: sendStatusChangeNotification - Attempting to send notification to: '" + recipientEmail + "' for status: " + newStatus.name());

        String subject;
        String body;

        switch (newStatus) {
            case ACTIVE:
                subject = "[namdinh] Tài khoản của bạn đã được kích hoạt";
                body = "Chào bạn,<br><br>"
                        + "Tài khoản của bạn với email <b>" + recipientEmail + "</b> đã được duyệt và kích hoạt thành công. "
                        + "Bạn hiện có thể đăng nhập vào hệ thống của chúng tôi.<br><br>"
                        + "Trân trọng,<br>Đội ngũ namdinh.";
                break;

            default:
                subject = "[namdinh] Cập nhật trạng thái tài khoản của bạn";
                body = "Chào bạn,<br><br>"
                        + "Tài khoản của bạn với email <b>" + recipientEmail + "</b> đã được cập nhật trạng thái thành: <b>" + newStatus.name() + "</b>.<br><br>"
                        + "Trân trọng,<br>Đội ngũ namdinh.";
                break;
        }

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(recipientEmail);
        helper.setSubject(subject);
        helper.setText(body, true);
        helper.setFrom(email);

        System.out.println("DEBUG: sendStatusChangeNotification - Preparing to send email:");
        System.out.println("DEBUG: To: " + recipientEmail);
        System.out.println("DEBUG: From: " + email);
        System.out.println("DEBUG: Subject: " + subject);

        javaMailSender.send(message);
        System.out.println("DEBUG: sendStatusChangeNotification - Email sent successfully to '" + recipientEmail + "' for status: " + newStatus.name());
    }

    @Async
    @Override
    public void sendPasswordResetEmail(String recipientEmail, String resetToken) throws MessagingException {
        System.out.println("DEBUG: sendPasswordResetEmail - Attempting to send password reset email to: '" + recipientEmail + "'");

        String resetLink = frontendUrl + "/reset-password?token=" + resetToken;



        String subject = "[namdinh] Yêu cầu đặt lại mật khẩu";
        String body = "Chào bạn,<br><br>"
                + "Chúng tôi nhận được yêu cầu đặt lại mật khẩu cho tài khoản của bạn. "
                + "Vui lòng nhấp vào liên kết sau để đặt lại mật khẩu của bạn: "
                + "<a href=\"" + resetLink + "\">Đặt lại mật khẩu</a><br><br>"
                + "Liên kết này sẽ hết hạn trong 15 phút. Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.<br><br>"
                + "Trân trọng,<br>Đội ngũ namdinh.";

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(recipientEmail);
        helper.setSubject(subject);
        helper.setText(body, true);
        helper.setFrom(email);

        System.out.println("DEBUG: sendPasswordResetEmail - Preparing to send email:");
        System.out.println("DEBUG: To: " + recipientEmail);
        System.out.println("DEBUG: From: " + email);
        System.out.println("DEBUG: Subject: " + subject);
        System.out.println("DEBUG: Reset Link: " + resetLink);

        javaMailSender.send(message);
        System.out.println("DEBUG: sendPasswordResetEmail - Password reset email sent successfully to '" + recipientEmail + "'");
    }

    @Async
    @Override
    public void sendHtmlEmail(String recipientEmail, String subject, String htmlBody) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(recipientEmail);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        helper.setFrom(email);

        System.out.println("DEBUG: sendHtmlEmail - Preparing to send email:");
        System.out.println("DEBUG: To: " + recipientEmail);
        System.out.println("DEBUG: From: " + email);
        System.out.println("DEBUG: Subject: " + subject);

        javaMailSender.send(message);
        System.out.println("DEBUG: sendHtmlEmail - Email sent successfully to '" + recipientEmail + "' with subject: " + subject);
    }






}