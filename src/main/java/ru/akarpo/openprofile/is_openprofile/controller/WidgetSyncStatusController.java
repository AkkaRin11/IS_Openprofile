package ru.akarpo.openprofile.is_openprofile.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.akarpo.openprofile.is_openprofile.domain.widget.WidgetSyncStatus;
import ru.akarpo.openprofile.is_openprofile.schema.response.ApiResponse;
import ru.akarpo.openprofile.is_openprofile.service.widget.WidgetSyncService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/widget-sync")
@RequiredArgsConstructor
@Tag(name = "Widget Sync", description = "Widget synchronization status")
public class WidgetSyncStatusController {

    private final WidgetSyncService widgetSyncService;

    @GetMapping("/profile/{profileId}")
    @Operation(summary = "Get sync status for all widgets in profile")
    public ResponseEntity<ApiResponse<List<WidgetSyncStatus>>> getProfileWidgetStatus(@PathVariable UUID profileId) {
        List<WidgetSyncStatus> statuses = widgetSyncService.getStatusByProfile(profileId);
        return ResponseEntity.ok(ApiResponse.<List<WidgetSyncStatus>>builder()
                .data(statuses)
                .build());
    }

    @PostMapping("/widget/{widgetId}/trigger")
    @Operation(summary = "Manually trigger widget sync")
    public ResponseEntity<ApiResponse<Void>> triggerSync(@PathVariable UUID widgetId) {
        widgetSyncService.createSyncStatus(widgetId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("Sync triggered")
                .build());
    }
}