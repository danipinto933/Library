package com.library.userservice.mapper;

import com.library.userservice.dto.UserDto;
import com.library.userservice.model.Role;
import com.library.userservice.model.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-30T19:32:22+0200",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.100.v20260624-0231, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDto toDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserDto.UserDtoBuilder userDto = UserDto.builder();

        userDto.role( userRoleRole( user ) );
        userDto.email( user.getEmail() );
        userDto.id( user.getId() );
        userDto.name( user.getName() );
        userDto.password( user.getPassword() );
        userDto.userName( user.getUserName() );

        return userDto.build();
    }

    @Override
    public User toEntity(UserDto userDto) {
        if ( userDto == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.email( userDto.getEmail() );
        user.id( userDto.getId() );
        user.name( userDto.getName() );
        user.password( userDto.getPassword() );
        user.userName( userDto.getUserName() );

        return user.build();
    }

    @Override
    public void updateEntityFromDto(UserDto userDto, User user) {
        if ( userDto == null ) {
            return;
        }

        user.setEmail( userDto.getEmail() );
        user.setName( userDto.getName() );
        user.setPassword( userDto.getPassword() );
        user.setUserName( userDto.getUserName() );
    }

    private String userRoleRole(User user) {
        Role role = user.getRole();
        if ( role == null ) {
            return null;
        }
        return role.getRole();
    }
}
