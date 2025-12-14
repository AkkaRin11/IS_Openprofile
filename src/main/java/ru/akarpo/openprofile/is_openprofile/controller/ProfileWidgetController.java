package ru.akarpo.openprofile.is_openprofile.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.akarpo.openprofile.is_openprofile.dto.profile.ProfileWidgetDTO;
import ru.akarpo.openprofile.is_openprofile.schema.response.ApiResponse;
import ru.akarpo.openprofile.is_openprofile.service.profile.ProfileWidgetService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/profile-widgets")
@RequiredArgsConstructor
@Tag(name = "Виджеты профиля", description = "Управление конкретными виджетами внутри профиля")
public class ProfileWidgetController {

    private final ProfileWidgetService profileWidgetService;

    @GetMapping("/{id}")
    @Operation(summary = "Получить виджет", description = "Возвращает детали конкретного виджета по ID.")
    public ResponseEntity<ApiResponse<ProfileWidgetDTO>> getProfileWidgetById(@PathVariable UUID id) {
        return profileWidgetService.findById(id)
                .map(widget -> ResponseEntity.ok(ApiResponse.<ProfileWidgetDTO>builder()
                        .data(widget)
                        .build()))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/profile/{profileId}")
    @Operation(summary = "Получить виджеты профиля", description = "Возвращает список всех виджетов, добавленных в указанный профиль.")
    public ResponseEntity<ApiResponse<List<ProfileWidgetDTO>>> getWidgetsByProfile(@PathVariable UUID profileId) {
        List<ProfileWidgetDTO> widgets = profileWidgetService.findByProfileId(profileId);
        return ResponseEntity.ok(ApiResponse.<List<ProfileWidgetDTO>>builder()
                .data(widgets)
                .build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить виджет", description = "Удаляет виджет из профиля.")
    public ResponseEntity<ApiResponse<Void>> deleteProfileWidget(@PathVariable UUID id) {
        if (profileWidgetService.findById(id).isPresent()) {
            profileWidgetService.deleteById(id);
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .message("Profile widget deleted successfully")
                    .build());
        }
        return ResponseEntity.notFound().build();
    }
}