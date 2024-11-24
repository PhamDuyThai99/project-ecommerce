package project.ecommerce.userMangementService.dto.response.external;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SearchProductsResponse {
    List<ProductExternalResponse> products;
}
