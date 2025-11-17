package ru.akarpo.openprofile.is_openprofile.controller;

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
public class ThemeController {

    private final ThemeService themeService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ThemeDTO>>> getAllThemes() {
        List<ThemeDTO> themes = themeService.findAll();
        return ResponseEntity.ok(ApiResponse.<List<ThemeDTO>>builder()
                .data(themes)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ThemeDTO>> getThemeById(@PathVariable UUID id) {
        return themeService.findById(id)
                .map(theme -> ResponseEntity.ok(ApiResponse.<ThemeDTO>builder()
                        .data(theme)
                        .build()))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse<ThemeDTO>> getThemeByName(@PathVariable String name) {
        return themeService.findByName(name)
                .map(theme -> ResponseEntity.ok(ApiResponse.<ThemeDTO>builder()
                        .data(theme)
                        .build()))
                .orElse(ResponseEntity.notFound().build());
    }
}