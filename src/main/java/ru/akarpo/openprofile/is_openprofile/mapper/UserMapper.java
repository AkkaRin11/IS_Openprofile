package ru.akarpo.openprofile.is_openprofile.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.akarpo.openprofile.is_openprofile.domain.User;
import ru.akarpo.openprofile.is_openprofile.dto.UserDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    UserDTO toDto(User user);
    User toEntity(UserDTO userDTO);
}