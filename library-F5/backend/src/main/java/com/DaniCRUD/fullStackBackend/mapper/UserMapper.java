package com.DaniCRUD.fullStackBackend.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.DaniCRUD.fullStackBackend.dto.response.UserDto;
import com.DaniCRUD.fullStackBackend.model.Role;
import com.DaniCRUD.fullStackBackend.model.User;
import com.DaniCRUD.fullStackBackend.repository.RoleRepository;

@Component
public class UserMapper //Traduce un objeto DTO a Entity (o al reves)
{
    @Autowired
    private RoleRepository roleRepository;

    public UserDto toDto(User user) //el usuario introduce lo que hay en este formulario y se guarda en la DDBB
    {
        if (user == null) return null;

        String roleName = (user.getRole() != null) ? user.getRole().getRole() : "USER";

        return UserDto.builder()
        .id(user.getId())
        .userName(user.getUserName())
        .name(user.getName())
        .email(user.getEmail())
        .password(user.getPassword())
        .role(roleName)
        .build();
    }

    public User toEntity (UserDto userDto)//aqui se le pasa toda la entidad de la DDBB al usuario y se le muestra en un profile
    {
        if (userDto == null) return null;

        return User.builder()
        .id(userDto.getId())
        .userName(userDto.getUserName())
        .name(userDto.getName())
        .email(userDto.getEmail())
        .password(userDto.getPassword())
        .build();
    }

    public void updateEntityFromDto(UserDto dto, User user)
    {

        Role role = roleRepository.findByRole(dto.getRole());
        System.out.println(role);

        if (dto.getName() != null)      user.setName(dto.getName());
        if (dto.getEmail() != null)     user.setEmail(dto.getEmail());
        if (dto.getUserName() != null)  user.setUserName(dto.getUserName());
        if (dto.getPassword() != null)  user.setPassword(dto.getPassword());
        if (dto.getRole() != null)      user.setRole(role);
    }
}
