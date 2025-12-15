package ru.akarpo.openprofile.is_openprofile.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.akarpo.openprofile.is_openprofile.domain.User;
import ru.akarpo.openprofile.is_openprofile.domain.VerificationToken;
import ru.akarpo.openprofile.is_openprofile.exception.BadRequestException;
import ru.akarpo.openprofile.is_openprofile.exception.ResourceNotFoundException;
import ru.akarpo.openprofile.is_openprofile.repository.UserRepository;
import ru.akarpo.openprofile.is_openprofile.repository.VerificationTokenRepository;
import ru.akarpo.openprofile.is_openprofile.schema.request.LoginRequest;
import ru.akarpo.openprofile.is_openprofile.schema.request.RegisterRequest;
import ru.akarpo.openprofile.is_openprofile.schema.request.ResetPasswordRequest;
import ru.akarpo.openprofile.is_openprofile.schema.response.AuthResponse;
import ru.akarpo.openprofile.is_openprofile.security.JwtService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final VerificationTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final EmailService emailService;
    private final TwoFactorService twoFactorService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("Email already registered");
        }

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .twoFactorEnabled(false)
                .emailVerified(false)
                .build();

        user = userRepository.save(user);

        String verificationToken = UUID.randomUUID().toString();
        VerificationToken token = VerificationToken.builder()
                .token(verificationToken)
                .user(user)
                .expiryDate(Instant.now().plus(24, ChronoUnit.HOURS))
                .type("EMAIL_VERIFICATION")
                .build();
        tokenRepository.save(token);

        emailService.sendVerificationEmail(user.getEmail(), verificationToken);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());

        java.util.Map<String, Object> extraClaims = new java.util.HashMap<>();
        // extraClaims.put("is_email_verified", false); // Moved to DB check
        extraClaims.put("is_2fa_verified", true); // No 2FA yet

        String jwtToken = jwtService.generateToken(extraClaims, userDetails);

        return AuthResponse.builder()
                .token(jwtToken)
                .email(user.getEmail())
                .userId(user.getId())
                .build();
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.getEmail()));

        if (!user.isEmailVerified()) {
            // throw new BadRequestException("Please verify your email before logging in");
            // Allow login but with restricted token
        }

        boolean is2faVerified = !user.isTwoFactorEnabled();

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());

        java.util.Map<String, Object> extraClaims = new java.util.HashMap<>();
        extraClaims.put("is_2fa_verified", is2faVerified);
        // extraClaims.put("is_email_verified", user.isEmailVerified());

        String jwtToken = jwtService.generateToken(extraClaims, userDetails);

        return AuthResponse.builder()
                .token(jwtToken)
                .email(user.getEmail())
                .userId(user.getId())
                .build();
    }

    @Transactional
    public void verifyEmail(String token) {
        VerificationToken verificationToken = tokenRepository.findByTokenAndType(token, "EMAIL_VERIFICATION")
                .orElseThrow(() -> new BadRequestException("Invalid verification token"));

        if (verificationToken.getExpiryDate().isBefore(Instant.now())) {
            tokenRepository.delete(verificationToken);
            throw new BadRequestException("Verification token expired");
        }

        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);

        tokenRepository.delete(verificationToken);
    }

    @Transactional
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        String resetToken = UUID.randomUUID().toString();
        VerificationToken token = VerificationToken.builder()
                .token(resetToken)
                .user(user)
                .expiryDate(Instant.now().plus(1, ChronoUnit.HOURS))
                .type("PASSWORD_RESET")
                .build();
        tokenRepository.save(token);

        emailService.sendPasswordResetEmail(email, resetToken);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        VerificationToken verificationToken = tokenRepository.findByTokenAndType(request.getToken(), "PASSWORD_RESET")
                .orElseThrow(() -> new BadRequestException("Invalid reset token"));

        if (verificationToken.getExpiryDate().isBefore(Instant.now())) {
            tokenRepository.delete(verificationToken);
            throw new BadRequestException("Reset token expired");
        }

        User user = verificationToken.getUser();
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        tokenRepository.delete(verificationToken);
    }

    @Transactional
    public String enableTwoFactor() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        String secret = twoFactorService.generateSecret();
        user.setTwoFactorSecret(secret);
        user.setTwoFactorEnabled(true);
        userRepository.save(user);

        return secret;
    }

    @Transactional
    public void verifyTwoFactor(String code) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        boolean valid = twoFactorService.verifyCode(user.getTwoFactorSecret(), code);
        if (!valid) {
            throw new BadRequestException("Invalid two-factor code");
        }
    }

    @Transactional
    public AuthResponse completeTwoFactorLogin(String code) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        boolean valid = twoFactorService.verifyCode(user.getTwoFactorSecret(), code);
        if (!valid) {
            throw new BadRequestException("Invalid two-factor code");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        java.util.Map<String, Object> extraClaims = new java.util.HashMap<>();
        extraClaims.put("is_2fa_verified", true);
        // extraClaims.put("is_email_verified", user.isEmailVerified());

        String jwtToken = jwtService.generateToken(extraClaims, userDetails);

        return AuthResponse.builder()
                .token(jwtToken)
                .email(user.getEmail())
                .userId(user.getId())
                .build();
    }

    public AuthResponse refreshToken(String refreshToken) {
        throw new BadRequestException("Refresh token not implemented yet");
    }

    public boolean getTwoFactorStatus() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return user.isTwoFactorEnabled();
    }
}