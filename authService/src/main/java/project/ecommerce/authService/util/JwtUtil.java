package project.ecommerce.authService.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import project.ecommerce.authService.dto.response.external.RoleResponse;
import project.ecommerce.authService.dto.response.external.UserInternalResponse;
import project.ecommerce.authService.exception.ApiError;
import project.ecommerce.authService.exception.AppException;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;

@Slf4j
public class JwtUtil {

    private static final String SECRET_KEY = "e499fd93ee8d8e85ae346277ba346d4b523b9d514fd3db9b8d641eeee9546fbd71a84caabb22a533f5d9ac95b46f96175e8fc3cc0f377ad386e9e9ad270b7f00";
    private static final long EXPIRATION_TIME = 600_000; // 1 hour

    public static String generateToken(UserInternalResponse response) {
        try {
            return Jwts.builder()
                    .setId(String.valueOf(UUID.randomUUID()))
                    .setSubject(response.getUsername())
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                    .claim("roles", setScope(response))
                    .claim("userId", response.getId())
                    .signWith(getSignInKey(), SignatureAlgorithm.HS512)
                    .compact();
        } catch (JwtException ex) {
            throw new AppException(ApiError.UNAUTHENTICATED);
        }
    }

    public static Claims validateToken(String token) {
        token = token.trim();
        log.info("validate token method");
        // verify signing key
        try {
            log.info("token: {}", token);
            var jwtParser = Jwts.parserBuilder()
                .setSigningKey(getSignInKey()).build();
            log.info("jwtParser.parseClaimsJws {}", jwtParser.parseClaimsJws(token));
            return Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException ex) {
            throw new AppException(ApiError.UNAUTHENTICATED);
        }
    }



    private static Key getSignInKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    private static List<String> setScope(UserInternalResponse response) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        return response.getRoles().stream()
                .map(RoleResponse::getName)
                .map(role -> stringJoiner.add("ROLE_" + role).toString())
                .toList();
    }
}
