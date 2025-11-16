package ru.akarpo.openprofile.is_openprofile.mapper.widget;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.akarpo.openprofile.is_openprofile.domain.widget.WidgetType;
import ru.akarpo.openprofile.is_openprofile.dto.widget.WidgetTypeDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface WidgetTypeMapper {
    WidgetTypeDTO toDto(WidgetType widgetType);
    WidgetType toEntity(WidgetTypeDTO widgetTypeDTO);
}
