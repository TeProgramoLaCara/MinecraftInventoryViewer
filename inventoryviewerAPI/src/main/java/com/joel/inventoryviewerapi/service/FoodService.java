package com.joel.inventoryviewerapi.service;

import com.joel.inventoryviewerapi.entity.Food;
import com.joel.inventoryviewerapi.repository.FoodRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FoodService {

    private final FoodRepository repository;

    public FoodService(FoodRepository repository) {
        this.repository = repository;
    }

    public List<Food> findAll() { return repository.findAll(); }

    public Food findById(Integer id) { return repository.findById(id).orElse(null); }

    public Food save(Food entity) { return repository.save(entity); }

    public void delete(Integer id) { repository.deleteById(id); }

    public java.util.Optional<Food> findByName(String name) {
        return repository.findByName(name);
    }
}
