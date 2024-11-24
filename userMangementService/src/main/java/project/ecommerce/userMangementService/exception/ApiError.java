package project.ecommerce.userMangementService.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ApiError {
    UNKNOWN_ERROR(9999, "Uncategorized exception Error", HttpStatus.INTERNAL_SERVER_ERROR),
    USERNAME_EXISTED(1001, "Username existed", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(1002, "User not found by id", HttpStatus.NOT_FOUND),
    ROLE_EXISTED(1003, "Role existed", HttpStatus.BAD_REQUEST),
    ROLE_NOT_FOUND(1004,"Role not found", HttpStatus.NOT_FOUND),
    EMAIL_IN_USED(1005, "Email is already in used", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;

    ApiError(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

}
