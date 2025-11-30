package ru.akarpo.openprofile.is_openprofile.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.akarpo.openprofile.is_openprofile.schema.response.ApiResponse;

import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public ResponseEntity<ApiResponse<Map<String, String>>> home() {
        return ResponseEntity.ok(ApiResponse.<Map<String, String>>builder()
                .message("OpenProfile API")
                .data(Map.of(
                        "version", "1.0.0",
                        "docs", "/swagger-ui.html"
                ))
                .build());
    }

    @GetMapping("/api")
    public ResponseEntity<ApiResponse<Map<String, String>>> api() {
        return ResponseEntity.ok(ApiResponse.<Map<String, String>>builder()
                .message("OpenProfile API")
                .data(Map.of(
                        "auth", "/api/auth",
                        "profiles", "/api/profiles",
                        "users", "/api/users",
                        "themes", "/api/themes",
                        "widgets", "/api/widget-types",
                        "docs", "/swagger-ui.html"
                ))
                .build());
    }
}
