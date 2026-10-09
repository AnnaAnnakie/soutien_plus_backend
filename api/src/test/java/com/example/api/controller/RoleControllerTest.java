package com.example.api.controller;

import com.example.api.controllers.RoleController;
import com.example.api.dto.RoleDTOs.RoleDTO;
import com.example.api.dto.UtilisateurDTOs.UtilisateurDTO;
import com.example.api.service.RoleService;
import com.example.api.service.UtilisateurService;
import com.example.api.service.VerificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoleControllerTest {

    @Mock
    private UtilisateurService utilisateurService;

    @Mock
    private RoleService roleService;

    @Mock
    private VerificationService verificationService;

    @InjectMocks
    private RoleController roleController;

    // TESTS DE RECUPERATION DE ROLE D'UN UTILISATEUR DANS UN GROUPE

    /** Test de getRoleOfUserFromGroupe
     * On simule la base de données et on test le role d'un utilisateur dans un groupe quelconque
     */
    @Test
    public void testGetRoleOfUserFromGroupe_Success() {
        Long groupId = 1L;
        Long userId = 2L;
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setId(1L);
        roleDTO.setName("Admin");

        when(verificationService.isAuthentificated()).thenReturn(true);
        when(verificationService.isAuthorizedToAccess(any(), eq(groupId))).thenReturn(true);
        when(roleService.getRoleFromGroupUser(groupId, userId)).thenReturn(roleDTO);

        ResponseEntity<?> response = roleController.getRoleOfUserFromGroupe(groupId, userId);

        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        assertEquals(roleDTO, response.getBody());
    }

    /** Test de getRoleOfUserFromGroupe
     * On tente de récupérer un role quelconque sans être authentifié
     * Renvoie un code 401 UNAUTHORIZED et une réponse "vous n'êtes pas connecté !"
     */
    @Test
    public void testGetRoleOfUserFromGroupe_NotAuthenticated() {
        ResponseEntity<?> response = roleController.getRoleOfUserFromGroupe(1L, 2L);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Vous n'êtes pas connecté !"));
    }

    /** Test de getRoleOfUserFromGroupe
     * On tente de récupérer un role dans un groupe sans les permissions
     * Renvoie un code 401 UNAUTHORIZED et une réponse "vous ne faîtes pas partie de ce groupe !"
     */
    @Test
    public void testGetRoleOfUserFromGroupe_NotAuthorized() {
        Long groupId = 1L;
        Long userId = 2L;

        when(verificationService.isAuthentificated()).thenReturn(true);
        when(verificationService.isAuthorizedToAccess(any(), eq(groupId))).thenReturn(false);

        ResponseEntity<?> response = roleController.getRoleOfUserFromGroupe(groupId, userId);
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Vous ne faîtes pas partie de ce groupe"));
    }

    // TEST POUR "MON" PROPRE ROLE

    /** Test de getMyRole
     * On simule la base de données et on cherche à récupérer mon role
     */
    @Test
    public void testGetMyRole_Success() {
        Long groupId = 1L;
        Long userId = 2L;
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setId(1L);
        roleDTO.setName("User");

        UtilisateurDTO fakeUser = new UtilisateurDTO();
        fakeUser.setId(userId);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("sam.lebrize@gmail.com");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(verificationService.isAuthentificated()).thenReturn(true);
        when(verificationService.isAuthorizedToAccess(anyList(), eq(groupId))).thenReturn(true);
        when(utilisateurService.getUtilisateurByEmail(anyString())).thenReturn(fakeUser);
        when(roleService.getRoleFromGroupUser(groupId, userId)).thenReturn(roleDTO);

        ResponseEntity<?> response = roleController.getMyRole(groupId);

        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        assertEquals(roleDTO, response.getBody());
    }

    /** Test de getMyRole
     * On tente de récupérer "mon" role sans être connecté
     * Renvoie un code 401 UNAUTHORIZED et une réponse "vous n'êtes pas connecté !"
     */
    @Test
    public void testGetMyRole_NotAuthenticated() {
        ResponseEntity<?> response = roleController.getMyRole(1L);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Vous n'êtes pas connecté !"));
    }

    /** Test de getMyRole
     * On tente de récupérer "mon" rôle sans être autorisé à accéder au groupe
     * Renvoie un code 401 UNAUTHORIZED et une réponse "Vous ne faîtes pas partie de ce groupe !"
     */
    @Test
    public void testGetMyRole_NotAuthorized() {
        Long groupId = 1L;

        when(verificationService.isAuthentificated()).thenReturn(true);
        when(verificationService.isAuthorizedToAccess(anyList(), eq(groupId))).thenReturn(false);

        ResponseEntity<?> response = roleController.getMyRole(groupId);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Vous ne faîtes pas partie de ce groupe !"));
    }

    // TEST POUR RECUPERER "MON" ROLE

    /** Test de addRole
     * On simule l'ajout d'un rôle avec des permissions valides et vérifie que le rôle est ajouté avec succès
     */
    @Test
    public void testAddRole_Success() {
        Long groupId = 1L;
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setName("Admin");

        when(verificationService.isAuthentificated()).thenReturn(true);
        when(verificationService.isAuthorizedToAccess(anyList(), eq(groupId))).thenReturn(true);
        when(roleService.addRoleToGroup(roleDTO, groupId)).thenReturn(roleDTO);

        ResponseEntity<?> response = roleController.addRole(roleDTO, groupId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(roleDTO, response.getBody());
    }

    /** Test de addRole
     * On tente d'ajouter un rôle sans être connecté, cela devrait renvoyer un code 401 UNAUTHORIZED
     */
    @Test
    public void testAddRole_NotAuthenticated() {
        RoleDTO roleDTO = new RoleDTO();
        ResponseEntity<?> response = roleController.addRole(roleDTO, 1L);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Vous n'êtes pas connecté !"));
    }

    /** Test de addRole
     * On tente d'ajouter un rôle sans avoir les permissions nécessaires
     * La réponse doit indiquer un code 401 UNAUTHORIZED avec un message approprié
     */
    @Test
    public void testAddRole_NotAuthorized() {
        Long groupId = 1L;
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setName("Admin");

        when(verificationService.isAuthentificated()).thenReturn(true);
        when(verificationService.isAuthorizedToAccess(anyList(), eq(groupId))).thenReturn(false);

        ResponseEntity<?> response = roleController.addRole(roleDTO, groupId);

        // Affichage du corps de la réponse pour débogage
        System.out.println("Response body: " + response.getBody());

        // On s'assure que la réponse est bien un Map et contient la clé "error"
        assertTrue(response.getBody() instanceof Map);
        Map<String, String> responseBody = (Map<String, String>) response.getBody();

        // Vérification que le message d'erreur est bien dans la réponse
        assertTrue(responseBody.containsKey("error"));
        assertEquals("Vous ne faîtes pas partie de ce groupe ou vous n'avez pas les permissions nécessaires !", responseBody.get("error"));
    }



    /** Test de addRole
     * On tente d'ajouter un rôle avec une authentification mais une erreur dans le service d'ajout de rôle
     * On vérifie que l'exception ou le retour d'erreur est bien géré
     */
    @Test
    public void testAddRole_Failure() {
        Long groupId = 1L;
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setName("Admin");

        when(verificationService.isAuthentificated()).thenReturn(true);
        when(verificationService.isAuthorizedToAccess(anyList(), eq(groupId))).thenReturn(true);
        when(roleService.addRoleToGroup(roleDTO, groupId)).thenThrow(new RuntimeException("Erreur lors de l'ajout du rôle"));

        ResponseEntity<?> response = roleController.addRole(roleDTO, groupId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Erreur lors de l'ajout du rôle"));
    }


    // TEST POUR LES ROLES A PARTIR D'UN ID

    /** Test de getRoleWithId
     * On simule la base de données et on vérifie qu'on retrouve bien le rôle à partir de son Id
     */
    @Test
    public void testGetRoleWithId_Success() {
        Long roleId = 1L;
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setId(roleId);
        roleDTO.setName("Admin");

        when(verificationService.isAuthentificated()).thenReturn(true);
        when(roleService.getRoleWithIdDTO(roleId)).thenReturn(roleDTO);

        ResponseEntity<?> response = roleController.getRoleWithId(roleId);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        assertEquals(roleDTO, response.getBody());
    }

    /** Test de addGroup
     * On tente de trouver un role quelconque sans être connecté
     * Renvoie un code 401 UNAUTHORIZED et une réponse "Vous n'êtes pas connecté !"
     */
    @Test
    public void testGetRoleWithId_NotAuthenticated() {
        ResponseEntity<?> response = roleController.getRoleWithId(1L);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Vous n'êtes pas connecté !"));
    }


}