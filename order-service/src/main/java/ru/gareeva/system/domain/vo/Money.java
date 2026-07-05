package ru.gareeva.system.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.management.ConstructorParameters;

@Getter
@Embeddable
@NoArgsConstructor
public class Money {
    @Column(name = "price_value")
    private Integer value;

    public Money(Integer inpValue) {
        if (inpValue < 0) {
            throw new IllegalArgumentException("Value can not be negative");
        }

        value = inpValue;
    }

    public Money increase(Money diff) {
        return new Money(value + diff.value);
    }
}
