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

    @org.springframework.context.event.EventListener(org.springframework.boot.context.event.ApplicationReadyEvent.class)
    @Transactional
    public void initializeSyncStatuses() {
        log.info("Initializing widget sync statuses...");
        List<ProfileWidget> widgetsWithBindings = widgetRepository.findAllWithBindings();
        int createdCount = 0;

        for (ProfileWidget widget : widgetsWithBindings) {
            if (syncStatusRepository.findByWidgetId(widget.getId()).isEmpty()) {
                createSyncStatus(widget.getId());
                createdCount++;
            }
        }

        log.info("Initialized sync statuses. Created {} new statuses for {} bound widgets.", createdCount,
                widgetsWithBindings.size());
    }

    @Transactional
    public void createSyncStatus(UUID widgetId) {
        ProfileWidget widget = widgetRepository.findById(widgetId)
                .orElseThrow(() -> new RuntimeException("Widget not found"));

        log.info("Creating/updating sync status for widget: {} (type: {})", widgetId, widget.getWidgetType().getCode());

        WidgetSyncStatus syncStatus = syncStatusRepository.findByWidgetId(widgetId)
                .orElse(null);

        if (syncStatus == null) {
            syncStatus = WidgetSyncStatus.builder()
                    .widget(widget)
                    .syncStatus("PENDING")
                    .retryCount(0)
                    .nextSyncAt(Instant.now())
                    .build();
            log.info("Creating new sync status for widget: {}", widgetId);
        } else {
            syncStatus.setSyncStatus("PENDING");
            syncStatus.setNextSyncAt(Instant.now());
            log.info("Updating existing sync status for widget: {}, resetting nextSyncAt to now", widgetId);
        }

        syncStatusRepository.save(syncStatus);
        log.info("Sync status saved with ID: {} for widget: {}", syncStatus.getId(), widgetId);
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

                ProfileWidget widget = widgetRepository.findById(widgetId)
                        .orElseThrow(() -> new RuntimeException("Widget not found: " + widgetId));

                String apiEndpoint = widget.getWidgetType().getApiEndpoint();
                if (apiEndpoint == null || apiEndpoint.isBlank()) {
                    log.warn("Widget type {} has no API endpoint, skipping sync", widget.getWidgetType().getCode());
                    updateSyncStatus(widgetId, "SUCCESS", null);
                    continue;
                }

                String accessToken = getAccessTokenForWidget(widget);
                if (accessToken == null) {
                    log.warn("No access token found for widget {}, skipping sync", widgetId);
                    updateSyncStatus(widgetId, "SUCCESS", null);
                    continue;
                }

                java.util.Map<String, Object> uriVariables = new java.util.HashMap<>();
                if (widget.getSettings() != null && widget.getSettings().isObject()) {
                    widget.getSettings().fields()
                            .forEachRemaining(entry -> uriVariables.put(entry.getKey(), entry.getValue().asText()));
                }

                log.info("Fetching data from: {} with params: {}", apiEndpoint, uriVariables);
                String responseData = fetchExternalData(apiEndpoint, accessToken, uriVariables);

                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                com.fasterxml.jackson.databind.JsonNode cachedData = mapper.readTree(responseData);
                widget.setCachedData(cachedData);
                widgetRepository.save(widget);

                log.info("Sync completed successfully for widget: {}, cached {} bytes", widgetId,
                        responseData.length());
                updateSyncStatus(widgetId, "SUCCESS", null);
            } catch (IllegalArgumentException e) {
                log.warn("Skipping sync for widget {}: Missing required settings for API URL - {}", widgetId,
                        e.getMessage());
                updateSyncStatus(widgetId, "ERROR", "Configuration error: " + e.getMessage());
            } catch (Exception e) {
                log.error("Failed to sync widget {}: {}", widgetId, e.getMessage());
                log.debug("Full stack trace for widget " + widgetId, e);
                updateSyncStatus(widgetId, "ERROR", e.getMessage());
            }
        }

        log.info("Sync batch completed. Processed {} widget(s)", pendingSyncs.size());
    }

    private String getAccessTokenForWidget(ProfileWidget widget) {
        if (widget.getBindings().isEmpty()) {
            return null;
        }
        return widget.getBindings().get(0).getConnection().getAccessToken();
    }

    private String fetchExternalData(String apiEndpoint, String accessToken, java.util.Map<String, Object> uriVariables)
            throws Exception {
        org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();

        if (apiEndpoint.contains("wakatime.com")) {
            String auth = accessToken + ":";
            String encodedAuth = java.util.Base64.getEncoder().encodeToString(auth.getBytes());
            headers.set("Authorization", "Basic " + encodedAuth);
            log.debug("Using Basic auth for WakaTime");
        } else {
            headers.set("Authorization", "Bearer " + accessToken);
            log.debug("Using Bearer token");
        }

        org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(headers);

        org.springframework.http.ResponseEntity<String> response = restTemplate.exchange(
                apiEndpoint,
                org.springframework.http.HttpMethod.GET,
                entity,
                String.class,
                uriVariables);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("API request failed with status: " + response.getStatusCode());
        }

        return response.getBody();
    }

    public List<WidgetSyncStatus> getStatusByProfile(UUID profileId) {
        return syncStatusRepository.findAll().stream()
                .filter(status -> status.getWidget().getProfile().getId().equals(profileId))
                .toList();
    }
}