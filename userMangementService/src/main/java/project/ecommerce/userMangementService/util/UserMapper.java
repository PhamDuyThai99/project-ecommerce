package project.ecommerce.userMangementService.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import project.ecommerce.userMangementService.dto.request.UserRegisteringRequest;
import project.ecommerce.userMangementService.dto.request.UserUpdatingRequest;
import project.ecommerce.userMangementService.dto.response.UserResponse;
import project.ecommerce.userMangementService.dto.response.internal.UserInternalResponse;
import project.ecommerce.userMangementService.entity.RoleEntity;
import project.ecommerce.userMangementService.entity.UserEntity;
import project.ecommerce.userMangementService.exception.ApiError;
import project.ecommerce.userMangementService.exception.AppException;
import project.ecommerce.userMangementService.repository.RoleRepository;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapper {
    private final ObjectMapper objectMapper;
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    public UserMapper(ObjectMapper objectMapper,
                      RoleRepository roleRepository, RoleMapper roleMapper) {
        this.objectMapper = objectMapper;
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
    }

    public UserEntity toUserEntityRegistering(UserRegisteringRequest request) {
        UserEntity userEntity = objectMapper.convertValue(request, UserEntity.class);
        userEntity.setRoles(
                request.getRoleNames().stream()
                        .map(role -> roleRepository.findByName(role).get())
                        .collect(Collectors.toSet())
        );
        return userEntity;
    }

    public UserEntity toUserEntityUpdating(UserEntity existingUser, UserUpdatingRequest request) {

        Set<RoleEntity> roleEntitySet = new HashSet<>();
        for (String role: request.getRoleNames()) {
            if(roleRepository.findByName(role).isEmpty()) {
                throw new AppException(ApiError.ROLE_NOT_FOUND);
            }
            roleEntitySet.add(roleRepository.findByName(role).get());
        }

        existingUser.setEmail(request.getEmail());
        existingUser.setRoles(roleEntitySet);
        return existingUser;
    }

    public UserResponse toUserResponse(UserEntity userEntity) {
        UserResponse userResponse = objectMapper.convertValue(userEntity, UserResponse.class);
        userResponse.setRoles(
                userEntity.getRoles().stream()
                        .map(roleMapper::toRoleResponse)
                        .collect(Collectors.toSet())
        );
        return userResponse;
    }

    public UserInternalResponse toUserInternalResponse(UserEntity userEntity) {
        UserInternalResponse userResponse = objectMapper.convertValue(userEntity, UserInternalResponse.class);
        userResponse.setRoles(
                userEntity.getRoles().stream()
                        .map(roleMapper::toRoleResponse)
                        .collect(Collectors.toSet())
        );
        return userResponse;
    }
}
