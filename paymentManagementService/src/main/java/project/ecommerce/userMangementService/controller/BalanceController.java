package project.ecommerce.userMangementService.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import project.ecommerce.userMangementService.dto.response.BalanceResponse;
import project.ecommerce.userMangementService.dto.response.common.ApiResponse;
import project.ecommerce.userMangementService.dto.response.common.ResponseStatus;
import project.ecommerce.userMangementService.service.BalanceService;

@RestController
@RequestMapping("/payment/balance")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BalanceController {

    BalanceService balanceService;

    @PostMapping("/create/{userId}")
    public ApiResponse<BalanceResponse> create(@PathVariable("userId") Long userId) {
        return ApiResponse.<BalanceResponse>builder()
                .status(new ResponseStatus())
                .data(balanceService.createBalance(userId))
                .build();
    }

    @GetMapping("/get/{userId}")
    public ApiResponse<BalanceResponse> getByUserId(@PathVariable("userId") Long userId) {
        return ApiResponse.<BalanceResponse>builder()
                .status(new ResponseStatus())
                .data(balanceService.getBalanceByUserId(userId))
                .build();
    }

    @GetMapping("/myBalance")
    public ApiResponse<BalanceResponse> getByUserId() {
        return ApiResponse.<BalanceResponse>builder()
                .status(new ResponseStatus())
                .data(balanceService.getCurrentUserBalance())
                .build();
    }
    
}
