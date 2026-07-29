package com.library.authservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad Role — Auth-Service solo necesita el nombre del rol para generar
 * el claim "roles" del JWT. No incluye la lista de usuarios (no necesaria aquí).
 *
 * Mapea la tabla "roles" existente — ddl-auto=validate, no la modifica.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String role;
}
