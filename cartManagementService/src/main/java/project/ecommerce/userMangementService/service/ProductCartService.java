package project.ecommerce.userMangementService.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
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
import project.ecommerce.userMangementService.dto.request.UpdateCartRequest;
import project.ecommerce.userMangementService.dto.request.external.ProductExternalRequest;
import project.ecommerce.userMangementService.dto.request.external.ProductSearchRequest;
import project.ecommerce.userMangementService.dto.response.ProductCartResponse;
import project.ecommerce.userMangementService.dto.response.common.ApiResponse;
import project.ecommerce.userMangementService.dto.response.external.ProductExternalResponse;
import project.ecommerce.userMangementService.dto.response.external.SearchProductsResponse;
import project.ecommerce.userMangementService.entity.ProductCartEntity;
import project.ecommerce.userMangementService.exception.ApiError;
import project.ecommerce.userMangementService.exception.AppException;
import project.ecommerce.userMangementService.repository.ProductCartRepository;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductCartService {
    @NonFinal
    static String GET_PRODUCT_BY_ID_URL = "http://localhost:8080/api/products/{id}";
    @NonFinal
    static String UPDATE_PRODUCT_URL = "http://localhost:8080/api/products/{id}";
    @NonFinal
    static String SEARCH_PRODUCTS_URL = "http://localhost:8080/api/products/search";

    RestTemplate restTemplate;
    ObjectMapper objectMapper;
    UserService userService;
    ProductCartRepository productCartRepository;

    @Transactional
    public void saveProductToCart(Long cartId, Long productId, ProductCartRequest request) {
        log.info("addProductToCart productCart service with request {}", request);

        ProductExternalResponse productStockResponse = fetchProductById(productId);

        // check product owner id should not equal to current user
        if (Objects.equals(userService.getCurrentUserId(), productStockResponse.getProductOwnerId())) {
            throw new AppException(ApiError.PRODUCT_OWNER_ID_EQUALS_CURRENT_USER_ID);
        }

        checkProductStockAvailability(productStockResponse, request.getQuantityInCart());

        saveProductInCart(cartId, productId, request.getQuantityInCart());
        updateProductStock(productStockResponse, request.getQuantityInCart());
    }

    // get product details by productId from Product Service
    private ProductExternalResponse fetchProductById(Long productId) {
        String url = GET_PRODUCT_BY_ID_URL.replace("{id}", productId.toString());
        ApiResponse response = restTemplate.getForObject(url, ApiResponse.class);

        if (response == null || response.getData() == null) {
            throw new AppException(ApiError.PRODUCT_NOT_FOUND);
        }
        log.info("found product at id {}: {}", productId, response);
        return objectMapper.convertValue(response.getData(), ProductExternalResponse.class);
    }

    // Checks if requested quantity is available in stock
    private void checkProductStockAvailability(ProductExternalResponse productResponse, int requestedQuantity) {
        if (requestedQuantity > productResponse.getStock()) {
            throw new AppException(ApiError.PRODUCT_OUT_OF_STOCK);
        }
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

    // Updates the product stock in the external system after a product is added to the cart
    private void updateProductStock(ProductExternalResponse productResponse, int requestedQuantity) {
        String url = UPDATE_PRODUCT_URL.replace("{id}", productResponse.getId().toString());
        ProductExternalRequest productExternalRequest = ProductExternalRequest.builder()
                .name(productResponse.getName())
                .stock(productResponse.getStock() - requestedQuantity)
                .price(productResponse.getPrice())
                .category(productResponse.getCategory())
                .productOwnerId(productResponse.getProductOwnerId())
                .build();
        try {
            restTemplate.put(url, productExternalRequest);
            log.info("Product stock updated successfully for product id {}", productResponse.getId());
        } catch (RuntimeException ex) {
            log.info("Failed to update product stock {}", ex.getMessage());
            throw new AppException(ApiError.UNKNOWN_ERROR);
        }
    }

    public List<ProductCartResponse> getProductDataFromStock(List<Long> productIds) {

        List<ProductExternalResponse> productStocks = fetchProductsByIds(productIds);
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

    private List<ProductExternalResponse> fetchProductsByIds(List<Long> productIds) {
        ProductSearchRequest searchRequest = ProductSearchRequest.builder().ids(productIds).build();
        ApiResponse<List<ProductExternalResponse>> response = restTemplate.postForObject(
                SEARCH_PRODUCTS_URL, searchRequest, ApiResponse.class);
        log.info("search product response: {}", response);

        if (response == null || response.getData() == null) {
            throw new AppException(ApiError.PRODUCT_SEARCH_FAILED);
        }
        SearchProductsResponse data = objectMapper.convertValue(response.getData(), SearchProductsResponse.class);
        return data.getProducts();
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
