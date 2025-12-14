package ru.akarpo.openprofile.is_openprofile.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.akarpo.openprofile.is_openprofile.dto.widget.WidgetSyncStatusDTO;
import ru.akarpo.openprofile.is_openprofile.schema.response.ApiResponse;
import ru.akarpo.openprofile.is_openprofile.service.widget.WidgetSyncService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/widget-sync")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Синхронизация виджетов", description = "Мониторинг и управление статусами синхронизации виджетов")
public class WidgetSyncStatusController {

    private final WidgetSyncService widgetSyncService;

    @GetMapping("/profile/{profileId}")
    @Transactional(readOnly = true)
    @Operation(summary = "Статус синхронизации профиля", description = "Возвращает текущие статусы синхронизации для всех виджетов в указанном профиле.")
    public ResponseEntity<ApiResponse<List<WidgetSyncStatusDTO>>> getProfileWidgetStatus(@PathVariable UUID profileId) {
        log.info("GET /api/widget-sync/profile/{} - Fetching sync statuses", profileId);

        List<WidgetSyncStatusDTO> statuses = widgetSyncService.getStatusByProfile(profileId).stream()
                .map(status -> WidgetSyncStatusDTO.builder()
                        .id(status.getId())
                        .widgetId(status.getWidget().getId())
                        .syncStatus(status.getSyncStatus())
                        .lastSyncAt(status.getLastSyncAt())
                        .nextSyncAt(status.getNextSyncAt())
                        .errorMessage(status.getErrorMessage())
                        .retryCount(status.getRetryCount())
                        .build())
                .toList();

        log.info("GET /api/widget-sync/profile/{} - Returning {} sync status(es)", profileId, statuses.size());

        return ResponseEntity.ok(ApiResponse.<List<WidgetSyncStatusDTO>>builder()
                .data(statuses)
                .build());
    }

    @PostMapping("/widget/{widgetId}/trigger")
    @Operation(summary = "Запустить синхронизацию", description = "Принудительно запускает процесс синхронизации данных для указанного виджета.")
    public ResponseEntity<ApiResponse<Void>> triggerSync(@PathVariable UUID widgetId) {
        log.info("POST /api/widget-sync/widget/{}/trigger - Triggering manual sync", widgetId);

        widgetSyncService.createSyncStatus(widgetId);

        log.info("POST /api/widget-sync/widget/{}/trigger - Sync triggered successfully", widgetId);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("Sync triggered")
                .build());
    }
}