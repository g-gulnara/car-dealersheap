package ru.gareeva.system.domain.entities.cars;

import lombok.*;
import ru.gareeva.system.domain.entities.cars.specifications.CarBodyStyle;
import ru.gareeva.system.domain.entities.cars.specifications.DriveTrain;
import ru.gareeva.system.domain.entities.cars.specifications.FuelType;
import ru.gareeva.system.domain.entities.cars.specifications.TransmissionType;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class CarFilter {
    private String brand;

    private String modelName;

    private CarBodyStyle carBody;

    private FuelType fuelType;

    private Integer enginePower;

    private Integer engineCapacity;

    private TransmissionType transmissionType;

    private DriveTrain driveTrain;
}
