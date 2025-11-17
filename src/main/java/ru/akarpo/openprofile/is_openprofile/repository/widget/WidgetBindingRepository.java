package ru.akarpo.openprofile.is_openprofile.repository.widget;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.akarpo.openprofile.is_openprofile.domain.widget.WidgetBinding;
import ru.akarpo.openprofile.is_openprofile.domain.widget.WidgetBindingId;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WidgetBindingRepository extends JpaRepository<WidgetBinding, WidgetBindingId> {
    Optional<WidgetBinding> findByProfileWidgetId(UUID id);
    Optional<WidgetBinding> findByConnectionId(UUID connectionId);
    void deleteByProfileWidgetIdAndConnectionId(UUID profileWidgetId, UUID connectionId);
    Optional<WidgetBinding> findByProfileWidgetIdAndConnectionId(UUID profileWidgetId, UUID connectionId);
}