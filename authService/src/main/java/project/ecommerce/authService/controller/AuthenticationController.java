package project.ecommerce.authService.controller;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import project.ecommerce.authService.dto.request.AuthenticationRequest;
import project.ecommerce.authService.dto.request.IntrospectRequest;
import project.ecommerce.authService.dto.request.LogoutRequest;
import project.ecommerce.authService.dto.response.AuthenticationResponse;
import project.ecommerce.authService.dto.response.GetCurrentUserInfoResponse;
import project.ecommerce.authService.dto.response.IntrospectResponse;
import project.ecommerce.authService.dto.response.common.ApiResponse;
import project.ecommerce.authService.dto.response.common.ResponseStatus;
import project.ecommerce.authService.service.AuthenticationService;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        log.info("login auth controller");
        return ApiResponse.<AuthenticationResponse>builder()
                .status(new ResponseStatus())
                .data(authenticationService.login(request))
                .build();
    }

    @PostMapping("/introspect")
    public ApiResponse<IntrospectResponse> introspect() {
        log.info("introspect auth controller");
        return ApiResponse.<IntrospectResponse>builder()
                .status(new ResponseStatus())
                .data(authenticationService.introspect())
                .build();
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        log.info("logout auth controller");
        authenticationService.logout();
        return ApiResponse.<Void>builder().build();
    }

    @GetMapping("/currentInfo")
    private ApiResponse<GetCurrentUserInfoResponse> getCurrentUserInfo() {
        log.info("getCurrentUserInfo auth controller");
        return ApiResponse.<GetCurrentUserInfoResponse>builder()
                .status(new ResponseStatus())
                .data(authenticationService.getCurrentUserByToken())
                .build();

    }
}
