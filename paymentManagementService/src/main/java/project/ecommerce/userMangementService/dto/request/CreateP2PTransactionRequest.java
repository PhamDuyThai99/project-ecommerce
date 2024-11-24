package project.ecommerce.userMangementService.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateP2PTransactionRequest {

    Long payerId;
    Long payeeId;
    BigDecimal amount;
}