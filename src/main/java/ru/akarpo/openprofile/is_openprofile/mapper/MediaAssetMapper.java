package ru.akarpo.openprofile.is_openprofile.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.akarpo.openprofile.is_openprofile.domain.MediaAsset;
import ru.akarpo.openprofile.is_openprofile.dto.MediaAssetDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MediaAssetMapper {

    @Mapping(source = "user.id", target = "userId")
    MediaAssetDTO toDto(MediaAsset mediaAsset);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "profileLinks", ignore = true)
    MediaAsset toEntity(MediaAssetDTO mediaAssetDTO);
}
