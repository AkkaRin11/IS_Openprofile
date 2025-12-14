package ru.akarpo.openprofile.is_openprofile.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Внешние сервисы", description = "Справочные данные и операции с внешними сервисами")
public class ExternalServiceController {

        private final ExternalServiceService externalServiceService;

        @GetMapping
        @Operation(summary = "Получить все внешние сервисы", description = "Возвращает список всех поддерживаемых внешних сервисов (например, YouTube, GitHub, Telegram).")
        public ResponseEntity<ApiResponse<List<ExternalServiceDTO>>> getAllServices() {
                List<ExternalServiceDTO> services = externalServiceService.findAll();
                return ResponseEntity.ok(ApiResponse.<List<ExternalServiceDTO>>builder()
                                .data(services)
                                .build());
        }

        @GetMapping("/{id}")
        @Operation(summary = "Получить сервис по ID", description = "Возвращает информацию о внешнем сервисе по его уникальному идентификатору.")
        public ResponseEntity<ApiResponse<ExternalServiceDTO>> getServiceById(@PathVariable UUID id) {
                return externalServiceService.findById(id)
                                .map(service -> ResponseEntity.ok(ApiResponse.<ExternalServiceDTO>builder()
                                                .data(service)
                                                .build()))
                                .orElse(ResponseEntity.notFound().build());
        }

        @GetMapping("/code/{code}")
        @Operation(summary = "Получить сервис по коду", description = "Возвращает информацию о внешнем сервисе по его кодовому названию (например, 'github' или 'telegram').")
        public ResponseEntity<ApiResponse<ExternalServiceDTO>> getServiceByCode(@PathVariable String code) {
                return externalServiceService.findByCode(code)
                                .map(service -> ResponseEntity.ok(ApiResponse.<ExternalServiceDTO>builder()
                                                .data(service)
                                                .build()))
                                .orElse(ResponseEntity.notFound().build());
        }
}