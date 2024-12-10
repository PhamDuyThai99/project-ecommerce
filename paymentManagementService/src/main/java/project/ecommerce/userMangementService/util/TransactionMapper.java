package project.ecommerce.userMangementService.util;

import org.springframework.stereotype.Component;
import project.ecommerce.userMangementService.dto.request.CreateCartTransactionRequest;
import project.ecommerce.userMangementService.dto.request.CreateP2PTransactionRequest;
import project.ecommerce.userMangementService.dto.request.CreateReloadTransactionRequest;
import project.ecommerce.userMangementService.dto.request.CreateWithdrawTransactionRequest;
import project.ecommerce.userMangementService.dto.response.CreateTransactionResponse;
import project.ecommerce.userMangementService.dto.response.TransactionDetailResponse;
import project.ecommerce.userMangementService.entity.TransactionEntity;
import project.ecommerce.userMangementService.enums.PaymentStatusEnum;
import project.ecommerce.userMangementService.enums.TransactionTypeEnum;

@Component
public class TransactionMapper {
    public TransactionDetailResponse toTransactionResponse(TransactionEntity transactionEntity) {
        return TransactionDetailResponse.builder()
                .transactionId(transactionEntity.getTransactionId())
                .payerId(transactionEntity.getPayerId())
                .payeeId(transactionEntity.getPayeeId())
                .amount(transactionEntity.getAmount())
                .orderStatus(transactionEntity.getOrderStatus())
                .cartId(transactionEntity.getCartId())
                .transactionType(transactionEntity.getTransactionType())
                .createdAt(transactionEntity.getCreatedAt())
                .updatedAt(transactionEntity.getUpdatedAt())
                .build();
    }

    public TransactionEntity toEntityForP2PTransaction(CreateP2PTransactionRequest request) {

        return TransactionEntity.builder()
                .payerId(request.getPayerId())
                .payeeId(request.getPayeeId())
                .amount(request.getAmount())
                .orderStatus(PaymentStatusEnum.PENDING.getStatus())
                .transactionType(TransactionTypeEnum.P2P.getTransactionType())
                .build();
    }

    public TransactionEntity toEntityForCartTransaction(CreateCartTransactionRequest request) {

        return TransactionEntity.builder()
                .payerId(request.getPayerId())
                .cartId(request.getCartId())
                .orderStatus(PaymentStatusEnum.PENDING.getStatus())
                .transactionType(TransactionTypeEnum.CART.getTransactionType())
                .build();
    }

    public TransactionEntity toEntityForWithdrawTransaction(CreateWithdrawTransactionRequest request) {
        return TransactionEntity.builder()
                .payerId(request.getPayerId())
                .amount(request.getAmount())
                .orderStatus(PaymentStatusEnum.PENDING.getStatus())
                .transactionType(TransactionTypeEnum.WITHDRAW.getTransactionType())
                .build();

    }

    public TransactionEntity toEntityForReloadTransaction(CreateReloadTransactionRequest request) {
        return TransactionEntity.builder()
                .payeeId(request.getPayeeId())
                .amount(request.getAmount())
                .orderStatus(PaymentStatusEnum.PENDING.getStatus())
                .transactionType(TransactionTypeEnum.RELOAD.getTransactionType())
                .build();

    }

    public CreateTransactionResponse toCreateTransactionResponse(TransactionEntity transaction) {
        return CreateTransactionResponse.builder()
                .transactionId(transaction.getTransactionId())
                .build();
    }
}
