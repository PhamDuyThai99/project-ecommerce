package project.ecommerce.userMangementService.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import project.ecommerce.userMangementService.dto.request.BalanceRequest;
import project.ecommerce.userMangementService.dto.response.BalanceResponse;
import project.ecommerce.userMangementService.entity.BalanceEntity;

@Component
public class BalanceMapper {
    private final ObjectMapper objectMapper;

    public BalanceMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public BalanceEntity toBalanceEntity(BalanceRequest request) {
        return objectMapper.convertValue(request, BalanceEntity.class);
    }

    public BalanceResponse toBalanceResponse(BalanceEntity balance) {
        return objectMapper.convertValue(balance, BalanceResponse.class);
    }
}
