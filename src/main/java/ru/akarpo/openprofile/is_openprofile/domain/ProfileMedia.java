package ru.akarpo.openprofile.is_openprofile.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "profile_media")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileMedia {

    @EmbeddedId
    private ProfileMediaId id;

    @ManyToOne(optional = false)
    @MapsId("profileId")
    @JoinColumn(name = "profile_id",
            foreignKey = @ForeignKey(name = "fk_profile_media_profile"))
    private Profile profile;

    @ManyToOne(optional = false)
    @MapsId("mediaId")
    @JoinColumn(name = "media_id",
            foreignKey = @ForeignKey(name = "fk_profile_media_media"))
    private MediaAsset media;

    @Column(nullable = false)
    @Builder.Default
    private String role = "gallery";

    @Column(name = "is_primary", nullable = false)
    @Builder.Default
    private boolean primary = false;
}
