package ru.akarpo.openprofile.is_openprofile.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.akarpo.openprofile.is_openprofile.domain.User;
import ru.akarpo.openprofile.is_openprofile.dto.UserDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    UserDTO toDto(User user);

    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "profiles", ignore = true)
    @Mapping(target = "connections", ignore = true)
    @Mapping(target = "mediaAssets", ignore = true)
    User toEntity(UserDTO userDTO);
}