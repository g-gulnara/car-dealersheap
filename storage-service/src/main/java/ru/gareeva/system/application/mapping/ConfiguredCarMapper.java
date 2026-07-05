package ru.gareeva.system.application.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.gareeva.system.application.contracts.models.ConfiguredCarDTO;
import ru.gareeva.system.domain.entities.cars.ConfiguredCar;

@Mapper(
        componentModel = "spring",
        uses = CarModelMapper.class
)
public interface ConfiguredCarMapper {
    @Mapping(source = "wheels.name", target = "wheels")
    @Mapping(source = "transmission.name", target = "transmission")
    @Mapping(source = "steeringWheel.name", target = "steeringWheel")
    @Mapping(source = "interior.name", target = "interior")
    @Mapping(source = "price.value", target = "price")
    ConfiguredCarDTO toDto(ConfiguredCar entity);
}
