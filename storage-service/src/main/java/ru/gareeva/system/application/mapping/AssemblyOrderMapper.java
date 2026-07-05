package ru.gareeva.system.application.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.gareeva.system.application.contracts.models.AssemblyOrderDto;
import ru.gareeva.system.domain.entities.AssemblyOrder;

@Mapper(componentModel = "spring")
public interface AssemblyOrderMapper {

    AssemblyOrderDto toDto(AssemblyOrder entity);

    AssemblyOrder toEntity(AssemblyOrderDto dto);

    @Mapping(target = "removed", ignore = true)
    void updateEntity(@MappingTarget AssemblyOrder entity, AssemblyOrderDto dto);
}