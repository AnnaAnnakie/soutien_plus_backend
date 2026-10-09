package com.example.api.service;

import com.example.api.dto.RoleDTOs.PermissionDTO;
import com.example.api.dto.RoleDTOs.RoleDTO;
import com.example.api.dto.UtilisateurDTOs.SimpleUtilisateurDTO;
import com.example.api.entity.GroupeEntities.Groupe;
import com.example.api.entity.GroupeEntities.GroupesRoles;
import com.example.api.entity.RoleEntities.Permission;
import com.example.api.entity.RoleEntities.Role;
import com.example.api.entity.UtilisateurEntities.UsersGroupesRoles;
import com.example.api.repository.GroupesRolesRepository;
import com.example.api.repository.PermissionRepository;
import com.example.api.repository.RoleRepository;
import com.example.api.repository.UsersGroupesRolesRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final GroupesRolesRepository groupesRolesRepository;
    private final PermissionRepository permissionRepository;
    private final UsersGroupesRolesRepository usersGroupesRolesRepository;


    public RoleService(PermissionRepository permissionRepository, RoleRepository roleRepository, GroupesRolesRepository groupesRolesRepository, UsersGroupesRolesRepository usersGroupesRolesRepository) {
        this.roleRepository = roleRepository;
        this.groupesRolesRepository = groupesRolesRepository;
        this.permissionRepository = permissionRepository;
        this.usersGroupesRolesRepository = usersGroupesRolesRepository;

    }



    public RoleDTO getRoleFromGroupUser(Long groupeId, Long userId) {

        Role role = roleRepository.findRolesByGroupIdAndUserId(groupeId, userId);

        if (role == null){
            return null;
        } else {
            return getRoleDTO(role);
        }
    }

    public boolean isAuthorized(String permission, Long idGroupe, Long idUser) {
        Role role = roleRepository.findRolesByGroupIdAndUserId(idGroupe, idUser);
        List<String> permissions = role.getPermissions().stream().map(Permission::getPermission).toList();
        return permissions.contains(permission);
    }

    public RoleDTO addRoleToGroup(RoleDTO roleDTO, Long groupId) {

        Role role = new Role();
        role.setName(roleDTO.getName());
        role.setDescription(roleDTO.getDescription());
        role.setBase(false);

        Role roleSaved = roleRepository.save(role);

        GroupesRoles groupesRoles = new GroupesRoles();
        groupesRoles.setId_groupe(groupId);
        groupesRoles.setId_role(roleSaved.getId());

        groupesRolesRepository.save(groupesRoles);

        return getRoleDTO(roleSaved);

    }

    public RoleDTO setPermissionsToRole(Long[] permissionIds, Long roleId) {
        // Récupérer le rôle depuis la base de données
        Optional<Role> optionalRole = roleRepository.findById(roleId);

        Role role = optionalRole.get();

        // Récupérer les permissions depuis la base de données
        List<Permission> permissions = permissionRepository.findAllById(List.of(permissionIds));

        // Mettre à jour la liste des permissions du rôle
        role.setPermissions(permissions);

        // Sauvegarder le rôle mis à jour
        roleRepository.save(role);

        return getRoleWithIdDTO(roleId);
    }

    public RoleDTO getRoleWithIdDTO(Long idRole){
        Optional<Role> role = roleRepository.findById(idRole);
        return role.map(this::getRoleDTO).orElse(null);
    }

    public RoleDTO getRoleDTO(Role role) {
        return RoleDTO
                .builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .base(role.isBase())
                .permissions(role.getPermissions() != null ?
                        role.getPermissions().stream()
                                .map(permission -> new PermissionDTO(permission.getId(),
                                        permission.getPermission(), permission.getDescription())).toList() : null)
                .build();
    }

}
