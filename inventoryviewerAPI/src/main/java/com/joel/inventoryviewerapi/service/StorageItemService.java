package com.joel.inventoryviewerapi.service;

import com.joel.inventoryviewerapi.entity.StorageItem;
import com.joel.inventoryviewerapi.repository.StorageItemRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StorageItemService {

    private final StorageItemRepository repository;

    public StorageItemService(StorageItemRepository repository) {
        this.repository = repository;
    }

    public List<StorageItem> findAll() { return repository.findAll(); }

    public StorageItem findById(Integer id) { return repository.findById(id).orElse(null); }

    public StorageItem save(StorageItem entity) { return repository.save(entity); }

    public void delete(Integer id) { repository.deleteById(id); }

    public List<StorageItem> search(String query) {
        return repository.findByItemNameContainingIgnoreCaseOrStorageBaseNameContainingIgnoreCase(query, query);
    }
}
