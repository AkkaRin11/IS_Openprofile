package ru.akarpo.openprofile.is_openprofile.mapper.profile;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.akarpo.openprofile.is_openprofile.domain.profile.Profile;
import ru.akarpo.openprofile.is_openprofile.dto.profile.ProfileDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProfileMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "theme.id", target = "themeId")
    ProfileDTO toDto(Profile profile);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "theme", ignore = true)
    @Mapping(target = "widgets", ignore = true)
    @Mapping(target = "media", ignore = true)
    @Mapping(target = "publications", ignore = true)
    Profile toEntity(ProfileDTO profileDTO);
}
