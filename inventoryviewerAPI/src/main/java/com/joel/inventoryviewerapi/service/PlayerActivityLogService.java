package com.joel.inventoryviewerapi.service;

import com.joel.inventoryviewerapi.entity.PlayerActivityLog;
import com.joel.inventoryviewerapi.repository.PlayerActivityLogRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PlayerActivityLogService {

    private final PlayerActivityLogRepository repository;

    public PlayerActivityLogService(PlayerActivityLogRepository repository) {
        this.repository = repository;
    }

    public List<PlayerActivityLog> findAll() { return repository.findAll(); }

    public PlayerActivityLog findById(Integer id) { return repository.findById(id).orElse(null); }

    public PlayerActivityLog save(PlayerActivityLog entity) { return repository.save(entity); }

    public void delete(Integer id) { repository.deleteById(id); }
}
