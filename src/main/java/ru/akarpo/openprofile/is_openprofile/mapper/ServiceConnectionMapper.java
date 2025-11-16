package ru.akarpo.openprofile.is_openprofile.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.akarpo.openprofile.is_openprofile.domain.ServiceConnection;
import ru.akarpo.openprofile.is_openprofile.dto.ServiceConnectionDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ServiceConnectionMapper {
    ServiceConnectionDTO toDto(ServiceConnection serviceConnection);
    ServiceConnection toEntity(ServiceConnectionDTO serviceConnectionDTO);
}
