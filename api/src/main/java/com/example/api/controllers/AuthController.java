package com.example.api.controllers;

import com.example.api.configuration.JwtUtils;
import com.example.api.entity.UtilisateurEntities.Utilisateur;
import com.example.api.repository.UtilisateurRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/soutien/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UtilisateurRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;


    @GetMapping("/validate-token")
    public ResponseEntity<Boolean> validateToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(false); // Token manquant ou invalide
        }

        String token = authorizationHeader.substring(7);

        if (!jwtUtils.isTokenExpired(token)) {
            return ResponseEntity.ok(true); // Le token est valide
        } else {
            return ResponseEntity.status(401).body(false); // Le token est expiré ou invalide
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Utilisateur user) {
        if (userRepository.findByUsrEmail(user.getUsrEmail()) != null) {
            return ResponseEntity.badRequest().body("Email déjà pris !");
        }
        user.setUsrPassword(passwordEncoder.encode(user.getUsrPassword()));
        return ResponseEntity.ok(userRepository.save(user));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Utilisateur user) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsrEmail(), user.getUsrPassword()));
            if (authentication.isAuthenticated()) {
                Map<String, Object> authData = new HashMap<>();
                authData.put("token", jwtUtils.generateToken(user));
                authData.put("type", "Bearer");
                return ResponseEntity.ok(authData);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Mauvais email ou mot de passe");
        } catch (AuthenticationException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Mauvais nom d'utilisateur ou mot de passe");
        }
    }
}
