package ru.akarpo.openprofile.is_openprofile.dto.widget;

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
public class WidgetTypeDTO {
    private UUID id;
    private String code;
    private String name;
    private boolean supportsBinding;
    private JsonNode schemaJson;
    private Instant createdAt;
}