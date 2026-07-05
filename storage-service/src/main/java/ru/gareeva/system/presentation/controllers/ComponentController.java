package ru.gareeva.system.presentation.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import ru.gareeva.system.application.contracts.models.CarComponentDTO;
import ru.gareeva.system.application.contracts.services.ComponentService;
import ru.gareeva.system.application.mapping.CarComponentMapper;
import ru.gareeva.system.application.services.ComponentServiceImpl;
import ru.gareeva.system.domain.entities.CarComponent;
import ru.gareeva.system.domain.vo.Money;
import ru.gareeva.system.presentation.models.UpdateComponentPriceRequest;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/components")
@AllArgsConstructor
public class ComponentController {
    private final ComponentService service;

    private final CarComponentMapper mapper;

    @GetMapping("/compatible")
    @PreAuthorize("hasRole('admin') or hasRole('manager')")
    public List<CarComponentDTO> getCompatibleComponents(@RequestParam UUID modelId,
                                                         @AuthenticationPrincipal Jwt jwt) {
        UUID clientId = UUID.fromString(jwt.getClaimAsString("sub"));
        return service.getCompatibleComponents(modelId, clientId).stream().map(mapper::toDto).toList();
    }

    @PreAuthorize("hasRole('admin') or hasRole('manager')")
    @PatchMapping("/{id}/price")
    public void updatePrice(@PathVariable UUID id,
                            @RequestBody @Valid UpdateComponentPriceRequest request,
                            @AuthenticationPrincipal Jwt jwt) {
        UUID clientId = UUID.fromString(jwt.getClaimAsString("sub"));
        Money newPrice = new Money(request.getNewPrice());
        service.updateComponentPrice(id, newPrice, clientId);
    }}
