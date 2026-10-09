package com.example.api.controllers;

import com.example.api.dto.DocumentDTO;
import com.example.api.dto.RoleDTOs.RoleDTO;
import com.example.api.dto.UtilisateurDTOs.UtilisateurDTO;
import com.example.api.service.UtilisateurService;
import com.example.api.service.VerificationService;
import com.example.api.utils.VerificationPermissions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/soutien/user")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;
    private final VerificationService verificationService;

    public UtilisateurController(UtilisateurService utilisateurService, VerificationService verificationService) {
        this.utilisateurService = utilisateurService;
        this.verificationService = verificationService;
    }

    @PostMapping("/getuser")
    public ResponseEntity<?> getUser() {


        if (!verificationService.isAuthentificated()){
            return VerificationPermissions.getErrorResponse("Vous n'êtes pas connecté !");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usrEmail = authentication.getName();
        UtilisateurDTO userInfo = utilisateurService.getUtilisateurByEmail(usrEmail);

        return ResponseEntity.ok().body(userInfo);

    }

    @GetMapping("/getuser/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        UtilisateurDTO utilisateurDTO= utilisateurService.getUtilisateurById(id);

        if (utilisateurDTO != null) {

            return ResponseEntity.ok(utilisateurDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}
