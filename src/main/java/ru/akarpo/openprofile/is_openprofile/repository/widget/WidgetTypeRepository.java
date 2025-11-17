package ru.akarpo.openprofile.is_openprofile.repository.widget;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.akarpo.openprofile.is_openprofile.domain.widget.WidgetType;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WidgetTypeRepository extends JpaRepository<WidgetType, UUID> {
    Optional<WidgetType> findByCode(String code);
    Optional<WidgetType> findBySupportsBinding(Boolean supportsBinding);
}