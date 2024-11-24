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
public class ProductExternalResponse {
    Long id;
    String name;
    BigDecimal price;
    Integer stock;
    String category;
    Long productOwnerId;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
