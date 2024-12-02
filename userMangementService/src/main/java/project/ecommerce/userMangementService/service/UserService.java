package project.ecommerce.userMangementService.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import project.ecommerce.userMangementService.dto.request.UserRegisteringRequest;
import project.ecommerce.userMangementService.dto.request.UserUpdatingRequest;
import project.ecommerce.userMangementService.dto.response.UserResponse;
import project.ecommerce.userMangementService.dto.response.internal.UserInternalResponse;
import project.ecommerce.userMangementService.entity.RoleEntity;
import project.ecommerce.userMangementService.entity.UserEntity;
import project.ecommerce.userMangementService.exception.ApiError;
import project.ecommerce.userMangementService.exception.AppException;
import project.ecommerce.userMangementService.repository.RoleRepository;
import project.ecommerce.userMangementService.repository.UserRepository;
import project.ecommerce.userMangementService.util.RoleMapper;
import project.ecommerce.userMangementService.util.UserMapper;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    public UserResponse register(UserRegisteringRequest request) {

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new AppException(ApiError.USERNAME_EXISTED);
        } else if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new AppException(ApiError.EMAIL_IN_USED);
        }

        Set<RoleEntity> roleEntitySet = new HashSet<>();
        if (!request.getRoleNames().isEmpty() || request.getRoleNames() != null) {
            for (String role : request.getRoleNames()) {
                if (roleRepository.findByName(role).isEmpty()) {
                    throw new AppException(ApiError.ROLE_NOT_FOUND);
                }
                roleEntitySet.add(roleRepository.findByName(role).get());
            }
        }

        // encrypt password
        UserEntity user = userMapper.toUserEntityRegistering(request);
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        user.setPassword(encodedPassword);

        // set Role
        user.setRoles(roleEntitySet);

        // response
        return userMapper.toUserResponse(userRepository.save(user));
    }

    public UserResponse getById(Long id) {
        UserEntity user = userRepository.findById(id).orElseThrow(
                () -> new AppException(ApiError.USER_NOT_FOUND)
        );

        return userMapper.toUserResponse(user);
    }

    public List<UserResponse> getAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserResponse)
                .toList();
    }

    public UserResponse updateUser(Long id, UserUpdatingRequest request) {

        Optional<UserEntity> userEntityOptional =  userRepository.findById(id);
        if (userEntityOptional.isEmpty()) {
            throw new AppException(ApiError.USER_NOT_FOUND);
        } else if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new AppException(ApiError.EMAIL_IN_USED);
        }

        UserEntity existingUser = userEntityOptional.get();
        UserEntity updatedUser = userMapper.toUserEntityUpdating(existingUser, request);

        // response
        return userMapper.toUserResponse(userRepository.save(updatedUser));
    }

    public void deleteUser(Long id) {
        UserEntity user = userRepository.findById(id).orElseThrow(
                () -> new AppException(ApiError.USER_NOT_FOUND)
        );
        userRepository.delete(user);
    }

    public UserInternalResponse getUserByUsername(String username) {
        log.info("getUserByUsername method");
        UserEntity user = userRepository.findByUsername(username).orElseThrow(
                () -> new AppException(ApiError.USER_NOT_FOUND)
        );

        return userMapper.toUserInternalResponse(user);
    }
}
