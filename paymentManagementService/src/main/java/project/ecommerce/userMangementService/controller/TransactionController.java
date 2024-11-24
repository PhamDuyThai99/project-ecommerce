package project.ecommerce.userMangementService.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import project.ecommerce.userMangementService.dto.request.CreateCartTransactionRequest;
import project.ecommerce.userMangementService.dto.request.CreateP2PTransactionRequest;
import project.ecommerce.userMangementService.dto.request.CreateReloadTransactionRequest;
import project.ecommerce.userMangementService.dto.request.CreateWithdrawTransactionRequest;
import project.ecommerce.userMangementService.dto.response.CreateTransactionResponse;
import project.ecommerce.userMangementService.dto.response.TransactionDetailResponse;
import project.ecommerce.userMangementService.dto.response.common.ApiResponse;
import project.ecommerce.userMangementService.dto.response.common.ResponseStatus;
import project.ecommerce.userMangementService.service.TransactionService;

@RestController
@RequestMapping("/payment/transaction")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TransactionController {

    TransactionService transactionService;

    @PostMapping("/create/p2p")
    public ApiResponse<CreateTransactionResponse> create(@RequestBody CreateP2PTransactionRequest request) {
        return ApiResponse.<CreateTransactionResponse>builder()
                .status(new ResponseStatus())
                .data(transactionService.createP2PTransaction(request))
                .build();
    }

    @PostMapping("/execute/p2p/{transactionId}")
    public ApiResponse<Void> executeP2P(@PathVariable("transactionId") String id) {
        transactionService.executeP2PTransaction(id);
        return ApiResponse.<Void>builder()
                .status(new ResponseStatus())
                .data(null)
                .build();
    }

    @PostMapping("/create/cart")
    public ApiResponse<Void> createCartCheckoutTransaction(@RequestBody CreateCartTransactionRequest request) {
        transactionService.createCartCheckoutTransaction(request);
        return ApiResponse.<Void>builder()
                .status(new ResponseStatus())
                .data(null)
                .build();
    }

    @PostMapping("/execute/cart/{cartId}")
    public ApiResponse<Void> executeCartCheckoutTransaction(@PathVariable("cartId") Long cartId) {
        transactionService.executeCartCheckoutTransaction(cartId);
        return ApiResponse.<Void>builder()
                .status(new ResponseStatus())
                .data(null)
                .build();
    }

    @PostMapping("/create/reload")
    public ApiResponse<CreateTransactionResponse> createReloadTransaction(
            @RequestBody CreateReloadTransactionRequest request) {

        return ApiResponse.<CreateTransactionResponse>builder()
                .status(new ResponseStatus())
                .data(transactionService.createReloadTransaction(request))
                .build();
    }

    @PostMapping("/execute/reload/{transactionId}")
    public ApiResponse<Void> executeReloadTransaction(@PathVariable("transactionId") String transactionId) {
        transactionService.executeReloadTransaction(transactionId);
        return ApiResponse.<Void>builder()
                .status(new ResponseStatus())
                .data(null)
                .build();
    }

    @PostMapping("/create/withdraw")
    public ApiResponse<CreateTransactionResponse> createWithdrawTransaction(
            @RequestBody CreateWithdrawTransactionRequest request) {

        return ApiResponse.<CreateTransactionResponse>builder()
                .status(new ResponseStatus())
                .data(transactionService.createWithdrawTransaction(request))
                .build();
    }

    @PostMapping("/execute/withdraw/{transactionId}")
    public ApiResponse<Void> executeWithdrawTransaction(@PathVariable("transactionId") String transactionId) {
        transactionService.executeWithdrawTransaction(transactionId);
        return ApiResponse.<Void>builder()
                .status(new ResponseStatus())
                .data(null)
                .build();
    }

    @GetMapping("/detail/{transactionId}")
    public ApiResponse<TransactionDetailResponse> getTransactionDetail(@PathVariable("transactionId") String id) {
        return ApiResponse.<TransactionDetailResponse>builder()
                .status(new ResponseStatus())
                .data(transactionService.getTransactionDetails(id))
                .build();
    }


    
}
