package project.ecommerce.apiGatewayService.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfiguration {

    @Value("${app.api-prefix}")
    private String apiPrefix;
    @Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth_route", r -> r
                        .path("/" + apiPrefix + "/auth/**")
                        .filters(f -> f
                                .stripPrefix(1))
                        .uri("http://localhost:8001"))

                .route("user_route", r -> r
                        .path("/" + apiPrefix + "/users/**")
                        .filters(f -> f
                                .stripPrefix(1))
                        .uri("http://localhost:8000"))
                .route("internal_user_route", r -> r
                        .path("/" + apiPrefix + "/internal/users/**")
                        .filters(f -> f
                                .stripPrefix(1))
                        .uri("http://localhost:8000"))
                .route("role_route", r -> r
                        .path("/" + apiPrefix + "/roles/**")
                        .filters(f -> f
                                .stripPrefix(1))
                        .uri("http://localhost:8000"))
                .route("product_route", r -> r
                        .path("/" + apiPrefix + "/products/**")
                        .filters(f -> f
                                .stripPrefix(1))
                        .uri("http://localhost:8002"))
                .route("product_route", r -> r
                        .path("/" + apiPrefix + "/cart/**")
                        .filters(f -> f
                                .stripPrefix(1))
                        .uri("http://localhost:8003"))
                .route("payment_route", r -> r
                        .path("/" + apiPrefix + "/payment/**")
                        .filters(f -> f
                                .stripPrefix(1))
                        .uri("http://localhost:8004"))
                .build();
    }
}
