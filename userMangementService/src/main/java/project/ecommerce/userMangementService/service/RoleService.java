package project.ecommerce.userMangementService.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import project.ecommerce.userMangementService.dto.request.RoleRequest;
import project.ecommerce.userMangementService.dto.response.RoleResponse;
import project.ecommerce.userMangementService.entity.RoleEntity;
import project.ecommerce.userMangementService.exception.ApiError;
import project.ecommerce.userMangementService.exception.AppException;
import project.ecommerce.userMangementService.repository.RoleRepository;
import project.ecommerce.userMangementService.util.RoleMapper;

import java.util.List;

@Service
@Slf4j
public class RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    public RoleService(RoleRepository roleRepository, RoleMapper roleMapper) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
    }

    public RoleResponse createRole(RoleRequest request) {
        log.info("start createRole role service");
        if (roleRepository.findByName(request.getName()).isPresent()) {
            log.error(ApiError.ROLE_EXISTED.getMessage());
            throw new AppException(ApiError.ROLE_EXISTED);
        }

        RoleEntity role = roleMapper.toRoleEntity(request);
        return roleMapper.toRoleResponse(roleRepository.save(role));
    }

    public RoleResponse getRoleById(Long id) {
        log.info("start getRoleById role service id {}", id);
        RoleEntity role = roleRepository.findById(id).orElseThrow(
                () -> {
                    log.error("cannot find user id {}", id);
                    return new AppException(ApiError.ROLE_NOT_FOUND);
                }
        );
        RoleResponse response = roleMapper.toRoleResponse(role);
        log.info("found role with response {} = ", response.toString());
        return response;
    }

    public List<RoleResponse> getAll(){
        return roleRepository.findAll().stream()
                .map(roleMapper::toRoleResponse)
                .toList();
    }

}
