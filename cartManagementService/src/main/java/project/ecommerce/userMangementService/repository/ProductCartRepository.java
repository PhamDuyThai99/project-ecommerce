package project.ecommerce.userMangementService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.ecommerce.userMangementService.entity.ProductCartEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductCartRepository  extends JpaRepository<ProductCartEntity, Long> {
    Optional<ProductCartEntity> findByProductId(Long productId);
    Optional<List<ProductCartEntity>> findByCartId(Long cartId);
}
