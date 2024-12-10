package project.ecommerce.userMangementService.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import project.ecommerce.userMangementService.dto.request.ProductRequest;
import project.ecommerce.userMangementService.dto.request.ProductSearchRequest;
import project.ecommerce.userMangementService.dto.response.ProductResponse;
import project.ecommerce.userMangementService.dto.response.SearchProductResponse;
import project.ecommerce.userMangementService.dto.response.common.ApiResponse;
import project.ecommerce.userMangementService.dto.response.common.ResponseStatus;
import project.ecommerce.userMangementService.service.ProductService;

import java.util.List;


@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductController {
    ProductService productService;

    @PostMapping("/create")
    public ApiResponse<ProductResponse> register(@RequestBody ProductRequest request) {
        return ApiResponse.<ProductResponse>builder()
                .status(new ResponseStatus())
                .data(productService.create(request))
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductResponse> getById(@PathVariable("id") Long id) {
        return ApiResponse.<ProductResponse>builder()
                .status(new ResponseStatus())
                .data(productService.getById(id))
                .build();
    }

    @GetMapping
    public ApiResponse<List<ProductResponse>> getAll() {
        return ApiResponse.<List<ProductResponse>>builder()
                .status(new ResponseStatus())
                .data(productService.getAll())
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<ProductResponse> updateById(@PathVariable("id") Long id,
                                                @RequestBody ProductRequest request) {
        return ApiResponse.<ProductResponse>builder()
                .status(new ResponseStatus())
                .data(productService.update(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<ProductResponse> deleteById(@PathVariable("id") Long id) {
        productService.delete(id);
        return ApiResponse.<ProductResponse>builder()
                .status(new ResponseStatus())
                .data(null)
                .build();
    }

    @PostMapping("/search")
    public ApiResponse<SearchProductResponse> getAllByIds(@RequestBody ProductSearchRequest request) {
        return ApiResponse.<SearchProductResponse>builder()
                .status(new ResponseStatus())
                .data(productService.getAllByIds(request))
                .build();
    }
}
