package ru.gareeva.system.application.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.gareeva.system.application.contracts.models.CarComponentDTO;
import ru.gareeva.system.domain.entities.CarComponent;

@Mapper(componentModel = "spring")
public interface CarComponentMapper {

    @Mapping(source = "extraPrice.value", target = "extraPrice")
    CarComponentDTO toDto(CarComponent entity);
}