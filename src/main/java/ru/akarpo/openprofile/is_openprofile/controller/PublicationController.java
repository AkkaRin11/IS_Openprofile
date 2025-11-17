package ru.akarpo.openprofile.is_openprofile.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.akarpo.openprofile.is_openprofile.dto.PublicationDTO;
import ru.akarpo.openprofile.is_openprofile.schema.response.ApiResponse;
import ru.akarpo.openprofile.is_openprofile.service.PublicationService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/publications")
@RequiredArgsConstructor
public class PublicationController {

    private final PublicationService publicationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PublicationDTO>>> getAllPublications() {
        List<PublicationDTO> publications = publicationService.findAll();
        return ResponseEntity.ok(ApiResponse.<List<PublicationDTO>>builder()
                .data(publications)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PublicationDTO>> getPublicationById(@PathVariable UUID id) {
        return publicationService.findById(id)
                .map(publication -> ResponseEntity.ok(ApiResponse.<PublicationDTO>builder()
                        .data(publication)
                        .build()))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/profile/{profileId}")
    public ResponseEntity<ApiResponse<List<PublicationDTO>>> getPublicationsByProfile(@PathVariable UUID profileId) {
        List<PublicationDTO> publications = publicationService.findByProfileId(profileId);
        return ResponseEntity.ok(ApiResponse.<List<PublicationDTO>>builder()
                .data(publications)
                .build());
    }

    @GetMapping("/profile/{profileId}/active")
    public ResponseEntity<ApiResponse<PublicationDTO>> getActivePublicationByProfile(@PathVariable UUID profileId) {
        return publicationService.findActiveByProfileId(profileId)
                .map(publication -> ResponseEntity.ok(ApiResponse.<PublicationDTO>builder()
                        .data(publication)
                        .build()))
                .orElse(ResponseEntity.notFound().build());
    }
}
