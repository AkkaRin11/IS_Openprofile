package ru.akarpo.openprofile.is_openprofile.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.akarpo.openprofile.is_openprofile.dto.profile.PublicProfileViewDTO;
import ru.akarpo.openprofile.is_openprofile.schema.response.ApiResponse;
import ru.akarpo.openprofile.is_openprofile.service.profile.PublicProfileViewService;

import java.util.UUID;

@RestController
@RequestMapping("/api/public-profiles")
@RequiredArgsConstructor
@Tag(name = "Публичный просмотр", description = "Публично доступные данные профилей и предпросмотр")
public class PublicProfileController {

        private final PublicProfileViewService publicProfileViewService;

        @GetMapping("/slug/{slug}")
        @Operation(summary = "Получить публичный профиль (slug)", description = "Возвращает данные публичного профиля по его URL-идентификатору (slug).")
        public ResponseEntity<ApiResponse<PublicProfileViewDTO>> getPublicProfileBySlug(@PathVariable String slug) {
                return publicProfileViewService.findBySlug(slug)
                                .map(profile -> ResponseEntity.ok(ApiResponse.<PublicProfileViewDTO>builder()
                                                .data(profile)
                                                .build()))
                                .orElse(ResponseEntity.notFound().build());
        }

        @GetMapping("/profile/{profileId}")
        @Operation(summary = "Получить публичный профиль (ID)", description = "Возвращает данные публичного профиля по ID профиля.")
        public ResponseEntity<ApiResponse<PublicProfileViewDTO>> getPublicProfileByProfileId(
                        @PathVariable UUID profileId) {
                return publicProfileViewService.findByProfileId(profileId)
                                .map(profile -> ResponseEntity.ok(ApiResponse.<PublicProfileViewDTO>builder()
                                                .data(profile)
                                                .build()))
                                .orElse(ResponseEntity.notFound().build());
        }
}