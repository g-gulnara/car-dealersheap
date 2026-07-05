package ru.gareeva.system.application.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.gareeva.system.application.contracts.models.OrderInStockDTO;
import ru.gareeva.system.application.contracts.models.PersonalOrderDTO;
import ru.gareeva.system.domain.entities.orders.OrderInStock;
import ru.gareeva.system.domain.entities.orders.PersonalOrder;

@Mapper(componentModel = "spring")
public interface PersonalOrderMapper {

    @Mapping(source = "clientId", target = "clientId")
    @Mapping(source = "managerId", target = "managerId")
    @Mapping(source = "carId", target = "car")
    @Mapping(source = "payed", target = "payed")
    PersonalOrderDTO toDto(PersonalOrder entity);
}