package ru.akarpo.openprofile.is_openprofile.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.akarpo.openprofile.is_openprofile.dto.PublicationDTO;
import ru.akarpo.openprofile.is_openprofile.schema.response.ApiResponse;
import ru.akarpo.openprofile.is_openprofile.service.PublicationService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/publications")
@RequiredArgsConstructor
@Tag(name = "Публикации", description = "История версий и управление публикациями профиля")
public class PublicationController {

        private final PublicationService publicationService;

        @GetMapping("/{id}")
        @Operation(summary = "Получить публикацию", description = "Возвращает детали конкретной версии публикации по её ID.")
        public ResponseEntity<ApiResponse<PublicationDTO>> getPublicationById(@PathVariable UUID id) {
                return publicationService.findById(id)
                                .map(publication -> ResponseEntity.ok(ApiResponse.<PublicationDTO>builder()
                                                .data(publication)
                                                .build()))
                                .orElse(ResponseEntity.notFound().build());
        }

        @GetMapping("/profile/{profileId}")
        @Operation(summary = "История публикаций", description = "Возвращает полный список версий публикаций для указанного профиля.")
        public ResponseEntity<ApiResponse<List<PublicationDTO>>> getPublicationsByProfile(
                        @PathVariable UUID profileId) {
                List<PublicationDTO> publications = publicationService.findByProfileId(profileId);
                return ResponseEntity.ok(ApiResponse.<List<PublicationDTO>>builder()
                                .data(publications)
                                .build());
        }

        @GetMapping("/profile/{profileId}/active")
        @Operation(summary = "Текущая публикация", description = "Возвращает текущую активную (опубликованную) версию профиля.")
        public ResponseEntity<ApiResponse<PublicationDTO>> getActivePublicationByProfile(@PathVariable UUID profileId) {
                return publicationService.findActiveByProfileId(profileId)
                                .map(publication -> ResponseEntity.ok(ApiResponse.<PublicationDTO>builder()
                                                .data(publication)
                                                .build()))
                                .orElse(ResponseEntity.notFound().build());
        }
}
