package project.ecommerce.userMangementService.service.external;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import project.ecommerce.userMangementService.dto.response.common.ApiResponse;
import project.ecommerce.userMangementService.dto.response.external.CartResponse;
import project.ecommerce.userMangementService.exception.ApiError;
import project.ecommerce.userMangementService.exception.AppException;

@Service
@Slf4j
public class CartService {
    @Value("${externalApis.cartService.getCartByIdUrl}")
    private static String GET_CART_BY_ID_URL;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;


    public CartService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public CartResponse getCartFromCartService(Long cartId) {
        String url = GET_CART_BY_ID_URL.replace("{cartId}", cartId.toString());
        ApiResponse apiResponse = restTemplate.getForObject(url, ApiResponse.class);
        if (apiResponse == null || apiResponse.getData() == null) {
            throw new AppException(ApiError.CART_NOT_FOUND);
        }
        return objectMapper.convertValue(apiResponse.getData(), CartResponse.class);
    }
}
