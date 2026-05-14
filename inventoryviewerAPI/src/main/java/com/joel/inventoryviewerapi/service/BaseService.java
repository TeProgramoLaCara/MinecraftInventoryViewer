package com.joel.inventoryviewerapi.service;

import com.joel.inventoryviewerapi.entity.Base;
import com.joel.inventoryviewerapi.repository.BaseRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BaseService {

    private final BaseRepository repository;

    public BaseService(BaseRepository repository) {
        this.repository = repository;
    }

    public List<Base> findAll() { return repository.findAll(); }

    public Base findById(Integer id) { return repository.findById(id).orElse(null); }

    public Base save(Base entity) { return repository.save(entity); }

    public Base update(Integer id, Base updatedEntity) {
        return repository.findById(id).map(existingEntity -> {
            // Assuming 'Base' has 'name' and 'description' fields
            // You might need to add more fields here depending on your Base entity
            existingEntity.setName(updatedEntity.getName());
            existingEntity.setDescription(updatedEntity.getDescription());
            return repository.save(existingEntity);
        }).orElseThrow(() -> new RuntimeException("Entity with id " + id + " not found")); // Consider a custom exception
    }

    public void delete(Integer id) { repository.deleteById(id); }
}
