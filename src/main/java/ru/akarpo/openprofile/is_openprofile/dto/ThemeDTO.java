package ru.akarpo.openprofile.is_openprofile.dto;

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
public class ThemeDTO {
    private UUID id;
    private String name;
    private JsonNode palette;
    private JsonNode typography;
    private Instant createdAt;
    private Instant updatedAt;
}