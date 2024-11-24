package project.ecommerce.authService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.ecommerce.authService.entity.OauthAccessTokenEntity;

@Repository
public interface OauthAccessTokenRepository extends JpaRepository<OauthAccessTokenEntity, String> {
}
