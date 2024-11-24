package project.ecommerce.userMangementService.dto.request.external;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductExternalRequest {
    String name;
    BigDecimal price;
    Integer stock;
    String category;
    Long productOwnerId;
}
