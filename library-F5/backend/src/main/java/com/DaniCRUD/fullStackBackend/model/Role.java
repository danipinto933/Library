package com.DaniCRUD.fullStackBackend.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Builder
@Data //desde lombok genera automaaticament los getters'n'setters sin mostrarmelo
@AllArgsConstructor //desde lombok genera automaticamente un constructor de todos los atributos
@NoArgsConstructor //desde lombok genera automaticament un constructor vacio
@Table (name="roles")
public class Role 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //genera una ID con un tipo de strategy (tipo de estrategia para generar ID's)
    private Long id;

    @NotBlank(message = "Rellenar el campo de rol")
    private String role;

    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "role") // En relacion 1:N, un usario tiene un rol, un rol muchos usuarios, entonces role esta mappeado a User
    private List<User> user;
}
