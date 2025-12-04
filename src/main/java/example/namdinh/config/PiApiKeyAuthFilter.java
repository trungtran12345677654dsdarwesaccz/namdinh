
package example.namdinh.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class PiApiKeyAuthFilter extends OncePerRequestFilter {

    @Value("${iot.api.key}")
    private String validApiKey;

    private static final String API_KEY_HEADER = "X-API-KEY";
    private static final String PI_INIT_PATH = "/api/scanning/drivers/init";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (request.getRequestURI().equals(PI_INIT_PATH)) {
            String requestApiKey = request.getHeader(API_KEY_HEADER);
            if (requestApiKey == null || !requestApiKey.equals(validApiKey)) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().write("Invalid or missing API Key for IoT endpoint.");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}

