package project.ecommerce.userMangementService.service.external;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import project.ecommerce.userMangementService.dto.request.external.ProductExternalRequest;
import project.ecommerce.userMangementService.dto.request.external.ProductSearchRequest;
import project.ecommerce.userMangementService.dto.response.common.ApiResponse;
import project.ecommerce.userMangementService.dto.response.external.ProductExternalResponse;
import project.ecommerce.userMangementService.dto.response.external.SearchProductsResponse;
import project.ecommerce.userMangementService.exception.ApiError;
import project.ecommerce.userMangementService.exception.AppException;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductService {

    @NonFinal
    @Value("${externalApi.productService.getProductByIdUrl}")
    static String GET_PRODUCT_BY_ID_URL = "http://localhost:8080/api/products/{id}";

    @NonFinal
    @Value("${externalApi.productService.updateProductUrl}")
    static String UPDATE_PRODUCT_URL = "http://localhost:8080/api/products/{id}";

    @NonFinal
    @Value("${externalApi.productService.searchProductsUrl}")
    static String SEARCH_PRODUCTS_URL = "http://localhost:8080/api/products/search";

    RestTemplate restTemplate;
    ObjectMapper objectMapper;

    public ProductExternalResponse fetchProductByIdFromProductService(Long productId) {
        String url = GET_PRODUCT_BY_ID_URL.replace("{id}", productId.toString());
        ApiResponse response = restTemplate.getForObject(url, ApiResponse.class);

        if (response == null || response.getData() == null) {
            throw new AppException(ApiError.PRODUCT_NOT_FOUND);
        }
        log.info("found product at id {}: {}", productId, response);
        return objectMapper.convertValue(response.getData(), ProductExternalResponse.class);
    }

    public void checkProductStockAvailability(ProductExternalResponse productResponse, int requestedQuantity) {
        if (requestedQuantity > productResponse.getStock()) {
            throw new AppException(ApiError.PRODUCT_OUT_OF_STOCK);
        }
    }

    public void updateProductToStockProductService(ProductExternalResponse productResponse, int requestedQuantity) {
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

    public List<ProductExternalResponse> searchProductsFromProductService(List<Long> productIds) {
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

}
