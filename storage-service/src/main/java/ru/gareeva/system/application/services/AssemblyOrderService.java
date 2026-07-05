package ru.gareeva.system.application.services;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gareeva.system.abstractions.repositories.AssemblyOrderRepository;
import ru.gareeva.system.application.contracts.models.AssemblyOrderDto;
import ru.gareeva.system.application.mapping.AssemblyOrderMapper;
import ru.gareeva.system.domain.entities.AssemblyOrder;
import ru.gareeva.system.domain.entities.AssemblyStatus;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AssemblyOrderService {

    private final AssemblyOrderRepository repository;
    private final AssemblyOrderMapper mapper;

    public AssemblyOrderDto create(AssemblyOrderDto dto) {
        AssemblyOrder entity = mapper.toEntity(dto);
        entity.setStatus(AssemblyStatus.CREATED);
        AssemblyOrder saved = repository.save(entity);
        return mapper.toDto(saved);
    }

    public List<AssemblyOrderDto> getAll() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    public AssemblyOrderDto getById(UUID id) {
        AssemblyOrder entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("AssemblyOrder not found"));
        return mapper.toDto(entity);
    }

    public AssemblyOrderDto update(UUID id, AssemblyOrderDto dto) {
        AssemblyOrder entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("AssemblyOrder not found"));

        mapper.updateEntity(entity, dto);
        AssemblyOrder saved = repository.save(entity);
        return mapper.toDto(saved);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}