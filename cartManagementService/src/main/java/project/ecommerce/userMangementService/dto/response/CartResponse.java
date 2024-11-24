package project.ecommerce.userMangementService.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

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
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
