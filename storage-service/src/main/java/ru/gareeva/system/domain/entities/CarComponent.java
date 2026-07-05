package ru.gareeva.system.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import ru.gareeva.system.domain.vo.Money;

import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@Builder
@NoArgsConstructor
@Entity
@Table(name = "car_components")
public class CarComponent extends BaseEntity {
    @Column(nullable = false)
    public String name;

    @Embedded
    private Money extraPrice;

    @ElementCollection
    @CollectionTable(name = "car_component_compatible_models", joinColumns = @JoinColumn(name = "component_id"))
    @Column(name = "model_id")
    private Set<UUID> compatibleModels;

    public boolean isCompatibleWith(UUID modelId) {
        return compatibleModels.contains(modelId);
    }

    public void updatePrice(Money newPrice) {
        extraPrice = newPrice;
    }
}
