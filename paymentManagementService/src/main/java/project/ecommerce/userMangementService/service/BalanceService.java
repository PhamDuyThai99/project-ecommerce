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
import project.ecommerce.userMangementService.service.external.UserService;
import project.ecommerce.userMangementService.util.BalanceMapper;

import java.math.BigDecimal;
import java.util.Optional;

@Slf4j
@Service
public class BalanceService {

    private final BalanceRepository balanceRepository;
    private final UserService userService;

    public BalanceService(BalanceRepository balanceRepository, UserService userService) {
        this.balanceRepository = balanceRepository;
        this.userService = userService;
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

        BalanceResponse response = BalanceMapper.toBalanceResponse(balanceRepository.save(newBalance));
        log.info("balance created successfully with response = {}", response);
        return response;
    }

    public BalanceResponse getBalanceByUserId(Long userId){
        log.info("start getting balance from user id {}", userId);
        checkUserIdFromUserService(userId);
        Optional<BalanceEntity> balance = balanceRepository.findByUserId(userId);
        if(balance.isEmpty()) {
            log.info("no balance found");
            throw new AppException(ApiError.USER_HAS_NO_BALANCE);
        }
        BalanceResponse response = BalanceMapper.toBalanceResponse(balance.get());
        log.info("balance found with response {}", response);
        return response;
    }

    public BalanceResponse getCurrentUserBalance() {
        return getBalanceByUserId(userService.getCurrentUserIdFromUserService());
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

        return BalanceMapper.toBalanceResponse(balanceRepository.save(balanceEntity));
    }

    public void checkUserIdFromUserService(Long userId) {
        userService.validateUserByIdFromUserService(userId);
    }

    public void checkBalanceByUserId(Long userId) {
        checkUserIdFromUserService(userId);
        if(balanceRepository.findByUserId(userId).isEmpty()) {
            log.info("no balance found");
            throw new AppException(ApiError.USER_HAS_NO_BALANCE);
        }
    }

}
