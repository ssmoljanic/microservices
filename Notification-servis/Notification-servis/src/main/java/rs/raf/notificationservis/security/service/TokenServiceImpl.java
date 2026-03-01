package rs.raf.notificationservis.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Service
public class TokenServiceImpl implements TokenService {

    private final Key key;

    public TokenServiceImpl(@Value("${app.jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Claims parseToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Long getUserId(String token) {
        Claims claims = parseToken(token);
        if (claims == null) return null;

        // kod tebe je ranije bilo "id"
        Object id = claims.get("id");
        if (id instanceof Integer) return ((Integer) id).longValue();
        if (id instanceof Long) return (Long) id;

        // fallback ako neko u token stavlja "userId"
        Object userId = claims.get("userId");
        if (userId instanceof Integer) return ((Integer) userId).longValue();
        if (userId instanceof Long) return (Long) userId;

        return null;
    }

    /**
     * Service-to-service JWT: koristi ga notification servis kad zove user-service internal endpoint.
     * User-service mora da dopusti ROLE_SERVICE na tom internal endpointu.
     */
    @Override
    public String generateServiceToken() {
        long now = System.currentTimeMillis();

        Claims claims = Jwts.claims();
        claims.put("role", "ROLE_SERVICE");
        claims.put("id", 0L);       // ili "userId" ako tako koristiš svuda


        return Jwts.builder()
                .setClaims(claims)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}