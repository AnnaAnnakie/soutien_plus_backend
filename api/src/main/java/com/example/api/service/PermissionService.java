package com.example.api.service;

import com.example.api.dto.RoleDTOs.PermissionDTO;
import com.example.api.entity.RoleEntities.Permission;
import com.example.api.repository.GroupesRolesRepository;
import com.example.api.repository.PermissionRepository;
import com.example.api.repository.RoleRepository;
import com.example.api.repository.UsersGroupesRolesRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PermissionService {
    private final PermissionRepository permissionRepository;


    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public List<PermissionDTO> getAllPermissions() {
        List<Permission> permissions = permissionRepository.findAll();

        return permissions.stream().map(permission ->
                new PermissionDTO(permission.getId(),permission.getPermission(),permission.getDescription())).toList();
    }
}
