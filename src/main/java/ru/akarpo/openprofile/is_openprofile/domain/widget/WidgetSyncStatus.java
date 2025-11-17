package ru.akarpo.openprofile.is_openprofile.domain.widget;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import ru.akarpo.openprofile.is_openprofile.domain.profile.ProfileWidget;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "widget_sync_status")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WidgetSyncStatus {

    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "widget_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_sync_widget"))
    private ProfileWidget widget;

    @Column(name = "last_sync_at")
    private Instant lastSyncAt;

    @Column(name = "sync_status", nullable = false)
    private String syncStatus;

    @Column(name = "error_message", length = 1000)
    private String errorMessage;

    @Column(name = "retry_count", nullable = false)
    @Builder.Default
    private int retryCount = 0;

    @Column(name = "next_sync_at")
    private Instant nextSyncAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}