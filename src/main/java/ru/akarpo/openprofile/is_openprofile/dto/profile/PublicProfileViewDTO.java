package ru.akarpo.openprofile.is_openprofile.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.Instant;
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
    private JsonNode snapshot;

    public JsonNode getSnapshot() {
        if (snapshot != null && snapshot.isTextual()) {
            try {
                return new com.fasterxml.jackson.databind.ObjectMapper().readTree(snapshot.asText());
            } catch (Exception e) {
                return snapshot;
            }
        }
        return snapshot;
    }
}