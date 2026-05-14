package com.joel.inventoryviewerapi.service;

import com.joel.inventoryviewerapi.entity.ItemEnchantment;
import com.joel.inventoryviewerapi.repository.ItemEnchantmentRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ItemEnchantmentService {

    private final ItemEnchantmentRepository repository;

    public ItemEnchantmentService(ItemEnchantmentRepository repository) {
        this.repository = repository;
    }

    public List<ItemEnchantment> findAll() { return repository.findAll(); }

    public ItemEnchantment findById(Integer id) { return repository.findById(id).orElse(null); }

    public ItemEnchantment save(ItemEnchantment entity) { return repository.save(entity); }

    public void delete(Integer id) { repository.deleteById(id); }
}
