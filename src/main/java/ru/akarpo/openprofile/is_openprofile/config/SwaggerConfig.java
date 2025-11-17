package ru.akarpo.openprofile.is_openprofile.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("OpenProfile API")
                .description("API для управления профилями и контентом")
                .version("1.0.0")
                .contact(new Contact()
                    .name("OpenProfile Team")
                    .email("support@openprofile.ru")))
            .servers(List.of(
                new Server().url("http://localhost:" + serverPort).description("Development Server"),
                new Server().url("https://api.openprofile.ru").description("Production Server")
            ));
    }
}
