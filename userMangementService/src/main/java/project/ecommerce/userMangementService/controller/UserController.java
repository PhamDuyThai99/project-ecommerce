package project.ecommerce.userMangementService.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
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
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @PostMapping("/register")
    public ApiResponse<UserResponse> register(@RequestBody UserRegisteringRequest request) {
        return ApiResponse.<UserResponse>builder()
                .status(new ResponseStatus())
                .data(userService.register(request))
                .build();
    }

    @GetMapping("/{id:[0-9]+}")
    public ApiResponse<UserResponse> getById(@PathVariable("id") Long id) {
        return ApiResponse.<UserResponse>builder()
                .status(new ResponseStatus())
                .data(userService.getById(id))
                .build();
    }

    @GetMapping
    public ApiResponse<List<UserResponse>> getAll() {
        return ApiResponse.<List<UserResponse>>builder()
                .status(new ResponseStatus())
                .data(userService.getAll())
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<UserResponse> updateById(@PathVariable("id") Long id,
                                                @RequestBody UserUpdatingRequest request) {
        return ApiResponse.<UserResponse>builder()
                .status(new ResponseStatus())
                .data(userService.updateUser(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<UserResponse> deleteById(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return ApiResponse.<UserResponse>builder()
                .status(new ResponseStatus())
                .data(null)
                .build();
    }
}
