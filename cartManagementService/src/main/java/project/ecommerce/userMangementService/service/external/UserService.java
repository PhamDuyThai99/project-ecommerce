package project.ecommerce.userMangementService.service.external;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import project.ecommerce.userMangementService.dto.response.common.ApiResponse;
import project.ecommerce.userMangementService.dto.response.external.GetCurrentUserInfoResponse;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    @NonFinal
    static String GET_CURRENT_USER_URL="http://localhost:8080/api/auth/currentInfo";

    RestTemplate restTemplate;
    ObjectMapper objectMapper;

    public Long getCurrentUserId() {
        log.info("start searching for get user id by token");
        ApiResponse response = restTemplate.getForObject(GET_CURRENT_USER_URL, ApiResponse.class);
        log.info("response GET_CURRENT_USER_URL: {}", response);

        assert response != null;
        GetCurrentUserInfoResponse data =
                objectMapper.convertValue(response.getData(), GetCurrentUserInfoResponse.class);

        return data.getUserId();
    }
}
