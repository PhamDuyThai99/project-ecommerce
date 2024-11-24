package project.ecommerce.userMangementService.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import project.ecommerce.userMangementService.dto.request.CreateCartRequest;
import project.ecommerce.userMangementService.dto.request.UpdateCartRequest;
import project.ecommerce.userMangementService.dto.response.CartResponse;
import project.ecommerce.userMangementService.dto.response.GetAllCartResponse;
import project.ecommerce.userMangementService.dto.response.common.ApiResponse;
import project.ecommerce.userMangementService.dto.response.common.ResponseStatus;
import project.ecommerce.userMangementService.service.CartService;

import java.util.List;


@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartController {
    CartService cartService;

    @PostMapping("/create")
    public ApiResponse<CartResponse> create() {
        return ApiResponse.<CartResponse>builder()
                .status(new ResponseStatus())
                .data(cartService.create())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<CartResponse> getById(@PathVariable("id") Long id) throws JsonProcessingException {
        return ApiResponse.<CartResponse>builder()
                .status(new ResponseStatus())
                .data(cartService.getById(id))
                .build();
    }

    @GetMapping("/getAll")
    public ApiResponse<GetAllCartResponse> getAllCart() {
        return ApiResponse.<GetAllCartResponse>builder()
                .status(new ResponseStatus())
                .data(cartService.getAll())
                .build();
    }

    @PutMapping("/{cartId}/products/{productId}")
    public ApiResponse<Void> updateByCartId(@PathVariable("cartId") Long cartId,
                                            @PathVariable("productId") Long productId,
                                            @RequestBody UpdateCartRequest request) {
        cartService.update(cartId, productId, request);
        return ApiResponse.<Void>builder()
                .status(new ResponseStatus())
                .data(null)
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<CartResponse> deleteById(@PathVariable("id") Long id) {
        cartService.delete(id);
        return ApiResponse.<CartResponse>builder()
                .status(new ResponseStatus())
                .data(null)
                .build();
    }
}
