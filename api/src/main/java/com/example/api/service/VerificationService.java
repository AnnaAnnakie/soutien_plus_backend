package com.example.api.service;

import com.example.api.dto.RoleDTOs.PermissionDTO;
import com.example.api.entity.UtilisateurEntities.UsersGroupesRoles;
import com.example.api.repository.*;
import com.example.api.utils.VerificationPermissions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class VerificationService {

    private final GroupService groupService;
    private final UtilisateurService utilisateurService;
    private final RoleService roleService;

    public VerificationService(RoleService roleService,GroupService groupService, UtilisateurService utilisateurService) {
        this.groupService = groupService;
        this.utilisateurService = utilisateurService;
        this.roleService = roleService;

    }


    public boolean isAuthentificated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }


    public boolean isAuthorizedToAccess(List<String> necessariesPermissions, Long idGroupe) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usrEmail = authentication.getName();
        Long usrId = this.utilisateurService.getUtilisateurByEmail(usrEmail).getId();


        if (this.roleService.getRoleFromGroupUser(idGroupe, usrId) == null) {
            return false;
        } else {
            List<String> permissionsUser = this.roleService.getRoleFromGroupUser(idGroupe, usrId)
                    .getPermissions().stream().map(PermissionDTO::getPermission).toList();



            return VerificationPermissions.isAuthorizedWithPermission(permissionsUser
                    , necessariesPermissions);
        }
    }



}