package project.ecommerce.userMangementService.service.TransactionServices;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import project.ecommerce.userMangementService.dto.response.BalanceResponse;
import project.ecommerce.userMangementService.dto.response.common.ApiResponse;
import project.ecommerce.userMangementService.dto.response.external.CartResponse;
import project.ecommerce.userMangementService.entity.TransactionEntity;
import project.ecommerce.userMangementService.enums.PaymentStatusEnum;
import project.ecommerce.userMangementService.exception.ApiError;
import project.ecommerce.userMangementService.exception.AppException;
import project.ecommerce.userMangementService.service.BalanceService;

import java.math.BigDecimal;

@Service
public class TransactionValidationService {
    private final static String GET_CART_BY_ID_URL = "http://localhost:8080/api/cart/{cartId}";
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final BalanceService balanceService;

    public TransactionValidationService(RestTemplate restTemplate,
                                        ObjectMapper objectMapper,
                                        BalanceService balanceService) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.balanceService = balanceService;
    }

    public void validateBalance(Long userId, BigDecimal amount) {
        BalanceResponse balance = balanceService.getByUserId(userId);
        if (balance.getBalance().compareTo(amount) < 0) {
            throw new AppException(ApiError.INSUFFICIENT_FUND);
        }
    }

    public void validateTransactionStatus(TransactionEntity transaction) {
        if(!transaction.getOrderStatus().equals(PaymentStatusEnum.PENDING.getStatus())) {
            throw new AppException(ApiError.INVALID_ORDER_STATUS);
        }
    }

    public void validateUserIdForTransaction(Long userId) {
        balanceService.checkBalanceByUserId(userId);
    }

    public CartResponse validateCart(Long cartId) {
        String url = GET_CART_BY_ID_URL.replace("{cartId}", cartId.toString());
        ApiResponse apiResponse = restTemplate.getForObject(url, ApiResponse.class);
        if (apiResponse == null || apiResponse.getData() == null) {
            throw new AppException(ApiError.CART_NOT_FOUND);
        }
        return objectMapper.convertValue(apiResponse.getData(), CartResponse.class);
    }
}
