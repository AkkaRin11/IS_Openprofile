package ru.akarpo.openprofile.is_openprofile.mapper.profile;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.akarpo.openprofile.is_openprofile.domain.profile.PublicProfileView;
import ru.akarpo.openprofile.is_openprofile.dto.profile.PublicProfileViewDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PublicProfileViewMapper {
    PublicProfileViewDTO toDto(PublicProfileView publicProfileView);
    PublicProfileView toEntity(PublicProfileViewDTO publicProfileViewDTO);
}