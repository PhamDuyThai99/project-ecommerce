package project.ecommerce.userMangementService.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ApiError {
    UNKNOWN_ERROR(9999, "Uncategorized exception Error", HttpStatus.INTERNAL_SERVER_ERROR),
    CART_EXISTED(1201, "Cart is already created", HttpStatus.BAD_REQUEST),
    CART_NOT_FOUND(1202, "cart not found by id", HttpStatus.NOT_FOUND),
    PRODUCT_OUT_OF_STOCK(1203, "quantity of product is over the stock", HttpStatus.BAD_REQUEST),
    PRODUCT_NOT_FOUND(1204, "product id not found", HttpStatus.BAD_REQUEST),
    PRODUCT_SEARCH_FAILED(1205, "search product failed", HttpStatus.BAD_REQUEST),
    PRODUCT_OWNER_ID_EQUALS_CURRENT_USER_ID(
            1206, "users cannot add their own product to cart", HttpStatus.BAD_REQUEST)
    ;

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;

    ApiError(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

}
