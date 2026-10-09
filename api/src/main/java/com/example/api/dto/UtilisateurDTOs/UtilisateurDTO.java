package com.example.api.dto.UtilisateurDTOs;

import com.example.api.dto.GroupeDTOs.SimpleGroupDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UtilisateurDTO {
    private Long id;
    private String email;
    private String name;
    private String second_name;
    private List<SimpleGroupDTO> groupes;
}
