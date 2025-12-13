package ru.akarpo.openprofile.is_openprofile.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.akarpo.openprofile.is_openprofile.dto.MediaAssetDTO;
import ru.akarpo.openprofile.is_openprofile.schema.response.ApiResponse;
import ru.akarpo.openprofile.is_openprofile.service.MediaAssetService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
@Tag(name = "Медиа", description = "Загрузка и обработка изображений")
public class MediaAssetController {

    private final MediaAssetService mediaAssetService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MediaAssetDTO>> getMediaById(@PathVariable UUID id) {
        return mediaAssetService.findById(id)
                .map(media -> ResponseEntity.ok(ApiResponse.<MediaAssetDTO>builder()
                        .data(media)
                        .build()))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<MediaAssetDTO>>> getMediaByUser(@PathVariable UUID userId) {
        List<MediaAssetDTO> media = mediaAssetService.findByUserId(userId);
        return ResponseEntity.ok(ApiResponse.<List<MediaAssetDTO>>builder()
                .data(media)
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMedia(@PathVariable UUID id) {
        if (mediaAssetService.findById(id).isPresent()) {
            mediaAssetService.deleteById(id);
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .message("Media deleted successfully")
                    .build());
        }
        return ResponseEntity.notFound().build();
    }
}
