package ru.akarpo.openprofile.is_openprofile.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExternalServiceDTO {
    private UUID id;
    private String code;
    private String name;
    private String authType;
    private Instant createdAt;
}
