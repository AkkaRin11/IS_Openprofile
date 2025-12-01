package ru.akarpo.openprofile.is_openprofile.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.akarpo.openprofile.is_openprofile.enm.Role;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private UUID id;
    private Role role;
    private String email;
    private boolean twoFactorEnabled;
    private boolean emailVerified;
    private Instant createdAt;
    private Instant updatedAt;
}