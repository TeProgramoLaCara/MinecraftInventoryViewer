package com.joel.inventoryviewerapi.service;

import com.joel.inventoryviewerapi.entity.Storage;
import com.joel.inventoryviewerapi.repository.StorageRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StorageService {

    private final StorageRepository repository;

    public StorageService(StorageRepository repository) {
        this.repository = repository;
    }

    public List<Storage> findAll() { return repository.findAll(); }

    public Storage findById(Integer id) { return repository.findById(id).orElse(null); }

    public Storage save(Storage entity) { return repository.save(entity); }

    public void delete(Integer id) { repository.deleteById(id); }
}
