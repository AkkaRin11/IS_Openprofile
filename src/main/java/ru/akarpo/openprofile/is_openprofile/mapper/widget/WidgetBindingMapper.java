package ru.akarpo.openprofile.is_openprofile.mapper.widget;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.akarpo.openprofile.is_openprofile.domain.widget.WidgetBinding;
import ru.akarpo.openprofile.is_openprofile.dto.widget.WidgetBindingDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface WidgetBindingMapper {

    @Mapping(source = "profileWidget.id", target = "profileWidgetId")
    @Mapping(source = "connection.id", target = "connectionId")
    WidgetBindingDTO toDto(WidgetBinding widgetBinding);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "profileWidget", ignore = true)
    @Mapping(target = "connection", ignore = true)
    WidgetBinding toEntity(WidgetBindingDTO widgetBindingDTO);
}
