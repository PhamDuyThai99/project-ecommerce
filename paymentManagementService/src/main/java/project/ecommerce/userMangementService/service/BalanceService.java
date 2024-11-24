package project.ecommerce.userMangementService.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import project.ecommerce.userMangementService.dto.response.BalanceResponse;
import project.ecommerce.userMangementService.dto.response.common.ApiResponse;
import project.ecommerce.userMangementService.dto.response.external.GetCurrentUserInfoResponse;
import project.ecommerce.userMangementService.entity.BalanceEntity;
import project.ecommerce.userMangementService.exception.ApiError;
import project.ecommerce.userMangementService.exception.AppException;
import project.ecommerce.userMangementService.repository.BalanceRepository;
import project.ecommerce.userMangementService.util.BalanceMapper;

import java.math.BigDecimal;
import java.util.Optional;

@Slf4j
@Service
public class BalanceService {

    private static final String GET_USER_BY_ID_URL = "http://localhost:8080/api/users/{userId}";
    private final static String GET_CURRENT_USER_URL="http://localhost:8080/api/auth/currentInfo";

    private final BalanceRepository balanceRepository;
    private final BalanceMapper balanceMapper;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public BalanceService(BalanceRepository balanceRepository, BalanceMapper balanceMapper,
                          RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.balanceRepository = balanceRepository;
        this.balanceMapper = balanceMapper;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public BalanceResponse createBalance(Long userId) {
        log.info("start creating balance method in balance service");

        // check user id existed in user service
        checkUserIdFromUserService(userId);

        // check balance with existed user id -> throw exception
        if(balanceRepository.findByUserId(userId).isPresent()) {
            log.info("balance is already in used by user id {}", userId);
            throw new AppException(ApiError.BALANCE_EXISTED);
        }

        // save to database
        BigDecimal initialBalance = BigDecimal.ZERO;  // Use configurable initial balance if needed
        BalanceEntity newBalance =  BalanceEntity.builder()
                .userId(userId)
                .balance(initialBalance)
                .build();

        BalanceResponse response = balanceMapper.toBalanceResponse(balanceRepository.save(newBalance));
        log.info("balance created successfully with response = {}", response);
        return response;
    }

    public BalanceResponse getByUserId(Long userId){
        log.info("start getting balance from user id {}", userId);
        checkUserIdFromUserService(userId);
        Optional<BalanceEntity> balance = balanceRepository.findByUserId(userId);
        if(balance.isEmpty()) {
            log.info("no balance found");
            throw new AppException(ApiError.USER_HAS_NO_BALANCE);
        }
        BalanceResponse response = balanceMapper.toBalanceResponse(balance.get());
        log.info("balance found with response {}", response);
        return response;
    }

    public BalanceResponse getCurrentUserBalance() {
        log.info("start searching for get user id by token");
        ApiResponse response = restTemplate.getForObject(GET_CURRENT_USER_URL, ApiResponse.class);
        log.info("response GET_CURRENT_USER_URL: {}", response);

        assert response != null;
        GetCurrentUserInfoResponse data =
                objectMapper.convertValue(response.getData(), GetCurrentUserInfoResponse.class);
        Long currentUserId = data.getUserId();

        return getByUserId(currentUserId);
    }

    @Transactional
    public BalanceResponse updateBalance(Long userId, BigDecimal balance){
        log.info("start updating balance from user id {}", userId);
        Optional<BalanceEntity> optionalBalance = balanceRepository.findByUserId(userId);
        if(optionalBalance.isEmpty()) {
            log.info("no balance found");
            throw new AppException(ApiError.USER_HAS_NO_BALANCE);
        }
        BalanceEntity balanceEntity = optionalBalance.get();
        balanceEntity.setBalance(balance);

        return balanceMapper.toBalanceResponse(balanceRepository.save(balanceEntity));
    }

    public void checkUserIdFromUserService(Long userId) {
        // check user id existed in user service
        String url = GET_USER_BY_ID_URL.replace("{userId}", userId.toString());
        try {
            restTemplate.getForObject(url, ApiResponse.class);
        } catch (RuntimeException ex) {
            log.info("fail to call to user service with ID {}", userId, ex);
            throw new AppException(ApiError.USER_SERVICE_UNAVAILABLE);
        }
    }

    public void checkBalanceByUserId(Long userId) {
        checkUserIdFromUserService(userId);
        if(balanceRepository.findByUserId(userId).isEmpty()) {
            log.info("no balance found");
            throw new AppException(ApiError.USER_HAS_NO_BALANCE);
        }
    }

}
