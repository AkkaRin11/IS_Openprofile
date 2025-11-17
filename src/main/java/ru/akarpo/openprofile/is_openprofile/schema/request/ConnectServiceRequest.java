package ru.akarpo.openprofile.is_openprofile.schema.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConnectServiceRequest {
    private String serviceCode;
    private String externalUserId;
    private String accessToken;
    private String refreshToken;
    private String tokenExpiresAt;
}