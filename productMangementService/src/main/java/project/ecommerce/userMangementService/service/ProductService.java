package project.ecommerce.userMangementService.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import project.ecommerce.userMangementService.dto.request.ProductRequest;
import project.ecommerce.userMangementService.dto.request.ProductSearchRequest;
import project.ecommerce.userMangementService.dto.response.ProductResponse;
import project.ecommerce.userMangementService.dto.response.SearchProductResponse;
import project.ecommerce.userMangementService.dto.response.common.ApiResponse;
import project.ecommerce.userMangementService.dto.response.external.GetCurrentUserInfoResponse;
import project.ecommerce.userMangementService.entity.ProductEntity;
import project.ecommerce.userMangementService.exception.ApiError;
import project.ecommerce.userMangementService.exception.AppException;
import project.ecommerce.userMangementService.repository.ProductRepository;
import project.ecommerce.userMangementService.util.ProductMapper;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ProductService {
    private final static String GET_CURRENT_USER_URL="http://localhost:8080/api/auth/currentInfo";
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper,
                          RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public ProductResponse create(ProductRequest request) {

        log.info("start method addProduct ProductService with request = {}", request.toString());
        if (productRepository.findByName(request.getName()).isPresent()) {
            log.info("product name {} existed", request.getName());
            throw new AppException(ApiError.PRODUCT_NAME_EXISTED);
        }
        ProductEntity product = productMapper.toEntity(request);

        // add product owner id -> current user id
        product.setProductOwnerId(getCurrentUserId());
        ProductResponse response = productMapper.toResponse(productRepository.save(product));
        log.info("response: {}", response);
        return response;
    }

    public ProductResponse getById(Long id) {
        log.info("start get product at id {}", id);
        ProductEntity product = productRepository.findById(id).orElseThrow(
                () -> {
                    log.error("product at id {} not found", id);
                    return new AppException(ApiError.PRODUCT_NOT_FOUND);
                }
        );
        ProductResponse response = productMapper.toResponse(product);
        log.info("response = {}", response);
        return response;
    }

    public List<ProductResponse> getAll() {
        return productRepository.findAll().stream()
                .map(productMapper::toResponse)
                .toList();
    }

    public ProductResponse update(Long id, ProductRequest request) {
        log.info("start updateUser Product service with id {}", id);
        log.info("update product request = {}", request);
        Optional<ProductEntity> productEntityOptional =  productRepository.findById(id);
        if (productEntityOptional.isEmpty()) {
            log.info("product at id {} not found", id);
            throw new AppException(ApiError.PRODUCT_NOT_FOUND);
        }

        ProductEntity product = productEntityOptional.get();
        log.info("found product= {}", product);
        ProductEntity updatingProduct = productMapper.updateProduct(product, request);

        // response
        ProductResponse response = productMapper.toResponse(productRepository.save(updatingProduct));
        log.info("response = {}", response);
        return response;
    }

    public void delete(Long id) {
        ProductEntity user = productRepository.findById(id).orElseThrow(
                () -> {
                    log.info("product at id {} not found", id);
                    return new AppException(ApiError.PRODUCT_NOT_FOUND);
                }
        );
        productRepository.delete(user);
    }

    public SearchProductResponse getAllByIds(ProductSearchRequest request) {
        List<ProductEntity> products = productRepository.findAllById(request.getIds());
        List<ProductResponse> productResponse = products.stream()
                .map(productMapper::toResponse)
                .toList();
        return SearchProductResponse.builder()
                .products(productResponse)
                .build();
    }

    private Long getCurrentUserId() {
        log.info("start searching for get user id by token");
        ApiResponse response = restTemplate.getForObject(GET_CURRENT_USER_URL, ApiResponse.class);
        log.info("response GET_CURRENT_USER_URL: {}", response);

        assert response != null;
        GetCurrentUserInfoResponse data =
                objectMapper.convertValue(response.getData(), GetCurrentUserInfoResponse.class);

        return data.getUserId();
    }
}
