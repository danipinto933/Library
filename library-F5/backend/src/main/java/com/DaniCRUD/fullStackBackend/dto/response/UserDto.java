package com.DaniCRUD.fullStackBackend.dto.response;

import com.DaniCRUD.fullStackBackend.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto //Esto es lo que ve el usuario al pedir datos
{
    private Long id;

    @NotNull (message = "Introduzca un nombre de usuario")
    private String userName;

    @NotNull (message = "Introduzca un nombre")
    private String name;

    @Email (message = "Introduzca un mail válido")
    private String email;

    @NotNull (message = "Introduzca una contraseña")
    private String password;

    private String role;
}