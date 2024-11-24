package project.ecommerce.userMangementService.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductCartResponse {
    Long productId;
    String productName;
    BigDecimal price;
    String category;
    Integer quantity;
    Long productOwnerId;
}
