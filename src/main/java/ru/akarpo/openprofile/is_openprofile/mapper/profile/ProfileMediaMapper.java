package ru.akarpo.openprofile.is_openprofile.mapper.profile;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.akarpo.openprofile.is_openprofile.domain.profile.ProfileMedia;
import ru.akarpo.openprofile.is_openprofile.dto.profile.ProfileMediaDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProfileMediaMapper {
    ProfileMediaDTO toDto(ProfileMedia profileMedia);
    ProfileMedia toEntity(ProfileMediaDTO profileMediaDTO);
}