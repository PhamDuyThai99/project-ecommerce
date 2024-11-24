package project.ecommerce.apiGatewayService.dto.response.common;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResponseStatus {
    @Builder.Default
    int code = 1000;
    @Builder.Default
    String message = "success";
    final LocalDateTime executedAt = LocalDateTime.now();
}
