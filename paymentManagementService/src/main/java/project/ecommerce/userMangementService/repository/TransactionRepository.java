package project.ecommerce.userMangementService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.ecommerce.userMangementService.entity.TransactionEntity;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<TransactionEntity, String> {
    List<Optional<TransactionEntity>> findByCartId(Long cartId);
    
}
