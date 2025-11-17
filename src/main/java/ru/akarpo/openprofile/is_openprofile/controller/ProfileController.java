package ru.akarpo.openprofile.is_openprofile.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.akarpo.openprofile.is_openprofile.dto.profile.ProfileDTO;
import ru.akarpo.openprofile.is_openprofile.enm.PrivacyLevel;
import ru.akarpo.openprofile.is_openprofile.schema.request.CreateProfileRequest;
import ru.akarpo.openprofile.is_openprofile.schema.request.UpdateProfileRequest;
import ru.akarpo.openprofile.is_openprofile.schema.response.ApiResponse;
import ru.akarpo.openprofile.is_openprofile.service.profile.ProfileService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProfileDTO>>> getAllProfiles() {
        List<ProfileDTO> profiles = profileService.findAll();
        return ResponseEntity.ok(ApiResponse.<List<ProfileDTO>>builder()
                .data(profiles)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProfileDTO>> getProfileById(@PathVariable UUID id) {
        return profileService.findById(id)
                .map(profile -> ResponseEntity.ok(ApiResponse.<ProfileDTO>builder()
                        .data(profile)
                        .build()))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<ApiResponse<ProfileDTO>> getProfileBySlug(@PathVariable String slug) {
        return profileService.findBySlug(slug)
                .map(profile -> ResponseEntity.ok(ApiResponse.<ProfileDTO>builder()
                        .data(profile)
                        .build()))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<ProfileDTO>>> getProfilesByUser(@PathVariable UUID userId) {
        List<ProfileDTO> profiles = profileService.findByUserId(userId);
        return ResponseEntity.ok(ApiResponse.<List<ProfileDTO>>builder()
                .data(profiles)
                .build());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProfileDTO>> createProfile(@RequestBody CreateProfileRequest request) {
        ProfileDTO profileDTO = ProfileDTO.builder()
                .name(request.getName())
                .slug(request.getSlug())
                .privacy(PrivacyLevel.valueOf(request.getPrivacy()))
                .themeId(request.getThemeId())
                .build();
        ProfileDTO saved = profileService.save(profileDTO);
        return ResponseEntity.ok(ApiResponse.<ProfileDTO>builder()
                .message("Profile created successfully")
                .data(saved)
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProfileDTO>> updateProfile(@PathVariable UUID id,
                                                                 @RequestBody UpdateProfileRequest request) {
        return profileService.findById(id)
                .map(existing -> {
                    ProfileDTO updated = ProfileDTO.builder()
                            .id(id)
                            .name(request.getName())
                            .slug(existing.getSlug()) // slug не меняется
                            .privacy(PrivacyLevel.valueOf(request.getPrivacy()))
                            .themeId(request.getThemeId())
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