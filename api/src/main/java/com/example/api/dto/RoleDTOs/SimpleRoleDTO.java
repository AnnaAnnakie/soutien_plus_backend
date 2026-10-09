package com.example.api.dto.RoleDTOs;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleRoleDTO {
    private Long id;
    private String name;
    private String description;
    private boolean base;

}
