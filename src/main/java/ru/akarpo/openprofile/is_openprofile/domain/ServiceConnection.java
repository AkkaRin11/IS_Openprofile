package ru.akarpo.openprofile.is_openprofile.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "service_connections",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "service_id", "external_user_id"})
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceConnection {

    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_connection_user"))
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "service_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_connection_service"))
    private ExternalService service;

    @Column(name = "external_user_id", nullable = false)
    private String externalUserId;

    @Column(name = "access_token")
    private String accessToken;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "token_expires_at")
    private Instant tokenExpiresAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /** widget_bindings.connection_id */
    @OneToMany(mappedBy = "connection", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<WidgetBinding> bindings = new ArrayList<>();
}
