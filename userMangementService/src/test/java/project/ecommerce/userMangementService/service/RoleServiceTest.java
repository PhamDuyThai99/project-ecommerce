package project.ecommerce.userMangementService.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import project.ecommerce.userMangementService.dto.request.RoleRequest;
import project.ecommerce.userMangementService.dto.response.RoleResponse;
import project.ecommerce.userMangementService.entity.RoleEntity;
import project.ecommerce.userMangementService.exception.ApiError;
import project.ecommerce.userMangementService.exception.AppException;
import project.ecommerce.userMangementService.repository.RoleRepository;
import project.ecommerce.userMangementService.util.RoleMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RoleMapper roleMapper;

    @InjectMocks
    private RoleService roleService;

    RoleRequest buildRoleRequest() {
        return RoleRequest.builder()
                .name("User")
                .description("description")
                .build();
    }

    @Test
    void createRole_whenRoleDoesNotExist_shouldSaveRole() {
        // Arrange
        RoleRequest request = RoleRequest.builder()
                .name("User")
                .description("description")
                .build();
        RoleEntity roleEntity = RoleEntity.builder()
                .id(1L)
                .name("User")
                .description("description")
                .createdAt(LocalDateTime.of(2024, 12, 3, 12, 3))
                .updatedAt(LocalDateTime.of(2024, 12, 4, 12, 3))
                .build();
        RoleResponse expectedResponse = RoleResponse.builder()
                .id(roleEntity.getId())
                .name(roleEntity.getName())
                .description(roleEntity.getDescription())
                .createdAt(roleEntity.getCreatedAt())
                .updatedAt(roleEntity.getUpdatedAt())
                .build();

        when(roleRepository.findByName(request.getName())).thenReturn(Optional.empty());
        when(roleMapper.toRoleEntity(request)).thenReturn(roleEntity);
        when(roleRepository.save(roleEntity)).thenReturn(roleEntity);
        when(roleMapper.toRoleResponse(roleEntity)).thenReturn(expectedResponse);

        // Act
        RoleResponse actualResponse = roleService.createRole(request);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(actualResponse, expectedResponse);

    }

    @Test
    void createRole_whenRoleExists_shouldThrowException() {
        // Arrange
        RoleRequest request = RoleRequest.builder()
                .name("User")
                .description("description")
                .build();
        RoleEntity roleEntity = RoleEntity.builder()
                .id(1L)
                .name(request.getName())
                .description(request.getDescription())
                .createdAt(LocalDateTime.of(2024, 12, 3, 12, 3))
                .updatedAt(LocalDateTime.of(2024, 12, 4, 12, 3))
                .build();
        when(roleRepository.findByName(request.getName())).thenReturn(Optional.ofNullable(roleEntity));

        // Act
        AppException exception = assertThrows(AppException.class, () -> roleService.createRole(request));

        // Assert
        assertEquals(ApiError.ROLE_EXISTED, exception.getApiError());
        verifyNoInteractions(roleMapper);
    }

    @Test
    void getRoleById_whenRoleNotFound_shouldThrowRoleException() {
        Long id = 1L;
        when(roleRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        AppException exception = assertThrows(AppException.class, () -> roleService.getRoleById(id));

        // Assert
        assertEquals(ApiError.ROLE_NOT_FOUND, exception.getApiError());
        verifyNoInteractions(roleMapper);
    }

    @Test
    void getRoleById_whenRoleFound_shouldReturnResponse() {
        Long id = 1L;
        RoleEntity roleEntity = RoleEntity.builder()
                .id(1L)
                .name("User")
                .description("description")
                .createdAt(LocalDateTime.of(2024, 12, 3, 12, 3))
                .updatedAt(LocalDateTime.of(2024, 12, 4, 12, 3))
                .build();
        RoleResponse expectedResponse = RoleResponse.builder()
                .id(id)
                .name(roleEntity.getName())
                .description(roleEntity.getDescription())
                .createdAt(roleEntity.getCreatedAt())
                .updatedAt(roleEntity.getUpdatedAt())
                .build();
        when(roleRepository.findById(id)).thenReturn(Optional.of(roleEntity));
        when(roleMapper.toRoleResponse(roleEntity)).thenReturn(expectedResponse);

        // Act
        RoleResponse actualResponse = roleService.getRoleById(id);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(actualResponse, expectedResponse);
    }

    @Test
    void getAll_returnListOfRoleResponse() {
        RoleEntity roleEntity1 = RoleEntity.builder()
                .id(1L)
                .name("User1")
                .description("description")
                .createdAt(LocalDateTime.of(2024, 12, 3, 12, 3))
                .updatedAt(LocalDateTime.of(2024, 12, 4, 12, 3))
                .build();

        RoleEntity roleEntity2 = RoleEntity.builder()
                .id(2L)
                .name("User2")
                .description("description")
                .createdAt(LocalDateTime.of(2024, 12, 3, 12, 3))
                .updatedAt(LocalDateTime.of(2024, 12, 4, 12, 3))
                .build();

        List<RoleEntity> roleEntityList = List.of(roleEntity1, roleEntity2);

        RoleResponse expectedResponse1 = RoleResponse.builder()
                .id(roleEntity1.getId())
                .name(roleEntity1.getName())
                .description(roleEntity1.getDescription())
                .createdAt(roleEntity1.getCreatedAt())
                .updatedAt(roleEntity1.getUpdatedAt())
                .build();
        RoleResponse expectedResponse2 = RoleResponse.builder()
                .id(roleEntity2.getId())
                .name(roleEntity2.getName())
                .description(roleEntity2.getDescription())
                .createdAt(roleEntity2.getCreatedAt())
                .updatedAt(roleEntity2.getUpdatedAt())
                .build();
        List<RoleResponse> expectedResponseList = List.of(expectedResponse1, expectedResponse2);

        when(roleRepository.findAll()).thenReturn(roleEntityList);
        when(roleMapper.toRoleResponse(roleEntity1)).thenReturn(expectedResponse1);
        when(roleMapper.toRoleResponse(roleEntity2)).thenReturn(expectedResponse2);

        // Act
        List<RoleResponse> roleResponseList = roleService.getAll();

        // Assert
        assertEquals(roleResponseList, expectedResponseList);
    }
}