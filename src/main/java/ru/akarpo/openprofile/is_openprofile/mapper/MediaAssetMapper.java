package ru.akarpo.openprofile.is_openprofile.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.akarpo.openprofile.is_openprofile.domain.MediaAsset;
import ru.akarpo.openprofile.is_openprofile.dto.MediaAssetDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MediaAssetMapper {
    MediaAssetDTO toDto(MediaAsset mediaAsset);
    MediaAsset toEntity(MediaAssetDTO mediaAssetDTO);
}
