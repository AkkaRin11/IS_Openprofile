package ru.akarpo.openprofile.is_openprofile.service.widget;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.akarpo.openprofile.is_openprofile.domain.profile.ProfileWidget;
import ru.akarpo.openprofile.is_openprofile.domain.widget.WidgetSyncStatus;
import ru.akarpo.openprofile.is_openprofile.repository.profile.ProfileWidgetRepository;
import ru.akarpo.openprofile.is_openprofile.repository.widget.WidgetSyncStatusRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WidgetSyncService {

    private final WidgetSyncStatusRepository syncStatusRepository;
    private final ProfileWidgetRepository widgetRepository;

    @Transactional
    public void createSyncStatus(UUID widgetId) {
        ProfileWidget widget = widgetRepository.findById(widgetId)
                .orElseThrow(() -> new RuntimeException("Widget not found"));

        log.info("Creating sync status for widget: {} (type: {})", widgetId, widget.getWidgetType().getCode());

        WidgetSyncStatus syncStatus = WidgetSyncStatus.builder()
                .widget(widget)
                .syncStatus("PENDING")
                .retryCount(0)
                .nextSyncAt(Instant.now())
                .build();

        syncStatusRepository.save(syncStatus);
        log.info("Sync status created with ID: {} for widget: {}", syncStatus.getId(), widgetId);
    }

    @Transactional
    public void updateSyncStatus(UUID widgetId, String status, String errorMessage) {
        WidgetSyncStatus syncStatus = syncStatusRepository.findByWidgetId(widgetId)
                .orElseGet(() -> {
                    log.warn("Sync status not found for widget: {}, creating new one", widgetId);
                    createSyncStatus(widgetId);
                    return syncStatusRepository.findByWidgetId(widgetId).orElseThrow();
                });

        log.info("Updating sync status for widget: {} from {} to {}", widgetId, syncStatus.getSyncStatus(), status);

        syncStatus.setSyncStatus(status);
        syncStatus.setLastSyncAt(Instant.now());
        syncStatus.setErrorMessage(errorMessage);

        if ("ERROR".equals(status)) {
            syncStatus.setRetryCount(syncStatus.getRetryCount() + 1);
            long delayMinutes = Math.min(60, (long) Math.pow(2, syncStatus.getRetryCount()));
            syncStatus.setNextSyncAt(Instant.now().plus(delayMinutes, ChronoUnit.MINUTES));
            log.warn("Widget {} sync failed (attempt {}). Next retry in {} minutes. Error: {}",
                    widgetId, syncStatus.getRetryCount(), delayMinutes, errorMessage);
        } else if ("SUCCESS".equals(status)) {
            syncStatus.setRetryCount(0);
            syncStatus.setNextSyncAt(Instant.now().plus(15, ChronoUnit.MINUTES));
            log.info("Widget {} synced successfully. Next sync at {}", widgetId, syncStatus.getNextSyncAt());
        }

        syncStatusRepository.save(syncStatus);
    }

    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void processPendingSyncs() {
        List<WidgetSyncStatus> pendingSyncs = syncStatusRepository.findByNextSyncAtBefore(Instant.now());

        if (pendingSyncs.isEmpty()) {
            log.debug("No pending syncs found");
            return;
        }

        log.info("Found {} widget(s) to sync", pendingSyncs.size());

        for (WidgetSyncStatus syncStatus : pendingSyncs) {
            UUID widgetId = syncStatus.getWidget().getId();
            try {
                log.info("Starting sync for widget: {} (status: {})", widgetId, syncStatus.getSyncStatus());

                // TODO: Здесь должна быть реальная логика синхронизации с внешним API
                // Например:
                // 1. Получить connection через widget.bindings
                // 2. Взять accessToken из connection
                // 3. Сделать HTTP запрос к API сервиса
                // 4. Сохранить полученные данные

                log.info("Sync completed successfully for widget: {}", widgetId);
                updateSyncStatus(widgetId, "SUCCESS", null);
            } catch (Exception e) {
                log.error("Failed to sync widget: {} - Error: {}", widgetId, e.getMessage(), e);
                updateSyncStatus(widgetId, "ERROR", e.getMessage());
            }
        }

        log.info("Sync batch completed. Processed {} widget(s)", pendingSyncs.size());
    }

    public List<WidgetSyncStatus> getStatusByProfile(UUID profileId) {
        return syncStatusRepository.findAll().stream()
                .filter(status -> status.getWidget().getProfile().getId().equals(profileId))
                .toList();
    }
}