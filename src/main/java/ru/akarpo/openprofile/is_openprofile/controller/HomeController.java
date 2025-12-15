package ru.akarpo.openprofile.is_openprofile.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.akarpo.openprofile.is_openprofile.schema.response.ApiResponse;

import java.util.Map;

@RestController
@Tag(name = "Система", description = "Общая информация о системе")
public class HomeController {

    @GetMapping("/")
    @Operation(summary = "Главная страница", description = "Возвращает информацию о версии API и ссылку на документацию.")
    public ResponseEntity<ApiResponse<Map<String, String>>> home() {
        return ResponseEntity.ok(ApiResponse.<Map<String, String>>builder()
                .message("OpenProfile API")
                .data(Map.of(
                        "version", "1.0.0",
                        "docs", "/swagger-ui.html"))
                .build());
    }

    @GetMapping("/api")
    @Operation(summary = "Обзор API", description = "Возвращает список основных эндопоинтов API.")
    public ResponseEntity<ApiResponse<Map<String, String>>> api() {
        return ResponseEntity.ok(ApiResponse.<Map<String, String>>builder()
                .message("OpenProfile API")
                .data(Map.of(
                        "auth", "/api/auth",
                        "profiles", "/api/profiles",
                        "users", "/api/users",
                        "themes", "/api/themes",
                        "widgets", "/api/widget-types",
                        "docs", "/swagger-ui.html"))
                .build());
    }
}
