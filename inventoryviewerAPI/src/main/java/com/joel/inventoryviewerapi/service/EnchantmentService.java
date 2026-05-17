package com.joel.inventoryviewerapi.service;

import com.joel.inventoryviewerapi.entity.Enchantment;
import com.joel.inventoryviewerapi.repository.EnchantmentRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EnchantmentService {

    private final EnchantmentRepository repository;

    public EnchantmentService(EnchantmentRepository repository) {
        this.repository = repository;
    }

    public List<Enchantment> findAll() { return repository.findAll(); }

    public Enchantment findById(Integer id) { return repository.findById(id).orElse(null); }

    public Enchantment save(Enchantment entity) { return repository.save(entity); }

    public void delete(Integer id) { repository.deleteById(id); }

    public java.util.Optional<Enchantment> findByName(String name) {
        return repository.findByName(name);
    }
}
