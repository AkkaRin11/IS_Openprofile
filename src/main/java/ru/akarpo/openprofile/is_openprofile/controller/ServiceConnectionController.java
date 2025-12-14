package ru.akarpo.openprofile.is_openprofile.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.akarpo.openprofile.is_openprofile.dto.ServiceConnectionDTO;
import ru.akarpo.openprofile.is_openprofile.schema.response.ApiResponse;
import ru.akarpo.openprofile.is_openprofile.service.ServiceConnectionService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/service-connections")
@RequiredArgsConstructor
@Tag(name = "Интеграции", description = "Подключения к внешним сервисам и синхронизация")
public class ServiceConnectionController {

    private final ServiceConnectionService serviceConnectionService;

    @GetMapping("/{id}")
    @Operation(summary = "Получить подключение", description = "Возвращает детали подключения к внешнему сервису по ID.")
    public ResponseEntity<ApiResponse<ServiceConnectionDTO>> getConnectionById(@PathVariable UUID id) {
        return serviceConnectionService.findById(id)
                .map(connection -> ResponseEntity.ok(ApiResponse.<ServiceConnectionDTO>builder()
                        .data(connection)
                        .build()))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Подключения пользователя", description = "Возвращает список всех подключенных внешних сервисов пользователя.")
    public ResponseEntity<ApiResponse<List<ServiceConnectionDTO>>> getConnectionsByUser(@PathVariable UUID userId) {
        List<ServiceConnectionDTO> connections = serviceConnectionService.findByUserId(userId);
        return ResponseEntity.ok(ApiResponse.<List<ServiceConnectionDTO>>builder()
                .data(connections)
                .build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить подключение", description = "Разрывает связь с внешним сервисом и удаляет сохраненные токены.")
    public ResponseEntity<ApiResponse<Void>> deleteConnection(@PathVariable UUID id) {
        if (serviceConnectionService.findById(id).isPresent()) {
            serviceConnectionService.deleteById(id);
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .message("Connection deleted successfully")
                    .build());
        }
        return ResponseEntity.notFound().build();
    }
}