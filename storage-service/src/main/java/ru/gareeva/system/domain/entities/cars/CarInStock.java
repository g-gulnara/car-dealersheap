package ru.gareeva.system.domain.entities.cars;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.gareeva.system.domain.entities.BaseEntity;
import ru.gareeva.system.domain.entities.cars.specifications.CarBodyStyle;
import ru.gareeva.system.domain.entities.cars.specifications.DriveTrain;
import ru.gareeva.system.domain.entities.cars.specifications.FuelType;
import ru.gareeva.system.domain.entities.cars.specifications.TransmissionType;
import ru.gareeva.system.domain.vo.Money;

import java.util.UUID;

@Entity
@Table(name = "cars_in_stock")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CarInStock extends BaseEntity {
    @ManyToOne
    @JoinColumn
    private CarModel model;

    @Column
    private boolean isReserved;

    @Embedded
    private Money price;


    public CarInStock(CarModel modelInp) {
        model = modelInp;
        price = modelInp.getPrice();
        isReserved = false;
    }


    public String getBrand() {
        return model.getBrand();
    }

    public String getModelName() {
        return model.getModelName();
    }

    public CarBodyStyle getCarBody() {
        return model.getCarBody();
    }

    public FuelType getFuelType() {
        return model.getFuelType();
    }

    public Integer getEnginePower() {
        return model.getEnginePower();
    }

    public Integer getEngineCapacity() {
        return model.getEngineCapacity();
    }

    public TransmissionType getTransmissionType() {
        return model.getTransmissionType();
    }

    public DriveTrain getDriveTrain() {
        return model.getDriveTrain();
    }


    public void realise() {
        isReserved = false;
    }

    public void reserve() {
        if (isReserved) {
            throw new RuntimeException("Car already reserved");
        }
        isReserved = true;
    }
}
