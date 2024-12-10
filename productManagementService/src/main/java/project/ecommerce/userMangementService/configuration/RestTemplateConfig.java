package project.ecommerce.userMangementService.configuration;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class RestTemplateConfig {
    private final HttpServletRequest servletRequest;

    public RestTemplateConfig(HttpServletRequest servletRequest) {
        this.servletRequest = servletRequest;
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new AuthenticationRequestInterceptor(servletRequest));
        restTemplate.setInterceptors(interceptors);
        return restTemplate;
    }
}
