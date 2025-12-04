package example.namdinh.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    // Khóa cho JWT (Bearer Token)
    private static final String JWT_SECURITY_SCHEME = "bearerAuth";

    private static final String IOT_API_KEY_SCHEME = "IotApiKey";
    private static final String API_KEY_HEADER = "X-API-KEY"; // Tên Header bạn dùng trong Filter

    @Bean
    public OpenAPI customSwagger() {
        // 1. Định nghĩa Security Schemes (JWT và API Key)
        Components components = new Components()
                .addSecuritySchemes(JWT_SECURITY_SCHEME, new SecurityScheme()
                        .name("Authorizations (JWT)")
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"))

                // THÊM CẤU HÌNH API KEY CHO IOT
                .addSecuritySchemes(IOT_API_KEY_SCHEME, new SecurityScheme()
                        .name("IoT API Key")
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.HEADER) // API Key được truyền qua Header
                        .name(API_KEY_HEADER) // Tên Header là X-API-KEY
                        .description("API Key for Raspberry Pi/IoT device authentication. Use the value from iot.api.key."));

        // 2. Thêm yêu cầu bảo mật vào danh sách chung
        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList(JWT_SECURITY_SCHEME) // Yêu cầu JWT cho hầu hết các API
                .addList(IOT_API_KEY_SCHEME); // Yêu cầu API Key cho các API IoT

        return new OpenAPI()
                .addSecurityItem(securityRequirement) // Áp dụng cả hai lược đồ
                .components(components)
                .info(new Info()
                        .description("Dự án TEST ABC")
                        .contact(new Contact().email("tranduytrung251105@gmail.com")))
                .addServersItem(new Server().url("http://localhost:8080/").description("Local server"));

    }
}