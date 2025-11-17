package ru.akarpo.openprofile.is_openprofile.schema.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyTwoFactorRequest {

    @NotBlank(message = "Code is required")
    @Pattern(regexp = "\\d{6}", message = "Code must be 6 digits")
    private String code;
}