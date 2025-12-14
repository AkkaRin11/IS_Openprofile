package ru.akarpo.openprofile.is_openprofile.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.akarpo.openprofile.is_openprofile.schema.response.ApiResponse;
import ru.akarpo.openprofile.is_openprofile.service.profile.PreviewService;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/preview")
@RequiredArgsConstructor
@Tag(name = "Предпросмотр", description = "Генерация предварительного просмотра профилей")
public class PreviewController {

    private final PreviewService previewService;

    @GetMapping("/profile/{profileId}")
    @Operation(summary = "Предпросмотр профиля", description = "Генерирует JSON-представление профиля для предпросмотра, объединяя настройки темы и виджетов.")
    public ResponseEntity<ApiResponse<Map<String, Object>>> previewProfile(@PathVariable UUID profileId) {
        Map<String, Object> preview = previewService.generatePreview(profileId);
        return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
                .data(preview)
                .build());
    }

    @GetMapping("/profile/{profileId}/device/{deviceType}")
    @Operation(summary = "Предпросмотр для устройства", description = "Генерирует предпросмотр под конкретное устройство.")
    public ResponseEntity<ApiResponse<Map<String, Object>>> previewProfileByDevice(
            @PathVariable UUID profileId,
            @PathVariable String deviceType) {

        Map<String, Object> preview = previewService.generateDevicePreview(profileId, deviceType);
        return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
                .data(preview)
                .build());
    }
}