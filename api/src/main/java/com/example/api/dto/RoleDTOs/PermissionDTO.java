package com.example.api.dto.RoleDTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PermissionDTO {
    private Long id;
    private String permission;
    private String description;
}
