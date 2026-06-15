package com.DaniCRUD.fullStackBackend.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleDto
{
    private Long id;

    @NotNull (message = "Introduzca un nombre para el rol")
    private String role;
}
