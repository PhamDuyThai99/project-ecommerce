package project.ecommerce.userMangementService.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import project.ecommerce.userMangementService.dto.request.BalanceRequest;
import project.ecommerce.userMangementService.dto.response.BalanceResponse;
import project.ecommerce.userMangementService.entity.BalanceEntity;

public class BalanceMapper {

    public static BalanceEntity toBalanceEntity(BalanceRequest request) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(request, BalanceEntity.class);
    }

    public static BalanceResponse toBalanceResponse(BalanceEntity balance) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(balance, BalanceResponse.class);
    }
}
