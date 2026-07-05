package ru.gareeva.system.presentation.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import ru.gareeva.system.application.contracts.models.TestDriveRequestDTO;
import ru.gareeva.system.application.contracts.services.TestDriveService;
import ru.gareeva.system.application.mapping.TestDriveRequestMapper;
import ru.gareeva.system.domain.entities.cars.TestDriveRequest;
import ru.gareeva.system.presentation.models.CreateTestDriveRequest;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/test-drives")
@RequiredArgsConstructor
public class TestDriveController {

    private final TestDriveService testDriveService;
    private final TestDriveRequestMapper mapper;

    @PostMapping
    @PreAuthorize("hasRole('admin') or hasRole('manager')")
    public TestDriveRequestDTO create(@RequestBody CreateTestDriveRequest request,
                                      @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getClaimAsString("sub"));
        TestDriveRequest entity = testDriveService.requestTestDrive(
                request.getCarId(),
                userId,
                request.getTime()
        );
        return mapper.toDTO(entity);
    }

    @GetMapping("/test-drive")
    @PreAuthorize("hasRole('admin') or hasRole('warehouse_admin') or hasRole('manager')")
    public List<TestDriveRequestDTO> getAll(@AuthenticationPrincipal Jwt jwt) {
        UUID clientId = UUID.fromString(jwt.getClaimAsString("sub"));

        return testDriveService.getTestDriveRequests(clientId)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }
}