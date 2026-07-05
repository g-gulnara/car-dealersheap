package ru.gareeva.system.application.mapping;

import org.mapstruct.Mapper;
import ru.gareeva.system.application.contracts.models.CarModelDTO;
import ru.gareeva.system.domain.entities.cars.CarModel;

@Mapper(componentModel = "spring")
public interface CarModelMapper {

    CarModelDTO toDto(CarModel model);
}
