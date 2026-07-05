package ru.gareeva.system.application.contracts.services;

import ru.gareeva.system.domain.entities.CarComponent;
import ru.gareeva.system.domain.vo.Money;

import java.util.List;
import java.util.UUID;

public interface ComponentService {
    List<CarComponent> getCompatibleComponents(UUID modelId, UUID user);

    void updateComponentPrice(UUID componentId, Money newPrice, UUID user);
}
