package ru.gareeva.system.domain.entities.cars;

import jakarta.persistence.*;
import lombok.*;
import ru.gareeva.system.domain.entities.BaseEntity;
import ru.gareeva.system.domain.entities.CarComponent;
import ru.gareeva.system.domain.entities.cars.specifications.CarBodyStyle;
import ru.gareeva.system.domain.entities.cars.specifications.DriveTrain;
import ru.gareeva.system.domain.entities.cars.specifications.FuelType;
import ru.gareeva.system.domain.entities.cars.specifications.TransmissionType;
import ru.gareeva.system.domain.vo.Money;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@Builder
@NoArgsConstructor
@Entity
@Table(name = "car_models")
public class CarModel extends BaseEntity {
    @Embedded
    private Money price;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String modelName;

    @Enumerated(EnumType.STRING)
    private CarBodyStyle carBody;

    @Enumerated(EnumType.STRING)
    private FuelType fuelType;

    private Integer enginePower;

    private Integer engineCapacity;

    @Enumerated(EnumType.STRING)
    private TransmissionType transmissionType;

    @Enumerated(EnumType.STRING)
    private DriveTrain driveTrain;
}
