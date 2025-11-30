package ru.akarpo.openprofile.is_openprofile.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.akarpo.openprofile.is_openprofile.domain.ServiceConnection;
import ru.akarpo.openprofile.is_openprofile.dto.ServiceConnectionDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ServiceConnectionMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "service.id", target = "serviceId")
    ServiceConnectionDTO toDto(ServiceConnection serviceConnection);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "service", ignore = true)
    @Mapping(target = "bindings", ignore = true)
    ServiceConnection toEntity(ServiceConnectionDTO serviceConnectionDTO);
}
