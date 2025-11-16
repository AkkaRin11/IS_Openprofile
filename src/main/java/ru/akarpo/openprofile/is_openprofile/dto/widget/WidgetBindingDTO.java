package ru.akarpo.openprofile.is_openprofile.dto.widget;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WidgetBindingDTO {
    private UUID profileWidgetId;
    private UUID connectionId;
}
