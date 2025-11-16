package ru.akarpo.openprofile.is_openprofile.repository.widget;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.akarpo.openprofile.is_openprofile.domain.widget.WidgetBinding;
import ru.akarpo.openprofile.is_openprofile.domain.widget.WidgetBindingId;

@Repository
public interface WidgetBindingRepository extends JpaRepository<WidgetBinding, WidgetBindingId> {
}