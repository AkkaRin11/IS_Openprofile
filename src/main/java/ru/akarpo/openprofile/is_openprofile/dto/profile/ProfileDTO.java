package ru.akarpo.openprofile.is_openprofile.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.akarpo.openprofile.is_openprofile.enm.PrivacyLevel;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDTO {
    private UUID id;
    private UUID userId;
    private String name;
    private String slug;
    private String description;
    private String image;
    private PrivacyLevel privacy;
    private UUID themeId;
    private Instant createdAt;
    private Instant updatedAt;
}