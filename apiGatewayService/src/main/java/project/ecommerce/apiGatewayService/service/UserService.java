package project.ecommerce.apiGatewayService.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import project.ecommerce.apiGatewayService.dto.response.common.ApiResponse;
import project.ecommerce.apiGatewayService.dto.response.external.IntrospectResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    @NonFinal
    static String URL_INTROSPECT_TOKEN = "http://localhost:8080/api/auth/introspect";

    WebClient webClient;

    public Mono<ApiResponse<IntrospectResponse>> introspect(String token) {
        return webClient.post()
                .uri(URL_INTROSPECT_TOKEN)  // URL of the introspect API
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }
}
