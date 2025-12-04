
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
import java.util.Arrays;
import java.util.List;

@Component
public class PiApiKeyAuthFilter extends OncePerRequestFilter {

    @Value("${iot.api.key}")
    private String validApiKey;

    private static final String API_KEY_HEADER = "X-API-KEY";

    // ĐỊNH NGHĨA CÁC ĐƯỜNG DẪN CẦN BẢO VỆ BẰNG API KEY
    private static final List<String> PI_PROTECTED_PATHS = Arrays.asList(
            "/api/scanning/drivers/**",
            "/api/trips/**"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Chỉ áp dụng xác thực API Key cho các đường dẫn được định nghĩa
        if (PI_PROTECTED_PATHS.contains(request.getRequestURI())) {
            String requestApiKey = request.getHeader(API_KEY_HEADER);

            if (requestApiKey == null || !requestApiKey.equals(validApiKey)) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Invalid or missing API Key for IoT endpoint.\"}");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}

