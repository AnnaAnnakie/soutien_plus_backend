package com.example.api.controllers;


import com.example.api.dto.GroupeDTOs.GroupDTO;
import com.example.api.dto.RoleDTOs.RoleDTO;
import com.example.api.dto.UtilisateurDTOs.UtilisateurDTO;
import com.example.api.entity.RoleEntities.Role;
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
@RequestMapping("/soutien")
public class RoleController {

    private final UtilisateurService utilisateurService;
    private final RoleService roleService;
    private final VerificationService verificationService;

    public RoleController(UtilisateurService utilisateurService, RoleService roleService, VerificationService verificationService) {
        this.utilisateurService = utilisateurService;
        this.roleService = roleService;
        this.verificationService = verificationService;
    }

    @GetMapping("/groupe/{idGroupe}/roleUser/{userId}")
    public ResponseEntity<?> getRoleOfUserFromGroupe(@PathVariable Long idGroupe, @PathVariable Long userId) {

        if (!verificationService.isAuthentificated()) {
            return VerificationPermissions.getErrorResponse("Vous n'êtes pas connecté !");
        }

        List<String> necessariesPermissions = List.of("role_access");
        if (!verificationService.isAuthorizedToAccess(necessariesPermissions, idGroupe)) {
            return VerificationPermissions.getErrorResponse("Vous ne faîtes pas partie de ce groupe ou vous n'avez pas les droits d'accéder à cette ressource !");
        }

        RoleDTO roleDTO = roleService.getRoleFromGroupUser(idGroupe, userId);
        return ResponseEntity.ok().body(roleDTO);

    }

    @GetMapping("/groupe/{idGroupe}/selfRole")
    public ResponseEntity<?> getMyRole(@PathVariable Long idGroupe) {

        if (!verificationService.isAuthentificated()) {
            return VerificationPermissions.getErrorResponse("Vous n'êtes pas connecté !");
        }

        List<String> necessariesPermissions = List.of();
        if (!verificationService.isAuthorizedToAccess(necessariesPermissions, idGroupe)) {
            return VerificationPermissions.getErrorResponse("Vous ne faîtes pas partie de ce groupe !");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usrEmail = authentication.getName();
        UtilisateurDTO userInfo = utilisateurService.getUtilisateurByEmail(usrEmail);

        RoleDTO roleDTO = roleService.getRoleFromGroupUser(idGroupe, userInfo.getId());
        return ResponseEntity.ok().body(roleDTO);

    }

    @PostMapping("/groupe/{idGroupe}/role/add")
    public ResponseEntity<?> addRole(@RequestBody RoleDTO roleDTO, @PathVariable Long idGroupe) {
        try {
            if (!verificationService.isAuthentificated()) {
                return VerificationPermissions.getErrorResponse("Vous n'êtes pas connecté !");
            }

            List<String> necessariesPermissions = List.of("role_create");
            if (!verificationService.isAuthorizedToAccess(necessariesPermissions, idGroupe)) {
                return VerificationPermissions.getErrorResponse("Vous ne faîtes pas partie de ce groupe ou vous n'avez pas les permissions nécessaires !");
            }

            RoleDTO role = roleService.addRoleToGroup(roleDTO, idGroupe);
            return ResponseEntity.ok().body(role);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de l'ajout du rôle");
        }
    }

    @GetMapping("/role/{idRole}")
    public ResponseEntity<?> getRoleWithId(@PathVariable Long idRole) {

        if (!verificationService.isAuthentificated()) {
            return VerificationPermissions.getErrorResponse("Vous n'êtes pas connecté !");
        }

        RoleDTO roleDTO = roleService.getRoleWithIdDTO(idRole);
        return ResponseEntity.ok().body(roleDTO);

    }
    
}
