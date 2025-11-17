package ru.akarpo.openprofile.is_openprofile.schema.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProfileRequest {
    private String name;
    private String slug;
    private String privacy;
    private UUID themeId;
}