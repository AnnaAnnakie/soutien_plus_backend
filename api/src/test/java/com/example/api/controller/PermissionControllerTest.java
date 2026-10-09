package com.example.api.controller;

import com.example.api.controllers.PermissionController;
import com.example.api.dto.RoleDTOs.PermissionDTO;
import com.example.api.service.PermissionService;
import com.example.api.service.VerificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PermissionControllerTest {

    @Mock
    private PermissionService permissionService;

    @Mock
    private VerificationService verificationService;

    @InjectMocks
    private PermissionController permissionController;

    // TESTS POUR LA RÉCUPÉRATION DE TOUTES LES PERMISSIONS

    /** Test de getAllPermissions
     * On simule la récupération de toutes les permissions et on vérifie la réponse
     */
    @Test
    public void testGetAllPermissions_Success() {
        List<PermissionDTO> permissionDTOList = List.of(
                new PermissionDTO(1L, "READ", "Permission to read"),
                new PermissionDTO(2L, "WRITE", "Permission to write")
        );

        when(verificationService.isAuthentificated()).thenReturn(true);
        when(permissionService.getAllPermissions()).thenReturn(permissionDTOList);

        ResponseEntity<?> response = permissionController.getAllPermissions();

        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        assertEquals(permissionDTOList, response.getBody());
    }

    /** Test de getAllPermissions
     * On tente de récupérer les permissions sans être authentifié
     * Renvoie un code 401 UNAUTHORIZED et une réponse "Vous n'êtes pas connecté !"
     */
    @Test
    public void testGetAllPermissions_NotAuthenticated() {
        when(verificationService.isAuthentificated()).thenReturn(false);

        ResponseEntity<?> response = permissionController.getAllPermissions();

        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Vous n'êtes pas connecté !"));
    }
}
