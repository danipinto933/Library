package com.library.userservice.model;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String userName;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @ToString.Exclude
    @JoinColumn(name = "role_id", nullable = false, referencedColumnName = "id")
    private Role role;

    // NOTA MICROSERVICIOS: La lista de 'reserves' se ha eliminado.
    // En arquitectura de microservicios, 'User-Service' no debe conocer a 'Reserve'.
    // Esa relación se maneja mediante IDs desde 'Reserve-Service'.
}
