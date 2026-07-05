package ru.gareeva.system.application.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.gareeva.system.abstractions.repositories.ConcreteCarComponentRepository;
import ru.gareeva.system.application.contracts.services.ComponentService;
import ru.gareeva.system.domain.entities.CarComponent;
import ru.gareeva.system.domain.vo.Money;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ComponentServiceImpl implements ComponentService {
    private final ConcreteCarComponentRepository componentRepository;

    public List<CarComponent> getCompatibleComponents(UUID modelId, UUID user) {
        return componentRepository.findAll().stream()
                .filter(c -> c.getCompatibleModels().contains(modelId))
                .toList();
    }

    public void updateComponentPrice(UUID componentId, Money newPrice, UUID userId) {

        CarComponent component = componentRepository.findById(componentId)
                .orElseThrow(() -> new RuntimeException("Component not found"));
        component.updatePrice(newPrice);

        componentRepository.save(component);
    }

    public Optional<CarComponent> getComponentById(UUID id) {

        return componentRepository.findById(id);
    }

    public CarComponent addComponent(CarComponent component) {
        return componentRepository.save(component);
    }
}
