package ru.akarpo.openprofile.is_openprofile.mapper.profile;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.akarpo.openprofile.is_openprofile.domain.profile.ProfileMedia;
import ru.akarpo.openprofile.is_openprofile.dto.profile.ProfileMediaDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProfileMediaMapper {

    @Mapping(source = "profile.id", target = "profileId")
    @Mapping(source = "media.id", target = "mediaId")
    ProfileMediaDTO toDto(ProfileMedia profileMedia);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "profile", ignore = true)
    @Mapping(target = "media", ignore = true)
    ProfileMedia toEntity(ProfileMediaDTO profileMediaDTO);
}
