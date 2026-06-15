package com.DaniCRUD.fullStackBackend.model;

import lombok.NoArgsConstructor;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table (name = "genres")
public class Genre
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //genera una ID con un tipo de strategy (tipo de estrategia para generar ID's)
    private Long id;

    @NotBlank (message = "El genero no puede estar en blanco")
    private String genreName;

    public void setGenreName(String genreName)
    {
        this.genreName = genreName;
    }
}
