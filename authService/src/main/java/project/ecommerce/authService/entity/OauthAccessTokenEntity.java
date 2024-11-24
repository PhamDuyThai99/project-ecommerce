package project.ecommerce.authService.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "oauth_access_token")
public class OauthAccessTokenEntity {
    @Id
    @Column(name = "token_value", unique = true)
    String token;

    @Column(name = "user_id")
    Long userId;

    String username;

    @Column(name = "created_timestamp")
    LocalDateTime createdTimestamp;

    @Column(name = "expired_timestamp")
    LocalDateTime expiredTimestamp;

}
