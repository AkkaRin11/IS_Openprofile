package ru.akarpo.openprofile.is_openprofile.dto.widget;

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
public class WidgetSyncStatusDTO {
    private UUID id;
    private UUID widgetId;
    private String syncStatus;
    private Instant lastSyncAt;
    private Instant nextSyncAt;
    private String errorMessage;
    private Integer retryCount;
}
