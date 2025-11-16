package ru.akarpo.openprofile.is_openprofile.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaAssetDTO {
    private UUID id;
    private UUID userId;
    private String filename;
    private String storageKey;
    private String contentType;
    private long sizeBytes;
    private Integer width;
    private Integer height;
    private String altText;
    private Instant createdAt;
}
