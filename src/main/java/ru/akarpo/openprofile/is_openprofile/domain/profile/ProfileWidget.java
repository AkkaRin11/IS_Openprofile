package ru.akarpo.openprofile.is_openprofile.domain.profile;

import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import ru.akarpo.openprofile.is_openprofile.domain.widget.WidgetBinding;
import ru.akarpo.openprofile.is_openprofile.domain.widget.WidgetType;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "profile_widgets", uniqueConstraints = {
                @UniqueConstraint(columnNames = { "profile_id", "position" })
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileWidget {

        @Id
        @GeneratedValue
        @UuidGenerator(style = UuidGenerator.Style.RANDOM)
        private UUID id;

        @ManyToOne(optional = false)
        @JoinColumn(name = "profile_id", nullable = false, foreignKey = @ForeignKey(name = "fk_widget_profile"))
        private Profile profile;

        @ManyToOne(optional = false)
        @JoinColumn(name = "widget_type_id", nullable = false, foreignKey = @ForeignKey(name = "fk_widget_type"))
        private WidgetType widgetType;

        private String title;

        @Type(JsonType.class)
        @Column(nullable = false, columnDefinition = "jsonb")
        private JsonNode settings;

        @Type(JsonType.class)
        @Column(nullable = false, columnDefinition = "jsonb")
        private JsonNode layout;

        @Type(JsonType.class)
        @Column(name = "cached_data", columnDefinition = "jsonb")
        private JsonNode cachedData;

        @Column(nullable = false)
        private int position;

        @CreationTimestamp
        @Column(name = "created_at", nullable = false, updatable = false)
        private Instant createdAt;

        @UpdateTimestamp
        @Column(name = "updated_at", nullable = false)
        private Instant updatedAt;

        /** widget_bindings.profile_widget_id */
        @OneToMany(mappedBy = "profileWidget", cascade = CascadeType.ALL, orphanRemoval = true)
        @Builder.Default
        private List<WidgetBinding> bindings = new ArrayList<>();

        @OneToOne(mappedBy = "widget", cascade = CascadeType.ALL, orphanRemoval = true)
        private ru.akarpo.openprofile.is_openprofile.domain.widget.WidgetSyncStatus syncStatus;
}
