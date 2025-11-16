package ru.akarpo.openprofile.is_openprofile.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;
import ru.akarpo.openprofile.is_openprofile.domain.profile.ProfileMedia;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "media_assets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaAsset {

    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_media_user"))
    private User user;

    @Column(nullable = false)
    private String filename;

    @Column(name = "storage_key", nullable = false)
    private String storageKey;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Column(name = "size_bytes", nullable = false)
    private long sizeBytes;

    private Integer width;
    private Integer height;

    @Column(name = "alt_text")
    private String altText;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /** profile_media.media_id */
    @OneToMany(mappedBy = "media", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProfileMedia> profileLinks = new ArrayList<>();
}
