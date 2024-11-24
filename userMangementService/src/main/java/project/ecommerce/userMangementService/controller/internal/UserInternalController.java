package project.ecommerce.userMangementService.controller.internal;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import project.ecommerce.userMangementService.dto.request.UserRegisteringRequest;
import project.ecommerce.userMangementService.dto.request.UserUpdatingRequest;
import project.ecommerce.userMangementService.dto.response.UserResponse;
import project.ecommerce.userMangementService.dto.response.common.ApiResponse;
import project.ecommerce.userMangementService.dto.response.common.ResponseStatus;
import project.ecommerce.userMangementService.dto.response.internal.UserInternalResponse;
import project.ecommerce.userMangementService.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/internal/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserInternalController {
    UserService userService;

    @GetMapping("/{username}")
    public ApiResponse<UserInternalResponse> getByUsername(@PathVariable("username") String username) {
        return ApiResponse.<UserInternalResponse>builder()
                .status(new ResponseStatus())
                .data(userService.getUserByUsername(username))
                .build();
    }
}
