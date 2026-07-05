package ru.gareeva.system.domain.entities.cars;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.gareeva.system.domain.entities.BaseEntity;
import ru.gareeva.system.domain.entities.CarComponent;
import ru.gareeva.system.domain.exceptions.IncompatibleComponentException;
import ru.gareeva.system.domain.vo.Money;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Getter
@NoArgsConstructor
@Entity
@Table(name = "configured_cars")
public class ConfiguredCar extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "model_id")
    private CarModel model;

    @Embedded
    private Money price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wheels_id")
    private CarComponent wheels;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transmission_component_id")
    private CarComponent transmission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "steering_wheel_id")
    private CarComponent steeringWheel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interior_id")
    private CarComponent interior;


    public ConfiguredCar(CarModel model,
                         CarComponent wheels,
                         CarComponent transmission,
                         CarComponent steeringWheel,
                         CarComponent interior) {

        List<CarComponent> comps = List.of(wheels, transmission, steeringWheel, interior);

        for (CarComponent c : comps) {
            if (!c.isCompatibleWith(model.getId())) {
                throw new IncompatibleComponentException(
                        "Component " + c.getName() + " not compatible with model " + model.getId()
                );
            }
        }

        Money total = calculateExtraPrice(comps);

        this.model = model;
        this.wheels = wheels;
        this.transmission = transmission;
        this.steeringWheel = steeringWheel;
        this.interior = interior;

        this.price = model.getPrice().increase(total);
    }

    private Money calculateExtraPrice(List<CarComponent> extras) {
        Money sum = new Money(0);

        for (CarComponent c : extras) {
            sum = sum.increase(c.getExtraPrice());
        }

        return sum;
    }

}
