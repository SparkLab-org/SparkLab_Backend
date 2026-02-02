package com.sparkLab.study.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Value;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private final String secret;

    @Value("${jwt.expiration}")
    private final Long expiration;

    public String issueAccessToken(String userId, String role, Long ver) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("role", role)
                .claim("version", ver)

                .setId(UUID.randomUUID().toString())
                .setAudience("access")
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expiration))

                .signWith(secret, SignatureAlgorithm.RS256)
                .compact();
    }
}
