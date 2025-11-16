package ru.akarpo.openprofile.is_openprofile.mapper.widget;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.akarpo.openprofile.is_openprofile.domain.widget.WidgetBinding;
import ru.akarpo.openprofile.is_openprofile.dto.widget.WidgetBindingDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface WidgetBindingMapper {
    WidgetBindingDTO toDto(WidgetBinding widgetBinding);
    WidgetBinding toEntity(WidgetBindingDTO widgetBindingDTO);
}
