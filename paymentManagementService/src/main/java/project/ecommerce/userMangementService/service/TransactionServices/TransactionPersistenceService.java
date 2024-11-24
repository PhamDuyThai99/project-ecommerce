package project.ecommerce.userMangementService.service.TransactionServices;

import org.springframework.stereotype.Service;
import project.ecommerce.userMangementService.entity.TransactionEntity;
import project.ecommerce.userMangementService.exception.ApiError;
import project.ecommerce.userMangementService.exception.AppException;
import project.ecommerce.userMangementService.repository.TransactionRepository;
import project.ecommerce.userMangementService.util.TransactionMapper;

import java.util.List;

@Service
public class TransactionPersistenceService {
    private final TransactionRepository transactionRepository;

    public TransactionPersistenceService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public TransactionEntity findTransactionById(String transactionId) {
        return transactionRepository.findById(transactionId).orElseThrow(
                () -> new AppException(ApiError.TRANSACTION_NOT_FOUND)
        );
    }

    public List<String> getTransactionIdByCartId(Long cartId) {
        return transactionRepository.findByCartId(cartId).stream()
                .map(optionalTransaction -> optionalTransaction.orElseThrow(
                                () -> new AppException(ApiError.NO_CART_TRANSACTION_FOUND))
                        .getTransactionId()
                ).toList();
    }

    public TransactionEntity saveTransaction(TransactionEntity transaction) {
        return transactionRepository.save(transaction);
    }
}
