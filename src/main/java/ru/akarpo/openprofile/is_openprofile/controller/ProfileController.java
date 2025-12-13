package ru.akarpo.openprofile.is_openprofile.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.akarpo.openprofile.is_openprofile.domain.User;
import ru.akarpo.openprofile.is_openprofile.dto.profile.ProfileDTO;
import ru.akarpo.openprofile.is_openprofile.enm.PrivacyLevel;
import ru.akarpo.openprofile.is_openprofile.exception.BadRequestException;
import ru.akarpo.openprofile.is_openprofile.exception.ResourceNotFoundException;
import ru.akarpo.openprofile.is_openprofile.repository.UserRepository;
import ru.akarpo.openprofile.is_openprofile.schema.request.CreateProfileRequest;
import ru.akarpo.openprofile.is_openprofile.schema.request.UpdateProfileRequest;
import ru.akarpo.openprofile.is_openprofile.schema.response.ApiResponse;
import ru.akarpo.openprofile.is_openprofile.service.profile.ProfileService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
@Tag(name = "Профили", description = "Управление профилями пользователей (CRUD)")
public class ProfileController {

    private final ProfileService profileService;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    private PrivacyLevel parsePrivacyLevel(String privacy) {
        if (privacy == null || privacy.isBlank()) {
            return PrivacyLevel.PUBLIC;
        }
        try {
            return PrivacyLevel.valueOf(privacy.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(
                    "Invalid privacy level: " + privacy + ". Allowed values: public, unlisted, private");
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить профиль по ID", description = "Возвращает детали профиля по его уникальному UUID.")
    public ResponseEntity<ApiResponse<ProfileDTO>> getProfileById(@PathVariable UUID id) {
        return profileService.findById(id)
                .map(profile -> ResponseEntity.ok(ApiResponse.<ProfileDTO>builder()
                        .data(profile)
                        .build()))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Получить профиль по Slug", description = "Возвращает детали профиля по его уникальному URL slug.")
    public ResponseEntity<ApiResponse<ProfileDTO>> getProfileBySlug(@PathVariable String slug) {
        return profileService.findBySlug(slug)
                .map(profile -> ResponseEntity.ok(ApiResponse.<ProfileDTO>builder()
                        .data(profile)
                        .build()))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Получить профили по ID пользователя", description = "Возвращает все профили, принадлежащие конкретному пользователю.")
    public ResponseEntity<ApiResponse<List<ProfileDTO>>> getProfilesByUser(@PathVariable UUID userId) {
        List<ProfileDTO> profiles = profileService.findByUserId(userId);
        return ResponseEntity.ok(ApiResponse.<List<ProfileDTO>>builder()
                .data(profiles)
                .build());
    }

    @GetMapping("/me")
    @Operation(summary = "Получить мои профили", description = "Возвращает все профили текущего аутентифицированного пользователя.")
    public ResponseEntity<ApiResponse<List<ProfileDTO>>> getMyProfiles() {
        User user = getCurrentUser();
        List<ProfileDTO> profiles = profileService.findByUserId(user.getId());
        return ResponseEntity.ok(ApiResponse.<List<ProfileDTO>>builder()
                .data(profiles)
                .build());
    }

    @PostMapping
    @Operation(summary = "Создать профиль", description = "Создает новый профиль для текущего аутентифицированного пользователя.")
    public ResponseEntity<ApiResponse<ProfileDTO>> createProfile(@RequestBody CreateProfileRequest request) {
        User user = getCurrentUser();

        ProfileDTO profileDTO = ProfileDTO.builder()
                .userId(user.getId())
                .name(request.getName())
                .slug(request.getSlug())
                .privacy(parsePrivacyLevel(request.getPrivacy()))
                .themeId(request.getThemeId())
                .build();
        ProfileDTO saved = profileService.save(profileDTO);
        return ResponseEntity.ok(ApiResponse.<ProfileDTO>builder()
                .message("Profile created successfully")
                .data(saved)
                .build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить профиль", description = "Обновляет существующий профиль. Будут обновлены только поля, указанные в теле запроса.")
    public ResponseEntity<ApiResponse<ProfileDTO>> updateProfile(@PathVariable UUID id,
            @RequestBody UpdateProfileRequest request) {
        return profileService.findById(id)
                .map(existing -> {
                    ProfileDTO updated = ProfileDTO.builder()
                            .id(id)
                            .name(request.getName() != null ? request.getName() : existing.getName())
                            .slug(existing.getSlug())
                            .privacy(request.getPrivacy() != null ? parsePrivacyLevel(request.getPrivacy())
                                    : existing.getPrivacy())
                            .themeId(request.getThemeId() != null ? request.getThemeId() : existing.getThemeId())
                            .userId(existing.getUserId())
                            .createdAt(existing.getCreatedAt())
                            .build();
                    ProfileDTO saved = profileService.save(updated);
                    return ResponseEntity.ok(ApiResponse.<ProfileDTO>builder()
                            .message("Profile updated successfully")
                            .data(saved)
                            .build());
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить профиль", description = "Безвозвратно удаляет профиль и все связанные с ним данные.")
    public ResponseEntity<ApiResponse<Void>> deleteProfile(@PathVariable UUID id) {
        if (profileService.findById(id).isPresent()) {
            profileService.deleteById(id);
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .message("Profile deleted successfully")
                    .build());
        }
        return ResponseEntity.notFound().build();
    }
}