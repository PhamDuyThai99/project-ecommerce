package project.ecommerce.userMangementService.service.TransactionServices;

import org.springframework.stereotype.Service;
import project.ecommerce.userMangementService.dto.response.BalanceResponse;
import project.ecommerce.userMangementService.dto.response.external.CartResponse;
import project.ecommerce.userMangementService.entity.TransactionEntity;
import project.ecommerce.userMangementService.enums.PaymentStatusEnum;
import project.ecommerce.userMangementService.exception.ApiError;
import project.ecommerce.userMangementService.exception.AppException;
import project.ecommerce.userMangementService.service.BalanceService;
import project.ecommerce.userMangementService.service.external.CartService;

import java.math.BigDecimal;

@Service
public class TransactionValidationService {
    private final CartService cartService;
    private final BalanceService balanceService;

    public TransactionValidationService(CartService cartService, BalanceService balanceService) {
        this.cartService = cartService;
        this.balanceService = balanceService;
    }

    public void validateBalance(Long userId, BigDecimal amount) {
        BalanceResponse balance = balanceService.getBalanceByUserId(userId);
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
        return cartService.getCartFromCartService(cartId);
    }
}
