package project.ecommerce.userMangementService.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import project.ecommerce.userMangementService.dto.request.RoleRequest;
import project.ecommerce.userMangementService.dto.response.RoleResponse;
import project.ecommerce.userMangementService.dto.response.common.ApiResponse;
import project.ecommerce.userMangementService.dto.response.common.ResponseStatus;
import project.ecommerce.userMangementService.service.RoleService;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {
    RoleService roleService;

    @PostMapping
    public ApiResponse<RoleResponse> createRole(@RequestBody RoleRequest request) {
        return ApiResponse.<RoleResponse>builder()
                .status(new ResponseStatus())
                .data(roleService.createRole(request))
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<RoleResponse> getRoleById(@PathVariable("id") Long id) {
        return ApiResponse.<RoleResponse>builder()
                .status(new ResponseStatus())
                .data(roleService.getRoleById(id))
                .build();
    }

    @GetMapping
    public ApiResponse<List<RoleResponse>> getAll() {
        return ApiResponse.<List<RoleResponse>>builder()
                .status(new ResponseStatus())
                .data(roleService.getAll())
                .build();
    }
}