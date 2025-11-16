package ru.akarpo.openprofile.is_openprofile.mapper.profile;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import ru.akarpo.openprofile.is_openprofile.domain.profile.ProfileWidget;
import ru.akarpo.openprofile.is_openprofile.dto.profile.ProfileWidgetDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProfileWidgetMapper {
    ProfileWidgetDTO toDto(ProfileWidget profileWidget);
    ProfileWidget toEntity(ProfileWidgetDTO profileWidgetDTO);
}