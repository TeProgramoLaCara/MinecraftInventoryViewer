package com.joel.inventoryviewerapi.service;

import com.joel.inventoryviewerapi.entity.BaseTag;
import com.joel.inventoryviewerapi.repository.BaseTagRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BaseTagService {

    private final BaseTagRepository repository;

    public BaseTagService(BaseTagRepository repository) {
        this.repository = repository;
    }

    public List<BaseTag> findAll() { return repository.findAll(); }

    public BaseTag findById(Integer id) { return repository.findById(id).orElse(null); }

    public BaseTag save(BaseTag entity) { return repository.save(entity); }

    public void delete(Integer id) { repository.deleteById(id); }
}
