package ru.akarpo.openprofile.is_openprofile.domain;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WidgetBindingId implements Serializable {
    private UUID profileWidgetId;
    private UUID connectionId;
}
