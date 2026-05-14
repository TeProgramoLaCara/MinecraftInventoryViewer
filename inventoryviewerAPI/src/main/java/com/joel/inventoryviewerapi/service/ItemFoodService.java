package com.joel.inventoryviewerapi.service;

import com.joel.inventoryviewerapi.entity.ItemFood;
import com.joel.inventoryviewerapi.repository.ItemFoodRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ItemFoodService {

    private final ItemFoodRepository repository;

    public ItemFoodService(ItemFoodRepository repository) {
        this.repository = repository;
    }

    public List<ItemFood> findAll() { return repository.findAll(); }

    public ItemFood findById(Integer id) { return repository.findById(id).orElse(null); }

    public ItemFood save(ItemFood entity) { return repository.save(entity); }

    public void delete(Integer id) { repository.deleteById(id); }
}
