package ru.akarpo.openprofile.is_openprofile.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.akarpo.openprofile.is_openprofile.domain.Publication;
import ru.akarpo.openprofile.is_openprofile.dto.PublicationDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PublicationMapper {
    PublicationDTO toDto(Publication publication);
    Publication toEntity(PublicationDTO publicationDTO);
}
