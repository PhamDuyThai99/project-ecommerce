package project.ecommerce.userMangementService.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import project.ecommerce.userMangementService.util.UserMapper;

import javax.management.relation.Role;
import javax.swing.text.html.Option;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void test_register_usernameExisted_throwException() {
        UserRegisteringRequest request = UserRegisteringRequest.builder()
                .username("abc")
                .password("xyz")
                .email("abc@123.com")
                .roleNames(null)
                .build();

        UserEntity user = UserEntity.builder()
                .username(request.getUsername())
                .password("xyz")
                .email("abc@123.com")
                .roles(null)
                .build();
        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.ofNullable(user));

        // Act
        AppException appException = assertThrows(AppException.class, () -> userService.register(request));

        // Assert
        assertEquals(appException.getApiError(), ApiError.USERNAME_EXISTED);
        verifyNoInteractions(userMapper);
    }

    @Test
    void test_register_emailExisted_throwException() {
        UserRegisteringRequest request = UserRegisteringRequest.builder()
                .username("abc")
                .password("xyz")
                .email("abc@123.com")
                .roleNames(null)
                .build();

        UserEntity user = UserEntity.builder()
                .username("abc xyz")
                .password("xyz")
                .email(request.getEmail())
                .roles(null)
                .build();
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.ofNullable(user));

        // Act
        AppException appException = assertThrows(AppException.class, () -> userService.register(request));

        // Assert
        assertEquals(appException.getApiError(), ApiError.EMAIL_IN_USED);
        verifyNoInteractions(userMapper);
    }

    @Test
    void test_register_RoleNotFound_throwException() {
        List<String> roleNamesRequest = List.of("role1", "role2");
        UserRegisteringRequest request = UserRegisteringRequest.builder()
                .username("abc")
                .password("xyz")
                .email("abc@123.com")
                .roleNames(roleNamesRequest)
                .build();

        when(roleRepository.findByName(roleNamesRequest.get(0))).thenReturn(Optional.empty());

        // Act
        AppException appException = assertThrows(AppException.class, () -> userService.register(request));

        // Assert
        assertEquals(appException.getApiError(), ApiError.ROLE_NOT_FOUND);
        verifyNoInteractions(userMapper);
    }

    @Test
    void test_register_successfully_shouldReturnResponse() {
        List<String> roleNamesRequest = List.of("role1", "role2");
        UserRegisteringRequest request = UserRegisteringRequest.builder()
                .username("abc")
                .password("xyz")
                .email("abc@123.com")
                .roleNames(roleNamesRequest)
                .build();

        UserEntity savedUser = UserEntity.builder()
                .id(1L)
                .build();
        UserResponse expectedResponse = UserResponse.builder()
                .id(savedUser.getId())
                .build();
        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(roleRepository.findByName(any())).thenReturn(Optional.of(new RoleEntity()));
        when(userMapper.toUserEntityRegistering(request)).thenReturn(new UserEntity());
        when(passwordEncoder.encode(any())).thenReturn("abc");
        when(userRepository.save(any())).thenReturn(savedUser);
        when(userMapper.toUserResponse(any())).thenReturn(expectedResponse);

        // Act
        UserResponse actualResponse = userService.register(request);

        // Assert
        assertEquals(actualResponse, expectedResponse);
    }

    @Test
    void test_getById_UserNotFound_throwException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        AppException appException = assertThrows(AppException.class, () -> userService.getById(1L));

        // Assert
        assertEquals(appException.getApiError(), ApiError.USER_NOT_FOUND);
    }

    @Test
    void test_getById_successfully_userFound() {
        UserEntity user = UserEntity.builder().id(1L).build();
        UserResponse expectedResponse = UserResponse.builder().id(user.getId()).build();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userMapper.toUserResponse(user)).thenReturn(expectedResponse);

        // Act
        UserResponse displayedResponse = userService.getById(1L);

        // Assert
        assertEquals(displayedResponse, expectedResponse);
    }

    @Test
    void test_getAll_successfully_shouldReturnListOfUserResponse() {
        UserEntity user = UserEntity.builder().id(1L).build();
        UserResponse expectedResponse = UserResponse.builder().id(user.getId()).build();

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toUserResponse(user)).thenReturn(expectedResponse);

        // Act
        List<UserResponse> userResponseList = userService.getAll();
        // Assert
        assertEquals(userResponseList, List.of(expectedResponse));
    }

    @Test
    void test_updateUser_UserNotFound_shouldThrowException() {

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        AppException appException = assertThrows(AppException.class,
                () -> userService.updateUser(1L, new UserUpdatingRequest()));

        assertEquals(appException.getApiError(), ApiError.USER_NOT_FOUND);
    }

    @Test
    void test_updateUser_EmailInUsed_shouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(UserEntity.builder().id(1L).build()));
        when(userRepository.findByEmail("email")).thenReturn(Optional.of(UserEntity.builder().id(1L).build()));

        AppException appException = assertThrows(AppException.class,
                () -> userService.updateUser(1L,
                        UserUpdatingRequest.builder().email("email").build()));

        assertEquals(appException.getApiError(), ApiError.EMAIL_IN_USED);
    }

    @Test
    void test_updateUser_successfully_shouldReturnUserResponse() {
        UserUpdatingRequest request = UserUpdatingRequest.builder().email("email").build();
        UserEntity existingUser = UserEntity.builder().id(1L).build();
        UserEntity updatedUser = UserEntity.builder().id(1L).email("email").build();
        UserResponse expectedResponse = UserResponse.builder().id(existingUser.getId()).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail("email")).thenReturn(Optional.empty());
        when(userMapper.toUserEntityUpdating(existingUser, request)).thenReturn(updatedUser);
        when(userRepository.save(updatedUser)).thenReturn(updatedUser);
        when(userMapper.toUserResponse(updatedUser)).thenReturn(expectedResponse);

        UserResponse actualResponse = userService.updateUser(1L, request);

        assertEquals(actualResponse,expectedResponse);
    }

    @Test
    void test_deleteUser_UserNotFound_throwException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        AppException appException = assertThrows(AppException.class,
                () -> userService.deleteUser(1L));

        assertEquals(appException.getApiError(), ApiError.USER_NOT_FOUND);
    }

    @Test
    void test_deleteUser_successfully_doNothing() {
        UserEntity existingUser = UserEntity.builder().id(1L).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        userService.deleteUser(1L);

        verify(userRepository).delete(existingUser);
    }

    @Test
    void test_getUserByUsername_UserNotFound_shouldThrowException() {
        when(userRepository.findByUsername("name")).thenReturn(Optional.empty());

        AppException appException = assertThrows(AppException.class,
                () -> userService.getUserByUsername("name"));

        assertEquals(appException.getApiError(), ApiError.USER_NOT_FOUND);
    }

    @Test
    void test_getUserByUsername_successfully_shouldReturnUserResponse() {
        UserEntity user = UserEntity.builder().id(1L).build();
        UserInternalResponse expectedResponse = UserInternalResponse.builder().id(user.getId()).build();

        when(userRepository.findByUsername("name")).thenReturn(Optional.of(user));
        when(userMapper.toUserInternalResponse(user)).thenReturn(expectedResponse);

        UserInternalResponse actualResponse = userService.getUserByUsername("name");
        assertEquals(actualResponse, expectedResponse);
    }
}