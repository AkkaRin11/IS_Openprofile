package ru.akarpo.openprofile.is_openprofile.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.akarpo.openprofile.is_openprofile.schema.request.*;
import ru.akarpo.openprofile.is_openprofile.schema.response.ApiResponse;
import ru.akarpo.openprofile.is_openprofile.schema.response.AuthResponse;
import ru.akarpo.openprofile.is_openprofile.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User authentication and registration")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register new user")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(ApiResponse.<AuthResponse>builder()
                .message("User registered successfully. Please check your email for verification.")
                .data(response)
                .build());
    }

    @PostMapping("/login")
    @Operation(summary = "Login user")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.<AuthResponse>builder()
                .message("Login successful")
                .data(response)
                .build());
    }

    @PostMapping("/verify-email")
    @Operation(summary = "Verify email with token")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        authService.verifyEmail(request.getToken());
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("Email verified successfully")
                .build());
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Request password reset")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request.getEmail());
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("Password reset email sent")
                .build());
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password with token")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("Password reset successfully")
                .build());
    }

    @PostMapping("/enable-2fa")
    @Operation(summary = "Enable two-factor authentication")
    public ResponseEntity<ApiResponse<String>> enableTwoFactor() {
        String secret = authService.enableTwoFactor();
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .message("2FA enabled. Use this secret in your authenticator app.")
                .data(secret)
                .build());
    }

    @PostMapping("/verify-2fa")
    @Operation(summary = "Verify 2FA code")
    public ResponseEntity<ApiResponse<Void>> verifyTwoFactor(@Valid @RequestBody VerifyTwoFactorRequest request) {
        authService.verifyTwoFactor(request.getCode());
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("2FA verified successfully")
                .build());
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.<AuthResponse>builder()
                .message("Token refreshed successfully")
                .data(response)
                .build());
    }
}