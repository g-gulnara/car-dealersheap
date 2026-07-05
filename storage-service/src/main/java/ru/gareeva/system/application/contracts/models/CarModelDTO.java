package ru.gareeva.system.application.contracts.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import ru.gareeva.system.domain.entities.BaseEntity;
import ru.gareeva.system.domain.entities.cars.specifications.CarBodyStyle;
import ru.gareeva.system.domain.entities.cars.specifications.DriveTrain;
import ru.gareeva.system.domain.entities.cars.specifications.FuelType;
import ru.gareeva.system.domain.entities.cars.specifications.TransmissionType;
import ru.gareeva.system.domain.vo.Money;

@AllArgsConstructor
@Getter
@Builder
public class CarModelDTO {
    private Money price;

    private String brand;

    private String modelName;

    private CarBodyStyle carBody;

    private FuelType fuelType;

    private Integer enginePower;

    private Integer engineCapacity;

    private TransmissionType transmissionType;

    private DriveTrain driveTrain;
}

