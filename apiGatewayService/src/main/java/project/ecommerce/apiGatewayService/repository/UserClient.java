package project.ecommerce.apiGatewayService.repository;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.PostExchange;
import project.ecommerce.apiGatewayService.dto.request.external.IntrospectRequest;
import project.ecommerce.apiGatewayService.dto.response.common.ApiResponse;
import project.ecommerce.apiGatewayService.dto.response.external.IntrospectResponse;
import reactor.core.publisher.Mono;


public interface UserClient {
    @PostExchange(url = "api/auth/introspect", contentType = MediaType.APPLICATION_JSON_VALUE)
    Mono<ApiResponse<IntrospectResponse>> introspect();
}
