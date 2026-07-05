package ru.gareeva.system.infrustructure.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import ru.gareeva.system.abstractions.repositories.OrderInStockRepository;
import ru.gareeva.system.abstractions.repositories.PersonalOrderRepository;

import java.util.UUID;

@Component("orderSecurity")
@RequiredArgsConstructor
public class OrderSecurity {

    private final OrderInStockRepository orderInStockRepository;
    private final PersonalOrderRepository personalOrderRepository;

    public boolean isOwnerInStock(UUID orderId, Jwt jwt) {
        return orderInStockRepository.findById(orderId)
                .map(order -> isIdMatch(order.getClientId(), jwt))
                .orElse(false);
    }

    public boolean isPersonalOwner(UUID orderId, Jwt jwt) {
        return personalOrderRepository.findById(orderId)
                .map(order -> isIdMatch(order.getClientId(), jwt))
                .orElse(false);
    }

    private boolean isIdMatch(UUID ownerId, Jwt jwt) {
        String currentUserId = jwt.getClaimAsString("sub");
        return ownerId != null && ownerId.toString().equals(currentUserId);
    }
}