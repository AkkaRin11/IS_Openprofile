package ru.akarpo.openprofile.is_openprofile.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.akarpo.openprofile.is_openprofile.domain.Theme;
import ru.akarpo.openprofile.is_openprofile.dto.ThemeDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ThemeMapper {
    ThemeDTO toDto(Theme theme);
    Theme toEntity(ThemeDTO themeDTO);
}
