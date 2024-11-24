package project.ecommerce.userMangementService.service;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import project.ecommerce.userMangementService.dto.request.UpdateCartRequest;
import project.ecommerce.userMangementService.dto.response.CartResponse;
import project.ecommerce.userMangementService.dto.response.GetAllCartResponse;
import project.ecommerce.userMangementService.dto.response.ProductCartResponse;
import project.ecommerce.userMangementService.entity.CartEntity;
import project.ecommerce.userMangementService.exception.ApiError;
import project.ecommerce.userMangementService.exception.AppException;
import project.ecommerce.userMangementService.repository.CartRepository;
import project.ecommerce.userMangementService.util.CartMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartService {
    CartRepository cartRepository;
    CartMapper cartMapper;
    ProductCartService productCartService;
    UserService userService;

    public CartResponse create() {

        log.info("start method create new cart CartService");
        // check cart with userId should not exist
        Long userId = userService.getCurrentUserId();
        if (cartRepository.findByUserId(userId).isPresent()) {
            throw new AppException(ApiError.CART_EXISTED);
        }

        CartEntity cart = CartEntity.builder()
                .userId(userId)
                .build();
        CartResponse response = cartMapper.toResponse(cartRepository.save(cart));
        response.setProducts(null);
        return response;
    }

    public CartResponse getById(Long id){
        log.info("start method get Cart at id= {}", id);
        CartEntity cart = cartRepository.findById(id).orElseThrow(
                () -> {
                    log.error("cart id {} not found", id);
                    return new AppException(ApiError.CART_NOT_FOUND);
                }
        );
        log.info("found cart = {}", cart);
        CartResponse response = cartMapper.toResponse(cart);

        // get product id
        List<Long> productIds = productCartService.getProductIdsByCartId(id);

        // get product detail from product service
        List<ProductCartResponse> productCartResponse = productCartService.getProductDataFromStock(productIds);
        response.setProducts(productCartResponse);
        return response;
    }

    public GetAllCartResponse getAll() {
        List<CartResponse> cartResponseList = cartRepository.findAll().stream()
                .map(cartMapper::toResponse)
                .collect(Collectors.toList());

        return GetAllCartResponse.builder()
                .carts(cartResponseList)
                .build();
    }

    @Transactional
    public void update(Long cartId, Long productId, UpdateCartRequest request) {
        log.info("update method Cart service with request: {}", request);

        // check if cart id is existed
        cartRepository.findById(cartId).orElseThrow(
                () -> {
                    log.error("cart id {} not found", cartId);
                    return new AppException(ApiError.CART_NOT_FOUND);
                }
        );

        // handle cart product logic then save to database
        productCartService.saveProductToCart(cartId, productId, request.getProduct());
    }

    public void delete(Long id) {
        CartEntity cart = cartRepository.findById(id).orElseThrow(
                () -> new AppException(ApiError.CART_NOT_FOUND)
        );
        // delete cart in cart table
        cartRepository.delete(cart);
        // delete product in cart product table
        productCartService.deleteByCartId(id);
    }
}
