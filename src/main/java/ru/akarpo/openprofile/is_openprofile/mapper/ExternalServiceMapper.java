package ru.akarpo.openprofile.is_openprofile.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.akarpo.openprofile.is_openprofile.domain.ExternalService;
import ru.akarpo.openprofile.is_openprofile.dto.ExternalServiceDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ExternalServiceMapper {
    ExternalServiceDTO toDto(ExternalService externalService);
    ExternalService toEntity(ExternalServiceDTO externalServiceDTO);
}
