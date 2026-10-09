package com.example.api.dto.RoleDTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleDTO {
    private Long id;
    private String name;
    private String description;
    private boolean base;
    private List<PermissionDTO> permissions;
}
