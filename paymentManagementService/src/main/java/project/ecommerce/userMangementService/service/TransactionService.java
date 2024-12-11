package project.ecommerce.userMangementService.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import project.ecommerce.userMangementService.dto.request.CreateCartTransactionRequest;
import project.ecommerce.userMangementService.dto.request.CreateP2PTransactionRequest;
import project.ecommerce.userMangementService.dto.request.CreateReloadTransactionRequest;
import project.ecommerce.userMangementService.dto.request.CreateWithdrawTransactionRequest;
import project.ecommerce.userMangementService.dto.response.CreateTransactionResponse;
import project.ecommerce.userMangementService.dto.response.TransactionDetailResponse;
import project.ecommerce.userMangementService.dto.response.external.CartResponse;
import project.ecommerce.userMangementService.entity.TransactionEntity;
import project.ecommerce.userMangementService.exception.ApiError;
import project.ecommerce.userMangementService.exception.AppException;
import project.ecommerce.userMangementService.repository.TransactionRepository;
import project.ecommerce.userMangementService.service.TransactionServices.TransactionPersistenceService;
import project.ecommerce.userMangementService.service.TransactionServices.TransactionProcessingService;
import project.ecommerce.userMangementService.service.TransactionServices.TransactionValidationService;
import project.ecommerce.userMangementService.util.TransactionMapper;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;

@Slf4j
@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionValidationService transactionValidationService;
    private final TransactionPersistenceService transactionPersistenceService;
    private final TransactionProcessingService transactionProcessingService;
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    public TransactionService(TransactionValidationService transactionValidationService,
                              TransactionPersistenceService transactionPersistenceService,
                              TransactionProcessingService transactionProcessingService,
                              TransactionRepository transactionRepository) {

        this.transactionValidationService = transactionValidationService;
        this.transactionPersistenceService = transactionPersistenceService;
        this.transactionProcessingService = transactionProcessingService;
        this.transactionRepository = transactionRepository;
    }

    public CreateTransactionResponse createP2PTransaction(CreateP2PTransactionRequest request) {
        // verify payer id balance
        transactionValidationService.validateUserIdForTransaction(request.getPayerId());

        // verify payee id balance
        transactionValidationService.validateUserIdForTransaction(request.getPayeeId());

        // save transaction to order with pending
        TransactionEntity transaction = TransactionMapper.toEntityForP2PTransaction(request);
        transactionPersistenceService.saveTransaction(transaction);

        return TransactionMapper.toCreateTransactionResponse(transaction);
    }

    @Transactional
    public void executeP2PTransaction(String transactionId) {
        TransactionEntity transaction = transactionPersistenceService.findTransactionById(transactionId);

        // validate payer balance
        transactionValidationService.validateBalance(transaction.getPayerId(), transaction.getAmount());

        // validate transaction status
        transactionValidationService.validateTransactionStatus(transaction);

        // execute transaction
        synchronized (transaction.getPayerId().toString().intern()) {
            synchronized (transaction.getPayeeId().toString().intern()) {
                transactionProcessingService.executeTransaction(transaction);
            }
        }
    }

    @Transactional
    public void createCartCheckoutTransaction(CreateCartTransactionRequest request) {
        // verify cart id
        CartResponse cart = transactionValidationService.validateCart(request.getCartId());

        // verify payer id balance
        transactionValidationService.validateBalance(request.getPayerId(),
                transactionProcessingService.getCartTotalCost(cart));

        // get map of productOwner and accumulatedAmount
        Map<Long, BigDecimal> productOwnerTotalAmountMap =
                transactionProcessingService.groupProductAmountByProductOwnerId(cart);

        // save this transaction to database
        List<TransactionEntity> transactions = productOwnerTotalAmountMap.entrySet().stream()
                .map(mapEntry -> {
                    TransactionEntity transaction = TransactionMapper.toEntityForCartTransaction(request);
                    transaction.setPayeeId(mapEntry.getKey());
                    transaction.setAmount(mapEntry.getValue());
                    return transaction;
                }).toList();
        transactionRepository.saveAll(transactions);
    }

    @Transactional
    @Async
    public void executeCartCheckoutTransaction(Long cartId) {
        // get transaction ids by cartId
        List<String> transactionIdList = transactionPersistenceService.getTransactionIdByCartId(cartId);

        RequestAttributes context = RequestContextHolder.currentRequestAttributes();

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (String transactionId : transactionIdList) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    RequestContextHolder.setRequestAttributes(context);
                    executeP2PTransaction(transactionId);
                } catch (Exception ex) {
                    log.error("Fail to execute transaction with id {}", transactionId, ex);
                    throw new AppException(ApiError.UNKNOWN_ERROR);
                } finally {
                    RequestContextHolder.resetRequestAttributes();
                }
            }, executorService);
            futures.add(future);
        }

        // Wait for all tasks to complete
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        try {
            allFutures.join(); // block until task is done
        } catch (Exception ex) {
            log.error("Error during transaction execution: {}", ex.getMessage());
        }
    }

    public TransactionDetailResponse getTransactionDetails(String transactionId) {
        log.info("get transaction at id {}", transactionId);
        return TransactionMapper.toTransactionResponse(
                transactionPersistenceService.findTransactionById(transactionId));
    }

    @Transactional
    public CreateTransactionResponse createWithdrawTransaction(CreateWithdrawTransactionRequest request) {
        log.info("start create withdraw transaction method");

        // validate payer id
        transactionValidationService.validateUserIdForTransaction(request.getPayerId());

        // validate balance
        transactionValidationService.validateBalance(request.getPayerId(), request.getAmount());

        TransactionEntity transaction = TransactionMapper.toEntityForWithdrawTransaction(request);
        transactionPersistenceService.saveTransaction(transaction);

        return TransactionMapper.toCreateTransactionResponse(transaction);
    }

    @Transactional
    public void executeWithdrawTransaction(String transactionId) {
        log.info("start execute withdraw transaction method");

        TransactionEntity transaction = transactionPersistenceService.findTransactionById(transactionId);

        // validate payer balance
        transactionValidationService.validateBalance(transaction.getPayerId(), transaction.getAmount());

        // validate transaction status
        transactionValidationService.validateTransactionStatus(transaction);

        transactionProcessingService.executeTransaction(transaction);
    }

    @Transactional
    public CreateTransactionResponse createReloadTransaction(CreateReloadTransactionRequest request) {
        log.info("start create withdraw transaction method");

        // validate payer id
        transactionValidationService.validateUserIdForTransaction(request.getPayeeId());

        // validate balance
        transactionValidationService.validateBalance(request.getPayeeId(), request.getAmount());

        TransactionEntity transaction = TransactionMapper.toEntityForReloadTransaction(request);
        transactionPersistenceService.saveTransaction(transaction);

        return TransactionMapper.toCreateTransactionResponse(transaction);
    }

    @Transactional
    public void executeReloadTransaction(String transactionId) {
        log.info("start execute reload transaction method");

        TransactionEntity transaction = transactionPersistenceService.findTransactionById(transactionId);

        // validate payer balance
        transactionValidationService.validateBalance(transaction.getPayerId(), transaction.getAmount());

        // validate transaction status
        transactionValidationService.validateTransactionStatus(transaction);

        transactionProcessingService.executeTransaction(transaction);
    }


}
