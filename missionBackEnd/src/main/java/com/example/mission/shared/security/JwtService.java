package com.example.mission.shared.security;

import com.example.mission.user.entity.Utilisateur;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long expiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public String generateToken(Utilisateur utilisateur) {
        return Jwts.builder()
                .subject(utilisateur.getEmail())
                .claim("role", utilisateur.getRole().name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    public String extractEmail(String token) {
        return parseClaims(token).getSubject();
    }

    public Date extractExpiration(String token) {
        return parseClaims(token).getExpiration();
    }

    public boolean isTokenValid(String token, String email) {
        try {
            String tokenEmail = extractEmail(token);
            Date exp = extractExpiration(token);
            return tokenEmail.equals(email) && exp.after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

