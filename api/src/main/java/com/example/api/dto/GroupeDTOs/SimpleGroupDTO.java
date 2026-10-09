package com.example.api.dto.GroupeDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleGroupDTO {
    private Long id;
    private String roomName;
    private String description;
    private int nbUtilisateurs;
}
