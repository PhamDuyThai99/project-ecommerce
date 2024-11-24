package project.ecommerce.userMangementService.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRegisteringRequest {
    String username;
    String password;
    String email;
    List<String> roleNames;
}
