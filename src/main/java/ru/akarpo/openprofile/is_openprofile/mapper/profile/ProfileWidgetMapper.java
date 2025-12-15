package ru.akarpo.openprofile.is_openprofile.mapper.profile;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.akarpo.openprofile.is_openprofile.domain.profile.ProfileWidget;
import ru.akarpo.openprofile.is_openprofile.domain.widget.WidgetBinding;
import ru.akarpo.openprofile.is_openprofile.domain.ServiceConnection;
import ru.akarpo.openprofile.is_openprofile.dto.profile.ProfileWidgetDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProfileWidgetMapper {

    @Mapping(source = "profile.id", target = "profileId")
    @Mapping(source = "widgetType.id", target = "widgetTypeId")
    @Mapping(target = "connectionId", expression = "java(getConnectionId(profileWidget))")
    ProfileWidgetDTO toDto(ProfileWidget profileWidget);

    default java.util.UUID getConnectionId(ProfileWidget profileWidget) {
        if (profileWidget.getBindings() != null && !profileWidget.getBindings().isEmpty()) {
            return profileWidget.getBindings().get(0).getConnection().getId();
        }
        return null;
    }

    @Mapping(target = "profile", ignore = true)
    @Mapping(target = "widgetType", ignore = true)
    @Mapping(target = "bindings", ignore = true)
    ProfileWidget toEntity(ProfileWidgetDTO profileWidgetDTO);
}
