package project.ecommerce.userMangementService.service.TransactionServices;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import project.ecommerce.userMangementService.dto.response.BalanceResponse;
import project.ecommerce.userMangementService.dto.response.external.CartResponse;
import project.ecommerce.userMangementService.entity.TransactionEntity;
import project.ecommerce.userMangementService.enums.PaymentStatusEnum;
import project.ecommerce.userMangementService.enums.TransactionTypeEnum;
import project.ecommerce.userMangementService.exception.ApiError;
import project.ecommerce.userMangementService.exception.AppException;
import project.ecommerce.userMangementService.repository.TransactionRepository;
import project.ecommerce.userMangementService.service.BalanceService;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TransactionProcessingService {
    private final BalanceService balanceService;
    private final TransactionRepository transactionRepository;

    public TransactionProcessingService(BalanceService balanceService, TransactionRepository transactionRepository) {
        this.balanceService = balanceService;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public synchronized void executeTransaction(TransactionEntity transaction) {
//        Objects.isNull(transaction.getPayerId());
//        Objects.isNull(transaction.getPayerId());
//        synchronized (transaction.getPayerId().toString().intern()) {
//            synchronized (transaction.getPayeeId().toString().intern()) {
                try {
                    String transactionType = transaction.getTransactionType();
                    BigDecimal amount = transaction.getAmount();
                    if (transactionType.equals(TransactionTypeEnum.P2P.getTransactionType()) ||
                            transactionType.equals(TransactionTypeEnum.CART.getTransactionType())) {

                        updateUserBalance(transaction.getPayerId(), amount, true);
                        updateUserBalance(transaction.getPayeeId(), amount, false);

                    } else if (transactionType.equals(TransactionTypeEnum.RELOAD.getTransactionType())) {
                        updateUserBalance(transaction.getPayeeId(), amount, false);

                    } else if (transactionType.equals(TransactionTypeEnum.WITHDRAW.getTransactionType())) {
                        updateUserBalance(transaction.getPayerId(), amount, true);
                    }

                    // update transaction status
                    transaction.setOrderStatus(PaymentStatusEnum.SUCCESS.getStatus());

                } catch (RuntimeException ex) {
                    transaction.setOrderStatus(PaymentStatusEnum.FAILED.getStatus());
                    log.error("Failed in execute transaction: {}", ex.getMessage());
                }
                transactionRepository.save(transaction);
//            }
//        }
    }

    public BigDecimal getCartTotalCost(CartResponse cart) {
        return cart.getProducts().stream()
                .map(product -> product.getPrice().multiply(BigDecimal.valueOf(product.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<Long,BigDecimal> groupProductAmountByProductOwnerId(CartResponse cart) {
        return cart.getProducts().stream()
                .collect(Collectors.groupingBy(
                        CartResponse.ProductCartResponse::getProductOwnerId, // Group by product owner ID
                        Collectors.reducing(
                                BigDecimal.ZERO, // Initial value
                                product -> product.getPrice().multiply(BigDecimal.valueOf(product.getQuantity())), // Transformation logic
                                BigDecimal::add // Reduction logic
                        )
                ));
    }

    private void updateUserBalance(Long userId, BigDecimal amount, Boolean isDebit) {
        if (amount.compareTo(BigDecimal.ZERO) < 1) {
            throw new AppException(ApiError.INVALID_AMOUNT_ERROR);
        }
        BigDecimal newPayerBalance;
        if(isDebit) {
            newPayerBalance = balanceService.getByUserId(userId)
                    .getBalance().subtract(amount);
        } else {
            newPayerBalance = balanceService.getByUserId(userId)
                    .getBalance().add(amount);
        }
        balanceService.updateBalance(userId, newPayerBalance);
    }

}
