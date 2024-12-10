package project.ecommerce.userMangementService.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import project.ecommerce.userMangementService.dto.request.ProductRequest;
import project.ecommerce.userMangementService.dto.response.ProductResponse;
import project.ecommerce.userMangementService.entity.ProductEntity;

@Component
public class ProductMapper {
    private final ObjectMapper objectMapper;

    public ProductMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public ProductEntity toEntity(ProductRequest request){
        return objectMapper.convertValue(request, ProductEntity.class);
    }
    public ProductResponse toResponse(ProductEntity product){
        return objectMapper.convertValue(product, ProductResponse.class);
    }
    public ProductEntity updateProduct(ProductEntity product, ProductRequest request){
        product.setName(request.getName());
        product.setCategory(request.getCategory());
        product.setStock(request.getStock());
        product.setPrice(request.getPrice());
        return product;
    };
}
