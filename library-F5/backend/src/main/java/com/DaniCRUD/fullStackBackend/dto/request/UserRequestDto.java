package com.DaniCRUD.fullStackBackend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDto //Lo que el usuario puede ingresar a través de formulario
{
    @NotBlank(message = "Rellenar el campo de apodo")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9-_]{3,23}$")
    private String userName;

    @NotBlank(message = "Rellenar el campo de nombre")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9-_]{3,23}$")
    private String name;
    
    @NotBlank(message = "Rellenar el campo de email")
    @Email(message = "Debe ser un correo electrónico válido")
    private String email;

    @NotBlank(message = "Rellenar el campo de contraseña")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%]).{8,24}$")
    private String password;
}
