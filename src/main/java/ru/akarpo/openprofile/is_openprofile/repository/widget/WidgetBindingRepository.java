package ru.akarpo.openprofile.is_openprofile.repository.widget;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.akarpo.openprofile.is_openprofile.domain.widget.WidgetBinding;
import ru.akarpo.openprofile.is_openprofile.domain.widget.WidgetBindingId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WidgetBindingRepository extends JpaRepository<WidgetBinding, WidgetBindingId> {
    List<WidgetBinding> findAllByProfileWidgetId(UUID id);
    List<WidgetBinding> findAllByConnectionId(UUID connectionId);
    void deleteByProfileWidgetIdAndConnectionId(UUID profileWidgetId, UUID connectionId);
    Optional<WidgetBinding> findByProfileWidgetIdAndConnectionId(UUID profileWidgetId, UUID connectionId);
}