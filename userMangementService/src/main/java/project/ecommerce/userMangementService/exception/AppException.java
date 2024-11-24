package project.ecommerce.userMangementService.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppException extends RuntimeException{
    private ApiError apiError;

    public AppException(ApiError errorCode) {
        super(errorCode.getMessage());
        this.apiError = errorCode;
    }
}
