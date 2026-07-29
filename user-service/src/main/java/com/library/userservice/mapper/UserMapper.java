package com.library.userservice.mapper;

import com.library.userservice.dto.UserDto;
import com.library.userservice.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "role", source = "role.role")
    UserDto toDto(User user);

    @Mapping(target = "role", ignore = true)
    User toEntity(UserDto userDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    void updateEntityFromDto(UserDto userDto, @MappingTarget User user);
}
