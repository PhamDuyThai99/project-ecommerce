package project.ecommerce.userMangementService.util;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import project.ecommerce.userMangementService.dto.request.CreateCartRequest;
import project.ecommerce.userMangementService.dto.response.CartResponse;
import project.ecommerce.userMangementService.entity.CartEntity;

@Mapper(componentModel = "spring")
public interface CartMapper {
    CartEntity toEntity(CreateCartRequest request);

    @Mapping(target = "products", ignore = true)
    CartResponse toResponse(CartEntity cart);
}
