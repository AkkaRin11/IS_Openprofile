package ru.akarpo.openprofile.is_openprofile.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.akarpo.openprofile.is_openprofile.domain.User;
import ru.akarpo.openprofile.is_openprofile.dto.MediaAssetDTO;
import ru.akarpo.openprofile.is_openprofile.exception.ResourceNotFoundException;
import ru.akarpo.openprofile.is_openprofile.repository.UserRepository;
import ru.akarpo.openprofile.is_openprofile.schema.response.ApiResponse;
import ru.akarpo.openprofile.is_openprofile.service.MediaAssetService;
import ru.akarpo.openprofile.is_openprofile.service.MediaUploadService;

import java.util.UUID;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
@Tag(name = "Медиа-сервис", description = "Операции с изображениями: загрузка, обрезка, сжатие")
public class MediaController {

    private final MediaUploadService mediaUploadService;
    private final MediaAssetService mediaAssetService;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Загрузить изображение", description = "Загружает изображение на сервер, сохраняет его и создает запись медиа-актива.")
    public ResponseEntity<ApiResponse<MediaAssetDTO>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "altText", required = false) String altText) {

        User user = getCurrentUser();
        MediaAssetDTO mediaAsset = mediaUploadService.uploadImage(user.getId(), file, altText);

        return ResponseEntity.ok(ApiResponse.<MediaAssetDTO>builder()
                .message("File uploaded successfully")
                .data(mediaAsset)
                .build());
    }

    @PostMapping("/{mediaId}/crop")
    @Operation(summary = "Обрезать изображение", description = "Создает обрезанную версию изображения. Принимает координаты (x, y) и размер (width, height) области.")
    public ResponseEntity<ApiResponse<MediaAssetDTO>> cropImage(
            @PathVariable UUID mediaId,
            @RequestParam int x,
            @RequestParam int y,
            @RequestParam int width,
            @RequestParam int height) {

        MediaAssetDTO croppedImage = mediaUploadService.cropImage(mediaId, x, y, width, height);

        return ResponseEntity.ok(ApiResponse.<MediaAssetDTO>builder()
                .message("Image cropped successfully")
                .data(croppedImage)
                .build());
    }

    @PostMapping("/{mediaId}/compress")
    @Operation(summary = "Сжать изображение", description = "Создает сжатую версию изображения с указанным качеством (от 0.0 до 1.0).")
    public ResponseEntity<ApiResponse<MediaAssetDTO>> compressImage(
            @PathVariable UUID mediaId,
            @RequestParam(defaultValue = "0.8") float quality) {

        MediaAssetDTO compressedImage = mediaUploadService.compressImage(mediaId, quality);

        return ResponseEntity.ok(ApiResponse.<MediaAssetDTO>builder()
                .message("Image compressed successfully")
                .data(compressedImage)
                .build());
    }

    @GetMapping("/{mediaId}/download")
    @Operation(summary = "Скачать файл", description = "Скачивает бинарный контент медиа-файла.")
    public ResponseEntity<byte[]> downloadMedia(@PathVariable UUID mediaId) {
        byte[] fileContent = mediaUploadService.getMediaFile(mediaId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"media-" + mediaId + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(fileContent);
    }

    @GetMapping("/{mediaId}/view")
    @Operation(summary = "Просмотр файла", description = "Возвращает файл для просмотра (inline). Доступно без авторизации.")
    public ResponseEntity<byte[]> viewMedia(@PathVariable UUID mediaId) {
        return mediaAssetService.findById(mediaId)
                .map(mediaAsset -> {
                    byte[] fileContent = mediaUploadService.getMediaFile(mediaId);
                    return ResponseEntity.ok()
                            .contentType(MediaType
                                    .parseMediaType(mediaAsset.getContentType()))
                            .body(fileContent);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}