package ru.gareeva.system.application.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.gareeva.system.application.contracts.models.CarInStockDTO;
import ru.gareeva.system.domain.entities.cars.CarInStock;
import ru.gareeva.system.domain.vo.Money;

import java.math.BigDecimal;

@Mapper(
        componentModel = "spring",
        uses = CarModelMapper.class
)
public interface CarInStockMapper {
    @Mapping(source = "price.value", target = "price")
    @Mapping(source = "reserved", target = "reserved")
    @Mapping(source = "model", target = "modelDTO")
    CarInStockDTO toDto(CarInStock entity);

//    default BigDecimal mapMoneyToBigDecimal(Money money) {
//        return (money != null) ? money.getValue() : null;
//    }
}
