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
public class BindWidgetRequest {
    private UUID connectionId;
}