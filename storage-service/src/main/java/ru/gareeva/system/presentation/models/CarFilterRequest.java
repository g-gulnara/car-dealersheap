package ru.gareeva.system.presentation.models;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import ru.gareeva.system.domain.entities.cars.specifications.CarBodyStyle;
import ru.gareeva.system.domain.entities.cars.specifications.DriveTrain;
import ru.gareeva.system.domain.entities.cars.specifications.FuelType;
import ru.gareeva.system.domain.entities.cars.specifications.TransmissionType;

@Data
public class CarFilterRequest {
    private String brand;

    private String modelName;

    private CarBodyStyle carBody;

    private FuelType fuelType;

    @Positive
    private Integer enginePower;

    @Positive
    private Integer engineCapacity;

    private TransmissionType transmissionType;

    private DriveTrain driveTrain;
}
