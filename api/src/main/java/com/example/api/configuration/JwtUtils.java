package com.example.api.configuration;

import com.example.api.entity.UtilisateurEntities.Utilisateur;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtils {

    @Value("${app.secret-key}")
    private String secretKey;

    @Value("${app.expiration-time}")
    private Long expirationTime;

    public String generateToken(Utilisateur user) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, user.getUsrEmail());
    }

    public String createToken(Map<String, Object> claims, String email) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration((new Date(System.currentTimeMillis() + expirationTime)))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private SecretKeySpec getSignKey() {
        byte[] keyBytes = secretKey.getBytes();
        return new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        String email = extractEmail(token);
        return email != null && email.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        Date expiration = extractExpirationDate(token);
        return expiration == null || expiration.before(new Date());
    }

    public String extractEmail(String token) {
        Claims claims = extractAllClaims(token);
        return claims != null ? claims.getSubject() : null;
    }

    private Date extractExpirationDate(String token) {
        Claims claims = extractAllClaims(token);
        return claims != null ? claims.getExpiration() : new Date(0);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSignKey())
                    .build()
                    .parseSignedClaims(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }
}
