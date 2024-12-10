package project.ecommerce.userMangementService.service.external;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import project.ecommerce.userMangementService.dto.response.common.ApiResponse;
import project.ecommerce.userMangementService.dto.response.external.GetCurrentUserInfoResponse;

@Service
@Slf4j
public class UserService {
    @Value("${externalApis.userService.getCurrentUserUrl}")
    private static String GET_CURRENT_USER_URL;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;


    public UserService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
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
