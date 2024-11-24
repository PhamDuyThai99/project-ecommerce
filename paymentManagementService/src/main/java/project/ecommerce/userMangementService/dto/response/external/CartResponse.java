package project.ecommerce.userMangementService.dto.response.external;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartResponse {
    Long id;
    Long userId;
    List<ProductCartResponse> products;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class ProductCartResponse {
        Long productId;
        String productName;
        BigDecimal price;
        String category;
        Integer quantity;
        Long productOwnerId;
    }

    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
