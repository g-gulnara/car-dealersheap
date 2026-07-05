package ru.gareeva.system.presentation.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import ru.gareeva.system.application.contracts.models.OrderInStockDTO;
import ru.gareeva.system.application.contracts.models.PersonalOrderDTO;
import ru.gareeva.system.application.contracts.services.PersonalOrderService;
import ru.gareeva.system.application.mapping.OrderInStockMapper;
import ru.gareeva.system.application.mapping.PersonalOrderMapper;
import ru.gareeva.system.domain.entities.orders.OrderInStock;
import ru.gareeva.system.domain.entities.orders.PersonalOrder;
import ru.gareeva.system.presentation.models.CreateOrderRequest;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/personal-orders")
@AllArgsConstructor
public class PersonalOrderController {
    private final PersonalOrderService orderService;
    private final PersonalOrderMapper mapper;


    @PostMapping
    @PreAuthorize("hasRole('admin') or hasRole('user') or hasRole('manager')")
    public OrderInStockDTO createPersonalOrder(@RequestBody @Valid CreateOrderRequest request,
                                               @AuthenticationPrincipal Jwt jwt) {
        UUID clientId = UUID.fromString(jwt.getClaimAsString("sub"));
        PersonalOrder order = orderService.createPersonalOrder(request.getCar(),
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
    public List<PersonalOrderDTO> getAllPersonalOrders(@RequestParam UUID userId) {
        return orderService.viewPersonalOrders(userId)
                .stream()
                .map(mapper::toDto)
                .toList();
    }


    @PostMapping("/{id}/next-step")
    @PreAuthorize("hasRole('admin') or hasRole('warehouse_admin') or hasRole('manager')")
    public void transitionToNextState(@PathVariable UUID id, @RequestParam UUID userId) {
        orderService.transitionToNextState(id, userId);
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('admin') or (hasRole('user') and @orderSecurity.isPersonalOwner(#id, authentication.name))")
    public void cancel(@PathVariable UUID id, @RequestParam UUID userId) {
        orderService.cancel(id, userId);
    }
}
