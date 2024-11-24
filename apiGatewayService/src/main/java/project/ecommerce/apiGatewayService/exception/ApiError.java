package project.ecommerce.apiGatewayService.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ApiError {
    UNKNOWN_ERROR(9999, "Uncategorized exception Error ApiGateway", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHENTICATED(2001, "unauthenticated ApiGateway", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(2002, "Not Allowed apiGateway", HttpStatus.FORBIDDEN),
    ACCESS_TOKEN_EXPIRED(2003, "Access token is expired", HttpStatus.UNAUTHORIZED)
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
