package project.ecommerce.userMangementService.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ApiError {
    UNKNOWN_ERROR(9999, "Uncategorized exception Error payment", HttpStatus.INTERNAL_SERVER_ERROR),
    BALANCE_EXISTED(1301, "balance is already created", HttpStatus.BAD_REQUEST),
    USER_HAS_NO_BALANCE(1302, "user has no balance", HttpStatus.NOT_FOUND),
    INSUFFICIENT_FUND(1303, "insufficient fund", HttpStatus.BAD_REQUEST),
    TRANSACTION_NOT_FOUND(1304, "transaction id not found", HttpStatus.NOT_FOUND),
    INVALID_ORDER_STATUS(1305, "invalid order status", HttpStatus.BAD_REQUEST),
    USER_SERVICE_UNAVAILABLE(1306, "user service unavailable", HttpStatus.BAD_GATEWAY),
    NO_CART_TRANSACTION_FOUND(1307, "no cart transaction found", HttpStatus.NOT_FOUND),
    CART_NOT_FOUND(1308, "cart not found", HttpStatus.NOT_FOUND),
    INVALID_AMOUNT_ERROR(1309, "amount should be equal or above to 0", HttpStatus.BAD_REQUEST)
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
