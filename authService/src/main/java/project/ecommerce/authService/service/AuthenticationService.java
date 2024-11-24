package project.ecommerce.authService.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import project.ecommerce.authService.dto.request.AuthenticationRequest;
import project.ecommerce.authService.dto.response.AuthenticationResponse;
import project.ecommerce.authService.dto.response.GetCurrentUserInfoResponse;
import project.ecommerce.authService.dto.response.IntrospectResponse;
import project.ecommerce.authService.dto.response.common.ApiResponse;
import project.ecommerce.authService.dto.response.external.UserInternalResponse;
import project.ecommerce.authService.entity.OauthAccessTokenEntity;
import project.ecommerce.authService.exception.ApiError;
import project.ecommerce.authService.exception.AppException;
import project.ecommerce.authService.repository.OauthAccessTokenRepository;
import project.ecommerce.authService.util.JwtUtil;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class AuthenticationService {

    private static final String GET_USER_BY_USERNAME_URL = "http://localhost:8080/api/internal/users/{username}";

    private final OauthAccessTokenRepository oauthAccessTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final HttpServletRequest request;

    public AuthenticationService(OauthAccessTokenRepository oauthAccessTokenRepository, PasswordEncoder passwordEncoder, RestTemplate restTemplate, ObjectMapper objectMapper, HttpServletRequest request) {
        this.oauthAccessTokenRepository = oauthAccessTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.request = request;
    }

    @Transactional
    public AuthenticationResponse login(AuthenticationRequest request) {
        String getUserUrl = GET_USER_BY_USERNAME_URL.replace("{username}", request.getUsername());
        ApiResponse apiResponse;
        try {
            log.info("AuthenticationRequest {}", request);
            apiResponse = restTemplate.getForObject(getUserUrl, ApiResponse.class);
            log.info("found user apiResponse: " + apiResponse);
        } catch (RuntimeException ex) {
            throw new AppException(ApiError.UNAUTHENTICATED);
        }
        assert apiResponse != null;
        UserInternalResponse response = objectMapper.convertValue(apiResponse.getData(), UserInternalResponse.class);
        log.info("UserInternalResponse: " + response);

        if (!passwordEncoder.matches(request.getPassword(), response.getPassword())) {
            throw new AppException(ApiError.UNAUTHENTICATED);
        }

        String accessToken = JwtUtil.generateToken(response);

        // store to database
        Claims claims = JwtUtil.validateToken(accessToken);

        LocalDateTime createdTimestamp = claims.getIssuedAt()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        LocalDateTime expiredTimestamp = claims.getExpiration()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        OauthAccessTokenEntity oauthAccessTokenEntity = OauthAccessTokenEntity.builder()
                .token(claims.getId())
                .userId(claims.get("userId", Long.class))
                .username(claims.getSubject())
                .createdTimestamp(createdTimestamp)
                .expiredTimestamp(expiredTimestamp)
                .build();

        oauthAccessTokenRepository.save(oauthAccessTokenEntity);
        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .build();
    }

    public IntrospectResponse introspect() {
        log.info("introspect auth service");

        // validate signer key
        Claims claims = JwtUtil.validateToken(getTokenFromAuthorizationHeaders());
        log.info("claims= {}", claims.toString());

        // check whether expired
        boolean isTokenNotExpired = isTokenNotExpired(claims);
        log.info("isTokenNotExpired= {}", isTokenNotExpired);

        return IntrospectResponse.builder()
                .isValid(isTokenNotExpired)
                .username(claims.getSubject())
                .roles(claims.get("roles", List.class))
                .build();
    }

    @Transactional
    public void logout() {
        try {
            String token = getTokenFromAuthorizationHeaders();
            log.info("token = {}", token);
            if (introspect().isValid()) {
                Claims claims = JwtUtil.validateToken(token);

                log.info("claims: {}", claims);
                OauthAccessTokenEntity tokenEntity = oauthAccessTokenRepository.findById(claims.getId()).orElseThrow(
                        () -> new AppException(ApiError.UNAUTHENTICATED)
                );

                // logout = set expired time to now
                LocalDateTime expiredTimestamp = new Date()
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();
                tokenEntity.setExpiredTimestamp(expiredTimestamp);

                // save to database
                oauthAccessTokenRepository.save(tokenEntity);
            }
        } catch (RuntimeException e) {
            throw new AppException(ApiError.ACCESS_TOKEN_EXPIRED);
        }
    }

    public GetCurrentUserInfoResponse getCurrentUserByToken() {


        String accessToken = getTokenFromAuthorizationHeaders();
        String tokenValue = JwtUtil.validateToken(accessToken).getId();
        OauthAccessTokenEntity tokenEntity = oauthAccessTokenRepository.findById(tokenValue).orElseThrow(
                () -> new AppException(ApiError.UNAUTHENTICATED)
        );
        return GetCurrentUserInfoResponse.builder()
                .userId(tokenEntity.getUserId())
                .username(tokenEntity.getUsername())
                .build();
    }

    private boolean isTokenNotExpired(Claims claims) {
        Optional<OauthAccessTokenEntity> tokenEntity = oauthAccessTokenRepository.findById(claims.getId());
        if(tokenEntity.isEmpty()) {
            return false;
        }
        LocalDateTime expirationTimestamp = tokenEntity.get().getExpiredTimestamp();
        return expirationTimestamp.isAfter(LocalDateTime.now());
    }

    private String getTokenFromAuthorizationHeaders() {
        String authorizationHeader = request.getHeader("Authorization");
        log.info("authorizationHeader: {}", authorizationHeader);
        String token = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
        }
        return token;
    }
}
