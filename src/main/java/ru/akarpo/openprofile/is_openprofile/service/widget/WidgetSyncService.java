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

        WidgetSyncStatus syncStatus = WidgetSyncStatus.builder()
                .widget(widget)
                .syncStatus("PENDING")
                .retryCount(0)
                .nextSyncAt(Instant.now())
                .build();

        syncStatusRepository.save(syncStatus);
    }

    @Transactional
    public void updateSyncStatus(UUID widgetId, String status, String errorMessage) {
        WidgetSyncStatus syncStatus = syncStatusRepository.findByWidgetId(widgetId)
                .orElseGet(() -> {
                    createSyncStatus(widgetId);
                    return syncStatusRepository.findByWidgetId(widgetId).orElseThrow();
                });

        syncStatus.setSyncStatus(status);
        syncStatus.setLastSyncAt(Instant.now());
        syncStatus.setErrorMessage(errorMessage);

        if ("ERROR".equals(status)) {
            syncStatus.setRetryCount(syncStatus.getRetryCount() + 1);
            long delayMinutes = Math.min(60, (long) Math.pow(2, syncStatus.getRetryCount()));
            syncStatus.setNextSyncAt(Instant.now().plus(delayMinutes, ChronoUnit.MINUTES));
        } else if ("SUCCESS".equals(status)) {
            syncStatus.setRetryCount(0);
            syncStatus.setNextSyncAt(Instant.now().plus(15, ChronoUnit.MINUTES));
        }

        syncStatusRepository.save(syncStatus);
    }

    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void processPendingSyncs() {
        List<WidgetSyncStatus> pendingSyncs = syncStatusRepository.findByNextSyncAtBefore(Instant.now());

        for (WidgetSyncStatus syncStatus : pendingSyncs) {
            try {
                log.info("Syncing widget: {}", syncStatus.getWidget().getId());
                updateSyncStatus(syncStatus.getWidget().getId(), "SUCCESS", null);
            } catch (Exception e) {
                log.error("Failed to sync widget: {}", syncStatus.getWidget().getId(), e);
                updateSyncStatus(syncStatus.getWidget().getId(), "ERROR", e.getMessage());
            }
        }
    }

    public List<WidgetSyncStatus> getStatusByProfile(UUID profileId) {
        return syncStatusRepository.findAll().stream()
                .filter(status -> status.getWidget().getProfile().getId().equals(profileId))
                .toList();
    }
}