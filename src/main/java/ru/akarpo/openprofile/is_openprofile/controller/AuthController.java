package ru.akarpo.openprofile.is_openprofile.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
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

    @GetMapping(value = "/verify-email", produces = MediaType.TEXT_HTML_VALUE)
    @Operation(summary = "Verify email with token (GET - for email links)")
    public ResponseEntity<String> verifyEmailGet(@RequestParam String token) {
        try {
            authService.verifyEmail(token);
            String html = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Email Verified - OpenProfile</title>
                    <style>
                        body {
                            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                            display: flex;
                            justify-content: center;
                            align-items: center;
                            min-height: 100vh;
                            margin: 0;
                            background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                        }
                        .card {
                            background: white;
                            padding: 40px;
                            border-radius: 16px;
                            box-shadow: 0 10px 40px rgba(0,0,0,0.2);
                            text-align: center;
                            max-width: 400px;
                        }
                        .icon { font-size: 64px; margin-bottom: 20px; }
                        h1 { color: #1a1a1a; margin-bottom: 10px; }
                        p { color: #666; line-height: 1.6; }
                        .btn {
                            display: inline-block;
                            margin-top: 20px;
                            padding: 12px 30px;
                            background: #667eea;
                            color: white;
                            text-decoration: none;
                            border-radius: 8px;
                            font-weight: 500;
                        }
                        .btn:hover { background: #5a6fd6; }
                    </style>
                </head>
                <body>
                    <div class="card">
                        <h1>Email Verified!</h1>
                        <p>Your email has been successfully verified. You can now log in to your OpenProfile account.</p>
                        <a href="/" class="btn">Go to Homepage</a>
                    </div>
                </body>
                </html>
                """;
            return ResponseEntity.ok(html);
        } catch (Exception e) {
            String html = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Verification Failed - OpenProfile</title>
                    <style>
                        body {
                            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                            display: flex;
                            justify-content: center;
                            align-items: center;
                            min-height: 100vh;
                            margin: 0;
                            background: linear-gradient(135deg, #e74c3c 0%%, #c0392b 100%%);
                        }
                        .card {
                            background: white;
                            padding: 40px;
                            border-radius: 16px;
                            box-shadow: 0 10px 40px rgba(0,0,0,0.2);
                            text-align: center;
                            max-width: 400px;
                        }
                        .icon { font-size: 64px; margin-bottom: 20px; }
                        h1 { color: #1a1a1a; margin-bottom: 10px; }
                        p { color: #666; line-height: 1.6; }
                        .error { color: #e74c3c; font-size: 14px; margin-top: 10px; }
                        .btn {
                            display: inline-block;
                            margin-top: 20px;
                            padding: 12px 30px;
                            background: #667eea;
                            color: white;
                            text-decoration: none;
                            border-radius: 8px;
                            font-weight: 500;
                        }
                    </style>
                </head>
                <body>
                    <div class="card">
                        <h1>Verification Failed</h1>
                        <p>We couldn't verify your email. The link may have expired or already been used.</p>
                        <p class="error">%s</p>
                        <a href="/" class="btn">Go to Homepage</a>
                    </div>
                </body>
                </html>
                """.formatted(e.getMessage());
            return ResponseEntity.badRequest().body(html);
        }
    }

    @PostMapping("/verify-email")
    @Operation(summary = "Verify email with token (POST - for API)")
    public ResponseEntity<ApiResponse<Void>> verifyEmailPost(@Valid @RequestBody VerifyEmailRequest request) {
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