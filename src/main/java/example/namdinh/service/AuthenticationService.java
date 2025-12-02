package example.namdinh.service;

import example.namdinh.dto.request.LoginRequest;
import example.namdinh.dto.request.RegisterRequest;
import example.namdinh.dto.request.StatusChangeRequest;
import example.namdinh.dto.response.UserResponse;
import example.namdinh.dto.response.UserSessionResponse;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;


import java.util.List;

public interface AuthenticationService {
    String login(LoginRequest request) throws MessagingException;
    UserResponse register(RegisterRequest request);
    String requestStatusChange(StatusChangeRequest request);

    void resetPassword(String token, String newPassword);
    void requestPasswordReset(String email) throws MessagingException;
    List<UserSessionResponse> getActiveSessions(HttpServletRequest request);

}

