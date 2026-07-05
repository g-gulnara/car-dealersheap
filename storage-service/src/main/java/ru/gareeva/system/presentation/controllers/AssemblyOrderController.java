package ru.gareeva.system.presentation.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import ru.gareeva.system.application.contracts.models.AssemblyOrderDto;
import ru.gareeva.system.application.services.AssemblyOrderService;


import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/assembly-orders")
@RequiredArgsConstructor
public class AssemblyOrderController {

    private final AssemblyOrderService assemblyOrderService;

    @PostMapping
    @PreAuthorize("hasRole('WAREHOUSE_ADMIN') or hasRole('ADMIN')")
    public AssemblyOrderDto create(@RequestBody AssemblyOrderDto dto,
                                   @AuthenticationPrincipal Jwt jwt) {
        return assemblyOrderService.create(dto);
    }

    @GetMapping
    @PreAuthorize("hasRole('warehouse_admin') or hasRole('admin')")
    public List<AssemblyOrderDto> getAll(@AuthenticationPrincipal Jwt jwt) {
        return assemblyOrderService.getAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('warehouse_admin') or hasRole('admin')")
    public AssemblyOrderDto getById(@PathVariable UUID id,
                                    @AuthenticationPrincipal Jwt jwt) {
        return assemblyOrderService.getById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('warehouse_admin') or hasRole('admin')")
    public AssemblyOrderDto update(@PathVariable UUID id,
                                   @RequestBody AssemblyOrderDto dto,
                                   @AuthenticationPrincipal Jwt jwt) {
        return assemblyOrderService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public void delete(@PathVariable UUID id,
                       @AuthenticationPrincipal Jwt jwt) {
        assemblyOrderService.delete(id);
    }
}