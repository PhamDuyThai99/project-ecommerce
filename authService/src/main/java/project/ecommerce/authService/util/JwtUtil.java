package project.ecommerce.authService.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${token.secreteKey}")
    private static String SECRET_KEY;
    @Value("${token.expiredTime}")
    private static long EXPIRATION_TIME;

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
