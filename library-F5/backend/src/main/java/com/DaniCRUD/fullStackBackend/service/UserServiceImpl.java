package com.DaniCRUD.fullStackBackend.service;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.DaniCRUD.fullStackBackend.dto.response.UserDto;
import com.DaniCRUD.fullStackBackend.exception.ResourceNotFoundException;
import com.DaniCRUD.fullStackBackend.exception.UserAlreadyExistsException;
import com.DaniCRUD.fullStackBackend.mapper.UserMapper;
import com.DaniCRUD.fullStackBackend.model.Reserve;
import com.DaniCRUD.fullStackBackend.model.Role;
import com.DaniCRUD.fullStackBackend.model.User;
import com.DaniCRUD.fullStackBackend.repository.UserRepository;
import com.DaniCRUD.fullStackBackend.security.UserDetail;


@Service
public class UserServiceImpl implements UserService, UserDetailsService //de los metodos creados, los implemento aqui
{
    private BCryptPasswordEncoder bcrypt;
    private UserRepository userRepository;
    private RoleServiceImpl roleServiceImpl;
    private UserMapper userMapper;
    
    public UserServiceImpl (UserRepository userRepository, RoleServiceImpl roleServiceImpl,
        UserMapper userMapper, BCryptPasswordEncoder bcrypt)
    {
        this.userRepository = userRepository;
        this.roleServiceImpl = roleServiceImpl;
        this.userMapper = userMapper;
        this.bcrypt = bcrypt;
    }

    @Override
    public ResponseEntity <User> addUser(UserDto userDto)
    {
        User user = userMapper.toEntity(userDto);

        if (userRepository.findByUserName(user.getUserName()) != null)
        {
            throw new UserAlreadyExistsException("El nombre de usuario '" + user.getUserName() + "' ya está registrado");
        }
        if (userRepository.findUserByEmail(user.getEmail()) != null)
        {
            throw new UserAlreadyExistsException("El correo electrónico '" + user.getEmail() + "' ya está registrado");
        }
        if (userRepository.findUserByName(user.getName()) != null)
        {
            throw new UserAlreadyExistsException("El nombre '" + user.getName() + "' ya está registrado");
        }

        Role role = roleServiceImpl.findRoleByIdRole(2L);

        String encryptedPassword = bcrypt.encode(user.getPassword());
        user.setPassword(encryptedPassword);
        
        if (role == null)
        {
            throw new ResourceNotFoundException("Rol con el nombre " + role + " no encontrado");
        }
        user.setRole(role);
        userRepository.save(user);
        return new ResponseEntity<>(user, HttpStatus.CREATED); //manda el codigo 201, el status de que ha sido creado
    }

    @Override
    public ResponseEntity<List<UserDto>> findAllUsers()
    {
        List<User> users = userRepository.findAll();

        List<UserDto> usersDtos = users.stream() //flujo de usuarios, los recolectamos todos
        .map(userMapper::toDto) //map transforma a todos los usuarios, ¿en que?, en DTO pasados por por UserMapper
        .toList();//lo transformamos en una lista

        return new ResponseEntity<>(usersDtos, HttpStatus.OK);
    }

    @Override
    public ResponseEntity <UserDto> findUserByUserName(String userName) 
    {
        User user = userRepository.findByUserName(userName);

        if (user == null)
        {
            throw new ResourceNotFoundException("Usuario con el apodo " + userName + " no encontrado");
        }

        UserDto userDto = userMapper.toDto(user);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }
    
    @Override
    public ResponseEntity<UserDto> findUserByName(String name)
    {
        User user = userRepository.findUserByName(name);

        if (user == null)
        {
            throw new ResourceNotFoundException("Usuario con el nombre " + name + " no encontrado");
        }

        UserDto userDto = userMapper.toDto(user);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @Override
    public User findUserByNameUser(String userName)
    {
        User user = userRepository.findByUserName(userName);

        if (user == null)
        {
            throw new ResourceNotFoundException("Usuario con el nick " + userName + " no encontrado");
        }

        return user;
    }
    

    @Override
    public ResponseEntity <UserDto> findUserByEmail(String email) 
    {
        User user = userRepository.findUserByEmail(email);

        if (user == null)
        {
            throw new ResourceNotFoundException("Usuario con el email " + email + " no encontrado");
        }

        UserDto userDto = userMapper.toDto(user);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<UserDto>> findUsersByRole(String roleName) 
    {
        Role role = roleServiceImpl.findRoleByRoleName(roleName);

        List<User> users = userRepository.findUsersByRole(role);
        List<UserDto> userDtos = users.stream()
        .map(userMapper::toDto)
        .toList();
        return new ResponseEntity<>(userDtos, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<Reserve>> findAllReservesByUserId(Long id)
    {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario con el id " + id + " no encontrado"));

        return new ResponseEntity<>(user.getReserves(), HttpStatus.OK);
    }

    @Override
    public User findByIdUser (Long id)
    {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario con el id " + id + " no encontrado"));
        return user;
    }

    @Override
    public ResponseEntity<User> updateUser(Long id, UserDto userDto)
    {
        User oldUser = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Usuario con el id " + id + " no encontrado")); //Obtenemos el usuario antiguo a través del ID
        userMapper.updateEntityFromDto(userDto, oldUser);

        String encryptedPassword = bcrypt.encode(oldUser.getPassword());
        oldUser.setPassword(encryptedPassword);

        User updatedUser = userMapper.toEntity(userDto);
        updatedUser = userRepository.save(oldUser); // Guardamos en un nuevo usuario los datos del antiguo usuario (ya tiene el nombre cambiado)
        return new ResponseEntity<>(updatedUser, HttpStatus.OK); // Devolvemos un response con un estado de HTTP
    }

    @Override
    public String deleteUser(Long id)
    {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario con el id " + id + " no encontrado"));

        userRepository.delete(user);
        return "Usuario eliminado correctamente";
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        User user = userRepository.findByUserName(username);

        if (user == null)
        {
            throw new UsernameNotFoundException(username);
        }

        return new UserDetail(user);
    }
}
