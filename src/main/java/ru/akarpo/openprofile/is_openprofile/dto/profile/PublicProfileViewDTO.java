package ru.akarpo.openprofile.is_openprofile.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicProfileViewDTO {
    private UUID publicationId;
    private String slug;
    private UUID profileId;
    private Instant publishedAt;
    private Map<String, Object> snapshot;
}