package project.ecommerce.userMangementService.dto.response;

import jakarta.persistence.Column;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import project.ecommerce.userMangementService.entity.TransactionEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransactionDetailResponse {
    String transactionId;
    Long payerId;
    Long payeeId;
    String orderStatus;
    BigDecimal amount;
    String transactionType;
    Long cartId;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
