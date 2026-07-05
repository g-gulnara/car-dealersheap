package ru.gareeva.system.presentation.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import ru.gareeva.system.application.contracts.models.OrderInStockDTO;
import ru.gareeva.system.application.contracts.services.OrderInStockService;
import ru.gareeva.system.application.mapping.OrderInStockMapper;
import ru.gareeva.system.domain.entities.orders.OrderInStock;
import ru.gareeva.system.presentation.models.CreateOrderRequest;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/in-orders")
@AllArgsConstructor
public class OrderInStockController {
    private final OrderInStockService orderService;
    private final OrderInStockMapper mapper;

    @PostMapping
    @PreAuthorize("hasRole('admin') or hasRole('user') or hasRole('manager')")
    public OrderInStockDTO createOrderInStock(@RequestBody @Valid CreateOrderRequest request,
                                              @AuthenticationPrincipal Jwt jwt) {
        UUID clientId = UUID.fromString(jwt.getClaimAsString("sub"));
        OrderInStock order = orderService.createStockOrder(request.getCar(),
                clientId);
        OrderInStockDTO dto = new OrderInStockDTO().builder()
                .managerId(order.getManagerId())
                .clientId(order.getClientId())
                .id(order.getId())
                .build();

        return dto;
    }

    @GetMapping
    @PreAuthorize("hasRole('admin') or hasRole('warehouse_admin') or hasRole('manager')")
    public List<OrderInStockDTO> getAllOrdersInStock(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getClaimAsString("sub"));

        return orderService.viewOrdersInStock(userId)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @PreAuthorize("hasRole('admin') or hasRole('manager')")
    @PostMapping("/{id}/next-step")
    public void transitionToNextState(@PathVariable UUID id,
                         @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getClaimAsString("sub"));

        orderService.transitionToNextState(id, userId);
    }

    @PreAuthorize("hasRole('admin') or (hasRole('user') and @orderSecurity.isOwnerInStock(#id, authentication.name))")
    @PostMapping("/{id}/cancel")
    public void cancel(@PathVariable UUID id) {

        orderService.cancel(id);
    }
}
