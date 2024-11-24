package project.ecommerce.apiGatewayService.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.adapter.DefaultServerWebExchange;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import project.ecommerce.apiGatewayService.repository.UserClient;

@Configuration
@Slf4j
public class WebClientConfiguration {

    @Bean
    WebClient webClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8080/api/auth")
                .build();
    }

    // register -> proxy will request to api with request
    @Bean
    UserClient userClient (WebClient webClient) {
        HttpServiceProxyFactory httpServiceProxyFactory =
                HttpServiceProxyFactory.builderFor(WebClientAdapter.create(webClient)).build();

        return httpServiceProxyFactory.createClient(UserClient.class);
    }
}
