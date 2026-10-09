package com.example.api.controllers;

import com.example.api.dto.RoleDTOs.PermissionDTO;
import com.example.api.dto.RoleDTOs.RoleDTO;
import com.example.api.service.PermissionService;
import com.example.api.service.VerificationService;
import com.example.api.utils.VerificationPermissions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/soutien/permission")
public class PermissionController {

    private final PermissionService permissionService;
    private final VerificationService verificationService;

    public PermissionController(PermissionService permissionService, VerificationService verificationService) {
        this.permissionService = permissionService;
        this.verificationService = verificationService;
    }

    @GetMapping
    public ResponseEntity<?> getAllPermissions() {

        if (!verificationService.isAuthentificated()) {
            return VerificationPermissions.getErrorResponse("Vous n'êtes pas connecté !");
        }

        List<PermissionDTO> permissionDTOS = this.permissionService.getAllPermissions();
        return ResponseEntity.ok().body(permissionDTOS);
    }
}
