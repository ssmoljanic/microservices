package rs.raf.notificationservis.security.service;

import io.jsonwebtoken.Claims;

public interface TokenService {
    Claims parseToken(String token);
    Long getUserId(String token);
    String generateServiceToken();
}