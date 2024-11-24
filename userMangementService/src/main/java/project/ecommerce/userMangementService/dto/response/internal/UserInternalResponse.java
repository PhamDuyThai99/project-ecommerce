package project.ecommerce.userMangementService.dto.response.internal;


import lombok.*;
import lombok.experimental.FieldDefaults;
import project.ecommerce.userMangementService.dto.response.RoleResponse;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserInternalResponse {

    Long id;
    String username;
    String password;
    String email;
    Set<RoleResponse> roles;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
