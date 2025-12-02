package example.namdinh.service;

import example.namdinh.dto.request.VerifyOTPRequest;
import example.namdinh.dto.response.AuthLoginResponse;
import example.namdinh.enumeration.UserStatus;
import jakarta.mail.MessagingException;


public interface EmailService {
    void sendOTP(String recipient) throws MessagingException;
    AuthLoginResponse verifyOtp(VerifyOTPRequest request);
    void sendPasswordResetEmail(String recipientEmail, String resetToken) throws MessagingException;
    void sendStatusChangeNotification(String recipientEmail, UserStatus newStatus) throws MessagingException;
    void sendHtmlEmail(String recipientEmail, String subject, String htmlBody) throws MessagingException;
}
