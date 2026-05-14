package com.joel.inventoryviewerapi.service;

import com.joel.inventoryviewerapi.entity.Biome;
import com.joel.inventoryviewerapi.repository.BiomeRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BiomeService {

    private final BiomeRepository repository;

    public BiomeService(BiomeRepository repository) {
        this.repository = repository;
    }

    public List<Biome> findAll() { return repository.findAll(); }

    public Biome findById(Integer id) { return repository.findById(id).orElse(null); }

    public Biome save(Biome entity) { return repository.save(entity); }

    public void delete(Integer id) { repository.deleteById(id); }
}
