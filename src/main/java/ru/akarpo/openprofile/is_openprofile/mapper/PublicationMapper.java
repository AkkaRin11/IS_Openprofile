package ru.akarpo.openprofile.is_openprofile.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.akarpo.openprofile.is_openprofile.domain.Publication;
import ru.akarpo.openprofile.is_openprofile.dto.PublicationDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PublicationMapper {

    @Mapping(source = "profile.id", target = "profileId")
    PublicationDTO toDto(Publication publication);

    @Mapping(target = "profile", ignore = true)
    Publication toEntity(PublicationDTO publicationDTO);
}
