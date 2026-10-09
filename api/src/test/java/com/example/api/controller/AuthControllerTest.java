package com.example.api.controller;

import com.example.api.controllers.AuthController;
import com.example.api.configuration.JwtUtils;
import com.example.api.entity.UtilisateurEntities.Utilisateur;
import com.example.api.repository.UtilisateurRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Utilisation de Mockito avec JUnit
public class AuthControllerTest {

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthController authController;

    // TESTS POUR LES TOKEN VALIDE

    /** Test de validateToken
     * On simule un token valide et on verifie qu'il est valide
     */
    @Test
    public void testValidateToken_Valid() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer validToken");
        when(jwtUtils.isTokenExpired("validToken")).thenReturn(false);

        ResponseEntity<Boolean> response = authController.validateToken(request);

        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        assertTrue(response.getBody());
    }

    /** Test de validateToken
     * On simule un token expiré
     * Renvoie un code 401 UNAUTHORIZED
     */
    @Test
    public void testValidateToken_Invalid() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer expiredToken");
        when(jwtUtils.isTokenExpired("expiredToken")).thenReturn(true);

        ResponseEntity<Boolean> response = authController.validateToken(request);

        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCodeValue());
        assertFalse(response.getBody());
    }

    // TESTS POUR LES REGISTER

    /** Test de register
     * On simule un utilisateur et on vérifie que l'utilisateur est enregistré et que le mot de passe est bien hashé
     */
    @Test
    public void testRegister_Success() {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setUsrEmail("aude.javel@gmail.com");
        utilisateur.setUsrPassword("Passw0rd!");

        when(utilisateurRepository.findByUsrEmail(utilisateur.getUsrEmail())).thenReturn(null);
        when(passwordEncoder.encode(utilisateur.getUsrPassword())).thenReturn("hashedPassword");
        when(utilisateurRepository.save(any(Utilisateur.class))).thenReturn(utilisateur);

        ResponseEntity<?> response = authController.register(utilisateur);

        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        verify(utilisateurRepository, times(1)).save(any(Utilisateur.class));
    }

    /** Test de register
     * On simule un utilisateur déjà existant
     * Renvoie un code 400 BAD_REQUEST
     */
    @Test
    public void testRegister_EmailAlreadyExists() {
        Utilisateur existingUser = new Utilisateur();
        existingUser.setUsrEmail("aude.javel@gmail.com");

        when(utilisateurRepository.findByUsrEmail(existingUser.getUsrEmail())).thenReturn(existingUser);

        ResponseEntity<?> response = authController.register(existingUser);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
        verify(utilisateurRepository, never()).save(any(Utilisateur.class));
    }

    // TESTS POUR LES CONNEXIONS

    /** Test de login
     * On simule une authentification réussie
     */
    @Test
    public void testLogin_Success() {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setUsrEmail("aude.javel@gmail.com");
        utilisateur.setUsrPassword("Passw0rd!");

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtUtils.generateToken(utilisateur)).thenReturn("mockedToken");

        ResponseEntity<?> response = authController.login(utilisateur);

        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    /** Test de login
     * On simule une échec d'authentification
     * Renvoie un code 401 UNAUTHORIZED et une réponse "Mauvais nom d'utilisateur ou mot de passe"
     */
    @Test
    public void testLogin_Failure() {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setUsrEmail("aude.javel@gmail.com");
        utilisateur.setUsrPassword("passwordToutNul");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new AuthenticationException("Authentication failed") {});

        ResponseEntity<?> response = authController.login(utilisateur);

        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCodeValue());
        assertEquals("Mauvais nom d'utilisateur ou mot de passe", response.getBody());
    }
}
