package project.ecommerce.userMangementService.configuration;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

@Configuration
public class AuthenticationRequestInterceptor implements ClientHttpRequestInterceptor {
    private final HttpServletRequest servletRequest;

    public AuthenticationRequestInterceptor(HttpServletRequest servletRequest) {
        this.servletRequest = servletRequest;
    }

    @Override
    public ClientHttpResponse intercept
            (HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        String authorizationHeader = servletRequest.getHeader(HttpHeaders.AUTHORIZATION);
        request.getHeaders().set(HttpHeaders.AUTHORIZATION, authorizationHeader);
        return execution.execute(request, body);
    }
}
