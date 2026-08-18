package com.library.userservice.service;

import com.library.userservice.dto.UserDto;
import com.library.userservice.exception.ResourceNotFoundException;
import com.library.userservice.exception.UserAlreadyExistsException;
import com.library.userservice.mapper.UserMapper;
import com.library.userservice.model.Role;
import com.library.userservice.model.User;
import com.library.userservice.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final BCryptPasswordEncoder bcrypt;
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, RoleService roleService,
                           UserMapper userMapper, BCryptPasswordEncoder bcrypt) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.userMapper = userMapper;
        this.bcrypt = bcrypt;
    }

    @Override
    public ResponseEntity<User> addUser(UserDto userDto) {
        User user = userMapper.toEntity(userDto);

        if (userRepository.findByUserNameIgnoreCase(user.getUserName()) != null || userRepository.findByUserName(user.getUserName()) != null) {
            throw new UserAlreadyExistsException("El nombre de usuario '" + user.getUserName() + "' ya está registrado");
        }
        if (userRepository.findUserByEmailIgnoreCase(user.getEmail()) != null || userRepository.findUserByEmail(user.getEmail()) != null) {
            throw new UserAlreadyExistsException("El correo electrónico '" + user.getEmail() + "' ya está registrado");
        }
        if (userRepository.findUserByNameIgnoreCase(user.getName()) != null || userRepository.findUserByName(user.getName()) != null) {
            throw new UserAlreadyExistsException("El nombre '" + user.getName() + "' ya está registrado");
        }

        // Asignamos el rol por defecto
        Role role = roleService.findRoleByRoleName("USER");
        if (role == null) {
            throw new ResourceNotFoundException("Rol por defecto no encontrado");
        }

        user.setRole(role);
        user.setPassword(bcrypt.encode(user.getPassword()));

        userRepository.save(user);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<List<UserDto>> findAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserDto> usersDtos = users.stream()
                .map(userMapper::toDto)
                .toList();
        return new ResponseEntity<>(usersDtos, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<UserDto> findUserByUserName(String userName) {
        User user = findUserByNameUser(userName);
        return new ResponseEntity<>(userMapper.toDto(user), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<UserDto> findUserByName(String name) {
        User user = userRepository.findUserByNameIgnoreCase(name);
        if (user == null) {
            user = userRepository.findUserByName(name);
        }
        if (user == null) {
            throw new ResourceNotFoundException("Usuario con el nombre " + name + " no encontrado");
        }
        return new ResponseEntity<>(userMapper.toDto(user), HttpStatus.OK);
    }

    @Override
    public User findUserByNameUser(String userName) {
        User user = userRepository.findByUserNameIgnoreCase(userName);
        if (user == null) {
            user = userRepository.findByUserName(userName);
        }
        if (user == null) {
            throw new ResourceNotFoundException("Usuario con el nick " + userName + " no encontrado");
        }
        return user;
    }

    @Override
    public ResponseEntity<UserDto> findUserByEmail(String email) {
        User user = userRepository.findUserByEmailIgnoreCase(email);
        if (user == null) {
            user = userRepository.findUserByEmail(email);
        }
        if (user == null) {
            throw new ResourceNotFoundException("Usuario con el email " + email + " no encontrado");
        }
        return new ResponseEntity<>(userMapper.toDto(user), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<UserDto>> findUsersByRole(String roleName) {
        Role role = roleService.findRoleByRoleName(roleName);
        List<User> users = userRepository.findUsersByRole(role);
        List<UserDto> userDtos = users.stream()
                .map(userMapper::toDto)
                .toList();
        return new ResponseEntity<>(userDtos, HttpStatus.OK);
    }

    @Override
    public User findByIdUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario con el id " + id + " no encontrado"));
    }

    @Override
    public ResponseEntity<User> updateUser(Long id, UserDto userDto) {
        User oldUser = findByIdUser(id);
        
        // Actualizamos campos básicos
        userMapper.updateEntityFromDto(userDto, oldUser);

        // Limpieza FASE VII: si mandan un nuevo password, lo encriptamos
        // de lo contrario, mantenemos el antiguo.
        if (userDto.getPassword() != null && !userDto.getPassword().isBlank()) {
            oldUser.setPassword(bcrypt.encode(userDto.getPassword()));
        }

        User updatedUser = userRepository.save(oldUser);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @Override
    public String deleteUser(Long id) {
        User user = findByIdUser(id);
        userRepository.delete(user);
        return "Usuario eliminado correctamente";
    }
}
