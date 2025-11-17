package ru.akarpo.openprofile.is_openprofile.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.akarpo.openprofile.is_openprofile.dto.MediaAssetDTO;
import ru.akarpo.openprofile.is_openprofile.schema.response.ApiResponse;
import ru.akarpo.openprofile.is_openprofile.service.MediaUploadService;

import java.util.UUID;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
@Tag(name = "Media Management", description = "Upload and manage media files")
public class MediaController {

    private final MediaUploadService mediaUploadService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload image file")
    public ResponseEntity<ApiResponse<MediaAssetDTO>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") UUID userId,
            @RequestParam(value = "altText", required = false) String altText) {

        MediaAssetDTO mediaAsset = mediaUploadService.uploadImage(userId, file, altText);

        return ResponseEntity.ok(ApiResponse.<MediaAssetDTO>builder()
                .message("File uploaded successfully")
                .data(mediaAsset)
                .build());
    }

    @PostMapping("/{mediaId}/crop")
    @Operation(summary = "Crop image")
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
    @Operation(summary = "Compress image")
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
    @Operation(summary = "Download media file")
    public ResponseEntity<byte[]> downloadMedia(@PathVariable UUID mediaId) {
        byte[] fileContent = mediaUploadService.getMediaFile(mediaId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"media-" + mediaId + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(fileContent);
    }
}