package ru.akarpo.openprofile.is_openprofile.repository.widget;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.akarpo.openprofile.is_openprofile.domain.widget.WidgetSyncStatus;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WidgetSyncStatusRepository extends JpaRepository<WidgetSyncStatus, UUID> {
    Optional<WidgetSyncStatus> findByWidgetId(UUID widgetId);
    List<WidgetSyncStatus> findByNextSyncAtBefore(Instant time);
    List<WidgetSyncStatus> findBySyncStatus(String status);
}