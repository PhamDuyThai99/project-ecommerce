package project.ecommerce.userMangementService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.ecommerce.userMangementService.entity.BalanceEntity;

import java.util.Optional;

@Repository
public interface BalanceRepository extends JpaRepository<BalanceEntity, Long> {
    Optional<BalanceEntity> findByUserId(Long userId);
}
