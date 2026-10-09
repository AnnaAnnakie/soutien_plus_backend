package com.example.api.controller;

import com.example.api.controllers.UtilisateurController;
import com.example.api.dto.UtilisateurDTOs.UtilisateurDTO;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UtilisateurControllerTest {

    @Mock
    private UtilisateurService utilisateurService;

    @Mock
    private VerificationService verificationService;

    @InjectMocks
    private UtilisateurController utilisateurController;

    // TEST POUR OBTENIR L'UTILISATEUR PAR EMAIL

    /** Test de getUser
     * On simule la base de données et on tente de recupérer l'utilisateur
     */
    @Test
    public void testGetUser_Success() {
        String email = "@gemal.allessetomas@gmail.com";
        UtilisateurDTO utilisateurDTO = UtilisateurDTO.builder()
                .id(1L)
                .email(email)
                .name("Gemal")
            .second_name("Allessetomas")
                .build();

        when(verificationService.isAuthentificated()).thenReturn(true);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(email);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(utilisateurService.getUtilisateurByEmail(email)).thenReturn(utilisateurDTO);

        ResponseEntity<?> response = utilisateurController.getUser();

        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        assertEquals(utilisateurDTO, response.getBody());
    }

    /** Test de getUser
     * On tente de récupérer un utilisateur quelconque sans être authentifié
     * Renvoie un code 401 UNAUTHORIZED et une réponse "vous n'êtes pas connecté !"
     */
    @Test
    public void testGetUser_NotAuthenticated() {
        when(verificationService.isAuthentificated()).thenReturn(false);

        ResponseEntity<?> response = utilisateurController.getUser();

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Vous n'êtes pas connecté !"));
    }

    // TEST POUR OBTENIR L'UTILISATEUR PAR ID

    /** Test de getUserById
     * On simule la base de données et on tente de récupérer l'utilisateur via son Id
     */
    @Test
    public void testGetUserById_Success() {
        Long id = 1L;
        UtilisateurDTO utilisateurDTO = UtilisateurDTO.builder()
                .id(id)
                .email("gemal.allessetomas@gmail.com")
                .name("Gemal")
                .second_name("Allessetomas")
                .build();

        when(utilisateurService.getUtilisateurById(id)).thenReturn(utilisateurDTO);

        ResponseEntity<?> response = utilisateurController.getUserById(id);

        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        assertEquals(utilisateurDTO, response.getBody());
    }

    /** Test de getUserById
     * On tente de récupérer un role quelconque sans être authentifié
     * Renvoie un code 404 NOT FOUND
     */
    @Test
    public void testGetUserById_NotFound() {
        Long id = 1L;
        when(utilisateurService.getUtilisateurById(id)).thenReturn(null);

        ResponseEntity<?> response = utilisateurController.getUserById(id);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
