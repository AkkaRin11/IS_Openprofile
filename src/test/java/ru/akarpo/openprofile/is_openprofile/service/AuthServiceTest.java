package ru.akarpo.openprofile.is_openprofile.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.akarpo.openprofile.is_openprofile.domain.User;
import ru.akarpo.openprofile.is_openprofile.domain.VerificationToken;
import ru.akarpo.openprofile.is_openprofile.exception.BadRequestException;
import ru.akarpo.openprofile.is_openprofile.repository.UserRepository;
import ru.akarpo.openprofile.is_openprofile.repository.VerificationTokenRepository;
import ru.akarpo.openprofile.is_openprofile.schema.request.LoginRequest;
import ru.akarpo.openprofile.is_openprofile.schema.request.RegisterRequest;
import ru.akarpo.openprofile.is_openprofile.schema.response.AuthResponse;
import ru.akarpo.openprofile.is_openprofile.security.JwtService;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private VerificationTokenRepository tokenRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private EmailService emailService;
    @Mock
    private TwoFactorService twoFactorService;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_ShouldRegisterUser_WhenEmailIsUnique() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");

        User savedUser = User.builder()
                .id(UUID.randomUUID())
                .email(request.getEmail())
                .passwordHash("encodedPassword")
                .build();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        when(userDetailsService.loadUserByUsername(request.getEmail())).thenReturn(mock(UserDetails.class));
        when(jwtService.generateToken(any())).thenReturn("jwt-token");

        // Act
        AuthResponse response = authService.register(request);

        // Assert
        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getEmail()).isEqualTo(request.getEmail());
        verify(emailService).sendVerificationEmail(eq("test@example.com"), anyString());
        verify(tokenRepository).save(any(VerificationToken.class));
    }

    @Test
    void register_ShouldThrowException_WhenEmailAlreadyExists() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setEmail("existing@example.com");
        request.setPassword("password");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(new User()));

        // Act & Assert
        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Email already registered");

        verify(userRepository, never()).save(any());
        verify(emailService, never()).sendVerificationEmail(anyString(), anyString());
    }

    @Test
    void login_ShouldReturnToken_WhenCredentialsAreValid() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");

        User user = User.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .emailVerified(true)
                .twoFactorEnabled(false)
                .build();

        when(authenticationManager.authenticate(any())).thenReturn(mock(Authentication.class));
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(userDetailsService.loadUserByUsername(user.getEmail())).thenReturn(mock(UserDetails.class));
        when(jwtService.generateToken(any())).thenReturn("jwt-token");

        // Act
        AuthResponse response = authService.login(request);

        // Assert
        assertThat(response.getToken()).isEqualTo("jwt-token");
    }

    @Test
    void login_ShouldThrowException_WhenNoteVerified() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");

        User user = User.builder()
                .emailVerified(false)
                .build();

        when(authenticationManager.authenticate(any())).thenReturn(mock(Authentication.class));
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));

        // Act & Assert
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Please verify your email before logging in");
    }

    @Test
    void verifyEmail_ShouldVerifyUser_WhenTokenIsValid() {
        // Arrange
        String tokenString = "valid-token";
        User user = new User();
        user.setEmailVerified(false);

        VerificationToken token = VerificationToken.builder()
                .token(tokenString)
                .user(user)
                .expiryDate(Instant.now().plusSeconds(3600))
                .type("EMAIL_VERIFICATION")
                .build();

        when(tokenRepository.findByTokenAndType(tokenString, "EMAIL_VERIFICATION")).thenReturn(Optional.of(token));

        // Act
        authService.verifyEmail(tokenString);

        // Assert
        assertThat(user.isEmailVerified()).isTrue();
        verify(userRepository).save(user);
        verify(tokenRepository).delete(token);
    }
}
