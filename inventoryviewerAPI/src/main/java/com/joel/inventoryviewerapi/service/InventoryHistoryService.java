package com.joel.inventoryviewerapi.service;

import com.joel.inventoryviewerapi.entity.InventoryHistory;
import com.joel.inventoryviewerapi.repository.InventoryHistoryRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class InventoryHistoryService {

    private final InventoryHistoryRepository repository;

    public InventoryHistoryService(InventoryHistoryRepository repository) {
        this.repository = repository;
    }

    public List<InventoryHistory> findAll() { return repository.findAll(); }

    public InventoryHistory findById(Integer id) { return repository.findById(id).orElse(null); }

    public InventoryHistory save(InventoryHistory entity) { return repository.save(entity); }

    public void delete(Integer id) { repository.deleteById(id); }
}
