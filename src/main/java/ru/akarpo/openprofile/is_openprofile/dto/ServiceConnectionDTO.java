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
public class ServiceConnectionDTO {
    private UUID id;
    private UUID userId;
    private UUID serviceId;
    private String externalUserId;
    private String accessToken;
    private String refreshToken;
    private Instant tokenExpiresAt;
    private Instant createdAt;
    private Instant updatedAt;
}