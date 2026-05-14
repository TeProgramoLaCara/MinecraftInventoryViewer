package com.joel.inventoryviewerapi.service;

import com.joel.inventoryviewerapi.entity.Item;
import com.joel.inventoryviewerapi.repository.ItemRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ItemService {

    private final ItemRepository repository;

    public ItemService(ItemRepository repository) {
        this.repository = repository;
    }

    public List<Item> findAll() { return repository.findAll(); }

    public Item findById(Integer id) { return repository.findById(id).orElse(null); }

    public Item save(Item entity) { return repository.save(entity); }

    public void delete(Integer id) { repository.deleteById(id); }

    public List<Item> search(String query) {
        return repository.findByNameContainingIgnoreCaseOrDisplayNameContainingIgnoreCase(query, query);
    }
}
