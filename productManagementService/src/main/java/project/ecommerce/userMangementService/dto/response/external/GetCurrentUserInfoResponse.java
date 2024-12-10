package project.ecommerce.userMangementService.dto.response.external;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetCurrentUserInfoResponse {
    Long userId;
    String username;
}
