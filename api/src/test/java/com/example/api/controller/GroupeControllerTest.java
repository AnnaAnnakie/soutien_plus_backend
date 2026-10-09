package com.example.api.controller;

import com.example.api.controllers.GroupeController;
import com.example.api.dto.RoleDTOs.RoleDTO;
import com.example.api.dto.RoleDTOs.SimpleRoleDTO;
import com.example.api.dto.UtilisateurDTOs.SimpleUtilisateurDTO;
import com.example.api.dto.UtilisateurDTOs.UtilisateurDTO;
import com.example.api.entity.GroupeEntities.Groupe;
import com.example.api.entity.UtilisateurEntities.UsersGroupesRoles;
import com.example.api.service.*;
import com.example.api.dto.GroupeDTOs.GroupDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class GroupeControllerTest {

    @Mock
    private GroupService groupService;

    @Mock
    private UtilisateurService utilisateurService;

    @Mock
    private UtilisateurDTO utilisateurDTO;

    @Mock
    private VerificationService verificationService;

    @Mock
    private RoleService roleService;

    @InjectMocks
    private GroupeController groupeController;

    // TESTS DE RÉCUPÉRATION DE GROUPE

    /** Test de getGroupe en étant connecté
     * On simule la base de données et on test qu'on trouve bien le groupe
     */
    @Test
    public void testGetGroup_Success() {
        Long groupId = 1L;
        List<SimpleUtilisateurDTO> utilisateurs = new ArrayList<>();
        List<SimpleRoleDTO> roles = new ArrayList<>();
        GroupDTO groupDTO = new GroupDTO(groupId, "room_test", "2234567890000", "Jean", "Aimarre", "test", 123456, utilisateurs, roles);

        when(verificationService.isAuthentificated()).thenReturn(true);
        when(verificationService.isAuthorizedToAccess(any(), eq(groupId))).thenReturn(true);
        when(groupService.getGroupById(groupId)).thenReturn(groupDTO);

        ResponseEntity<?> response = groupeController.getGroup(groupId);

        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        assertEquals(groupDTO, response.getBody());
    }

    /** Test de getGroupe sans être authentifié
     * On tente de récupérer un groupe quelconque
     * Renvoie un code 401 UNAUTHORIZED et une réponse "vous n'êtes pas connecté !"
     */
    @Test
    public void testGetGroup_NotAuthenticated() {
        ResponseEntity<?> response = groupeController.getGroup(1L);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Vous n'êtes pas connecté !"));
    }

    /** Test de getGroupe
     * On tente de récupérer un groupe sans les permissions
     * Renvoie un code 401 UNAUTHORIZED et une réponse "vous ne faîtes pas partie de ce groupe !"
     */
    @Test
    public void testGetGroup_NotAuthorized() {
        Long groupId = 1L;

        when(verificationService.isAuthentificated()).thenReturn(true);
        when(verificationService.isAuthorizedToAccess(any(), eq(groupId))).thenReturn(false);

        ResponseEntity<?> response = groupeController.getGroup(groupId);

        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCodeValue());

        assertEquals("Vous ne faîtes pas partie de ce groupe !", ((Map<String, String>) response.getBody()).get("error"));
    }

    //  TESTS POUR LES ROLES

    /** Test de getRolesOfGroupe
     * On simule la base de données et on cherche qu'on arrive bien à trouver les roles du groupe
     */
    @Test
    public void testGetRolesOfGroupe_Success() {
        Long groupId = 1L;
        List<SimpleRoleDTO> roles = new ArrayList<>();
        roles.add(new SimpleRoleDTO(1L, "test", "desc_test", true));

        when(verificationService.isAuthentificated()).thenReturn(true);
        when(verificationService.isAuthorizedToAccess(any(), eq(groupId))).thenReturn(true);
        when(groupService.getRoleGroup(groupId)).thenReturn(roles);

        ResponseEntity<?> response = groupeController.getRolesOfGroupe(groupId);

        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        assertEquals(roles, response.getBody());
    }

    /** Test de getRolesOfGroupe
     * On tente de récupérer des roles sans être connecté
     * Renvoie un code 401 UNAUTHORIZED et une réponse "vous n'êtes pas connecté !"
     */
    @Test
    public void testGetRolesOfGroupe_NotAuthenticated() {
        Long groupId = 1L;

        ResponseEntity<?> response = groupeController.getRolesOfGroupe(groupId);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Vous n'êtes pas connecté !"));
    }

    /** Test de getRolesOfGroupe
     * On tente de récupérer des roles sans les permissions
     * Renvoie un code 401 UNAUTHORIZED et une réponse "vous ne faîtes pas partie de ce groupe !"
     */
    @Test
    public void testGetRolesOfGroupe_NotAuthorized() {
        Long groupId = 1L;

        when(verificationService.isAuthentificated()).thenReturn(true);
        when(verificationService.isAuthorizedToAccess(any(), eq(groupId))).thenReturn(false);

        ResponseEntity<?> response = groupeController.getRolesOfGroupe(groupId);

        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCodeValue());

        assertEquals("Vous ne faîtes pas partie de ce groupe ou vous n'avez pas les droits d'accéder à cette ressource !", ((Map<String, String>) response.getBody()).get("error"));
    }

    // TESTS POUR ADD UN GROUPE

    /** Test de addGroup
     * On simule la base de données et on tente d'y ajouter un nouveau groupe
     */
    @Test
    public void testAddGroup_Success() {
        GroupDTO groupDTO = new GroupDTO(3L, "room_test", "2234567890000", "Jean", "Aimarre", "test", 123456, new ArrayList<>(), new ArrayList<>());

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("jean.aimarre@gmail.com");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UtilisateurDTO utilisateurMock = mock(UtilisateurDTO.class);
        when(utilisateurService.getUtilisateurByEmail("jean.aimarre@gmail.com")).thenReturn(utilisateurMock);
        when(utilisateurMock.getId()).thenReturn(1L);

        when(groupService.addGroup(groupDTO, 1L)).thenReturn(groupDTO);
        when(verificationService.isAuthentificated()).thenReturn(true);

        ResponseEntity<?> response = groupeController.addGroup(groupDTO);

        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        assertEquals(groupDTO, response.getBody());
    }


    /** Test de addGroup
     * On tente d'ajouter un groupe sans être connecté
     * Renvoie un code 401 UNAUTHORIZED et une réponse "vous n'êtes pas connecté !"
     */
    @Test
    public void testAddGroup_NotAuthenticated() {
        ResponseEntity<?> response = groupeController.addGroup(new GroupDTO());

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Vous n'êtes pas connecté !"));
    }

    // TESTS POUR LES ROLES SUR UN UTILISATEUR

    /** Test de setRoleToUserInGroup
     *  On simule la base de données et on tente d'ajouter un role à un utilisateur
     */
    @Test
    public void testSetRoleToUserInGroup_Success() {
        Long groupId = 3L;
        Long userId = 2L;
        Long roleId = 1L;

        UsersGroupesRoles usersGroupesRoles = new UsersGroupesRoles();
        usersGroupesRoles.setId(666L);
        usersGroupesRoles.setId_user(userId);
        usersGroupesRoles.setId_groupe(groupId);
        usersGroupesRoles.setId_role(roleId);

        when(verificationService.isAuthentificated()).thenReturn(true);
        when(verificationService.isAuthorizedToAccess(any(), eq(groupId))).thenReturn(true);
        when(groupService.setUserOfGroupeToRole(eq(groupId), eq(roleId), eq(userId))).thenReturn(usersGroupesRoles);

        ResponseEntity<?> response = groupeController.setRoleToUserInGroup(groupId, userId, roleId);

        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        assertEquals(usersGroupesRoles, response.getBody());
    }

    /** Test de setRoleToUserInGroup
     * On tente d'ajouter un role à un utilisateur sans être connecté
     * Renvoie un code 401 UNAUTHORIZED et une réponse "vous n'êtes pas connecté !"
     */
    @Test
    public void testSetRoleToUserInGroup_NotAuthenticated() {
        Long groupId = 2L;
        Long userId = 3L;
        Long roleId = 1L;

        ResponseEntity<?> response = groupeController.setRoleToUserInGroup(groupId, userId, roleId);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Vous n'êtes pas connecté !"));
    }

    /** Test de setRoleToUserInGroup
     * On tente d'ajouter un role à un utilisateur sans les permissions
     * Renvoie un code 401 UNAUTHORIZED et une réponse "Vous ne faîtes pas partie de ce groupe !"
     */
    @Test
    public void testSetRoleToUserInGroup_NotAuthorized() {
        Long groupId = 2L;
        Long userId = 3L;
        Long roleId = 1L;

        when(verificationService.isAuthentificated()).thenReturn(true);
        when(verificationService.isAuthorizedToAccess(any(), eq(groupId))).thenReturn(false);

        ResponseEntity<?> response = groupeController.setRoleToUserInGroup(groupId, userId, roleId);

        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCodeValue());
        assertEquals("Vous ne faîtes pas partie de ce groupe !", ((Map<String, String>) response.getBody()).get("error"));
    }

    // TESTS POUR RÉCUPÉRER LES UTILISATEURS À PARTIR D'UN ROLE DANS LE GROUPE

    /** Test de getUtilisateurFromRoleOfGroupe
     *  On simule la base de données et on tente de récupérer un utilisateur dans un groupe
     */
    @Test
    public void testGetUtilisateursFromRoleOfGroupe_Success() {
        Long groupId = 1L;
        Long roleId = 1L;

        List<SimpleUtilisateurDTO> utilisateurs = List.of(new SimpleUtilisateurDTO(), new SimpleUtilisateurDTO());

        when(verificationService.isAuthentificated()).thenReturn(true);
        when(verificationService.isAuthorizedToAccess(any(), eq(groupId))).thenReturn(true);
        when(utilisateurService.getUsersFromRoleAndGroupe(eq(roleId), eq(groupId))).thenReturn(utilisateurs);

        ResponseEntity<?> response = groupeController.getUtilisateursFromRoleOfGroupe(groupId, roleId);

        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        assertEquals(utilisateurs, response.getBody());
    }

    /** Test de getUtilisateurFromRoleOfGroupe
     * On tente de récupérer un utilisateur sans être connecté
     * Renvoie un code 401 UNAUTHORIZED et une réponse "vous n'êtes pas connecté !"
     */
    @Test
    public void testGetUtilisateursFromRoleOfGroupe_NotAuthenticated() {
        Long groupId = 1L;
        Long roleId = 1L;

        ResponseEntity<?> response = groupeController.getUtilisateursFromRoleOfGroupe(groupId, roleId);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Vous n'êtes pas connecté !"));
    }

    /** Test de getUtilisateurFromRoleOfGroupe
     * On tente de récupérer un utilisateur sans les permissions
     * Renvoie un code 401 UNAUTHORIZED et une réponse "Vous ne faîtes pas partie de ce groupe !"
     */
    @Test
    public void testGetUtilisateursFromRoleOfGroupe_NotAuthorized() {
        Long groupId = 1L;
        Long roleId = 1L;

        when(verificationService.isAuthentificated()).thenReturn(true);
        when(verificationService.isAuthorizedToAccess(any(), eq(groupId))).thenReturn(false);

        ResponseEntity<?> response = groupeController.getUtilisateursFromRoleOfGroupe(groupId, roleId);

        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCodeValue());
        assertEquals("Vous ne faîtes pas partie de ce groupe !", ((Map<String, String>) response.getBody()).get("error"));
    }

    // TESTS POUR LES PERMISSIONS D'UN ROLE

    /** Test de setPermissionToRole
     *  On simule la base de données et on tente d'attribuer des permissions à un role dans un groupe
     */
    @Test
    public void testSetPermissionToRole_Success() {
        Long groupId = 1L;
        Long roleId = 1L;
        Long[] permissionIds = {1L, 2L};

        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setId(roleId);
        roleDTO.setName("Admin");
        roleDTO.setDescription("Rôle administrateur");

        when(verificationService.isAuthentificated()).thenReturn(true);
        when(verificationService.isAuthorizedToAccess(any(), eq(groupId))).thenReturn(true);

        when(roleService.setPermissionsToRole(eq(permissionIds), eq(roleId))).thenReturn(roleDTO);

        ResponseEntity<?> response = groupeController.setPermissionToRole(groupId, roleId, permissionIds);

        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());

        assertEquals(roleDTO, response.getBody());
    }

    /** Test de setPermissionToRole
     * On tente d'attribuer des permissions à un role dans un groupe sans être connecté
     * Renvoie un code 401 UNAUTHORIZED et une réponse "vous n'êtes pas connecté !"
     */
    @Test
    public void testSetPermissionToRole_NotAuthenticated() {
        Long groupId = 1L;
        Long roleId = 1L;
        Long[] permissionIds = {1L, 2L};

        ResponseEntity<?> response = groupeController.setPermissionToRole(groupId, roleId, permissionIds);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Vous n'êtes pas connecté !"));
    }

    /** Test de setPermissionToRole
     * On tente d'attribuer des permissions à un role dans un groupe sans les permissions
     * Renvoie un code 401 UNAUTHORIZED et une réponse "Vous ne faîtes pas partie de ce groupe !"
     */
    @Test
    public void testSetPermissionToRole_NotAuthorized() {
        Long groupId = 1L;
        Long roleId = 1L;
        Long[] permissionIds = {1L, 2L};

        when(verificationService.isAuthentificated()).thenReturn(true);
        when(verificationService.isAuthorizedToAccess(any(), eq(groupId))).thenReturn(false);

        ResponseEntity<?> response = groupeController.setPermissionToRole(groupId, roleId, permissionIds);

        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCodeValue());
        assertEquals("Vous ne faîtes pas partie de ce groupe ou vous n'avez pas les permissions de faire cette action !", ((Map<String, String>) response.getBody()).get("error"));
    }


    // TESTS JOIN GROUPE

    /** Test de joinGroup
     *  On simule la base de données et on tente de rejoindre un groupe
     */
    @Test
    public void testJoinGroup_Success() {
        List<SimpleUtilisateurDTO> utilisateurs = new ArrayList<>();
        List<SimpleRoleDTO> roles = new ArrayList<>();

        GroupDTO groupDTO = new GroupDTO(3L, "room_test", "2234567890000", "Jean", "Aimarre", "test", 123456, utilisateurs, roles);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user@example.com");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(groupService.joinGroup(eq(groupDTO), eq("user@example.com"))).thenReturn(new Groupe());

        when(verificationService.isAuthentificated()).thenReturn(true);

        ResponseEntity<?> response = groupeController.joinGroup(groupDTO);

        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }



    /** Test de joinGroup
     * On tente de rejoindre un groupe sans être connecté
     * Renvoie un code 401 UNAUTHORIZED et une réponse "vous n'êtes pas connecté !"
     */
    @Test
    public void testJoinGroup_NotAuthenticated() {
        List<SimpleUtilisateurDTO> utilisateurs= new ArrayList<SimpleUtilisateurDTO>();
        List<SimpleRoleDTO> roles = new ArrayList<SimpleRoleDTO>();

        GroupDTO groupDTO = new GroupDTO(Long.valueOf(3), "room_test", "2234567890000", "Jean", "Aimarre", "test", 123456, utilisateurs, roles);

        ResponseEntity<?> response = groupeController.joinGroup(groupDTO);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Vous n'êtes pas connecté !"));
    }


}
