package ru.akarpo.openprofile.is_openprofile.domain.widget;

import jakarta.persistence.*;
import lombok.*;
import ru.akarpo.openprofile.is_openprofile.domain.ServiceConnection;
import ru.akarpo.openprofile.is_openprofile.domain.profile.ProfileWidget;

@Entity
@Table(name = "widget_bindings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WidgetBinding {

    @EmbeddedId
    private WidgetBindingId id;

    @ManyToOne(optional = false)
    @MapsId("profileWidgetId")
    @JoinColumn(name = "profile_widget_id",
            foreignKey = @ForeignKey(name = "fk_binding_widget"))
    private ProfileWidget profileWidget;

    @ManyToOne(optional = false)
    @MapsId("connectionId")
    @JoinColumn(name = "connection_id",
            foreignKey = @ForeignKey(name = "fk_binding_connection"))
    private ServiceConnection connection;
}
