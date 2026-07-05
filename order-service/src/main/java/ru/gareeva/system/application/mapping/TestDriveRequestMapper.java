package ru.gareeva.system.application.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.gareeva.system.application.contracts.models.TestDriveRequestDTO;
import ru.gareeva.system.domain.entities.cars.TestDriveRequest;


@Mapper(componentModel = "spring")
public interface TestDriveRequestMapper {
    @Mapping(source = "client", target = "client")
    @Mapping(source = "car", target = "car")
    @Mapping(source = "date", target = "date")
    @Mapping(source = "time", target = "time")
    TestDriveRequestDTO toDTO(TestDriveRequest request);
}
