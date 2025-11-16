package ru.akarpo.openprofile.is_openprofile.mapper.profile;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.akarpo.openprofile.is_openprofile.domain.profile.Profile;
import ru.akarpo.openprofile.is_openprofile.dto.profile.ProfileDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProfileMapper {
    ProfileDTO toDto(Profile profile);
    Profile toEntity(ProfileDTO profileDTO);
}
