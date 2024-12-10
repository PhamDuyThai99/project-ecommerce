package project.ecommerce.userMangementService.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ApiError {
    UNKNOWN_ERROR(9999, "Uncategorized exception Error", HttpStatus.INTERNAL_SERVER_ERROR),
    PRODUCT_NAME_EXISTED(1101, "Product name existed", HttpStatus.BAD_REQUEST),
    PRODUCT_NOT_FOUND(1102, "Product not found by id", HttpStatus.NOT_FOUND);

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;

    ApiError(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

}
