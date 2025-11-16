package ru.akarpo.openprofile.is_openprofile.dto.profile;

import com.fasterxml.jackson.databind.JsonNode;
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
public class ProfileWidgetDTO {
    private UUID id;
    private UUID profileId;
    private UUID widgetTypeId;
    private String title;
    private JsonNode settings;
    private JsonNode layout;
    private int position;
    private Instant createdAt;
    private Instant updatedAt;
}
