package ru.akarpo.openprofile.is_openprofile.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.akarpo.openprofile.is_openprofile.dto.ExternalServiceDTO;
import ru.akarpo.openprofile.is_openprofile.schema.response.ApiResponse;
import ru.akarpo.openprofile.is_openprofile.service.ExternalServiceService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/external-services")
@RequiredArgsConstructor
public class ExternalServiceController {

    private final ExternalServiceService externalServiceService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ExternalServiceDTO>>> getAllServices() {
        List<ExternalServiceDTO> services = externalServiceService.findAll();
        return ResponseEntity.ok(ApiResponse.<List<ExternalServiceDTO>>builder()
                .data(services)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ExternalServiceDTO>> getServiceById(@PathVariable UUID id) {
        return externalServiceService.findById(id)
                .map(service -> ResponseEntity.ok(ApiResponse.<ExternalServiceDTO>builder()
                        .data(service)
                        .build()))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<ExternalServiceDTO>> getServiceByCode(@PathVariable String code) {
        return externalServiceService.findByCode(code)
                .map(service -> ResponseEntity.ok(ApiResponse.<ExternalServiceDTO>builder()
                        .data(service)
                        .build()))
                .orElse(ResponseEntity.notFound().build());
    }
}