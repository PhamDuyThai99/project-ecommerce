package project.ecommerce.userMangementService.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import project.ecommerce.userMangementService.dto.response.common.ApiResponse;
import project.ecommerce.userMangementService.dto.response.common.ResponseStatus;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
//    @ExceptionHandler(value = RuntimeException.class)
//    ResponseEntity<ApiResponse<Void>> handlingRunTimeException(RuntimeException ex) {
//        ResponseStatus responseStatus = ResponseStatus.builder()
//                .code(ApiError.UNKNOWN_ERROR.getCode())
//                .message(ApiError.UNKNOWN_ERROR.getMessage())
//                .build();
//        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
//                .status(responseStatus)
//                .data(null)
//                .build();
//        return ResponseEntity
//                .status(ApiError.UNKNOWN_ERROR.getStatusCode())
//                .body(apiResponse);
//
//    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse<Void>> handlingAppException(AppException ex) {
        ApiError apiError = ex.getApiError();

        ResponseStatus responseStatus = ResponseStatus.builder()
                .code(apiError.getCode())
                .message(apiError.getMessage())
                .build();
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .status(responseStatus)
                .data(null)
                .build();
        return ResponseEntity.status(apiError.getStatusCode()).body(apiResponse);
    }
}