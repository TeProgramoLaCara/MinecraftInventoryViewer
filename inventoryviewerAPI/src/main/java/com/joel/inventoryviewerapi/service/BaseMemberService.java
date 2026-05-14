package com.joel.inventoryviewerapi.service;

import com.joel.inventoryviewerapi.entity.BaseMember;
import com.joel.inventoryviewerapi.repository.BaseMemberRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BaseMemberService {

    private final BaseMemberRepository repository;

    public BaseMemberService(BaseMemberRepository repository) {
        this.repository = repository;
    }

    public List<BaseMember> findAll() { return repository.findAll(); }

    public BaseMember findById(Integer id) { return repository.findById(id).orElse(null); }

    public BaseMember save(BaseMember entity) { return repository.save(entity); }

    public void delete(Integer id) { repository.deleteById(id); }
}
