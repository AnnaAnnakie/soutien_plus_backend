package com.example.api.dto.UtilisateurDTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SimpleUtilisateurDTO {
    private Long id;
    private String email;
    private String name;
    private String second_name;
    private String role_name;
}
