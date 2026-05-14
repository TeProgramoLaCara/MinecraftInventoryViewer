package com.joel.inventoryviewerapi.service;

import com.joel.inventoryviewerapi.entity.ContainerType;
import com.joel.inventoryviewerapi.repository.ContainerTypeRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ContainerTypeService {

    private final ContainerTypeRepository repository;

    public ContainerTypeService(ContainerTypeRepository repository) {
        this.repository = repository;
    }

    public List<ContainerType> findAll() { return repository.findAll(); }

    public ContainerType findById(Integer id) { return repository.findById(id).orElse(null); }

    public ContainerType save(ContainerType entity) { return repository.save(entity); }

    public void delete(Integer id) { repository.deleteById(id); }
}
