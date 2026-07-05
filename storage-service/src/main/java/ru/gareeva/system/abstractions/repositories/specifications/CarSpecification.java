package ru.gareeva.system.abstractions.repositories.specifications;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import ru.gareeva.system.domain.entities.cars.CarFilter;
import ru.gareeva.system.domain.entities.cars.CarInStock;
import ru.gareeva.system.domain.entities.cars.CarModel;

import java.util.ArrayList;
import java.util.List;


public class CarSpecification {
    public static Specification<CarInStock> build(CarFilter filter) {
        return (root, query, cb) -> {
                List <Predicate> predicates = new ArrayList<>();

            Join<CarInStock, CarModel> modelJoin = root.join("model");

            if (filter.getBrand() != null) {
                predicates.add(cb.equal(modelJoin.get("brand"), filter.getBrand()));
            }

            if (filter.getModelName() != null) {
                predicates.add(cb.equal(modelJoin.get("modelName"), filter.getModelName()));
            }

            if (filter.getFuelType() != null) {
                predicates.add(cb.equal(modelJoin.get("fuelType"), filter.getFuelType()));
            }

            if (filter.getEnginePower() != null) {
                predicates.add(cb.equal(modelJoin.get("enginePower"), filter.getBrand()));
            }

            if (filter.getEngineCapacity() != null) {
                predicates.add(cb.equal(modelJoin.get("engineCapacity"), filter.getModelName()));
            }

            if (filter.getTransmissionType() != null) {
                predicates.add(cb.equal(modelJoin.get("transmissionType"), filter.getFuelType()));
            }

            if (filter.getDriveTrain() != null) {
                predicates.add(cb.equal(modelJoin.get("driveTrain"), filter.getFuelType()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

    }
}
