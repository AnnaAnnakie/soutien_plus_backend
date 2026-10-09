package com.example.api.service;

import com.example.api.dto.GroupeDTOs.GroupDTO;
import com.example.api.dto.RoleDTOs.SimpleRoleDTO;
import com.example.api.dto.UtilisateurDTOs.SimpleUtilisateurDTO;
import com.example.api.entity.GroupeEntities.Groupe;
import com.example.api.entity.GroupeEntities.GroupesRoles;
import com.example.api.entity.GroupeEntities.UserGroupe;
import com.example.api.entity.RoleEntities.Role;
import com.example.api.entity.UtilisateurEntities.UsersGroupesRoles;
import com.example.api.entity.UtilisateurEntities.Utilisateur;
import com.example.api.repository.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupService {
    private final GroupeRepository groupeRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final UsersGroupesRolesRepository usersGroupesRolesRepository;
    private final RoleRepository roleRepository;
    private final GroupesRolesRepository groupesRolesRepository;
    private final UserGroupeRepository userGroupeRepository;
    private final RoleService roleService;

    public GroupService(RoleService roleService,UserGroupeRepository userGroupeRepository,GroupesRolesRepository groupesRolesRepository,GroupeRepository groupeRepository,RoleRepository roleRepository, UtilisateurRepository utilisateurRepository, UsersGroupesRolesRepository usersGroupesRolesRepository) {
        this.groupeRepository = groupeRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.usersGroupesRolesRepository = usersGroupesRolesRepository;
        this.roleRepository = roleRepository;
        this.groupesRolesRepository = groupesRolesRepository;
        this.userGroupeRepository = userGroupeRepository;
        this.roleService = roleService;
    }

    public GroupDTO getGroupById(Long id) {
        Groupe groupe = groupeRepository.findByRoomId(id);
        if (groupe == null){
            return null;
        } else {
            return getGroupDTO(groupe);
        }
    }

    public GroupDTO getGroupDTO(Groupe groupe) {

        List<Role> baseRoles = roleRepository.findRolesBase();
        List<SimpleRoleDTO> baseRolesDTO = new ArrayList<>();

        for (Role role : baseRoles) {
            baseRolesDTO.add(new SimpleRoleDTO(role.getId(), role.getName(),
                    role.getDescription(), role.isBase()));
        }


        List<SimpleRoleDTO> roles = groupe.getRoles() != null ?
                groupe.getRoles().stream()
                        .map(role -> new SimpleRoleDTO(
                                role.getId(),
                                role.getName(),
                                role.getDescription(), role.isBase())).toList()
                : null;

        if (roles != null) {
            baseRolesDTO.addAll(roles);
        }

        return GroupDTO
                .builder()
                .roomId(groupe.getRoomId())
                .roomName(groupe.getRoomName())
                .roomSecurityNumber(groupe.getRoomSecurityNumber())
                .roomPersonName(groupe.getRoomPersonName())
                .roomPersonSecondName(groupe.getRoomPersonSecondName())
                .roomDescription(groupe.getRoomDescription())
                .roomInvitationCode(groupe.getRoomInvitationCode())
                .users(
                        groupe.getUsers() != null ?
                                groupe.getUsers().stream()
                                        .map(user -> new SimpleUtilisateurDTO(user.getUsrId(),
                                                user.getUsrEmail(), user.getUsrName(), user.getUsrSecondName(), this.roleService.getRoleFromGroupUser(groupe.getRoomId(), user.getUsrId()).getName()))
                                        .collect(Collectors.toList())
                                : null
                )
                .roles(baseRolesDTO)
                .build();
    }

    public List<SimpleRoleDTO> getRoleGroup(Long id) {
        Groupe groupe = groupeRepository.findByRoomId(id);
        if (groupe == null) {
            return null;
        } else {
            List<Role> baseRoles = roleRepository.findRolesBase();
            List<SimpleRoleDTO> baseRolesDTO = new ArrayList<>();

            for (Role role : baseRoles) {
                baseRolesDTO.add(new SimpleRoleDTO(role.getId(), role.getName(),
                        role.getDescription(), role.isBase()));
            }

            ArrayList<SimpleRoleDTO> roles = new ArrayList<>();
            for (Role role : groupe.getRoles()) {
                roles.add(new SimpleRoleDTO(role.getId(), role.getName(), role.getDescription(), role.isBase()));
            }
            baseRolesDTO.addAll(roles);
                return baseRolesDTO;
            }
        }

    public GroupDTO addGroup(GroupDTO groupDTO, Long userId) {
        Utilisateur utilisateur = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable avec l'ID : " + userId));

        // Créer une nouvelle instance de Groupe à partir du DTO
        Groupe groupe = new Groupe();
        groupe.setRoomName(groupDTO.getRoomName());
        groupe.setRoomSecurityNumber(groupDTO.getRoomSecurityNumber());
        groupe.setRoomPersonName(groupDTO.getRoomPersonName());
        groupe.setRoomPersonSecondName(groupDTO.getRoomPersonSecondName());
        groupe.setRoomDescription(groupDTO.getRoomDescription());

        // Ajouter l'utilisateur au groupe
        groupe.setUsers(new ArrayList<>());
        groupe.getUsers().add(utilisateur);

        // Sauvegarder le groupe dans la base de données
        Groupe savedGroup = groupeRepository.save(groupe);

        UsersGroupesRoles usersGroupesRoles = new UsersGroupesRoles();
        // Correspond au chef de famille
        long idrole = 1;

        usersGroupesRoles.setId_user(userId);
        usersGroupesRoles.setId_role(idrole);
        usersGroupesRoles.setId_groupe(savedGroup.getRoomId());
        usersGroupesRolesRepository.save(usersGroupesRoles);

        // Ajouter le groupe dans la liste des groupes de l'utilisateur
        utilisateur.getGroups().add(savedGroup);
        utilisateurRepository.save(utilisateur);

        // Retourner le DTO du groupe nouvellement créé
        return getGroupDTO(savedGroup);
    }

    public Groupe joinGroup(GroupDTO groupDTO, String userEmail) {
        //Chercher le groupe par son numero de securite social
        Groupe groupe = groupeRepository.findByRoomInvitationCode(groupDTO.getRoomInvitationCode());
        Utilisateur user = utilisateurRepository.findByUsrEmail(userEmail);

        groupe.getUsers().add(user);
        user.getGroups().add(groupe);
        Groupe groupeJoined = groupeRepository.save(groupe);
        utilisateurRepository.save(user);

        setUserOfGroupeToRole(groupeJoined.getRoomId(), 11L, user.getUsrId());

        return groupeJoined;
    }


    public boolean isUserInGroup(Long userId, Long groupId) {
        UserGroupe userGroupe = this.userGroupeRepository.findByUsrIdAndRoomId(userId, groupId);
        return userGroupe != null;
    }

    public UsersGroupesRoles setUserOfGroupeToRole(Long groupeId, Long roleId, Long userId) {
        GroupesRoles groupeRoles = groupesRolesRepository.findByGroupIdAndRoleId(groupeId, roleId);
        List<Long> roleIds = roleRepository.findRolesBase().stream()
                .map(Role::getId)
                .toList();

        if (!roleIds.contains(roleId) && groupeRoles == null ){
            return null;
        } else {
            UsersGroupesRoles searchedUsersGroupesRoles = usersGroupesRolesRepository.findByGroupIdAndUserId(groupeId, userId);
            if (searchedUsersGroupesRoles != null){
                usersGroupesRolesRepository.updateRoleByGroupIdAndUserId(roleId,groupeId, userId);
                return usersGroupesRolesRepository.findByGroupeUserAndRole(groupeId, userId, roleId);
            } else {
                UsersGroupesRoles usersGroupesRoles = new UsersGroupesRoles();
                usersGroupesRoles.setId_groupe(groupeId);
                usersGroupesRoles.setId_role(roleId);
                usersGroupesRoles.setId_user(userId);
                return usersGroupesRolesRepository.save(usersGroupesRoles);
            }

        }
    }


}
