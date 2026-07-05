package ru.gareeva.system.presentation.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.gareeva.system.application.contracts.models.CarDTO;
import ru.gareeva.system.application.mapping.CarGrpcMapper;
import ru.gareeva.system.grpc.CarGrpcClient;
import ru.gareeva.system.grpc.CarResponse;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarGrpcClient carGrpcClient;
    private final CarGrpcMapper mapper;

    @GetMapping
    @PreAuthorize("hasAnyRole('user', 'manager', 'admin')")
    public ResponseEntity<List<CarDTO>> getAvailableCars() {

        List<CarDTO> cars = carGrpcClient.getAvailableCars()
                .stream()
                .map(mapper::toDto)
                .toList();

        return ResponseEntity.ok(cars);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('user', 'manager', 'admin')")
    public ResponseEntity<CarDTO> getCarById(@PathVariable String id) {

        CarDTO car = mapper.toDto(
                carGrpcClient.getCarById(id)
        );

        return ResponseEntity.ok(car);
    }
}