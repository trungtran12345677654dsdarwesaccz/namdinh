package example.namdinh.config;

import example.namdinh.entity.User;
import example.namdinh.enumeration.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;


@Component
public class JwtUtil {

    @Value("${application.security.jwt.secret-key}")
    private String SECRET_KEY;

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    @Value("${application.security.jwt.reset-password-expiration}")
    private long resetPasswordExpiration;

    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    public void blacklistToken(String token) {
        blacklistedTokens.add(token);
    }
    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }


    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();

        // Role
        if (user.getRole() != null) {
            claims.put("role", user.getRole().name());
            claims.put("authorities", List.of("ROLE_" + user.getRole().name()));
        } else {
            claims.put("role", "ROLE_USER");
            claims.put("authorities", List.of("ROLE_USER"));
        }

        // Username
        claims.put("username", user.getUsername());

        // Tùy chọn ID theo role
        if (user.getRole() == UserRole.OWNER_LENDER) {
            claims.put("managerId", user.getUserId());
        }

        System.out.println("Generated token claims: " + claims);

        return createToken(claims, user.getEmail());
    }

    public String extractTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject) // Email hoặc Username
                .setIssuedAt(new Date(System.currentTimeMillis()))
                // SỬA QUAN TRỌNG: LUÔN THÊM THỜI GIAN HẾT HẠN
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration)) // Sử dụng jwtExpiration
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        // Kiểm tra username khớp và token chưa hết hạn
        return (username != null && username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    public boolean validateToken(String token) {
        try {
            String username = extractUsername(token);
            return (username != null && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }




    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // SỬA: Bắt ngoại lệ khi trích xuất thời gian hết hạn để tránh NullPointerException
    private Date extractExpiration(String token) {
        try {
            return extractClaim(token, Claims::getExpiration);
        } catch (Exception e) {
            System.err.println("DEBUG: Error extracting expiration from token: " + e.getMessage());
            return null; // Trả về null nếu không thể trích xuất (token lỗi hoặc thiếu claim)
        }
    }

    // SỬA: Xử lý trường hợp extractExpiration trả về null
    private Boolean isTokenExpired(String token) {
        Date expiration = extractExpiration(token);
        if (expiration == null) {
            // Nếu không thể trích xuất thời gian hết hạn, coi như đã hết hạn hoặc không hợp lệ
            System.err.println("DEBUG: Token expiration is null, treating as expired/invalid.");
            return true;
        }
        return expiration.before(new Date());
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    // Phương thức validate riêng cho token đặt lại mật khẩu
    public Boolean validateResetPasswordToken(String token, User user) {
        final String email = extractUsername(token); // Subject của reset token là email
        // Lấy thời gian token được cấp phát
        Date issuedAt = extractClaim(token, Claims::getIssuedAt);

        // Kiểm tra loại token và email khớp
        // Kiểm tra xem claim 'type' có tồn tại và giá trị của nó có phải là "reset_password" không
        boolean isResetToken = "reset_password".equals(extractClaim(token, claims -> claims.get("type", String.class)));

        // Kiểm tra token chưa hết hạn
        boolean notExpired = !isTokenExpired(token);

        // Kiểm tra token được cấp sau lần đặt lại mật khẩu cuối cùng của người dùng
        // Nếu lastPasswordResetDate là null (chưa bao giờ reset), thì mọi token đều hợp lệ
        boolean issuedAfterLastReset = user.getLastPasswordResetDate() == null ||
                issuedAt.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
                        .isAfter(user.getLastPasswordResetDate());


        return (email != null && email.equals(user.getEmail()) && isResetToken && notExpired && issuedAfterLastReset);
    }

    // Phương thức tạo token khôi phục mật khẩu
    public String generatePasswordResetToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "reset_password");
        claims.put("jti", UUID.randomUUID().toString());

        return createToken(claims, user.getEmail(), resetPasswordExpiration);
    }

    private String createToken(Map<String, Object> claims, String subject, long expirationTimeMillis) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTimeMillis))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Bỏ phần "Bearer "
        }
        return null;
    }
}
