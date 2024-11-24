package project.ecommerce.apiGatewayService.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import project.ecommerce.apiGatewayService.dto.response.common.ApiResponse;
import project.ecommerce.apiGatewayService.dto.response.common.ResponseStatus;
import project.ecommerce.apiGatewayService.dto.response.external.IntrospectResponse;
import project.ecommerce.apiGatewayService.exception.ApiError;
import project.ecommerce.apiGatewayService.service.UserService;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    @NonFinal
    static String BEARER_PREFIX = "Bearer ";

    @NonFinal
    static String[] PUBLIC_ENDPOINTS = {
            "/api/users/register",
            "/api/auth/login",
            "/api/auth/logout",
            "/api/auth/introspect",
            "/api/auth/currentInfo",
            "/api/internal/users"
    };

    UserService userService;
    ObjectMapper objectMapper;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        // get token and path
        log.info("Start authentication filter");
        String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        String path = exchange.getRequest().getURI().getPath();
        log.info("path " + path);
        String httpMethod = exchange.getRequest().getMethod().name();
        log.info("httpMethod " + httpMethod);

        
        // Skip public endpoints
        if (isPublicEndpoint(path)) {
            log.info("path is in public endpoints");
            return chain.filter(exchange);
        }
        log.info("not public endpoint");
        if (token == null || !token.startsWith(BEARER_PREFIX)) {
            log.info("token invalid {}", token);
            return unauthenticated(exchange.getResponse());
        }

        token = token.substring(7);
        log.info("token: " + token);

        Mono<ApiResponse<IntrospectResponse>> response = userService.introspect(token);
        return response.flatMap(introspectResponse -> {

            log.info("introspectResponse: {} ", introspectResponse.toString());
            if (introspectResponse.getData().isValid()) {
                if (hasRole(path, httpMethod, introspectResponse.getData().getRoles())) {
                    log.info("hasRole(path, httpMethod, introspectResponse.getData().getRoles()) {}",
                            hasRole(path, httpMethod, introspectResponse.getData().getRoles()));
                    return chain.filter(exchange);

                } else {
                    log.info("hasRole(path, httpMethod, introspectResponse.getData().getRoles()) {}",
                            hasRole(path, httpMethod, introspectResponse.getData().getRoles()));
                    return unauthorized(exchange.getResponse());
                }
            } else {
                return unauthenticated(exchange.getResponse());
            }
        }).onErrorResume(throwable -> unauthenticated(exchange.getResponse()));
    }

    private boolean isPublicEndpoint(String path) {
        return Arrays.stream(PUBLIC_ENDPOINTS).toList().contains(path) ||
                path.startsWith("/api/internal/users/");
    }

    // config role for endpoint
    private boolean hasRole(String path, String httpMethod, List<String> roles) {

        if (roles.contains("ROLE_ADMIN")) {
            return hasAdminRole(path, httpMethod);
        }

        if (roles.contains("ROLE_USER")) {
            return hasUserRole(path, httpMethod);
        }

        return false;
    }

    private boolean hasAdminRole(String path, String httpMethod) {
        return path.startsWith("/api/roles") ||
                (path.startsWith("/api/users") && httpMethod.equals("GET")) ||
                (path.startsWith("/api/users") && httpMethod.equals("DELETE")) ||
                (path.startsWith("/api/cart")) ||
                (path.startsWith("/api/products")) ||
                (path.startsWith("/api/payment"));
    }

    private boolean hasUserRole(String path, String httpMethod) {
        return ((path.startsWith("/api/users") && (httpMethod.equals("GET") || httpMethod.equals("PUT")))) ||
                (path.startsWith("/api/cart") && !path.equals("/api/cart/getAll")) ||
                (path.startsWith("/api/payment")) ||
                (path.startsWith("/api/products"));
    }

    @Override
    public int getOrder() {
        return -1;
    }

    private Mono<Void> unauthenticated(ServerHttpResponse response) {
        ResponseStatus responseStatus = ResponseStatus.builder()
                .code(ApiError.UNAUTHENTICATED.getCode())
                .message(ApiError.UNAUTHENTICATED.getMessage())
                .build();

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .status(responseStatus)
                .data(null)
                .build();

        String body;
        try {
            body = objectMapper.writeValueAsString(apiResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }

    private Mono<Void> unauthorized(ServerHttpResponse response) {
        ResponseStatus responseStatus = ResponseStatus.builder()
                .code(ApiError.UNAUTHORIZED.getCode())
                .message(ApiError.UNAUTHORIZED.getMessage())
                .build();

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .status(responseStatus)
                .data(null)
                .build();
        String body;
        try {
            body = objectMapper.writeValueAsString(apiResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }

}
