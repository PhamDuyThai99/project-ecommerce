package project.ecommerce.userMangementService.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import project.ecommerce.userMangementService.dto.request.RoleRequest;
import project.ecommerce.userMangementService.dto.response.RoleResponse;
import project.ecommerce.userMangementService.entity.RoleEntity;

@Component
public class RoleMapper {
    private final ObjectMapper objectMapper;

    public RoleMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public RoleEntity toRoleEntity(RoleRequest request) {
        return objectMapper.convertValue(request, RoleEntity.class);
    }

    public RoleResponse toRoleResponse(RoleEntity roleEntity) {
        return objectMapper.convertValue(roleEntity, RoleResponse.class);
    }
}
