package project.ecommerce.userMangementService.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import project.ecommerce.userMangementService.dto.request.ProductCartRequest;
import project.ecommerce.userMangementService.dto.request.external.ProductSearchRequest;
import project.ecommerce.userMangementService.dto.response.ProductCartResponse;
import project.ecommerce.userMangementService.dto.response.common.ApiResponse;
import project.ecommerce.userMangementService.dto.response.external.ProductExternalResponse;
import project.ecommerce.userMangementService.dto.response.external.SearchProductsResponse;
import project.ecommerce.userMangementService.entity.ProductCartEntity;
import project.ecommerce.userMangementService.exception.ApiError;
import project.ecommerce.userMangementService.exception.AppException;
import project.ecommerce.userMangementService.repository.ProductCartRepository;
import project.ecommerce.userMangementService.service.external.ProductService;
import project.ecommerce.userMangementService.service.external.UserService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductCartService {
    ProductService productService;
    UserService userService;
    ProductCartRepository productCartRepository;

    @Transactional
    public void saveProductToCart(Long cartId, Long productId, ProductCartRequest request) {
        log.info("addProductToCart productCart service with request {}", request);

        ProductExternalResponse productStockResponse = productService.fetchProductByIdFromProductService(productId);

        // check product owner id should not equal to current user
        if (Objects.equals(userService.getCurrentUserId(), productStockResponse.getProductOwnerId())) {
            throw new AppException(ApiError.PRODUCT_OWNER_ID_EQUALS_CURRENT_USER_ID);
        }

        productService.checkProductStockAvailability(productStockResponse, request.getQuantityInCart());
        saveProductInCart(cartId, productId, request.getQuantityInCart());

        productService.updateProductToStockProductService(productStockResponse, request.getQuantityInCart());
    }

    // Saves or updates the product entry in the cart
    private void saveProductInCart(Long cartId, Long productId, int requestedQuantity) {

        ProductCartEntity productCart = productCartRepository.findByProductId(productId)
                .orElse(
                        ProductCartEntity.builder()
                                .cartId(cartId)
                                .productId(productId)
                                .build()
                );
        productCart.setQuantityInCart(requestedQuantity);
        productCartRepository.save(productCart);
    }

    public List<ProductCartResponse> getProductDataFromStock(List<Long> productIds) {

        List<ProductExternalResponse> productStocks = productService.searchProductsFromProductService(productIds);
        return productStocks.stream()
                .map(this::mapToProductCartResponse)
                .collect(Collectors.toList());
    }

    public List<Long> getProductIdsByCartId(Long cartId) {
        // get product id
        Optional<List<ProductCartEntity>> productCartsOptional = productCartRepository.findByCartId(cartId);
        log.info("found productCarts: {}", productCartsOptional.get());
        return productCartsOptional.get().stream()
                .map(ProductCartEntity::getProductId)
                .toList();
    }

    private ProductCartResponse mapToProductCartResponse(ProductExternalResponse productStock) {
        Integer quantity = productCartRepository.findByProductId(productStock.getId())
                .map(ProductCartEntity::getQuantityInCart)
                .orElse(null); // Returns null if product not found in cart

        return ProductCartResponse.builder()
                .productId(productStock.getId())
                .price(productStock.getPrice())
                .productName(productStock.getName())
                .category(productStock.getCategory())
                .quantity(quantity)
                .productOwnerId(productStock.getProductOwnerId())
                .build();
    }

    @Transactional
    public void deleteByCartId(Long cartId) {
        List<ProductCartEntity> productCartList = productCartRepository.findByCartId(cartId).orElseThrow(
                () -> {
                    log.info("Cannot find product in cart id {}", cartId);
                    return new AppException(ApiError.UNKNOWN_ERROR);
                }
        );
        List<Long> productCartIds = productCartList.stream()
                .map(ProductCartEntity::getId).toList();

        productCartRepository.deleteAllById(productCartIds);
    }
}
