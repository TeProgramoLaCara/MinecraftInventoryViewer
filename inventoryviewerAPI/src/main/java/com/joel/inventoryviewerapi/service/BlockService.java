package com.joel.inventoryviewerapi.service;

import com.joel.inventoryviewerapi.entity.Block;
import com.joel.inventoryviewerapi.repository.BlockRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BlockService {

    private final BlockRepository repository;

    public BlockService(BlockRepository repository) {
        this.repository = repository;
    }

    public List<Block> findAll() { return repository.findAll(); }

    public Block findById(Integer id) { return repository.findById(id).orElse(null); }

    public Block save(Block entity) { return repository.save(entity); }

    public void delete(Integer id) { repository.deleteById(id); }

    public java.util.Optional<Block> findByName(String name) {
        return repository.findByName(name);
    }
}
