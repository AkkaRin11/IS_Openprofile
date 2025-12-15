package ru.akarpo.openprofile.is_openprofile.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.akarpo.openprofile.is_openprofile.dto.ThemeDTO;
import ru.akarpo.openprofile.is_openprofile.schema.response.ApiResponse;
import ru.akarpo.openprofile.is_openprofile.service.ThemeService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/themes")
@RequiredArgsConstructor
@Tag(name = "Темы оформления", description = "Управление и выбор тем оформления профиля")
public class ThemeController {

    private final ThemeService themeService;

    @GetMapping
    @Operation(summary = "Получить все темы", description = "Возвращает список всех доступных тем оформления.")
    public ResponseEntity<ApiResponse<List<ThemeDTO>>> getAllThemes() {
        List<ThemeDTO> themes = themeService.findAll();
        return ResponseEntity.ok(ApiResponse.<List<ThemeDTO>>builder()
                .data(themes)
                .build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить тему по ID", description = "Возвращает детали темы по её уникальному идентификатору.")
    public ResponseEntity<ApiResponse<ThemeDTO>> getThemeById(@PathVariable UUID id) {
        return themeService.findById(id)
                .map(theme -> ResponseEntity.ok(ApiResponse.<ThemeDTO>builder()
                        .data(theme)
                        .build()))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Получить тему по названию", description = "Возвращает детали темы по её названию (например, 'Dark', 'Light').")
    public ResponseEntity<ApiResponse<ThemeDTO>> getThemeByName(@PathVariable String name) {
        return themeService.findByName(name)
                .map(theme -> ResponseEntity.ok(ApiResponse.<ThemeDTO>builder()
                        .data(theme)
                        .build()))
                .orElse(ResponseEntity.notFound().build());
    }
}