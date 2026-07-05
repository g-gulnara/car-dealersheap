package ru.gareeva.system.application.mapping;

import org.springframework.stereotype.Component;
import ru.gareeva.system.application.contracts.models.CarDTO;
import ru.gareeva.system.grpc.CarResponse;

@Component
public class CarGrpcMapper {

    public CarDTO toDto(CarResponse response) {
        return new CarDTO(
                response.getId(),
                response.getBrand(),
                response.getModelName()
        );
    }
}