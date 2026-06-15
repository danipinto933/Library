package com.DaniCRUD.fullStackBackend.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@Data
@Entity //desde lombok genera automaaticament los getters'n'setters sin mostrarmelo
@AllArgsConstructor //desde lombok genera automaticamente un constructor de todos los atributos
@NoArgsConstructor //desde lombok genera automaticament un constructor vacio
@Table (name="users")
public class User
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //genera una ID con un tipo de strategy (tipo de estrategia para generar ID's)
    private Long id;

    @Column (nullable = false, unique = true, length = 50)
    private String userName;

    @Column (nullable = false, unique = true, length = 50)
    private String name;

    @Column (nullable = false, unique = true)
    private String email;

    @Column (nullable = false)
    private String password;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "role_id", nullable = false, referencedColumnName = "id") // En relación 1:N, un usuario tiene un rol, pero un rol muchos usuarios, asi que el id_role es una FK
    private Role role;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Reserve> reserves;
}