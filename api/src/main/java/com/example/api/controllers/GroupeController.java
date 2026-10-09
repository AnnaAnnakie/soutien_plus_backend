package com.example.api.controllers;

import com.example.api.dto.GroupeDTOs.GroupDTO;
import com.example.api.dto.RoleDTOs.RoleDTO;
import com.example.api.dto.RoleDTOs.SimpleRoleDTO;
import com.example.api.dto.UtilisateurDTOs.SimpleUtilisateurDTO;
import com.example.api.entity.GroupeEntities.Groupe;
import com.example.api.entity.UtilisateurEntities.UsersGroupesRoles;
import com.example.api.service.GroupService;
import com.example.api.service.RoleService;
import com.example.api.service.UtilisateurService;
import com.example.api.service.VerificationService;
import com.example.api.utils.VerificationPermissions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/soutien/groupe")
public class GroupeController {
    private final GroupService groupService;
    private final UtilisateurService utilisateurService;
    private final VerificationService verificationService;
    private final RoleService roleService;

    public GroupeController(VerificationService verificationService,GroupService groupService, UtilisateurService utilisateurService, RoleService roleService) {
        this.groupService = groupService;
        this.utilisateurService = utilisateurService;
        this.verificationService = verificationService;
        this.roleService = roleService;

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getGroup(@PathVariable Long id) {
        if (!verificationService.isAuthentificated()) {
            return VerificationPermissions.getErrorResponse("Vous n'êtes pas connecté !");
        }

        List<String> necessariesPermissions = List.of();
        if (!verificationService.isAuthorizedToAccess(necessariesPermissions, id)) {
            return VerificationPermissions.getErrorResponse("Vous ne faîtes pas partie de ce groupe !");
        }

        return ResponseEntity.ok(groupService.getGroupById(id));
    }



    @GetMapping("/{id}/roles")
    public ResponseEntity<?> getRolesOfGroupe(@PathVariable Long id) {

        if (!verificationService.isAuthentificated()) {
            return VerificationPermissions.getErrorResponse("Vous n'êtes pas connecté !");
        }

        List<String> necessariesPermissions = List.of("role_access");
        if (!verificationService.isAuthorizedToAccess(necessariesPermissions, id)) {
            return VerificationPermissions.getErrorResponse("Vous ne faîtes pas partie de ce groupe ou vous n'avez pas les droits d'accéder à cette ressource !");
        }

        List<SimpleRoleDTO> rolesDTO = groupService.getRoleGroup(id);
        return ResponseEntity.ok().body(rolesDTO);

    }

    @PostMapping("/add")
    public ResponseEntity<?> addGroup(@RequestBody GroupDTO groupDTO) {


        if (!verificationService.isAuthentificated()) {
            return VerificationPermissions.getErrorResponse("Vous n'êtes pas connecté !");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usrEmail = authentication.getName();
        Long idUser = utilisateurService.getUtilisateurByEmail(usrEmail).getId();

        GroupDTO groupInfo = groupService.addGroup(groupDTO, idUser);
        return ResponseEntity.ok().body(groupInfo);


    }

    @GetMapping("/{idGroupe}/role/{idRole}/addUser/{userId}")
    public ResponseEntity<?> setRoleToUserInGroup(@PathVariable Long idGroupe, @PathVariable Long userId, @PathVariable Long idRole) {

        if (!verificationService.isAuthentificated()) {
            return VerificationPermissions.getErrorResponse("Vous n'êtes pas connecté !");
        }

        List<String> necessariesPermissions = List.of("role_set_person");
        if (!verificationService.isAuthorizedToAccess(necessariesPermissions, idGroupe)) {
            return VerificationPermissions.getErrorResponse("Vous ne faîtes pas partie de ce groupe !");
        }

        UsersGroupesRoles usersGroupesRolesSaved = groupService.setUserOfGroupeToRole(idGroupe,idRole,userId);
        return ResponseEntity.ok().body(usersGroupesRolesSaved);
    }


    @GetMapping("/{idGroupe}/role/{idRole}/utilisateurs")
    public ResponseEntity<?> getUtilisateursFromRoleOfGroupe(@PathVariable Long idGroupe, @PathVariable Long idRole) {

        if (!verificationService.isAuthentificated()) {
            return VerificationPermissions.getErrorResponse("Vous n'êtes pas connecté !");
        }

        List<String> necessariesPermissions = List.of("role_access");
        if (!verificationService.isAuthorizedToAccess(necessariesPermissions, idGroupe)) {
            return VerificationPermissions.getErrorResponse("Vous ne faîtes pas partie de ce groupe !");
        }

        List<SimpleUtilisateurDTO> utilisateurDTOS = utilisateurService.getUsersFromRoleAndGroupe(idRole,idGroupe);
        return ResponseEntity.ok().body(utilisateurDTOS);

    }

    @PostMapping("/{idGroupe}/role/{idRole}/setpermissions")
    public ResponseEntity<?> setPermissionToRole(@PathVariable Long idGroupe, @PathVariable Long idRole, @RequestBody Long[] permissionIds) {

        if (!verificationService.isAuthentificated()) {
            return VerificationPermissions.getErrorResponse("Vous n'êtes pas connecté !");
        }

        List<String> necessariesPermissions = List.of("role_set_permissions");
        if (!verificationService.isAuthorizedToAccess(necessariesPermissions, idGroupe)) {
            return VerificationPermissions.getErrorResponse("Vous ne faîtes pas partie de ce groupe ou vous n'avez pas les permissions de faire cette action !");
        }

        return ResponseEntity.ok().body(this.roleService.setPermissionsToRole(permissionIds, idRole));

    }

    @PostMapping("/join")
    public ResponseEntity<?> joinGroup(@RequestBody GroupDTO group) {

        if (!verificationService.isAuthentificated()) {
            return VerificationPermissions.getErrorResponse("Vous n'êtes pas connecté !");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String usrEmail = authentication.getName();
        Groupe groupJoined = groupService.joinGroup(group,usrEmail);

        return ResponseEntity.ok().body(groupJoined);

    }


}
