package ru.gareeva.system.presentation.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import ru.gareeva.system.application.contracts.models.CarInStockDTO;
import ru.gareeva.system.application.contracts.services.CarService;
import ru.gareeva.system.application.mapping.CarInStockMapper;
import ru.gareeva.system.domain.entities.cars.CarFilter;
import ru.gareeva.system.domain.entities.cars.CarInStock;
import ru.gareeva.system.presentation.models.AddNewCarToStockRequest;
import ru.gareeva.system.presentation.models.CarFilterRequest;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/cars")
@AllArgsConstructor
public class CarController {
    private final CarService carService;
    private final CarInStockMapper mapper;

    @PreAuthorize("hasRole('admin') or hasRole('warehouse_admin') or hasRole('manager') or hasRole('user')")
    @GetMapping
    List<CarInStockDTO> getCarsWithFilter(@Valid CarFilterRequest filterRequest) {
        CarFilter filter = new CarFilter(
                filterRequest.getBrand(),
                filterRequest.getModelName(),
                filterRequest.getCarBody(),
                filterRequest.getFuelType(),
                filterRequest.getEnginePower(),
                filterRequest.getEngineCapacity(),
                filterRequest.getTransmissionType(),
                filterRequest.getDriveTrain()
        );

        return carService.getCarsWithFilter(filter)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('admin') or hasRole('warehouse_admin') or hasRole('manager')")
    public CarInStockDTO getCar(@PathVariable UUID id) {
        return mapper.toDto(carService.getCarInfo(id));
    }

    @PreAuthorize("hasRole('admin') or hasRole('manager')")
    @PostMapping("/{id}")
    public void addNewCarToStock(@RequestBody AddNewCarToStockRequest request,
                                 @AuthenticationPrincipal Jwt jwt) {
        UUID clientId = UUID.fromString(jwt.getClaimAsString("sub"));
        carService.addNewCarToStock(request.getCar(), clientId);
    }

    @GetMapping("/test-drive")
    @PreAuthorize("hasRole('admin') or hasRole('manager')")
    public List<UUID> getCarsForTestDrive() {
        return carService.getCarsForTestDrive();
    }

    @PostMapping("/test-drive/{carId}")
    @PreAuthorize("hasRole('admin') or hasRole('warehouse_admin')")
    public void addCarToTestDrive(@PathVariable CarInStock carId) {
        carService.addCarToTestDrive(carId);
    }

    @DeleteMapping("/test-drive/{carId}")
    @PreAuthorize("hasRole('admin') or hasRole('warehouse_admin')")
    public void removeCarFromTestDrive(@PathVariable UUID carId) {
        carService.removeCarFromTestDrive(carId);
    }
}
