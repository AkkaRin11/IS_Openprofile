package ru.akarpo.openprofile.is_openprofile.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.akarpo.openprofile.is_openprofile.dto.ExternalServiceDTO;
import ru.akarpo.openprofile.is_openprofile.schema.response.ApiResponse;
import ru.akarpo.openprofile.is_openprofile.service.ExternalServiceService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/external-services")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Администрирование сервисов", description = "Управление списком поддерживаемых внешних сервисов")
public class AdminExternalServiceController {

    private final ExternalServiceService externalServiceService;

    @GetMapping
    @Operation(summary = "Получить все сервисы", description = "Возвращает полный список внешних сервисов для администрирования.")
    public ResponseEntity<ApiResponse<List<ExternalServiceDTO>>> getAll() {
        return ResponseEntity.ok(ApiResponse.<List<ExternalServiceDTO>>builder()
                .data(externalServiceService.findAll())
                .build());
    }

    @PostMapping
    @Operation(summary = "Создать сервис", description = "Регистрирует новый внешний сервис в системе (например, добавляет поддержку GitLab).")
    public ResponseEntity<ApiResponse<ExternalServiceDTO>> create(@RequestBody ExternalServiceDTO dto) {
        ExternalServiceDTO saved = externalServiceService.save(dto);
        return ResponseEntity.ok(ApiResponse.<ExternalServiceDTO>builder()
                .message("External service created successfully")
                .data(saved)
                .build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить сервис", description = "Обновляет параметры существующего внешнего сервиса.")
    public ResponseEntity<ApiResponse<ExternalServiceDTO>> update(@PathVariable UUID id,
            @RequestBody ExternalServiceDTO dto) {
        dto.setId(id);
        ExternalServiceDTO saved = externalServiceService.save(dto);
        return ResponseEntity.ok(ApiResponse.<ExternalServiceDTO>builder()
                .message("External service updated successfully")
                .data(saved)
                .build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить сервис", description = "Удаляет внешний сервис из справочника. Внимание: это может нарушить работу существующих интеграций пользователей.")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        externalServiceService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("External service deleted successfully")
                .build());
    }
}
