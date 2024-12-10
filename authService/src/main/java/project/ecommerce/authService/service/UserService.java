package project.ecommerce.authService.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import project.ecommerce.authService.dto.response.common.ApiResponse;
import project.ecommerce.authService.dto.response.external.UserInternalResponse;
import project.ecommerce.authService.exception.ApiError;
import project.ecommerce.authService.exception.AppException;

@Service
@Slf4j
public class UserService {
    @Value("${externalApi.userService.getUserByUsernameUrl}")
    private static String GET_USER_BY_USERNAME_URL;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public UserService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public UserInternalResponse getUserByUsernameFromUserService(String username) {
        String getUserUrl = GET_USER_BY_USERNAME_URL.replace("{username}", username);
        ApiResponse apiResponse;
        try {
            apiResponse = restTemplate.getForObject(getUserUrl, ApiResponse.class);
            log.info("found user apiResponse: " + apiResponse);
        } catch (RuntimeException ex) {
            throw new AppException(ApiError.UNAUTHENTICATED);
        }
        assert apiResponse != null;
        UserInternalResponse response = objectMapper.convertValue(apiResponse.getData(), UserInternalResponse.class);
        log.info("UserInternalResponse: " + response);
        return response;
    }
}
