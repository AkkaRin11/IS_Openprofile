package ru.akarpo.openprofile.is_openprofile.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.akarpo.openprofile.is_openprofile.dto.profile.PublicProfileViewDTO;
import ru.akarpo.openprofile.is_openprofile.schema.response.ApiResponse;
import ru.akarpo.openprofile.is_openprofile.service.profile.PublicProfileViewService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/public-profiles")
@RequiredArgsConstructor
public class PublicProfileController {

    private final PublicProfileViewService publicProfileViewService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PublicProfileViewDTO>>> getAllPublicProfiles() {
        List<PublicProfileViewDTO> profiles = publicProfileViewService.findAll();
        return ResponseEntity.ok(ApiResponse.<List<PublicProfileViewDTO>>builder()
                .data(profiles)
                .build());
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<ApiResponse<PublicProfileViewDTO>> getPublicProfileBySlug(@PathVariable String slug) {
        return publicProfileViewService.findBySlug(slug)
                .map(profile -> ResponseEntity.ok(ApiResponse.<PublicProfileViewDTO>builder()
                        .data(profile)
                        .build()))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/profile/{profileId}")
    public ResponseEntity<ApiResponse<PublicProfileViewDTO>> getPublicProfileByProfileId(@PathVariable UUID profileId) {
        return publicProfileViewService.findByProfileId(profileId)
                .map(profile -> ResponseEntity.ok(ApiResponse.<PublicProfileViewDTO>builder()
                        .data(profile)
                        .build()))
                .orElse(ResponseEntity.notFound().build());
    }
}