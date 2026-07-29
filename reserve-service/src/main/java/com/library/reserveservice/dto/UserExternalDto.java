package com.library.reserveservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserExternalDto {
    private Long id;
    private String name;
    private String email;
    private String userName;
    private String nif;
    private String address;
    private String phone;
    // Ignoramos password y rol por seguridad en la reserva, no los necesitamos
}
