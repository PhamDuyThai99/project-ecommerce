package project.ecommerce.userMangementService.service.external;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import project.ecommerce.userMangementService.dto.response.common.ApiResponse;
import project.ecommerce.userMangementService.dto.response.external.GetCurrentUserInfoResponse;
import project.ecommerce.userMangementService.exception.ApiError;
import project.ecommerce.userMangementService.exception.AppException;

@Service
@Slf4j
public class UserService {
    @Value("${externalApis.cartService.getCartByIdUrl}")
    private static String GET_USER_BY_ID_URL;
    @Value("${externalApis.cartService.getCurrentUserUrl}")
    private static String GET_CURRENT_USER_URL;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public UserService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public void validateUserByIdFromUserService(Long userId){
        // check user id existed in user service
        String url = GET_USER_BY_ID_URL.replace("{userId}", userId.toString());
        try {
            restTemplate.getForObject(url, ApiResponse.class);
        } catch (RuntimeException ex) {
            log.info("fail to call to user service with ID {}", userId, ex);
            throw new AppException(ApiError.USER_SERVICE_UNAVAILABLE);
        }
    }

    public Long getCurrentUserIdFromUserService() {
        log.info("start searching for get user id by token");
        ApiResponse response = restTemplate.getForObject(GET_CURRENT_USER_URL, ApiResponse.class);
        log.info("response GET_CURRENT_USER_URL: {}", response);

        assert response != null;
        GetCurrentUserInfoResponse data =
                objectMapper.convertValue(response.getData(), GetCurrentUserInfoResponse.class);
        return data.getUserId();
    }
}
