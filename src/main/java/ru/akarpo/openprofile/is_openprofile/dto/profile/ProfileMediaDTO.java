package ru.akarpo.openprofile.is_openprofile.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileMediaDTO {
    private UUID profileId;
    private UUID mediaId;
    private String role;
    private boolean primary;
}
