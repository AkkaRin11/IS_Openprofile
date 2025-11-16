package ru.akarpo.openprofile.is_openprofile.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.akarpo.openprofile.is_openprofile.enm.PublicationStatus;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicationDTO {
    private UUID id;
    private UUID profileId;
    private PublicationStatus status;
    private boolean active;
    private int version;
    private JsonNode snapshot;
    private Instant publishedAt;
    private Instant createdAt;
}
